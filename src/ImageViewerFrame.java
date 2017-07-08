import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by user on 08.07.2017.
 */
public class ImageViewerFrame extends JFrame {

    private JFileChooser chooser;
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;
    private ImagePanel panel;

    private String path = "";

    public ImageViewerFrame() {

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem openItem = new JMenuItem("Open");
        menu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getPath();
                    panel.setImage(path);
                }
            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        menu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        panel = new ImagePanel();
        this.add( new Button("-"), BorderLayout.WEST);
        this.add( new Button("+"),BorderLayout.EAST);
        this.add(panel);
    }

    public String getPath() {
        return path;
    }
}