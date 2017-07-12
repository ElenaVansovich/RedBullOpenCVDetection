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
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
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

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry);
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
                        if(!path.equals("")) {
                          writerBad.write("Bad " + relativePath + "\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void closeWriters(){
        try {
            writerGood.close();
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
        }
        else {
            path = files.get(currentFile).getPath();
            imagePanel.setImage(path);
            return true;
        }
    }

    public String getRectangleCoordinates(){
        Point press = imagePanel.getPress();
        Point pressNo = imagePanel.getPressNo();
        return " " + 1 + " " + (int)(Math.min(press.getX(), pressNo.getX()) - imagePanel.getAlignmentX()) +
                " " + (int) (Math.min(press.getY(), pressNo.getY()) - imagePanel.getAlignmentX()) +
                " " + (int) Math.abs(pressNo.getX() - press.getX()) +
                " " + (int) Math.abs(pressNo.getY() - press.getY());
    }

    public String getRelativePath(){
        return path.substring(folder.getPath().length());
    }


    public void test() {
        if (files.size() != 0) {
            Detect det = new Detect();
            det.detect(files, xmlFile.getPath(), null);
        }
    }

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

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem openItem = new JMenuItem("Open");
        menu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getPath();
                    imagePanel.setImage(path);
                    writeDatFiles();
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
                    folder = chooser.getSelectedFile();
                    listFilesForFolder(folder);
                    showFiles(files);
                    writeDatFiles();
                }
            }
        });

        JMenuItem quickCheckerItem = new JMenuItem("Quick checker");
        menu.add(quickCheckerItem);
        quickCheckerItem.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    folder = chooser.getSelectedFile();
                    path = chooser.getSelectedFile().getPath();
                    writeDatFiles();
                    quickCheckingBadFiles(folder, writerBad);
                }
            }
        }));

        JMenuItem exitItem = new JMenuItem("Exit");
        menu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                closeWriters();
                System.exit(0);
            }
        });

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
                    if(!path.equals("")) {
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
                        if ((imagePanel.getPress()).getX() == 0 &&
                                (imagePanel.getPress()).getY() == 0 &&
                                (imagePanel.getPressNo()).getX() == 0 &&
                                (imagePanel.getPressNo()).getY() == 0) {
                            JOptionPane.showMessageDialog(null, "Area is not selected");
                        } else {
                                writerGood.write("Good" + getRelativePath() + getRectangleCoordinates() + "\n");
                                getNextImage();
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


        JMenu testMenu = new JMenu("Test");
        menuBar.add(testMenu);

        JMenuItem addXmlItem = new JMenuItem("Add xml");
        testMenu.add(addXmlItem);
        addXmlItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int result = chooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    xmlFile = chooser.getSelectedFile();
                    isXmlAdded = true;
                }
            }
        });

        JMenuItem testItem = new JMenuItem("Test");
        testMenu.add(testItem);
        testItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!isXmlAdded){
                    JOptionPane.showMessageDialog(null, "Xml is not added");
                }
                else test();
            }
        });


    }

    public String getPath() {
        return path;
    }

    public ArrayList<File> getFiles() {
        return files;
    }
}
