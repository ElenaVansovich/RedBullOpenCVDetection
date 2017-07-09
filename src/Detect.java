import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.List;


/**
 * Created by user on 09.07.2017.
 */
public class Detect {

    public void detect(List<File> files, String xmlName){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.out.println("\nRunning Detector");

        CascadeClassifier classifier = new CascadeClassifier(xmlName);
        String path = "";

        for(int i = 0; i <  files.size(); i++){

            path = (files.get(i)).getPath();

            Mat image = Highgui.imread(path);

            MatOfRect detections = new MatOfRect();
            classifier.detectMultiScale(image, detections);

            System.out.println(String.format("Detected %s elements", detections.toArray().length));

            for (Rect rect : detections.toArray()) {
                Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
            }

            String filename = getNewFileName(path, detections.toArray().length);
            System.out.println(String.format("Writing %s", filename));
            Highgui.imwrite(filename, image);
        }

    }

    public String getNewFileName(String s, int amount){
        String name = "", extension = "";
        int dotIdx = s.lastIndexOf('.');
        int bslashIdx = s.lastIndexOf('\\');

        if (dotIdx != -1 && bslashIdx != -1) {
            name = s.substring(bslashIdx + 1, dotIdx);
            extension = s.substring(dotIdx, s.length());
        }
        if(amount == 0){
            return "no_elements\\" + name + extension;
        }
        else{
            return "with_elements\\" + name + "_detect_ " + amount + "_elements" + extension;
        }
    }
}
