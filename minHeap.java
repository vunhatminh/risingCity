public class minHeap {

    private static HeapNode[] Heap;     // heap array
    public static int HeapSize;         // current size of heap
    private static int maxSize;         // max size of heap (given 2000)

    // Constructor of min heap
    public minHeap(int maxSize) {
        this.maxSize = maxSize;
        this.HeapSize = 0;
        Heap = new HeapNode[this.maxSize + 1];
        Heap[0] = null;
    }

    static class HeapNode {
        int key;                    // key, which is the index of the node on the heap
        int buildingNum;            // building number
        int executed_time;          // executed time
        int total_time;             // total time to finished

        public HeapNode(int buildingNum) {
            this.key = -1;
            this.buildingNum = buildingNum;
            this.total_time = 0;
            this.executed_time = 0;
        }
    }

    // Initialize node
    public static HeapNode initNode(int buildingNum, int total_time){
        HeapNode heapNode = new HeapNode(buildingNum);
        heapNode.total_time = total_time;
        return heapNode;
    }

    // The heapify function of min heap
    private static void heapify(HeapNode node) {

        if (!isLeaf(node)) {
            // Case 1: node has no right child. Swap if the child is smaller in the sense of given assignment
            if (Heap[rightIndex(node.key)] == null) {
                if (smaller(Heap[leftIndex(node.key)], Heap[node.key])) {
                    swap(Heap[node.key],Heap[leftIndex(node.key)]);
                    // Continue to heapify until leave
                    heapify(Heap[node.key]);
                }
            }
            // Case 2: node has two children. Swap with the smaller child of that child is smaller
            else {
                if (smaller(Heap[leftIndex(node.key)], Heap[node.key])
                        || smaller(Heap[rightIndex(node.key)], Heap[node.key])) {
                    if (smaller(Heap[leftIndex(node.key)], Heap[rightIndex(node.key)])) {
                        swap(Heap[node.key],Heap[leftIndex(node.key)]);
                        // Continue to heapify until leaf
                        heapify(Heap[node.key]);
                    }
                    else {
                        swap(Heap[node.key],Heap[rightIndex(node.key)]);
                        // Continue to heapify until leaf
                        heapify(Heap[node.key]);
                    }
                }
            }
        }
    }

    // Insert node to heap by node
    public static void insert (HeapNode heapNode) {
        // If there is no node in the heap, the insert node is the first
        if (HeapSize == 0) {
            HeapSize = 1;
            Heap[HeapSize] = heapNode;
            heapNode.key = HeapSize;
        }
        // Insert to the last of the place of the heap
        else {
            if (HeapSize >= maxSize) {
                return;
            }
            HeapSize = HeapSize +1;
            Heap[HeapSize] = heapNode;
            heapNode.key = HeapSize;
        }
        // Iteratively swap the node with its parent if it is smaller
        int index = HeapSize;
        while (smaller(Heap[index], Heap[parentIndex(index)])) {
            swap(Heap[index],Heap[parentIndex(index)]);
            index = parentIndex(index);
        }
    }

    // Insert node to heap by building number and time
    public static void insert(int buildingNumber, int time) {
        HeapNode heapNode = initNode(buildingNumber,time);
        insert(heapNode);
    }

    // Get the first node of the heap
    public static HeapNode getTop() {
        if (HeapSize > 0) {
            HeapNode node = Heap[1];
            return node;
        } else {
            return null;
        }
    }

    // Increase min node executed time
    public static void increaseExecutedTime(HeapNode node, int time) {
        node.executed_time = node.executed_time + time;
        RedBlackTree.Node RBT_Node = RedBlackTree.findNode(node.buildingNum);
        RBT_Node.executed_time = node.executed_time;
        // After increasing the executed time, we need to fix the heap by heapify
        heapify(node);
    }

    // Remove min node from heap. Assume there is at least one node on the heap
    public static HeapNode removeMin() {
        HeapNode minNode = Heap[1];
        if (HeapSize > 1) {
            Heap[1] = Heap[HeapSize];
            Heap[1].key = 1;
            Heap[HeapSize] = null;
            HeapSize = HeapSize - 1;
            heapify(Heap[1]);
        }
        else {
            Heap[1] = null;
            HeapSize = 0;
        }
        return minNode;
    }

    // Remove node from heap
    public static void remove(HeapNode node) {
        if (HeapSize > node.key) {
            Heap[node.key] = Heap[HeapSize];
            Heap[node.key].key = node.key;
            Heap[HeapSize] = null;
            HeapSize = HeapSize - 1;

            int index = node.key;
            if (smaller(Heap[index], Heap[parentIndex(index)])) {
                while (smaller(Heap[index], Heap[parentIndex(index)])) {
                    swap(Heap[index], Heap[parentIndex(index)]);
                    index = parentIndex(index);
                }
            }
            else {
                heapify(Heap[node.key]);
            }
        }
        else {
            Heap[HeapSize] = null;
            HeapSize = HeapSize - 1;
        }
    }

    // Return the index of the parent
    private static int parentIndex(int index) {
        if (index == 1) {
            return 1;
        } else {
            return index/2;
        }
    }

    // Return the index of the left child
    public static int leftIndex(int index) {
        return (2*index);
    }

    // Return the index of the right child
    public static int rightIndex(int index) {
        return (2*index)+1;
    }

    // Check if node is a leaf node
    public static boolean isLeaf (HeapNode node) {
        if (node.key > (HeapSize / 2) && node.key <= HeapSize) {
            return true;
        }
        return false;
    }

    // Check if node 1 is "smaller" than node 2.
    // Smaller is defined  based on executed_time. If the executed_time(s) are equals, the order is based on building number
    private static boolean smaller (HeapNode node1, HeapNode node2) {
        if (node1.executed_time < node2.executed_time) {
            return true;
        } else if (node1.executed_time > node2.executed_time) {
            return false;
        } else {
            if (node1.buildingNum < node2.buildingNum) {
                return true;
            } else {
                return false;
            }
        }
    }

    // Swap two nodes on the heap
    private static void swap(HeapNode node1, HeapNode node2) {
        int tempkey;
        tempkey = node1.key;
        node1.key = node2.key;
        node2.key = tempkey;
        Heap[node1.key] = node1;
        Heap[node2.key] = node2;
    }

    // Print the heap
    public static void printHeap() {
        for (int index = 1; index <= HeapSize/2; index++) {
            System.out.print("Building: " + Heap[index].buildingNum);
            System.out.print(" | Key: " + Heap[index].key);
            System.out.print(" | Parent: " + Heap[parentIndex(index)].buildingNum);
            if (Heap[leftIndex(index)] != null) {
                System.out.print(" | Left: " + Heap[leftIndex(index)].buildingNum);
            }
            if (Heap[rightIndex(index)] != null) {
                System.out.print(" | Right: " + Heap[rightIndex(index)].buildingNum);
            }
            System.out.println("");
        }
    }
}
