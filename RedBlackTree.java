import java.io.FileWriter;
import java.io.IOException;

public class RedBlackTree{

    final static int RED = 0;       // Color red
    final static int BLACK = 1;     // Color black
    final static int LEFT = 0;      // Variable defined the scenario in insertion
    final static int RIGHT = 1;     // Variable defined the scenario in insertion
    static boolean first = true;    // boolean value for printing
    static Node root;               // root of the tree

    static class Node {
        int key;                    // key, which is also the building number
        Node left;                  // pointer to left child
        Node right;                 // pointer to right child
        Node parent;                // pointer to parent node
        int color;                  // color of the node
        int buildingNum;            // building number
        int executed_time;          // executed time
        int total_time;             // total time to finished
        minHeap.HeapNode heapNode;  // pointer to the corresponding heap node

        // Constructor for RBT node
        public Node(int key, int color) {
            this.key = key;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.color = color;
            this.buildingNum = key;
            this.total_time = 0;
            this.executed_time = 0;
            this.heapNode = null;
        }
    }

    // Initialize node
    public static Node initNode(int key, int total_time){
        Node node = new Node(key,RED);
        node.total_time = total_time;
        node.left = new Node(-1,BLACK);
        node.right = new Node(-1,BLACK);
        return node;
    }

    // Find node by key (or building number)
    public static Node findNode(int key) {
        Node temp = root;
        while(true){
            if (temp.key > key) {
                if(temp.left.key == -1){
                    return null;
                }
                temp = temp.left;
                continue;
            }
            if (temp.key < key) {
                if(temp.right.key==-1){
                    return null;
                }
                temp = temp.right;
            }
            if (temp.key == key) {
                return temp;
            }
        }
    }

    // Find node "next" with smallest key such that the next.key is greater than or equal
    // to the node.key and the degree of next is 0 or 1
    private static Node findNextNode(Node node) {
        if (node == null) {
            return null;
        }
        else {
            Node next = node.right;

            if (next.key == -1) {
                return node;
            }
            if (node.left.key == -1) {
                return node;
            }
            while(next.left.key != -1){
                next = next.left;
            }
            return next;
        }
    }

    // Swap data of two nodes
    private static void swapData(Node node1, Node node2) {
        int tempkey;
        tempkey = node1.key;
        node1.key = node2.key;
        node2.key = tempkey;

        int tempBuildingNum = node1.buildingNum;
        node1.buildingNum = node2.buildingNum;
        node2.buildingNum = tempBuildingNum;

        int temp_executed_time = node1.executed_time;
        node1.executed_time = node2.executed_time;
        node2.executed_time = temp_executed_time;

        int temp_total_time = node1.total_time;
        node1.total_time = node2.total_time;
        node2.total_time = temp_total_time;

        minHeap.HeapNode temp_heapNode = node1.heapNode;
        node1.heapNode = node2.heapNode;
        node2.heapNode = temp_heapNode;
    }

    // Insert node to Tree
    public static void insert(Node node) {
        if(root == null){
            root = node;
            root.color = BLACK;
            return;
        }

        Node temp = root;

        while(true){
            if(temp.key > node.key){
                if(temp.left.key == -1){
                    temp.left = node;
                    node.parent = temp;
                    // Fix node after insert
                    remedy(node);
                    return;
                }
                temp = temp.left;
                continue;
            }
            if(temp.key < node.key){
                if(temp.right.key==-1){
                    temp.right = node;
                    node.parent = temp;
                    // Fix node after insert
                    remedy(node);
                    return;
                }
                temp = temp.right;
            }
        }
    }

    // Insert node to Tree by building number and total time
    public static void insert(int buildingNum, int time){
        Node node = initNode(buildingNum,time);
        insert(node);
    }

    // Remove node from tree
    public static void remove(int key) {

        if (root == null) {
            return;
        }

        // Find the node with the key
        Node temp = findNode(key);

        // If not found, then return.
        if (temp == null) {
//            System.out.println("No building of building number " + key + " to remove");
            return;
        }

        // removeNode is the non-degree-2 node to be removed
        Node removeNode = findNextNode(temp);

        swapData(temp,removeNode);

        Node parent = removeNode.parent;
        Node child;
        if (removeNode.left.key == -1 ) {
            child = removeNode.right;
        }
        else {
            child = removeNode.left;
        }

        // Case remove node is root
        if (parent == null) {
            if (removeNode.right.key == -1 && removeNode.left.key == -1) {
                root = null;
            }
            else {
                root = child;
                root.parent = null;
                root.color = BLACK;
            }
        }
        // Case remove node is non-root
        else {
            if (parent.right == removeNode) {
                parent.right = child;
                if (child.key != -1) {
                    child.parent = parent;
                }

                // Only need to fix if removeNode.color is BLACK
                if (removeNode.color == BLACK) {
                    fix(parent,RIGHT);
                }
            }
            else {
                parent.left = child;
                if (child.key != -1) {
                    child.parent = parent;
                }

                // Only need to fix if removeNode.color is BLACK
                if (removeNode.color == BLACK) {
                    fix(parent,LEFT);
                }
            }
        }
    }

