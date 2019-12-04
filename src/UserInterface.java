import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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



}
