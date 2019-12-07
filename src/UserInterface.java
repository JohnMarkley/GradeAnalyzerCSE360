import com.sun.deploy.util.ArrayUtil;

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
import java.sql.Timestamp;
import java.util.Scanner;
import java.text.NumberFormat;
import java.util.*;
import java.awt.event.*;
import java.lang.String;
import java.util.Arrays;

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
    static Timestamp timestamp;
    static String OS = System.getProperty("os.name").toLowerCase();
    static String desktop = System.getProperty ("user.home") + "/Desktop/";
    static File filename = new File(desktop+"/error.txt");
    static String toolsPath = "C:/WINDOWS/system32/notepad.exe ";
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
                try
                {
                    //Following conditions to check if filename is valid and outputs errors if not valid.
                    timestamp = new Timestamp(System.currentTimeMillis());
                    String inputString = inputTextField.getText();
                    inputString = inputString.trim();
                    filename.createNewFile();
                    FileWriter fw = new FileWriter(filename, true);
                    int dotPosition = inputString.indexOf("."); /*Checks if the filename has a dot for the filename to validate if
     the input is a valid file name.*/
                    int space = inputString.indexOf(" ");
                    if(space != -1){
                        fw.write("["+timestamp+"] Import file failure: Please import one file at a time\n");
                    }
                    else if(dotPosition == -1) {
                        fw.write("["+timestamp+"] Import file failure: Please check the file name\n");
                    }
                    else {// if the dot exists in the string
                        if(dotPosition + 3 > inputString.length() -1)
                            fw.write("["+timestamp+"] Import file failure: The "+inputString+" is not allowed. Please make sure the file type is .txt or .csv.\n");
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
                                    fw.write("["+timestamp+"] Import file failure: The "+inputString+" does not exist. Please check the file name\n");
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
                                        fw.write("["+timestamp+"] ValueError: "+value+" is not allowed. Please check the file's data.\n");
                                        cont = false;
                                    }
                                    if(cont){  //if there are no more errors
                                        //Checking if contents are out of bounds or not

                                        //Checking if values are within bounds set by user.
                                        if (value <= highBound && value >= lowBound) {
                                            importGrades[position] = value;
                                            position++;
                                        }
                                        else{
                                            fw.write("[" + timestamp + "] ValueError: " + value + " is out of bounds. Please check the value again\n");
                                        }
                                    }

                                }
                                readIn.close();
                                getData();
                            }
                            else {
                                fw.write("["+timestamp+"] Import file failure: The "+inputString+" is not allowed. Please make sure the file type is .txt or .csv.\n");
                            }
                        }
                    }
                    fw.flush();
                    fw.close();
                    history = "Created a new grade set\n";
                } catch (IOException ex1) {
                    ex1.printStackTrace();
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
                timestamp = new Timestamp(System.currentTimeMillis());
                String inputString = inputTextField.getText();
                inputString = inputString.trim();
                readIn = new Scanner(inputString);
                float value = 0;
                boolean cont = true; //continue or not if number is valid
                try { value = Float.parseFloat(inputString); }
                catch (NumberFormatException ex) {
                    try {
                        FileWriter fw = new FileWriter(filename, true);
                        fw.write("["+timestamp+"] ValueError: "+ value +" is not allowed. Please check the file's data.\n");
                        fw.flush();
                        fw.close();
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                    cont = false;
                }
                if (cont) {
                    float[] kbgrades = new float[importGrades.length + 1];

                    for(int i = 0; i < importGrades.length; i++) //copy old array to new larger array to include new number
                        kbgrades[i] = importGrades[i];


                    //Checking if values are within bounds set by user.
                    if (value <= highBound && value >= lowBound) {
                        kbgrades[importGrades.length] = value;
                    }
                    else {
                        try {
                            FileWriter fw = new FileWriter(filename, true);
                            fw.write("[" + timestamp + "] ValueError: " + value + " is out of bounds. Please check the value again\n");
                            fw.flush();
                            fw.close();
                        } catch (IOException ex1) {
                            ex1.printStackTrace();
                        }
                        cont = false;
                    }
                    if(cont) {
                        importGrades = new float[kbgrades.length];
                        for(int i = 0; i < kbgrades.length; i++)
                            importGrades[i] = kbgrades[i];
                        addToTableSet(importGrades);
                        getData();
                        history = history + "Appended " + value + " to grade set\n";
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
                timestamp = new Timestamp(System.currentTimeMillis());
                String inputString = inputTextField.getText();
                inputString = inputString.trim();
                int dotPosition = inputString.indexOf("."); /*Checks if the filename has a dot for the filename to validate if
     the input is a valid file name.*/
                int space = inputString.indexOf(" ");
                if(space != -1) {
                    try {
                        FileWriter fw = new FileWriter(filename, true);
                        fw.write("["+timestamp+"] Import file failure: Please import one file at a time\n");
                        fw.flush();
                        fw.close();
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }
                else if(dotPosition == -1){
                    try {
                        FileWriter fw = new FileWriter(filename, true);
                        fw.write("["+timestamp+"] Import file failure: Invalid Data type. Please check the your data\n");
                        fw.flush();
                        fw.close();
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }

                else {// if the dot exists in the string
                    if(dotPosition + 3 > inputString.length() -1){
                        try {
                            FileWriter fw = new FileWriter(filename, true);
                            fw.write("["+timestamp+"] Import file failure: Invalid Data type. Please check the your data\n");
                            fw.flush();
                            fw.close();
                        } catch (IOException ex1) {
                            ex1.printStackTrace();
                        }
                    }
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
                                    try {
                                        FileWriter fw = new FileWriter(filename, true);
                                        fw.write("["+timestamp+"] ValueError: "+ value +" is not allowed. Please check the file's data.\n");
                                        fw.flush();
                                        fw.close();
                                    } catch (IOException ex1) {
                                        ex1.printStackTrace();
                                    }
                                    cont = false;
                                }
                                if(cont){  //if there are no more errors
                                    //Checking if contents are out of bounds or not

                                    //Checking if values are within bounds set by user.
                                    if (value < highBound && value > lowBound) {
                                        appendGrades[position + importGrades.length] = value;
                                        position++;
                                    }
                                    else{
                                        try {
                                            FileWriter fw = new FileWriter(filename, true);
                                            fw.write("[" + timestamp + "] ValueError: " + value + " is out of bounds. Please check the value again\n");
                                            fw.flush();
                                            fw.close();
                                        } catch (IOException ex1) {
                                            ex1.printStackTrace();
                                        }
                                    }
                                }

                            }

                            importGrades = new float[appendGrades.length];


                            for(int i = 0; i < appendGrades.length; i++)
                                importGrades[i] = appendGrades[i];

                            readIn.close();
                            addToTableSet(importGrades);
                            getData();
                        }
                        else{
                            try {
                                FileWriter fw = new FileWriter(filename, true);
                                fw.write("["+timestamp+"] Import file failure: The "+inputString+" is not allowed. Please make sure the file type is .txt or .csv.\n");
                                fw.flush();
                                fw.close();
                                history = history + "Appended grades from file " + inputString + "\n";
                            } catch (IOException ex1) {
                                ex1.printStackTrace();
                            }
                        }

                    }
                }
            }

        });
        errorLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(OS.indexOf("win")>=0) {
                    try {
                        Process exec = Runtime.getRuntime().exec(toolsPath + desktop + "error.txt");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if(OS.indexOf("mac")>=0){
                    try {
                        Process p = new ProcessBuilder("open", desktop+"error.txt").start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
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

                history = history + "Displayed analysis information\n";
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

        searchAndDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //NOTE: Currently there's this error where the number deleted is replaced by a zero if it's not
                //the last one in the set. Will work on resolving this.
                String inputString = inputTextField.getText();
                inputString = inputString.trim();
                float value = 0;
                boolean cont = true; //continue or not if number is valid
                try {
                    value = Float.parseFloat(inputString);
                } catch (NumberFormatException ex) {
                    System.out.println("Value is not allowed"); //--------------------REPLACE ERROR
                    cont = false;
                }
                if (cont) {
                    if (value > highBound || value < lowBound) {
                        cont = false;
                        System.out.println("Value is not allowed"); //-----REPLACE ERROR
                    }
                    if (cont) {
                        float[] kbgrades = new float[importGrades.length - 1];
                        int numAt = -1; //-1 represents the number not being in the set
                        Arrays.sort(importGrades);
                        reverseArr(importGrades);
                        for (int i = 0; i < importGrades.length; i++) //find number and mark its index in the original set
                        {
                            if (importGrades[i] == value) {
                                numAt = i;
                                i = importGrades.length;
                                System.out.println(numAt);
                            }
                        }
                        if (numAt == -1) {
                            System.out.println("Number not in set");
                        }
                        else {
                            int kIndex = 0;
                            int impIndex = 0;
                            while (kIndex < kbgrades.length) { //copy everything over except for the one marked index, the one we want to delete
                                if (impIndex == numAt) {
                                    System.out.println("Skipping " + importGrades[impIndex]);
                                    impIndex++;
                                }
                                kbgrades[kIndex] = importGrades[impIndex];
                                System.out.println("Temp arr at i is: " + kbgrades[kIndex]);
                                impIndex++;
                                kIndex++;
                            }

                            //debuggin
                            for(int i = 0; i < kbgrades.length; i++)
                            {
                                System.out.println("Test grade holder: " + kbgrades[i]);
                            }

                            importGrades = new float[kbgrades.length]; //copy everything back over to the working set
                            for (int i = 0; i < kbgrades.length; i++) {
                                importGrades[i] = kbgrades[i];
                                System.out.println("New set at " + i + " is: " + importGrades[i]);
                            }
                            addToTableSet(importGrades);
                            getData();
                            history = history + "Deleted " + value + " from the current grade set\n";
                        }
                    }
                }
            }
        });

        setBoundryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String high = highBoundryInput.getText();
                String low = lowBoundryInput.getText();
                int highBoundary = 1;
                int lowBoundary = 0;

                try { highBoundary = Integer.parseInt(high);}
                catch (NumberFormatException ex) {System.out.println("Value is not allowed");} //fix message
                try { lowBoundary = Integer.parseInt(low);}
                catch (NumberFormatException ex) {System.out.println("Value is not allowed");}
                if(highBoundary <= lowBoundary)
                {
                    System.out.println("High boundary must be greater than low boundary");

                    highBoundryInput.setText(Integer.toString(highBound));
                    lowBoundryInput.setText(Integer.toString(lowBound));
                }
                else
                {
                    boolean validBound = true;
                    for(int i = 0; i < importGrades.length; i++)
                    {
                        if(importGrades[i] > highBoundary || importGrades[i] < lowBoundary)
                        {
                            i = importGrades.length;
                            validBound = false;
                            System.out.println("There's data that is outside of these new bounds.\n" +
                                    "Either make a new data set or change the boundary values.");
                            highBoundryInput.setText(Integer.toString(highBound));
                            lowBoundryInput.setText(Integer.toString(lowBound));
                        }
                    }
                    if(validBound == true) {
                        highBound = highBoundary;
                        lowBound = lowBoundary;
                        history = history + "Set high bound to " + highBound + " and low bound to " + lowBound + "\n";
                    }
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
        Arrays.sort(arr);
        reverseArr(arr); //These two function ensure the array is in descending order
        DefaultTableModel newModel = getTableModel();
        int columnLength = 5; //This can be changed
        //If we need to expand the length of columns this will do so by doubling it once reaching capacity
        while(columnLength * 4 < arr.length){
            columnLength = columnLength * 2;
        }

        int deleteRowCount = 0;
        //If there is only one column just add that shit
        if (arr.length <= columnLength) {
            for (int i = 0; i < arr.length; i++)
                newModel.addRow(new Object[]{arr[i]});
        }else if(arr.length <= columnLength * 2 ) {
            //Two columns worth of data
            for(int i = 0; i < arr.length; i++) {
                if(i + columnLength < arr.length) {
                    newModel.addRow(new Object[]{arr[i], arr[i + columnLength]});
                    deleteRowCount++;

                }else {
                    newModel.addRow(new Object[]{arr[i]});
                }
            }
            //Three columns worth of data
        }else if(arr.length > columnLength * 2 && arr.length <= columnLength * 3 ) {
            for(int i = 0; i < arr.length; i++) {
                if(i + columnLength * 2 < arr.length) {
                    newModel.addRow(new Object[]{arr[i], arr[i + columnLength], arr[i + columnLength * 2]});
                    deleteRowCount++;
                }else if(i + columnLength < arr.length) {
                    newModel.addRow(new Object[]{arr[i], arr[i + columnLength]});
                    deleteRowCount++;
                } else {
                    newModel.addRow(new Object[]{arr[i]});
                }
            }
            //Four columns worth of data
        }else {
            for(int i = 0; i < arr.length; i++) {
                if(i + columnLength * 3 < arr.length){
                    newModel.addRow(new Object[]{arr[i], arr[i + columnLength], arr[i + columnLength * 2], arr[i + columnLength * 3]});
                    deleteRowCount++;
                }
                else if(i + columnLength * 2 < arr.length) {
                    newModel.addRow(new Object[]{arr[i], arr[i + columnLength], arr[i + columnLength * 2]});
                    deleteRowCount++;
                }else if(i + columnLength < arr.length) {
                    newModel.addRow(new Object[]{arr[i], arr[i + columnLength]});
                    deleteRowCount++;
                } else {
                    newModel.addRow(new Object[]{arr[i]});
                }
            }

        }

        tableSet.setModel(newModel);
        while(deleteRowCount > 0 ){
            DefaultTableModel temp = (DefaultTableModel)tableSet.getModel();
            temp.removeRow(temp.getRowCount() - 1);
            tableSet.setModel(temp);
            deleteRowCount--;
        }

    }

    private DefaultTableModel getTableModel(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");
        return model;
    }

    private void findSectionAverages()
    {
        int sectionLength = importGrades.length / 10; //10 is used here since we need 10 average
        if(importGrades.length / 10 < 1)
        {
            sectionLength = 1; //at least one number per section
        }
        if(importGrades.length <= 10)
        {
            sectionAverage = new float[importGrades.length];
        }
        else
        {
            sectionAverage = new float[10]; //10 averages at most
        }
        float localSum = 0;
        float localAvg = 0;
        for(int count = 0; count < sectionAverage.length; count++)
        {
            for(int sectionCount = 0; sectionCount < sectionLength; sectionCount++)
            {
                localSum += importGrades[sectionCount + (count * sectionLength)];
            }
            localAvg = localSum / sectionLength;
            sectionAverage[count] = localAvg;
            localSum = 0;
        }
    }
    private float[] reverseArr(float[] arr)
    {
        for(int i = 0; i < arr.length/2; i++)
        {
            float hold = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = hold;
        }
        return arr;
    }

}
