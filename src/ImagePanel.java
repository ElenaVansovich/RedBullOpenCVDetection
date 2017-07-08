import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

/**
 * Created by user on 08.07.2017.
 */
public class ImagePanel extends JPanel {

    public ImagePanel() {

        /*this.addComponentListener(new ComponentAdapter() {
            //перерисовывает картинку в случае изменени€ размеров фрейма...
            @Override
            public void componentResized(ComponentEvent ce) {
                repaint();
            }
        });*/

        this.addMouseWheelListener(new MouseWheelListener() {
            //слушает колЄсико мышки и измен€ет размер картинки
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() == 1) {
                    zoom -= 0.1;
                } else {
                    zoom += 0.1;
                }
                repaint();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            //перва€ точка, где кликаетс€ мышкой
            @Override
            public void mousePressed(MouseEvent e) {
                press = e.getPoint();
                pressedBtn = true;
            }

            //втора€ (конечна€) точка пр€моугольника
            @Override
            public void mouseReleased(MouseEvent e) {
                pressNo = e.getPoint();
                pressedBtn = false;
                //проверка, не выходит ли пр€моугольник за границы картинки
                if(pressNo.getX() != 0 && pressNo.getY() != 0) {
                    if (xImg <= press.getX() && yImg <= press.getY()
                            &&
                            xImg <= pressNo.getX() && yImg <= pressNo.getY()
                            &&

                            wImg >= pressNo.getX() && hImg >= pressNo.getY() &&
                            wImg >= press.getX() && hImg >= press.getY()
                            ) {
                        System.out.println(press.getX() + " " + press.getY() + " " + pressNo.getX() + " " + pressNo.getY());
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            //пр€моугольник, который показывает то, что выдел€етс€
            public void mouseDragged(MouseEvent e) {
                double x, y, w, h;

                if (e.getX() > (int) press.getX()) {
                    x = press.getX();
                    w = (double) e.getX() - press.getX();
                } else {
                    x = (double) e.getX();
                    w = press.getX() - (double) e.getX();
                }

                if (e.getY() > (int) press.getY()) {
                    y = press.getY();
                    h = (double) e.getY() - press.getY();
                } else {
                    y = (double) e.getY();
                    h = press.getY() - (double) e.getY();
                }
                setRect(new Rectangle2D.Double(x, y, w, h));
            }
        });
    }

    public void setRect(Rectangle2D rect) {
        this.rect = rect;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        if (img != null) {
            g2.drawImage(img, 0, 0, (int)(wImg*zoom), (int)(hImg*zoom),
                    xImg,
                    yImg,
                    xImg + wImg,
                    yImg + hImg,
                    null);
            if (pressedBtn) {
                g2.draw(rect);
            }
        }
    }

    public void setImage(String path) {
        try {
            img = ImageIO.read(new File(path));
            zoom = 1;
            xImg = 0;
            yImg = 0;
            wImg = img.getWidth(ImagePanel.this);
            hImg = img.getHeight(ImagePanel.this);
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int xImg, yImg, wImg, hImg; // координаты углов картинки
    private double zoom = 1;
    private Rectangle2D rect = new Rectangle2D.Double();
    private static Image img = null;
    private Point2D press = new Point2D.Double(0, 0);
    private Point2D pressNo = new Point2D.Double(0, 0);
    private boolean pressedBtn = false;
}
