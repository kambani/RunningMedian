import java.util.*;
import java.io.*;

public class Main {

    //
    // This function plots the median from
    // our RunningMedian model and via traditional
    // method (store all numbers, sort and find middle)
    //
    public static void testRMPlotExpectedVsActualMedian(RunningMedian rm, List<Integer> series) {

        //
        // Expected. Median via our model
        //
        double medianRM = rm.getMedian();

        //
        // Actual. Median via traditional method.
        // Sort the entire series, sort and find the middle
        //
        double median;
        Collections.sort(series);
        int middle = series.size()/2;

        if (series.size() % 2 == 1) {
            median = series.get(middle);
        } else {
            median = (series.get(middle-1) + series.get(middle)) / 2.0;
        }

        //
        // Print
        //
        System.out.println(median + "," + medianRM);
    }

    //
    // Function to test the RunningMedian model
    //
    public static void testRunningMedianModel(String testRunningMedianModel) {
        RunningMedian rm = new RunningMedian();
        List<Integer> list = new ArrayList<Integer>();

        //
        // Read the series from the file
        // Numbers to be stored on individual line
        //
        File file = new File(testRunningMedianModel);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text;

            System.out.println("Expected,Actual");
            while ((text = reader.readLine()) != null) {
                rm.insert(Integer.parseInt(text));
                list.add(Integer.parseInt(text));
                testRMPlotExpectedVsActualMedian(rm, list);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }

        System.out.println("Input Series:");
        System.out.println(Arrays.toString(list.toArray()));
    }

    //
    // Main
    //
    public static void main(String[] args) {
        // Scanner for user input
        testRunningMedianModel(args[0]);
    }
}
