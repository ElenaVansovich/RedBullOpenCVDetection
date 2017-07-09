import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 08.07.2017.
 */
public class ImageViewerFrame extends JFrame {

    private JFileChooser chooser;
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;
    private ImagePanel panel;
    private ArrayList<File> files = new ArrayList<>();

    private String path = "";
    private Integer currentFile = 0;

    private File badFile;
    private File goodFile;

    private BufferedWriter writerBad;
    private BufferedWriter writerGood;

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry);
            }
        }
    }

    public void writeDatFiles() {
        badFile = new File("bad.dat");
        goodFile = new File("good.dat");

        try {
            writerBad = new BufferedWriter(new FileWriter(badFile));
            writerGood = new BufferedWriter(new FileWriter(goodFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFiles(ArrayList<File> files) {
        path = files.get(currentFile).getPath();
        panel.setImage(path);
    }

    public boolean getNextImage() {
        currentFile++;
        if (currentFile >= files.size()) {
            try {
                writerGood.close();
                writerBad.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "No more files");
            return false;
        }
        else {
            path = files.get(currentFile).getPath();
            panel.setImage(path);
            return true;
        }
    }

    public String getRectangleCoordinates(){
        Point2D press = panel.getPress();
        Point2D pressNo = panel.getPressNo();
        return "" + 1 + " " + press.getX() + " " + press.getY() + " " + pressNo.getX() + " " + pressNo.getY();
    }

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

        JMenuItem chooseItem = new JMenuItem("Choose directory");
        menu.add(chooseItem);
        chooseItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File folder = chooser.getSelectedFile();
                    listFilesForFolder(folder);
                    showFiles(files);
                    writeDatFiles();
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
        JButton buttonMinus = new JButton("-");
        this.add(buttonMinus, BorderLayout.WEST);
        buttonMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(getNextImage()) {
                        writerBad.write(path + "\n");
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        JButton buttonPlus = new JButton("+");
        this.add(buttonPlus, BorderLayout.EAST);
        buttonPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(getNextImage()) {
                        writerGood.write(path + getRectangleCoordinates() + "\n");
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.add(panel);
    }

    public String getPath() {
        return path;
    }

    public ArrayList<File> getFiles() {
        return files;
    }
}