    // Count the number of red children of a node
    private static int countRedChild(Node node) {
        if (node.left.color == RED && node.right.color == RED){
            return 2;
        }
        else if (node.left.color == BLACK && node.right.color == BLACK) {
            return 0;
        }
        else {
            return 1;
        }
    }

    // Fix node after removal
    private static void fix(Node node, int side) {
        // If node y is RED, just need to color it BLACK
        if (side == RIGHT) {
            if (node.right.key != -1) {
                if (node.right.color == RED) {
                    node.right.color = BLACK;
                    return;
                }
            }
        }
        else {
            if (node.left.key != -1) {
                if (node.left.color == RED) {
                    node.left.color = BLACK;
                    return;
                }
            }
        }

        if (side == RIGHT) {
            Node sibling = node.left;
            if (sibling.color == BLACK) {
                //Case RB
                int n = countRedChild(sibling);
                switch (n) {
                    case 0:
                        if (node.color == BLACK) {
                            sibling.color = RED;
                            if (node.parent != null) {
                                if (node.parent.right == node) {
                                    fix(node.parent,RIGHT);
                                }
                                else {
                                    fix(node.parent,LEFT);
                                }
                            }
                        }
                        else {
                            node.color = BLACK;
                            sibling.color = RED;
                        }
                        break;
                    case 1:
                        if (sibling.left.key != -1) {
                            if (sibling.left.color == RED) {
                                sibling.left.color = BLACK;
                                sibling.color = node.color;
                                node.color = BLACK;
                                rightRotate(sibling);
                            }
                        }
                        if (sibling.right.key != -1) {
                            if (sibling.right.color == RED) {
                                sibling.right.color = node.color;
                                node.color = BLACK;
                                leftRotate(sibling.right);
                                rightRotate(sibling.parent);
                            }
                        }
                        break;
                    case 2:
                        sibling.right.color = node.color;
                        node.color = BLACK;
                        leftRotate(sibling.right);
                        rightRotate(sibling.parent);
                        break;
                }
            }
            else {
                //Case RR
                int n = countRedChild(sibling.right);
                switch (n) {
                    case 0:
                        sibling.right.color = RED;
                        sibling.color = BLACK;
                        rightRotate(sibling);
                        break;
                    case 1:
                        if (sibling.right.left.key != -1) {
                            if (sibling.right.left.color == RED) {
                                sibling.right.left.color = BLACK;
                                leftRotate(sibling.right);
                                rightRotate(sibling.parent);
                            }
                        }
                        if (sibling.right.right.key != -1) {
                            if (sibling.right.right.color == RED) {
                                sibling.right.right.color = BLACK;
                                leftRotate(sibling.right);
                                leftRotate(sibling.parent.right);
                                rightRotate(sibling.parent.parent);
                                rightRotate(sibling);
                            }
                        }
                        break;
                    case 2:
                        sibling.right.right.color = BLACK;
                        leftRotate(sibling.right);
                        leftRotate(sibling.parent.right);
                        rightRotate(sibling.parent.parent);
                        rightRotate(sibling);
                        break;
                }
            }
        }
        else {
            Node sibling = node.right;
            if (sibling.color == BLACK) {
                //Case LB
                int n = countRedChild(sibling);
                switch (n) {
                    case 0:
                        if (node.color == BLACK) {
                            sibling.color = RED;
                            if (node.parent != null) {
                                if (node.parent.left == node) {
                                    fix(node.parent,LEFT);
                                }
                                else {
                                    fix(node.parent,RIGHT);
                                }
                            }
                        }
                        else {
                            node.color = BLACK;
                            sibling.color = RED;
                        }
                        break;
                    case 1:
                        if (sibling.right.key != -1) {
                            if (sibling.right.color == RED) {
                                sibling.right.color = BLACK;
                                sibling.color = node.color;
                                node.color = BLACK;
                                leftRotate(sibling);
                            }
                        }
                        if (sibling.left.key != -1) {
                            if (sibling.left.color == RED) {
                                sibling.left.color = node.color;
                                node.color = BLACK;
                                rightRotate(sibling.left);
                                leftRotate(sibling.parent);
                            }
                        }
                        break;
                    case 2:
                        sibling.left.color = node.color;
                        node.color = BLACK;
                        rightRotate(sibling.left);
                        leftRotate(sibling.parent);
                        break;
                }
            }
            else {
                //Case LR
                int n = countRedChild(sibling.left);
                switch (n) {
                    case 0:
                        sibling.left.color = RED;
                        sibling.color = BLACK;
                        leftRotate(sibling);
                        break;
                    case 1:
                        if (sibling.left.right.key != -1) {
                            if (sibling.left.right.color == RED) {
                                sibling.left.right.color = BLACK;
                                rightRotate(sibling.left);
                                leftRotate(sibling.parent);
                            }
                        }
                        if (sibling.left.left.key != -1) {
                            if (sibling.left.left.color == RED) {
                                sibling.left.left.color = BLACK;
                                rightRotate(sibling.left);
                                rightRotate(sibling.parent.left);
                                leftRotate(sibling.parent.parent);
                                leftRotate(sibling);
                            }
                        }
                        break;
                    case 2:
                        sibling.left.left.color = BLACK;
                        rightRotate(sibling.left);
                        rightRotate(sibling.parent.left);
                        leftRotate(sibling.parent.parent);
                        leftRotate(sibling);
                        break;
                }
            }
        }



    }

