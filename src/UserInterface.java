import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Scanner;
import java.text.NumberFormat;
import java.util.*;
import java.awt.event.*;
import java.lang.String;

public class UserInterface extends GradeAnalyzer {
    //Need this for the main class
    JPanel panel1;

    //Input Variables
    static File gradeFile;
    static Scanner readIn;
    // static String inputTextField = "grades.txt"; // temporary text field.
    private float[] importGrades = new float[0]; //This is the array the text list will be imported into.
    private int lowBound = 0;
    private int highBound = 100;//Global variables for the low and high bounds.
    private float mode = 0, mean = 0, max = 0, min = 0, median = 0; //If set is empty, these are accurate
    private String history = ""; //Variable to keep track of actions taken
    private float[] sectionAverage = new float[10]; //average of each 10% of data

    //Swing Components
    private JTextField highBoundryInput;
    private JTextField lowBoundryInput;
    private JButton setBoundryButton;
    private JButton createNewSetFromFileButton;
    private JButton searchAndDeleteButton;
    private JButton appendFromFileButton;
    private JButton appendFromKeyboardButton;
    private JButton displayAnalysisButton;
    private JButton createReportButton;
    private JButton displayGraphButton;
    private JButton errorLogButton;
    private JTextField inputTextField;
    private JLabel modeLabel;
    private JLabel medianLabel;
    private JLabel meanLabel;
    private JLabel numOfEntriesLabel;
    private JLabel highLabel;
    private JLabel lowLabel;
    private JTable tableSet;

    //Object Variables we may need
    private int highBoundry, lowBoundry;


    //Following conditions to check if filename is valid and outputs errors if not valid.

