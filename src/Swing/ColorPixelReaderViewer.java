package Swing;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author nikalsh
 */
public class ColorPixelReaderViewer {

    private static final Color TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.0f);

    JFrame DISPLAY_FRAME;
    JPanel DISPLAY_PANEL;
    JLabel DISPLAY;

    DynamicRectangle cameraFrame;
    Color color;
    JFrame CURSOR_FRAME;
    Repainter cameraFrameTransformer;

    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private final int monitorW = gd.getDisplayMode().getWidth();
    private final int monitorH = gd.getDisplayMode().getHeight();

    int H;
    int W;
    int zoom;
    boolean RECORD = false;
    boolean SNAPSHOT = false;

    public ColorPixelReaderViewer() throws AWTException {
        W = 250;
        H = 250;

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

    }
}
