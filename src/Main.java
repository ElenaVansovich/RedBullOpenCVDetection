import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 06.07.2017.
 */
public class Main {

    public static void main(String[] args) {

        JFrame frame = new ImageViewerFrame();
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ((ImageViewerFrame)frame).closeWriters();
                    System.exit(0);
            }
        });
    }
}
