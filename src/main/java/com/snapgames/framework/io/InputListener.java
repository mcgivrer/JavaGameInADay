package com.snapgames.framework.io;

import com.snapgames.framework.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class InputListener implements KeyListener, Serializable {
    private final Game app;
    public boolean[] keys = new boolean[1024];

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
            app.exit = true;
        }
        if (isKeyPressed(KeyEvent.VK_Z) && e.isControlDown()) {
            app.getSceneManager().getActiveScene().reset();
        }
        keys[e.getKeyCode()] = false;
    }
}