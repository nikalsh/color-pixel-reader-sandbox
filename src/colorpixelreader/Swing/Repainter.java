package colorpixelreader.Swing;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author niklash
 */
public interface Repainter {

    
    void updateColor(Color c);
    
    void updateDim(int w, int h);
    
    void updatePos(Point p);
    
    void update(Color c, int w, int h, Point p);
}
