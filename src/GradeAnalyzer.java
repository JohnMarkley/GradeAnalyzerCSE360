import javax.swing.*;

public class GradeAnalyzer {

    /*
      Main method which creates an UserInterface object.
      As of right now this should be the only method here.
     */
    public static void main(String[] args) {
        UserInterface app = new UserInterface();
        JFrame frame = new JFrame("UserInterface");
        frame.setContentPane(app.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
