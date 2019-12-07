import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GradeAnalyzer {

    /*
      Main method which creates an UserInterface object.
      As of right now this should be the only method here.
     */
    public static void main(String[] args) throws FileNotFoundException {
        UserInterface app = new UserInterface();
        JFrame frame = new JFrame("UserInterface");
        frame.setContentPane(app.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                File filename = new File("error.txt");
                filename.delete();
                try {
                    filename.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

}
