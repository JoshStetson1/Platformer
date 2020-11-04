package platformer;

import java.awt.GridLayout;
import javax.swing.JFrame;

public class Platformer {

    public static void main(String[] args) {
        JFrame f = new JFrame("");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(750, 485);
        f.setLayout(new GridLayout(1, 1, 0, 0));
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        Screen s = new Screen(f.getX(), f.getY());
        f.add(s);
        f.setVisible(true);
    }
    
}
