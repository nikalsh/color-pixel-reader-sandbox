package colorpixelreader.Swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nikalsh
 */
public class myKeyListener implements KeyListener {

    int ctrl = KeyEvent.VK_CONTROL;

    HashSet<Integer> activeKeys;

    public myKeyListener() {
        activeKeys = new HashSet<>();

    }

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
        }

        activeKeys.remove(e.getKeyCode());

    }
}
