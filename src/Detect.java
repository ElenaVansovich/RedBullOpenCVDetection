import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by user on 09.07.2017.
 */
public class Detect {

    public void detect(List<File> files, String xmlName, List<File> filesXml){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.out.println("\nRunning Detector");

        CascadeClassifier classifier = new CascadeClassifier(xmlName);
        String path = "";

        Map<String, File> map = new TreeMap<>();
        if(filesXml != null) {

            String nameImage = "";
            String nameXml = "";

            for (int i = 0; i < filesXml.size(); i++) {
                nameXml = (filesXml.get(i)).getName();

                int dotIdx = nameXml.lastIndexOf('.');

                if (dotIdx != -1) {
                    nameImage = nameXml.substring(1, dotIdx - 7);
                }
                map.put(nameImage, filesXml.get(i));
            }
        }


        File dirNo = new File("no_elements\\");
        File dirWith = new File("with_elements\\");
        dirNo.mkdir();
        dirWith.mkdir();

        for(int i = 0; i <  files.size(); i++){

            String filename = (files.get(i)).getName();

            if(map.size() != 0) {
                if (map.containsKey(filename) && map.get(filename).length() <= 290) {
                    noElement(files.get(i));
                }
            }
            else {
                path = (files.get(i)).getPath();

                Mat image = Highgui.imread(path);

                MatOfRect detections = new MatOfRect();
                classifier.detectMultiScale(image, detections);

                System.out.println(String.format("Detected %s elements", detections.toArray().length));

                if(detections.toArray().length == 0){
                    noElement(files.get(i));
                }
                else {
                    for (Rect rect : detections.toArray()) {
                        Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                                new Scalar(0, 255, 0));
                    }
                    filename = getNewFileName(path, detections.toArray().length);
                    System.out.println(String.format("Writing %s", filename));
                    Highgui.imwrite(filename, image);
                }
            }
        }

    }

    public void noElement(File file){
        Path destDir = Paths.get("no_elements\\");
        try {
            Files.copy(file.toPath(), destDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
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
        return "with_elements\\" + name + "_detect_ " + amount + "_elements" + extension;
    }
}
