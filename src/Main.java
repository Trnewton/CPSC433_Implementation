
import Exceptions.InvalidSchedulingException;
import OrTree.OTreeModel;
import Parser.Reader;
import SetBased.Fact;
import SetBased.SetBased;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        boolean useConfig = true;
        boolean richout = false;
        if (args.length == 1) {
            useConfig = args[0].equals("y");
        }
        if(args.length == 2) {
      //     richout = true;
        }

        // Weights to be changed according to configReader file or
        float wMin = 1;
        float wPref = 1;
        float wPair = 1;
        float wSecD = 1;
        float p_CMin = 1;
        float p_LMin = 1;
        float p_Pair = 1;
        float p_Sec = 1;
        List<String> listOfInput = new LinkedList<>();

        // Config
        File config = new File("config.txt");
        if (config.exists() && config.length() > 0 && useConfig) { // If config is present and not empty
            System.out.println("Status: Reading Config");
            try (Scanner configReader = new Scanner(config).useDelimiter("\\n")) {
                while (configReader.hasNext()) {
                    listOfInput.add("InputFiles/" + configReader.nextLine());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {    // If the config file does not exist look for files in the directory InputFiles/Tests
            try { 
                File folder = new File("InputFiles/Tests");
                File[] listOfFiles = folder.listFiles();
                for (File test : listOfFiles) {
                    if (/*test.toString().contains("6") &&*/test.isFile()
                            && !test.toString().contains("/.")
                            && !test.toString().contains("output")) {
                        listOfInput.add(test.toString() + " 1 1 1 1 1 1");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(0);
            }
        }

        // Iterate over inputs, maybe combine this with the weights with non enhanced loop
        for (String line : listOfInput) {
            if (!line.equals("")) {
                String[] stringSplit = line.split(" ");
                if (!stringSplit[0].equals("InputFiles///")) {
                    String inputFile = stringSplit[0];
                    wMin = Float.parseFloat(stringSplit[1]);
                    wPref = Float.parseFloat(stringSplit[2]);
                    wPair = Float.parseFloat(stringSplit[3]);
                    wSecD = Float.parseFloat(stringSplit[4]);
                    p_CMin = Float.parseFloat(stringSplit[5]);
                    p_LMin = Float.parseFloat(stringSplit[6]);
                    p_Pair = Float.parseFloat(stringSplit[7]);
                    p_Sec = Float.parseFloat(stringSplit[8]);
                    float[] weights = (new float[]{wMin, wPref, wPair, wSecD, p_CMin, p_LMin, p_Pair, p_Sec});

                    solveProb(inputFile, weights, richout);
                }
            }
        }

    }

    public static void solveProb(String inputFile, float[] weights, boolean richout) {
        String output = inputFile.replace(".", "_output.");
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.printf("Status: Reading File - %s\n", inputFile);
        System.out.printf("Weights:\n" + "\twMin:%2.2f\n" + "\twPref:%2.2f\n"
                + "\twPair:%2.2f\n" + "\twSecDiff:%2.2f\n" + "\tpen_CourseMin:%2.2f\n"
                + "\tpen_LabMin:%2.2f\n" + "\tpen_NotPaired:%2.2f\n" + "\tpen_Section:%2.2f\n", 
                weights[0], weights[1], weights[2], weights[3], weights[4], weights[5], 
                weights[6], weights[7]);

        try {
            Reader reader = new Reader(inputFile, false);
            System.out.printf("Number of Course Slots: %d\n", reader.getCourseSlots().size());
            System.out.printf("Number of Lab Slots: %d\n", reader.getLabSlots().size());
            System.out.printf("Number of Courses: %d\n", reader.getCourses().size());
            System.out.printf("Number of Lab: %d\n", reader.getLabs().size());
            
            OTreeModel otree;

            try { // Try intializing OTree
                System.out.println("Status: Initiating Or Tree Model");
                otree = new OTreeModel(reader, inputFile);
            } catch (InvalidSchedulingException err) { // Catch Error in initialization and continue
                System.out.printf("Name: %s\nStatus: UNSOLVED\n%s\n\n", reader.getName(), err.getMessage());
                return; // If Otree was not initialized then move to next input
            }

            System.out.println("Status: Initiating Set Based Model");
            SetBased setBased = new SetBased(reader, otree, weights, inputFile, richout);
            System.out.println("Status: Begining Set Based Search");
            Fact f = setBased.run();

            // If f is null then there no solution was found
            if (f == null) {
                System.out.printf("\nName: %s\nStatus: UNSOLVED\nReason: Infeasible Problem\n\n", reader.getName());
                try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
                    writer.write("Status: UNSOLVED\nReason: Problem is Unsolvable\n\n");
                }
            } else { // Solution found, print it out and write it to file
                System.out.printf("\nName: %s\nStatus: SOLVED\nSolution:\n%s\n", reader.getName(), f.toString());
                try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
                    writer.write("Status: SOLVED\n"+f.toString());
                }
            }

        } catch (Exception e) { // If any other error occurs catch it and write to file
            System.out.printf(e.toString());
        }
    }
}
/**
 *
 * Or tree:
 *
 */
