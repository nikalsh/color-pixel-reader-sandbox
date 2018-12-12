package colorpixelreader.Swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author nikalsh
 */
public class TestPane extends JPanel implements Repainter {

    private Color color = Color.WHITE;
    int W = 250;
    int H = 250;
    int x = 0;
    int y = 0;

    public TestPane() {
        setOpaque(false);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
//            g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
//            g2d.fillRect(0, 0, getWidth(), getHeight());
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
    public void update(Color c, int w, int h, Point p) {
        this.color = c;
        W = w;
        H = h;
        x = p.x;
        y = p.y;
        repaint();

    }

}
