package colorpixelreader.Swing.GIFMaker;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorPixelReader {

    private static final Color TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.0f);

    JFrame DISPLAY_FRAME;
    JPanel DISPLAY_PANEL;
    JLabel DISPLAY;

    DynamicRectangle cameraFrame;
    Color color;
    Robot robot;
    JFrame CURSOR_FRAME;
    Repainter cameraFrameTransformer;

    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private final int monitorW = gd.getDisplayMode().getWidth();
    private final int monitorH = gd.getDisplayMode().getHeight();
    private final Object lock = new Object();

    int H;
    int W;
    int zoom;
    boolean RECORD = false;
    boolean SNAPSHOT = false;
    Point p = MouseInfo.getPointerInfo().getLocation();

    public int getZoom() {
//        synchronized (lock) {
        return zoom;
//        }
    }

    public ColorPixelReader() throws AWTException {
        W = 250;
        H = 250;
        zoom = 1;
        robot = new Robot();

        DISPLAY_FRAME = new JFrame();
        DISPLAY_PANEL = new JPanel();
        DISPLAY = new JLabel();

        CURSOR_FRAME = new JFrame("Testing");
        cameraFrame = new DynamicRectangle();

        DISPLAY_FRAME.setUndecorated(false);
        DISPLAY_FRAME.setTitle("Camera");
        DISPLAY_PANEL.setBackground(TRANSPARENT);
        DISPLAY.setPreferredSize(new Dimension(W, H));
        DISPLAY_PANEL.add(DISPLAY);

        DISPLAY_FRAME.add(DISPLAY_PANEL);

        DISPLAY_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CURSOR_FRAME.setUndecorated(true);
        CURSOR_FRAME.setBackground(new Color(0, 0, 0, 0));
        CURSOR_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cameraFrame.setPreferredSize(new Dimension(W, H));
        CURSOR_FRAME.add(cameraFrame);

        cameraFrameTransformer = cameraFrame;

        DISPLAY_FRAME.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    if (zoom < 25) {
                        synchronized (lock) {
                            zoom++;
                        }
                    }
                } else if (e.getWheelRotation() > 0) {

                    if (zoom > 1) {
                        synchronized (lock) {
                            zoom--;
                        }
                    }
                }
            }
        });

        DISPLAY_FRAME.addKeyListener(new KeyListener() {
            int ctrl = KeyEvent.VK_CONTROL;
            HashSet<Integer> activeKeys = new HashSet<>();

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                activeKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R && activeKeys.contains(ctrl)) {

                    System.out.println("ctrl + r");
                    if (RECORD) {
                        RECORD = false;
                        System.out.println("stopped recording");
                    } else {
                        RECORD = true;
                        System.out.println("started recording");
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_C && activeKeys.contains(ctrl)) {

                    SNAPSHOT = true;
                    System.out.println("copy current image to clipboard");
                    //copy method will reset snapshot
                }

                activeKeys.remove(e.getKeyCode());
            }

        });
    }

    public void loop() throws InterruptedException {

        while (true) {
            W = DISPLAY_PANEL.getWidth() - 10;
            H = DISPLAY_PANEL.getHeight() - 10;

            p = MouseInfo.getPointerInfo().getLocation();

            color = robot.getPixelColor(p.x, p.y);

            cameraFrame.setPreferredSize(new Dimension(W, H));
            CURSOR_FRAME.setPreferredSize(new Dimension(W, H));
            CURSOR_FRAME.setLocation(p.x - W / 2, p.y - H / 2);
            CURSOR_FRAME.pack();

            DISPLAY.setIcon(new ImageIcon(getScreenshot(p, W - 10, H - 10, color)));
            DISPLAY.setPreferredSize(new Dimension(W - 10, H - 10));
        }
    }

    private Image getScreenshot(Point p, int w, int h, Color color) throws InterruptedException {
        Color inverted = invertColor(color);

        int cursor_len = 25;
        Point newP = p;

        w = (w < 1 ? 1 : w);
        h = (h < 1 ? 1 : h);
        newP.x -= w / 2;
        newP.y -= h / 2;

        BufferedImage cap = null;
        cap = robot.createScreenCapture(new Rectangle(newP, new Dimension(w, h)));

        BufferedImage zoomed = new BufferedImage(w, h, cap.getType());
        zoomed = scale(cap, getZoom());

        cameraFrameTransformer.redraw(inverted, w, h, p);

        Graphics g = zoomed.getGraphics();
        g.setColor(inverted);
        g.drawLine(w / 2 - cursor_len, h / 2 - cursor_len, w / 2 + cursor_len, h / 2 + cursor_len);
        g.drawLine(w / 2 - cursor_len, h / 2 + cursor_len, w / 2 + cursor_len, h / 2 - cursor_len);
        g.dispose();

        if (getZoom() > 1) {
            zoomed = zoomed.getSubimage(0, 0, w, h);

        } else {
            zoomed = zoomed.getSubimage(0, 0, w, h);

        }

        if (RECORD) {
            addCurrentImgToImgList(zoomed);
        }

        if (!RECORD) {
            encodeImgListToGif();
        }

        if (SNAPSHOT) {
            System.out.println("we snappin");
            encodeCurrentImgToPNG(zoomed);
            SNAPSHOT = false;
        }
        return zoomed;
    }

    public BufferedImage scale(BufferedImage img, int scale) {
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage scaled = null;

        scaled = op.filter(img, scaled);
        return scaled;
    }

    public void encodeCurrentImgToPNG(BufferedImage img) {

        try {
            File file = new File("src/colorpixelreader/swing/GIFMaker/output/screenshot "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-Hms"))
                    + ".png/");
            ImageIO.write(img, "png", file);
            
            copyToClipboard(file);

        } catch (IOException ex) {
            Logger.getLogger(ColorPixelReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    List<BufferedImage> imgs = new ArrayList<>();

    private void addCurrentImgToImgList(BufferedImage img) throws InterruptedException {
        imgs.add(img);
        Thread.sleep(100);
    }

    private void encodeImgListToGif() {
        if (!imgs.isEmpty()) {
            doTheSave();
            imgs.clear();
        }
    }

    private void doTheSave() {
        try {
            File file = new File("src/colorpixelreader/swing/GIFMaker/output/rec "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-Hms"))
                    + ".gif/");

            ImageOutputStream output = new FileImageOutputStream(file);
            GifSequenceWriter writer = new GifSequenceWriter(output, imgs.get(1).getType(), 0, true);

            for (BufferedImage img : imgs) {
                writer.writeToSequence(img);
            }

            copyToClipboard(file);

            writer.close();
            output.close();

        } catch (IOException ex) {
            Logger.getLogger(ColorPixelReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public <T> void copyToClipboard(T object) {
        //first we make it transferable to clipboard
        GenericTransferableObject gto = new GenericTransferableObject(object);
        //then transfer to clipboard
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(gto, null);
    }

    private Color invertColor(Color color) {
        Color iColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        return iColor;
    }

    public static void main(String[] args) throws AWTException, InterruptedException {
        ColorPixelReader cp = new ColorPixelReader();

        cp.DISPLAY_FRAME.pack();
        cp.DISPLAY_FRAME.setLocationRelativeTo(null);
        cp.DISPLAY_FRAME.setVisible(true);
        cp.DISPLAY_FRAME.setAlwaysOnTop(true);
//        AWTUtilities.setWindowShape(cp.DISPLAY_FRAME, new Ellipse2D.Double(5, 5, cp.DISPLAY_FRAME.getWidth() - 10, cp.DISPLAY_FRAME.getHeight() - 10));
        cp.CURSOR_FRAME.pack();
        cp.CURSOR_FRAME.setVisible(true);
        cp.CURSOR_FRAME.setAlwaysOnTop(true);

        cp.loop();
    }
}
