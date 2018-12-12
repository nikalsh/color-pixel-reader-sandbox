package colorpixelreader.Swing;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class ColorPixelReader {

    JFrame frame;
    JPanel panel;
    JLabel label;
    JLayeredPane lp;
    JLabel labelCords;
    JLabel RGB;

    Color color;
    Robot robot;

    int zoom;

    Point p = MouseInfo.getPointerInfo().getLocation();

    public ColorPixelReader() throws AWTException {

        zoom = 1;
        robot = new Robot();

        frame = new JFrame();
        panel = new JPanel();
        label = new JLabel();
        labelCords = new JLabel();
        RGB = new JLabel();

        class WheelListener implements MouseWheelListener {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (e.getWheelRotation() < 0) {
                    System.out.println(zoom);
                    if (zoom < 25) {
                        zoom++;
                    }
                } else if (e.getWheelRotation() > 0) {
                    System.out.println(zoom);

                    if (zoom > 1) {
                        zoom--;
                    }

                }
            }

            public WheelListener() {
            }

        }

        Box vbox = Box.createVerticalBox();

        panel.add(label);

        panel.setBounds(0, 0, 500, 500);
        panel.setPreferredSize(new Dimension(500, 500));
//        lp = new JLayeredPane();
//        lp.setPreferredSize(new Dimension(750, 750));
//        lp.setBounds(0, 0, 750, 750);
//        lp.add(panel, new Integer(0));

//        frame.add(lp);
        vbox.add(panel);
        vbox.add(labelCords);
        vbox.add(RGB);
        frame.addMouseWheelListener(new WheelListener());
        frame.add(vbox);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocationRelativeTo(null);

        frame.setPreferredSize(new Dimension(600, 600));

        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

        while (true) {

            p = MouseInfo.getPointerInfo().getLocation();
            color = robot.getPixelColor(p.x, p.y);
            label.setIcon(new ImageIcon(getScreenshot(p, 200, color)));
            panel.setBackground(color);
            labelCords.setText(String.format("x:%s y:%s", p.x, p.y));
            RGB.setText(String.format("R: %s G: %s B: %s", color.getRed(), color.getGreen(), color.getBlue()));

//            System.out.println(color);
        }

    }

    private Image getScreenshot(Point p, int size, Color color) {

        Point newP = p;

        newP.x = newP.x - size / 2;
        newP.y = newP.y - size / 2;
        BufferedImage cap = robot.createScreenCapture(new Rectangle(newP, new Dimension(size, size)));

        Graphics g = cap.getGraphics();
        g.setColor(invertColor(color));
        g.drawLine(size / 2 - 2, size / 2 - 2, size / 2 + 2, size / 2 + 2);
        g.drawLine(size / 2 - 2, size / 2 + 2, size / 2 + 2, size / 2 - 2);
        g.dispose();
//        Image temp = cap.getScaledInstance(size * zoom, size * zoom, Image.SCALE_FAST);

        double widthScale = size / cap.getWidth(null);
        double heightScale = size / cap.getHeight(null);
        AffineTransform tx = new AffineTransform();

        tx.scale(zoom, zoom);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage resized = null;
        resized = op.filter((BufferedImage) cap, null);

        return resized;
    }

    private Color invertColor(Color color) {

        Color iColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        return iColor;
    }

    public static void main(String[] args) throws AWTException {
        ColorPixelReader cp = new ColorPixelReader();
    }

}