    public UserInterface() throws FileNotFoundException {

        createTableSet();


        createNewSetFromFileButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e)  {
                //Following conditions to check if filename is valid and outputs errors if not valid.
                String inputString = inputTextField.toString();
                int dotPosition = inputString.indexOf("."); /*Checks if the filename has a dot for the filename to validate if
     the input is a valid file name.*/
                int space = inputString.indexOf(" ");
                if(space != -1) System.out.println("Too Many Files");
                else if(dotPosition == -1) System.out.println("Invalid Value"); //There is no dot in the filename

                else {// if the dot exists in the string
                    if(dotPosition + 3 > inputString.length() -1)
                        System.out.println("Invalid Data type"); //-------------------------------REPLACE ERROR
                    else {
                        String extensionType = inputString.substring(dotPosition +1, dotPosition +4);
                        //extensionType grabs the extension type of input filename
                        if(extensionType.equals("txt") || extensionType.equals("csv")) {
                            //File reading begins here. -------------------------------------------------------------

                            try {
                                gradeFile = new File(inputString);
                                readIn = new Scanner(gradeFile);
                            }
                            catch (FileNotFoundException ex) {
                                System.out.print("File not found");
                            }

                            int lineCount = 0;
                            while(readIn.hasNextLine()) { //while not at end of file
                                boolean cont = true;
                                lineCount++;
                                String num = readIn.nextLine();
                                float value = 0;
                                try { value = Float.parseFloat(num); }
                                catch (NumberFormatException ex) {
                                    lineCount--;
                                    cont = false;
                                }
                                if(cont){  //if there are no more errors
                                    //Checking if contents are out of bounds or not

                                    //Checking if values are within bounds set by user.
                                    if (value >= highBound || value <= lowBound) {
                                        lineCount--;
                                    }
                                }
                            }

                            importGrades = new float[lineCount];
                            try {
                                readIn = new Scanner(gradeFile);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            //reading in file line by line
                            int position = 0;
                            while(readIn.hasNextLine()) { //while not at end of file
                                String num = readIn.nextLine();

                                boolean cont = true; //no problem reading in the file, then continue.
                                float value = 0;
                                //Try catch tries to make sure contents in files is a number.
                                try { value = Float.parseFloat(num); }
                                catch (NumberFormatException ex) {
                                    System.out.println("Value is not allowed"); //--------------------REPLACE ERROR
                                    cont = false;
                                }
                                if(cont){  //if there are no more errors
                                    //Checking if contents are out of bounds or not

                                    //Checking if values are within bounds set by user.
                                    if (value < highBound && value > lowBound) {
                                        importGrades[position] = value;
                                        position++;
                                    }
                                    else
                                        System.out.println("Value is out of bounds");
                                }

                            }
                            readIn.close();

                            history = "Created new data set";
                            getData();
                        }
                        else
                            System.out.print("Invalid Data Type");

                    }
                }
            }


        });

        appendFromKeyboardButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputString = inputTextField.getText();
                readIn = new Scanner(inputString);
                float value = 0;
                boolean cont = true; //continue or not if number is valid
                try { value = Float.parseFloat(inputString); }
                catch (NumberFormatException ex) { System.out.println("Value is not allowed"); //--------------------REPLACE ERROR
                    cont = false;
                }
                if (cont) {
                    float[] kbgrades = new float[importGrades.length + 1];

                    for(int i = 0; i < importGrades.length; i++) //copy old array to new larger array to include new number
                        kbgrades[i] = importGrades[i];


                    //Checking if values are within bounds set by user.
                    if (value < highBound && value > lowBound) {
                        kbgrades[importGrades.length] = value;
                    }
                    else {
                        System.out.println("Value is out of bounds");
                        cont = false;
                    }
                    if(cont) {
                        importGrades = new float[kbgrades.length];
                        for(int i = 0; i < kbgrades.length; i++) {
                            importGrades[i] = kbgrades[i];
                        }
                        addToTableSet(importGrades);

                        history = history + "Appended " + value + " to grade set\n";
                        getData();
                    }
                }
            }

        });

        appendFromFileButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputString = inputTextField.toString();
                int dotPosition = inputString.indexOf("."); /*Checks if the filename has a dot for the filename to validate if
     the input is a valid file name.*/
                int space = inputString.indexOf(" ");
                if(space != -1) System.out.println("Too Many Files");
                else if(dotPosition == -1) System.out.println("Invalid Value"); //There is no dot in the filename

                else {// if the dot exists in the string
                    if(dotPosition + 3 > inputString.length() -1)
                        System.out.println("Invalid Data type"); //-------------------------------REPLACE ERROR
                    else {
                        String extensionType = inputString.substring(dotPosition +1, dotPosition +4);
                        //extensionType grabs the extension type of input filename
                        if(extensionType.equals("txt") || extensionType.equals("csv")) {
                            //File reading begins here. -------------------------------------------------------------

                            try {
                                gradeFile = new File(inputString);
                                readIn = new Scanner(gradeFile);
                            }
                            catch (FileNotFoundException ex) {
                                System.out.print("File not found");
                            }

                            int lineCount = 0;
                            while(readIn.hasNextLine()) { //while not at end of file
                                boolean cont = true;
                                lineCount++;
                                String num = readIn.nextLine();
                                float value = 0;
                                try { value = Float.parseFloat(num); }
                                catch (NumberFormatException ex) {
                                    lineCount--;
                                    cont = false;
                                }
                                if(cont){  //if there are no more errors
                                    //Checking if contents are out of bounds or not

                                    //Checking if values are within bounds set by user.
                                    if (value >= highBound || value <= lowBound) {
                                        lineCount--;
                                    }
                                }
                            }

                            float[] appendGrades = new float[lineCount + importGrades.length];
                            //Copying contents from old array to a temporary array for the appended data
                            for(int i = 0; i < importGrades.length; i++)
                                appendGrades[i] = importGrades[i];

                            try {
                                readIn = new Scanner(gradeFile);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            //reading in file line by line
                            int position = 0;
                            while(readIn.hasNextLine()) { //while not at end of file
                                String num = readIn.nextLine();

                                boolean cont = true; //no problem reading in the file, then continue.
                                float value = 0;
                                //Try catch tries to make sure contents in files is a number.
                                try { value = Float.parseFloat(num); }
                                catch (NumberFormatException ex) {
                                    System.out.println("Value is not allowed"); //--------------------REPLACE ERROR
                                    cont = false;
                                }
                                if(cont){  //if there are no more errors
                                    //Checking if contents are out of bounds or not

                                    //Checking if values are within bounds set by user.
                                    if (value < highBound && value > lowBound) {
                                        appendGrades[position + importGrades.length] = value;
                                        position++;
                                    }
                                    else
                                        System.out.println("Value is out of bounds");
                                }

                            }

                            importGrades = new float[appendGrades.length];


                            for(int i = 0; i < appendGrades.length; i++)
                                importGrades[i] = appendGrades[i];

                            readIn.close();
                            addToTableSet(importGrades);

                            history = history + "Appended grades from " + inputString + "\n";
                            getData();
                        }
                        else
                            System.out.print("Invalid Data Type");

                    }
                }
            }

        });


        displayAnalysisButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                numOfEntriesLabel.setText("Number of Entries: " + importGrades.length);
                getData();
                highLabel.setText("High: " + max);
                lowLabel.setText("Low: " + min);
                meanLabel.setText("Mean: " + mean);
                modeLabel.setText("Mode: " + mode);
                medianLabel.setText("Median: " + median);


            }
        });

        searchAndDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //NOTE: Currently there's this error where the number deleted is replaced by a zero if it's not
                //the last one in the set. Will work on resolving this.
                String inputString = inputTextField.getText();
                float value = 0;
                boolean cont = true; //continue or not if number is valid
                try { value = Float.parseFloat(inputString); }
                catch (NumberFormatException ex) { System.out.println("Value is not allowed"); //--------------------REPLACE ERROR
                    cont = false;
                }
                if (cont) {
                    if(value > highBound || value < lowBound)
                    {
                        cont = false;
                        System.out.println("Value is not allowed"); //-----REPLACE ERROR
                    }
                    if(cont) {
                        float[] kbgrades = new float[importGrades.length - 1];
                        float numAt = -1; //-1 represents the number not being in the set
                        for (int i = 0; i < importGrades.length; i++) //find number and mark its index in the original set
                        {
                            if (importGrades[i] == value) {
                                numAt = i;
                                i = importGrades.length;
                            }
                        }

                        int kIndex = 0;
                        int impIndex = 0;
                        while (kIndex < kbgrades.length)
                        { //copy everything over except for the one marked index, the one we want to delete
                            if (impIndex == numAt) {
                                impIndex++;
                            }
                            else {
                                kbgrades[kIndex] = importGrades[impIndex];
                            }
                            impIndex++;
                            kIndex++;
                        }

                        importGrades = new float[kbgrades.length]; //copy everything back over to the working set
                        for(int i = 0; i < kbgrades.length; i++) {
                            importGrades[i] = kbgrades[i];
                        }
                        addToTableSet(importGrades);
                        getData();
                        history = history + "Deleted " + value + " from the current grade set\n";
                    }
                }
            }
        });

        createReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String boundReport = "The bounds for the data set are: \n"
                        +"High bound: " + highBound + "\n" + "Low bound: " + lowBound+ "\n";
                String analysisReport = "A current analysis of the data results in:\n"
                        + "Number of entries: " + importGrades.length + "\n"
                        + "Highest Value: " + max + "\n"
                        + "Lowest Value: " + min + "\n"
                        + "Mean: " + mean + "\n"
                        + "Median: " + median + "\n"
                        + "Mode: " + mode + "\n";
                //each average here
                String averages = "";
                findSectionAverages();
                String percentile = "";
                for(int i = 0; i < sectionAverage.length; i++)
                {
                    percentile = Integer.toString(i+1);
                    averages = averages + "Group " + percentile +" average is: " + sectionAverage[i] + "\n";
                }
                System.out.println(boundReport);
                System.out.println(analysisReport);
                System.out.println(averages);
                System.out.println(history);

                File file = new File("report.txt");
                if(file.canWrite() == true)
                {
                    try {
                        file.createNewFile();
                    }
                    catch(IOException ex)
                    {
                        System.out.println("File exists already");
                    }
                }
                else
                {
                    file.delete();
                    try {
                        file.createNewFile();
                    }
                    catch(IOException ex)
                    {
                        System.out.println("File exists already");
                    }
                }

                try {
                    FileWriter writer = new FileWriter(file);
                    writer.write(boundReport);
                    writer.append("\n");
                    writer.append(analysisReport);
                    writer.append("\n");
                    writer.append(averages);
                    writer.append("\n");
                    writer.append(history);
                    writer.flush();
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void getData() {


        //Median find
        if(importGrades.length > 0) {
            float sum = 0, tempMin = importGrades[0], tempMax = importGrades[0];

            float sortedArr[] = importGrades;
            Arrays.sort(sortedArr);
            median = sortedArr[importGrades.length / 2];

            //Parts of the following code taken from GeeksForGeeks
            //Mode find
            Map<Float, Float> map = new HashMap<>();
            for (int i = 0; i < importGrades.length; i++) {
                float key = sortedArr[i];
                if (map.containsKey(key)) {
                    float freq = map.get(key);
                    freq++;
                    map.put(key, freq);
                } else {
                    map.put(key, 1f);
                }
            }
            float max_count = 0f, result = -1f;
            for (Map.Entry<Float, Float> val : map.entrySet()) {
                if (max_count < val.getValue()) {
                    result = val.getKey();
                    max_count = val.getValue();
                }
            }

            mode = result;

            //Mean, Min & Max find

            for (int i = 0; i < importGrades.length; i++) {

                if (tempMax < importGrades[i])
                    tempMax = importGrades[i];

                if (tempMin > importGrades[i])
                    tempMin = importGrades[i];

                sum += importGrades[i];


            }

            max = tempMax;
            min = tempMin;
            mean = sum / importGrades.length;
        }else{
            min = 0;
            max = 0;
            median = 0;
            mode = 0;
        }


    }

    private void createTableSet(){
        tableSet.setAutoCreateRowSorter(true);
        tableSet.setFillsViewportHeight(true);
        tableSet.setPreferredScrollableViewportSize(new Dimension(550, 200));
        tableSet.setModel(getTableModel());
    }

    private void addToTableSet(float[] arr){
        DefaultTableModel newModel = getTableModel();
        for(int i = 0; i < arr.length; i++)
            newModel.addRow(new Object[] { arr[i] });
        tableSet.setModel(newModel);

    }

    private DefaultTableModel getTableModel(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Grades");
        return model;
    }

    private void findSectionAverages()
    {
        int sectionLength = importGrades.length / 10; //10 is used here since we need 10 averages
        float localSum = 0;
        float localAvg = 0;
        for(int count = 0; count < sectionAverage.length; count++)
        {
            for(int sectionCount = 0; sectionCount < sectionLength; sectionCount++)
            {
                localSum += importGrades[sectionCount];
            }
            localAvg = localSum / sectionLength;
            sectionAverage[count] = localAvg;
            System.out.println(sectionAverage[count]);
            localSum = 0;
        }
    }



}
