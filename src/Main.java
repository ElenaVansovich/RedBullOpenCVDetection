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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        List<File> files = new ArrayList<>();

        while(true) {
            files = ((ImageViewerFrame) frame).getFiles();
            if (files.size() != 0) {
                Detect det = new Detect();
                det.detect(files, "cascade.xml");
                break;
            }
        }
    }
}
