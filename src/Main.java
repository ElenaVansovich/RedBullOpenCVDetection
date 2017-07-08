import javax.swing.*;


/**
 * Created by user on 06.07.2017.
 */
public class Main {

    public static void main(String[] args) {

        JFrame frame = new ImageViewerFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        String path = "";

        while(path.equals("")) {
            path = ((ImageViewerFrame) frame).getPath();
            if (!path.equals("")) {

                Detect det = new Detect();
                det.detect(path);
            }
        }
    }
}
