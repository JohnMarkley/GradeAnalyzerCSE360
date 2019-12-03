import javax.swing.*;
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
    static float[] importGrades = new float[0]; //This is the array the text list will be imported into.
    static int lowBound = 0;
    static int highBound = 100;//Global variables for the low and high bounds.

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

    //Object Variables we may need
    private int highBoundry, lowBoundry;
    //Following conditions to check if filename is valid and outputs errors if not valid.

    public UserInterface() throws FileNotFoundException {
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
                        for(int i = 0; i < kbgrades.length; i++)
                            importGrades[i] = kbgrades[i];
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
                        }
                        else
                            System.out.print("Invalid Data Type");

                    }
                }
            }

        });
    }



}
