package com.snapgames.framework.io;

import com.snapgames.framework.Game;
import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class InputListener implements KeyListener, Serializable, GSystem {
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

        SceneManager scnMgr = SystemManager.get(SceneManager.class);
        if (isKeyPressed(KeyEvent.VK_Q) || isKeyPressed(KeyEvent.VK_ESCAPE)) {
            app.requestExit();
        }
        if (isKeyPressed(KeyEvent.VK_Z) && e.isControlDown()) {
            scnMgr.getActiveScene().reset();
        }
        if (isKeyPressed(KeyEvent.VK_D)) {
            app.setDebug(app.getDebug() + 1 < 6 ? app.getDebug() + 1 : 0);
        }
        if (isKeyPressed(KeyEvent.VK_P) || isKeyPressed(KeyEvent.VK_PAUSE)) {
            app.setPause(app.isNotPaused());
        }
        if (isKeyPressed(KeyEvent.VK_F11)) {
            Renderer renderer = SystemManager.get(Renderer.class);
            renderer.switchFullScreenMode();
        }
        keys[e.getKeyCode()] = false;
    }

    @Override
    public Collection<Class<?>> getDependencies() {
        return List.of(Config.class, Renderer.class);
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public void start(Game game) {

    }

    @Override
    public void process(Game game) {
        SceneManager sceneManager = SystemManager.get(SceneManager.class);
        Scene scene = sceneManager.getActiveScene();
        scene.input(this);
        scene.getEntities().values()
                .forEach(e -> e.getBehaviors()
                        .forEach(b -> b.input(this, e)));
    }

    @Override
    public void stop(Game game) {

    }

    @Override
    public void dispose(Game game) {

    }
}