    // Fix node after insertion
    private static void remedy(Node node) {

        //If node is root, color it black
        if (node.parent == null) {
            root = node;
            root.color = BLACK;
            return;
        }

        //If parent is black, do nothing
        if (node.parent.color == BLACK) {
            return;
        }

        //If not root and parent is red, find grandparent's other child (sibling)
        Node sibling = null;
        if (node.parent.parent.left == node.parent) {
            sibling = node.parent.parent.right;
        }
        else {
            sibling = node.parent.parent.left;
        }

        /*If sibling color is red:
            change sibling's color to black,
            change parent's color to black,
            change grandparent's color to red
            and remedy grandparent*/
        if (sibling.color == RED) {
            sibling.color = BLACK;
            node.parent.color = BLACK;
            node.parent.parent.color = RED;
            remedy(node.parent.parent);
            return;
        }
        //If sibling color is black:
        else {
            //Case xLB
            if (node == node.parent.left) {
                //Case LLB
                if (node.parent == node.parent.parent.left) {
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rightRotate(node.parent);
                }
                //Case RLB
                else {
                    node.color = BLACK;
                    node.parent.parent.color = RED;
                    rightRotate(node);
                    leftRotate(node);
                }
            }
            //Case xRB
            else {
                //Case RRB
                if (node.parent == node.parent.parent.right) {
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    leftRotate(node.parent);
                }
                //Case LRB
                else {
                    node.color = BLACK;
                    node.parent.parent.color = RED;
                    leftRotate(node);
                    rightRotate(node);
                }
            }
        }
    }

    // Rotate right move (only change pointers, not color)
    private static void rightRotate(Node node){
        node.parent.left = node.right;
        node.right = node.parent;
        node.parent = node.parent.parent;
        node.right.parent = node;
        if (node.right.left != null) {
            node.right.left.parent = node.right;
        }
        if (node.parent == null) {
            root = node;
            return;
        }
        else {
            if (node.parent.left == node.right) {
                node.parent.left = node;
            }
            else {
                node.parent.right = node;
            }
        }
    }

    // Rotate left move (only change pointers, not color)
    private static void leftRotate(Node node){
        node.parent.right = node.left;
        node.left = node.parent;
        node.parent = node.parent.parent;
        node.left.parent = node;
        if (node.left.right != null) {
            node.left.right.parent = node.left;
        }
        if (node.parent == null) {
            root = node;
            return;
        }
        else {
            if (node.parent.right == node.left) {
                node.parent.right = node;
            }
            else {
                node.parent.left = node;
            }
        }
    }

    ///////////////////// Printing functions ///////////////////////////////////////
    // The printBuilding in based on buildingNumber
    public static void PrintBuilding(FileWriter outputWriter, int buildingNumber) {
        try {
            if (root == null) {
//                System.out.println("(0,0,0)");
                outputWriter.write("(0,0,0)\n");
            }
            else {
                Node node = findNode(buildingNumber);
                if (node == null) {
//                    System.out.println("(0,0,0)");
                    outputWriter.write("(0,0,0)\n");
                }
                else {
//                    System.out.println("(" + node.key + "," + node.executed_time + "," + node.total_time + ")");
                    outputWriter.write("(" + node.key + "," + node.executed_time + "," + node.total_time + ")\n");
                }
            }
        }
        catch (IOException e) {
//            System.out.println("Cannot write to file");
        }
    }

