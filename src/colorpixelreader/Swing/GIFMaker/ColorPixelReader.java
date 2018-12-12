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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorPixelReader {

    JFrame frame;
    JPanel panel;
    JLabel label;

    TestPane camPanel;
    Color color;
    Robot robot;
    JFrame camFrame;
    Repainter panelTransformer;
    boolean RECORD = false;

    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private final int monitorW = gd.getDisplayMode().getWidth();
    private final int monitorH = gd.getDisplayMode().getHeight();

    int H;
    int W;

    private final Object lock = new Object();

    int zoom;

    Point p = MouseInfo.getPointerInfo().getLocation();

    public int getZoom() {

//        synchronized (lock) {
        return zoom;
//        }
    }

    public ColorPixelReader() throws AWTException {
        class WheelListener implements MouseWheelListener {

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

            public WheelListener() {
            }

        }
        W = 250;
        H = 250;
        zoom = 1;
        robot = new Robot();

//        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame();
        panel = new JPanel();
        label = new JLabel();

        camFrame = new JFrame("Testing");

        camPanel = new TestPane();

        frame.setUndecorated(false);
//        frame.getLayeredPane().remove(frame.getLayeredPane().getComponent(1));
//        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        Color transp = new Color(1.0f, 1.0f, 1.0f, 0.0f);
//        frame.setBackground(transp);
        frame.setTitle("Camera");
        panel.setBackground(transp);
        Box vbox = Box.createVerticalBox();

        label.setPreferredSize(new Dimension(W, H));
        panel.add(label);
//        vbox.add(panel);

        frame.addMouseWheelListener(new WheelListener());
        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        camFrame.setUndecorated(true);
        camFrame.setBackground(new Color(0, 0, 0, 0));
        camFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        camPanel.setPreferredSize(new Dimension(W-10, H-10));
        camPanel.setPreferredSize(new Dimension(W, H));
        camFrame.add(camPanel);
        panelTransformer = camPanel;

        frame.addKeyListener(new KeyListener() {
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
                activeKeys.remove(e.getKeyCode());
            }
        });

    }

    public void loop() throws InterruptedException {

        while (true) {
            W = panel.getWidth() - 10;
            H = panel.getHeight() - 10;

            p = MouseInfo.getPointerInfo().getLocation();

            color = robot.getPixelColor(p.x, p.y);

            camPanel.setPreferredSize(new Dimension(W, H));
            camFrame.setPreferredSize(new Dimension(W, H));
            camFrame.setLocation(p.x - W / 2, p.y - H / 2);
            camFrame.pack();

            label.setIcon(new ImageIcon(getScreenshot(p, W - 10, H - 10, color)));
            label.setPreferredSize(new Dimension(W - 10, H - 10));

//            panel.setBackground(color);
        }

    }

    private Image getScreenshot(Point p, int w, int h, Color color) throws InterruptedException {

        Color inverted = invertColor(color);

        Point newP = p;

        w = (w < 1 ? 1 : w);
        h = (h < 1 ? 1 : h);

        newP.x -= w / 2;
        newP.y -= h / 2;

        BufferedImage cap = null;

        cap = robot.createScreenCapture(new Rectangle(newP, new Dimension(w, h)));

//        Image temp = cap.getScaledInstance(size * zoom, size * zoom, Image.SCALE_FAST);
        AffineTransform at = new AffineTransform();

        at.scale(getZoom(), getZoom());

        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage zoomed = null;

        zoomed = op.filter(cap, zoomed);
        int siize = zoomed.getHeight();

        BufferedImage cropped = new BufferedImage(w, h, cap.getType());

        Graphics g = zoomed.getGraphics();

//        panelTransformer.updateColor(inverted);
//        panelTransformer.updateDim(w, h);
//        panelTransformer.updatePos(p);
        panelTransformer.update(inverted, w, h, p);

        g.setColor(inverted);

        int len = 25;

        g.drawLine(w / 2 - len, h / 2 - len, w / 2 + len, h / 2 + len);
        g.drawLine(w / 2 - len, h / 2 + len, w / 2 + len, h / 2 - len);
        g.dispose();

//        int x = (siize - (size * getZoom()));
//        int y = (siize - (size * getZoom()));
        if (getZoom() > 1) {

            cropped = zoomed.getSubimage(0, 0, w, h);

//            cropped = zoomed.getSubimage(siize - size*getZoom(), siize - size*getZoom(), size, size);
        } else {
            cropped = zoomed.getSubimage(0, 0, w, h);

        }
        if (RECORD) {

            recordCurrentFrame(cropped);

        }

        if (!RECORD) {

            encodeRecordedFramesToGif();

        }
        return cropped;

    }
    List<BufferedImage> imgs = new ArrayList<>();

    private void recordCurrentFrame(BufferedImage img) throws InterruptedException {
        imgs.add(img);
        Thread.sleep(100);

    }

    private void encodeRecordedFramesToGif() {

        if (!imgs.isEmpty()) {
            doTheSave();

            imgs.clear();
        }
    }

    int name = 0;

    class ClipboardFile implements Transferable {

        List<File> fileList;

        public ClipboardFile(File file) {
            fileList = new ArrayList<>();
            fileList.add(file);

        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {

            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return fileList;
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

            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new ClipboardFile(file),
                            null);

            writer.close();
            output.close();

        } catch (IOException ex) {
            Logger.getLogger(ColorPixelReader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Color invertColor(Color color) {

        Color iColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        return iColor;
    }

    public static void main(String[] args) throws AWTException, InterruptedException {
        ColorPixelReader cp = new ColorPixelReader();

        cp.frame.pack();
        cp.frame.setLocationRelativeTo(null);
        cp.frame.setVisible(true);
        cp.frame.setAlwaysOnTop(true);

//        AWTUtilities.setWindowShape(cp.frame, new Ellipse2D.Double(5, 5, cp.frame.getWidth() - 10, cp.frame.getHeight() - 10));
        cp.camFrame.pack();
        cp.camFrame.setVisible(true);
        cp.camFrame.setAlwaysOnTop(true);

        cp.loop();

    }

}
