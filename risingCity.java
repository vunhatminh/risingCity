import java.io.*;

public class risingCity {

    private static minHeap.HeapNode building = null;    // The building that the company is working on
    private static int days_on_construction = 0;          // The number of remained days that the company need to work on a chosen building
    private static int current_day = 0;                 // Current day based on global clock
    private static int total_working_time = 0;          // Total number of working days
    private static int idle_days = 0;                     // The number of days that the company has no building to work on

    // Initialize min heap
    public static minHeap heap = new minHeap(2000);

    // Insert building to the data structure
    public static void Insert(int buildingNum, int total_time) {
        RedBlackTree.Node node = RedBlackTree.initNode(buildingNum,total_time);
        minHeap.HeapNode heapNode = minHeap.initNode(buildingNum,total_time);
        node.heapNode = heapNode;
        heapNode.RBTNode = node;
        RedBlackTree.insert(node);
        heap.insert(heapNode);
    }

    // Remove building from the data structure
    public static void Remove(int buildingNum) {
        RedBlackTree.Node node = RedBlackTree.findNode(buildingNum);
        if (node != null) {
            minHeap.remove(node.heapNode);
            RedBlackTree.remove(buildingNum);
        }
    }

    // Remove building if the executed time equal to total time
    private static void RemoveAtTime(FileWriter outputWriter, minHeap.HeapNode node) {
        if (building.executed_time == building.total_time) {
            System.out.println("(" + building.buildingNum + "," + current_day + ")");
            try {
                outputWriter.write("(" + building.buildingNum + "," + current_day + ")\n");
            }
            catch (IOException e) {
                System.out.println("Cannot write to file");
            }
            Remove(building.buildingNum);
            building = null;
        }
    }

    // Print and Write the buildings in range(buildingNum1, buildingNum2)
    public static void Print(FileWriter outputWriter, int buildingNum1, int buildingNum2) {
        RedBlackTree.PrintBuilding(outputWriter, buildingNum1,buildingNum2);
    }

    // Print the and Write the building with buildingNum
    public static void Print(FileWriter outputWriter, int buildingNum) {
        RedBlackTree.PrintBuilding(outputWriter, buildingNum);
    }

    // Update the data structure after "dayPassed" days without and insertion of new buildings
    private static int Update(FileWriter outputWriter, int dayPassed) {

        int dayRemain; // The days remained after spending the time to finish current task

        // If the company is not working on any building, it selects a new building to work on
        if (days_on_construction == 0) {
            // If there is no building to select, the company goes idle
            if (heap.HeapSize == 0) {
                idle_days = idle_days + dayPassed;
                current_day = current_day + dayPassed;
                return 0;
            }
            // If there is building to select, the company selects the building with minimum executed time
            else {
                building = heap.getTop();
                int remain_time = building.total_time - building.executed_time;
                // Determine the time to work on the selected building
                if (remain_time <= 5) {
                    days_on_construction = remain_time;
                    return dayPassed;
                }
                else
                {
                    days_on_construction = 5;
                    return dayPassed;
                }
            }
        }

        // If the given days is 0, just quit the function
        if (dayPassed == 0) {
            return 0;
        }

        // If the company is still on construction of an building, it spends time to finish the task on that building

        // If the given time is greater than the time to finish current task, the remained time "dayRemain" is return
        if (days_on_construction < dayPassed) {
            current_day = current_day + days_on_construction;
            dayRemain = dayPassed - days_on_construction;
            heap.increaseExecutedTime(building, days_on_construction);
            RemoveAtTime(outputWriter, building);
            days_on_construction = 0;
            return dayRemain;
        }
        // If the given time is lesser than the time to finish the current task, all given time is spent on the task
        else if (days_on_construction > dayPassed) {
            current_day = current_day + dayPassed;
            heap.increaseExecutedTime(building, dayPassed);
            days_on_construction = days_on_construction - dayPassed;
            return 0;
        }
        // If the given time is the same as the time intended to spend on the task, return and check for the command before remove node if necessary
        else {
            current_day = current_day + days_on_construction;
            heap.increaseExecutedTime(building, days_on_construction);
            days_on_construction = 0;
            return 0;
        }
    }

    public static void main(String [] args){
        String file_name = args[0];
        String s;
        try{
            FileReader fileReader = new FileReader(file_name);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            File outputFile = new File("output_file.txt");
            outputFile.createNewFile();
            FileWriter outputWriter = new FileWriter(outputFile, false);

            // Continue to read from the input file
            while ((s = bufferedReader.readLine()) != null) {
                int day = Integer.parseInt(s.split(":",2)[0]);    // Given time from input
                int day_passed = day - current_day;                         // The time duration between two commands from input

                // While there is time between two commands the company continues to work on the given data structure
                while (day_passed > 0) {
                    day_passed = Update(outputWriter, day_passed);
                }
                // Insert building to the data structure
                if (s.contains("Insert(")) {
                    int buildingNum = Integer.parseInt(s.split("\\(")[1].split(",")[0]);
                    int total_time = Integer.parseInt(s.split(",")[1].split("\\)")[0]);
                    Insert(buildingNum,total_time);
                    total_working_time = total_working_time + total_time;
                    if (building != null) {
                        RemoveAtTime(outputWriter, building);
                    }
                }
                // Print buildings based on the query
                else if (s.contains("Print")) {
                    // Print range of buildings
                    if (s.contains(",")) {
                        int buildingNum1 = Integer.parseInt(s.split("\\(")[1].split(",")[0]);
                        int buildingNum2 = Integer.parseInt(s.split(",")[1].split("\\)")[0]);
                        Print(outputWriter, buildingNum1, buildingNum2);
                        if (building != null) {
                            RemoveAtTime(outputWriter, building);
                        }
                        Update(outputWriter,0);
                    }
                    // Print a specific building
                    else {
                        int buildingNum = Integer.parseInt(s.split("\\(")[1].split("\\)")[0]);
                        Print(outputWriter, buildingNum);
                        RemoveAtTime(outputWriter, building);
                    }
                }
                else {
                    System.out.println("Invalid command");
                }
            }

            int dayLeft = total_working_time + idle_days - current_day;    // The number of days left until the company finishes all works

            // The company continues to finish all remained works after the last command from the input
            while (dayLeft > 0) {
                dayLeft = Update(outputWriter, dayLeft);
                if (building != null) {
                    RemoveAtTime(outputWriter, building);
                }
            }

            outputWriter.flush();
            outputWriter.close();
            bufferedReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Can't open file");
        } catch (IOException e) {
            System.out.println("Error loading file");
        }
    }
}