    // Range query (k1,k2). Assume k1 < k2
    private static void rangeQuery(FileWriter outputWriter, Node node, int k1, int k2) {
        if(node.key==-1){
            return;
        }
        if (k1 < node.buildingNum) {
            rangeQuery(outputWriter, node.left, k1, k2);
        }
        if (k1 <= node.buildingNum && k2 >= node.buildingNum){
            if (first == true) {
//                System.out.print("(" + node.key + "," + node.executed_time + "," + node.total_time + ")");
                try {
                    outputWriter.write("(" + node.key + "," + node.executed_time + "," + node.total_time + ")");
                }
                catch (IOException e) {
//                    System.out.println("Cannot write to file");
                }
                first = false;
            }
            else {
//                System.out.print(",(" + node.key + "," + node.executed_time + "," + node.total_time + ")");
                try {
                    outputWriter.write(",(" + node.key + "," + node.executed_time + "," + node.total_time + ")");
                }
                catch (IOException e) {
//                    System.out.println("Cannot write to file");
                }
            }
        }

        if (k2 > node.buildingNum) {
            rangeQuery(outputWriter, node.right, k1, k2);
        }
    }

    //The printBuilding in range (buildingNumber1, buildingNumber2). Assume buildingNumber1 < buildingNumber2
    public static void PrintBuilding(FileWriter outputWriter, int buildingNumber1, int buildingNumber2) {
        try {
            if (root != null) {
                rangeQuery(outputWriter, root, buildingNumber1, buildingNumber2);
                if (first == true) {
//                    System.out.println("(0,0,0)");
                    outputWriter.write("(0,0,0)\n");
                }
                first = true;
//                System.out.println("");
                outputWriter.write("\n");
            }
            else {
//                System.out.println("(0,0,0)");
                outputWriter.write("(0,0,0)\n");
            }
        }
        catch (IOException e) {
//            System.out.println("Cannot write to file");
        }

    }


    ///////////////////// Checking functions ///////////////////////////////////////
    // Count Number of Black Node for each path
    private static void countBlack(Node node, int count, int numberBlack){
        if (node.key == -1) {
            if (count != numberBlack) {
                System.out.print("Black violation \n");
            }
        }
        else {
            if (node.color == BLACK) {
                countBlack(node.left, count + 1, numberBlack);
                countBlack(node.right, count + 1, numberBlack);
            }
            else {
                countBlack(node.left, count, numberBlack);
                countBlack(node.right, count, numberBlack);
            }
        }
    }

    // Count Number of Black Node on the leftmost path
    private static int countBlackLeftMost (Node node, int numberBlack) {
        if (node.key == -1) {
            return numberBlack;
        }
        else {
            if (node.color == BLACK) {
                return countBlackLeftMost(node.left, numberBlack + 1);
            }
            else {
                return countBlackLeftMost(node.left, numberBlack);
            }
        }
    }

    // Check no consecutive red nodes
    private static void checkRed(Node node, int before){
        if (node.key == -1) {
            return;
        }
        else {
            if (node.color == RED) {
                if (before == RED) {
                    System.out.println("Violation at node: " + node.key);
                    return;
                }
                else {
                    checkRed(node.left, RED);
                    checkRed(node.right, RED);
                }
            }
            else {
                checkRed(node.left, BLACK);
                checkRed(node.right, BLACK);
            }
        }
    }

    // Check RBT
    public static void checkRBT() {

        if (root == null) {
            System.out.println("Zero black node.");
        }
        else {
            countBlack(root, 0, countBlackLeftMost(root, 0));
            checkRed(root, BLACK);
        }
    }

    // Preorder Traversal
    private static void preOrder(Node node){
        if(node.key==-1){
            return;
        }
        if (node == root) {
            System.out.print("Key: " + node.key + " | Left: " + node.left.key + " | Right: " + node.right. key + " | Color: " + node.color + " | minHeap: " + node.heapNode.buildingNum + "\n");
        }
        else {
            System.out.print("Key: " + node.key + " | Left: " + node.left.key + " | Right: " + node.right. key +  " | Parent: " + node.parent.key + " | Color: " + node.color + " | minHeap: " + node.heapNode.buildingNum + "\n");
        }
        preOrder(node.left);
        preOrder(node.right);
    }

    // Display Tree
    public static void showTree(){
        if(root == null){
            System.out.println("Empty Tree");
            return;
        }
        System.out.print("Tree:\n");
        preOrder(root);
        System.out.println();
    }

    // Set all node to black
    public static void setBlack(){
        for (int i = 1; i<=2000; i++) {
            Node node = findNode(i);
            if (node != null) {
                node.color = BLACK;
            }
        }
    }

}