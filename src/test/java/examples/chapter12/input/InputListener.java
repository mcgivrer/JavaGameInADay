package examples.chapter12.input;

import game.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static com.snapgames.framework.utils.Log.debug;

public class InputListener implements KeyListener {
    private final Game app;
    public boolean[] keys = new boolean[1024];

    public InputListener(Game app) {
        this.app = app;
        debug(InputListener.class, "Start of processing");
    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (e.isControlDown()) {
            if (isKeyPressed(KeyEvent.VK_Q) || isKeyPressed(KeyEvent.VK_ESCAPE)) {
                app.requestExit();
            }
            if (isKeyPressed(KeyEvent.VK_P) || isKeyPressed(KeyEvent.VK_PAUSE)) {
                app.setPause(app.isNotPaused());
            }
            if (isKeyPressed(KeyEvent.VK_D)) {
                app.setDebug(app.getDebug() + 1 < 6 ? app.getDebug() + 1 : 0);
            }
        }
    }

    public void dispose(Game game) {

    }
}