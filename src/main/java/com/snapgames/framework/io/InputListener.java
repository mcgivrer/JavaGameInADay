package com.snapgames.framework.io;

import com.snapgames.framework.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class InputListener implements KeyListener, Serializable {
    private final Game app;
    public boolean[] keys = new boolean[1024];
    private boolean fullScreen = false;

    public InputListener(Game app) {
        this.app = app;
    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isKeyPressed(KeyEvent.VK_Q) || isKeyPressed(KeyEvent.VK_ESCAPE)) {
            app.requestExit();
        }
        if (isKeyPressed(KeyEvent.VK_Z) && e.isControlDown()) {
            app.getSceneManager().getActiveScene().reset();
        }
        if (isKeyPressed(KeyEvent.VK_D)) {
            app.setDebug(app.getDebug() + 1 < 6 ? app.getDebug() + 1 : 0);
        }
        if (isKeyPressed(KeyEvent.VK_P) || isKeyPressed(KeyEvent.VK_PAUSE)) {
            app.setPause(app.isNotPaused());
        }
        if (isKeyPressed(KeyEvent.VK_F11)) {
            switchFullScreenMode();
        }
        keys[e.getKeyCode()] = false;
    }

    private void switchFullScreenMode() {
        app.setPause(true);
        fullScreen = !fullScreen;
        JFrame w = app.getRenderer().getWindow();
        Dimension dim = w.getSize();
        String title = w.getTitle();
        app.getRenderer().newWindow(title, dim, fullScreen);
        app.setPause(false);
    }
}