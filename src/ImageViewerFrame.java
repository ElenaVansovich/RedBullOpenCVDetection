import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 08.07.2017.
 */
public class ImageViewerFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private JFileChooser chooser;
    private ImagePanel imagePanel;
    private Panel panel;
    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<File> testFiles = new ArrayList<>();

    private String folderPath = "";
    private String path = "";
    private Integer currentFile = 0;

    private File badFile;
    private File goodFile;
    private File folder;
    private File xmlFile;

    private boolean isXmlAdded = false;

    private BufferedWriter writerBad;
    private BufferedWriter writerGood;

    public ImageViewerFrame() {

        //setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        setSize(screenWidth, screenHeight - 30);
        setBackground(new Color(216, 230, 243));

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        imagePanel = new ImagePanel();
        imagePanel.setPreferredSize(new Dimension(DEFAULT_WIDTH - 50, DEFAULT_HEIGHT - 50));
        imagePanel.setBackground(new Color(216, 230, 243));

        panel = new Panel();
        panel.setBackground(new Color(216, 230, 243));

        ImageIcon icon1 = new ImageIcon("minus.png");

        Image scaled1 = icon1.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

        JButton buttonMinus = new JButton();
        buttonMinus.setIcon(new ImageIcon(scaled1));

        panel.add(buttonMinus, BorderLayout.WEST);
        buttonMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!path.equals("")) {
                        writerBad.write("Bad" + getRelativePath() + "\n");
                        getNextImage();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        JButton buttonFinish = new JButton("FINISH");
        panel.add(buttonFinish, BorderLayout.CENTER);
        buttonFinish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWriters();
                JOptionPane.showMessageDialog(null, "Sorting is finished");
            }
        });

        ImageIcon icon2 = new ImageIcon("plus.png");
        Image scaled2 = icon2.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

        JButton buttonPlus = new JButton();
        buttonPlus.setIcon(new ImageIcon(scaled2));

        panel.add(buttonPlus, BorderLayout.EAST);
        buttonPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!path.equals("")) {
                        if (!imagePanel.isAreaSelected()) {
                            JOptionPane.showMessageDialog(null, "Area is not selected");
                        } else {
                            writerGood.write("Good" + getRelativePath() + getRectangleCoordinates() + "\n");
                            getNextImage();
                            imagePanel.resetArea();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.add(panel, BorderLayout.NORTH);
        this.add(imagePanel, BorderLayout.SOUTH);
        this.setResizable(false);

        JMenu trainingMenu = new JMenu("Training");
        menuBar.add(trainingMenu);

        JMenuItem chooseItem = new JMenuItem("Choose directory");
        trainingMenu.add(chooseItem);
        chooseItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    folder = chooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null, "Directory \"" + folder.getName() + "\" is choosed");
                    listFilesForFolder(files, folder);
                    showFiles(files);
                    writeDatFiles();
                }
            }
        });

        JMenuItem quickCheckerItem = new JMenuItem("Quick checker");
        trainingMenu.add(quickCheckerItem);
        quickCheckerItem.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    folder = chooser.getSelectedFile();
                    path = chooser.getSelectedFile().getPath();
                    writeDatFiles();
                    quickCheckingBadFiles(folder, writerBad);
                }
            }
        }));

        JMenuItem trainItem = new JMenuItem("Train");
        trainingMenu.add(trainItem);
        trainItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ExecuteShellComand obj = new ExecuteShellComand();

                String current = System.getProperty("user.dir");

                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileFilter filter = new FileNameExtensionFilter("Executable File", "exe");
                chooser.setFileFilter(filter);
                int result = chooser.showOpenDialog(null);

                String exePath = "";
                if (result == JFileChooser.APPROVE_OPTION) {
                    exePath = chooser.getSelectedFile().getAbsolutePath();
                }

                String command = exePath + " -data " +
                        current + "\\haarcascade -vec " + current + "\\samples.vec -bg " +
                        current + "\\bad.dat -numStages " +
                        "8 -minhitrate 0.99 -maxFalseAlarmRate 0.4 -numPos 114 -numNeg 304 -w 90 -h 50" +
                        " -precalcValBufSize 1024 -precalcIdxBufSize 1024";

                String output = obj.executeCommand(command);
                System.out.println(output);
            }
        });


        JMenu testMenu = new JMenu("Test");
        menuBar.add(testMenu);

        JMenuItem addXmlItem = new JMenuItem("Add xml");
        testMenu.add(addXmlItem);
        addXmlItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int result = chooser.showOpenDialog(null);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileFilter filter = new FileNameExtensionFilter("Xml File", "xml");
                chooser.setFileFilter(filter);

                if (result == JFileChooser.APPROVE_OPTION) {
                    xmlFile = chooser.getSelectedFile();
                    isXmlAdded = true;
                    JOptionPane.showMessageDialog(null, "Xml file is added");
                }
            }
        });

        JMenuItem chooseTestFilesItem = new JMenuItem("Choose test directory");
        testMenu.add(chooseTestFilesItem);
        chooseTestFilesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File testFolder = chooser.getSelectedFile();
                    listFilesForFolder(testFiles, testFolder);
                    JOptionPane.showMessageDialog(null, "Directory \"" + testFolder.getName() + "\" is choosed");
                }
            }
        });

        JMenuItem testItem = new JMenuItem("Test");
        testMenu.add(testItem);
        testItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!isXmlAdded) {
                    JOptionPane.showMessageDialog(null, "Xml is not added");
                } else test();
            }
        });
    }

    public void listFilesForFolder(ArrayList<File> list, final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(list, fileEntry);
            } else {
                list.add(fileEntry);
            }
        }
    }

    public void quickCheckingBadFiles(final File file, final BufferedWriter writerBad) {
        for (final File fileEntry : file.listFiles()) {
            if (fileEntry.isDirectory()) {
                folderPath = file.getName();
                quickCheckingBadFiles(fileEntry, writerBad);
            } else {
                double fileSize = fileEntry.length();
                System.out.println(fileSize);
                if (fileSize < 300.0) {
                    System.out.println(folder.getName());
                    try {
                        path = fileEntry.getPath();
                        String relativePath = folderPath + "/framesFolder/" + fileEntry.getName().substring(0, fileEntry.getName().length() - 11);
                        if (!path.equals("")) {
                            writerBad.write("Bad " + relativePath + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void closeWriters() {
        try {
            if (writerGood != null)
                writerGood.close();
            if (writerBad != null)
                writerBad.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        imagePanel.setImage(path);
    }

    public boolean getNextImage() {
        currentFile++;
        if (currentFile >= files.size()) {
            closeWriters();
            JOptionPane.showMessageDialog(null, "No more files");
            return false;
        } else {
            path = files.get(currentFile).getPath();
            imagePanel.setImage(path);
            return true;
        }
    }

    public String getRectangleCoordinates() {
        Point press = imagePanel.getPress();
        Point pressNo = imagePanel.getPressNo();
        return " " + 1 + " " + (int) (Math.min(press.getX(), pressNo.getX()) - imagePanel.getAlignmentX()) +
                " " + (int) (Math.min(press.getY(), pressNo.getY()) - imagePanel.getAlignmentX()) +
                " " + (int) Math.abs(pressNo.getX() - press.getX()) +
                " " + (int) Math.abs(pressNo.getY() - press.getY());
    }

    public String getRelativePath() {
        return path.substring(folder.getPath().length());
    }

    public void test() {
        if (testFiles.size() != 0) {
            Detect det = new Detect();
            det.detect(testFiles, xmlFile.getPath(), null);
        } else JOptionPane.showMessageDialog(null, "Test files are not added");
    }

    public String getPath() {
        return path;
    }

    public ArrayList<File> getFiles() {
        return files;
    }
}