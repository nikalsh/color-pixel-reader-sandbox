package colorpixelreader.Swing.GIFMaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JPanel;

/**
 *
 * @author nikalsh
 */
public class DynamicRectangle extends JPanel implements Repainter {

    private Color color = Color.WHITE;
    int W = 250;
    int H = 250;
    int x = 0;
    int y = 0;

    public DynamicRectangle() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.setColor(color);
        g2d.drawRect(5, 5, W, H);
        g2d.dispose();
    }

    @Override
    public void updateColor(Color c) {
        this.color = c;
        repaint();
    }

    @Override
    public void updateDim(int w, int h) {
        W = w;
        H = h;
        repaint();
    }

    @Override
    public void updatePos(Point p) {
        x = p.x;
        y = p.y;
        repaint();
    }

    @Override
    public void redraw(Color c, int w, int h, Point p) {
        this.color = c;
        W = w;
        H = h;
        x = p.x;
        y = p.y;
        repaint();
    }
}
