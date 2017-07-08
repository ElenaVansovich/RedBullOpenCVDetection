import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Created by user on 09.07.2017.
 */
public class Detect {

    public void detect(String path){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.out.println("\nRunning Detector");

        CascadeClassifier faceDetector = new CascadeClassifier("cascade.xml");
        Mat image = Highgui
                .imread(path);


        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s elements", faceDetections.toArray().length));

        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }

        String filename = "ouput.bmp";
        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, image);
    }
}
