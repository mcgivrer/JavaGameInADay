package com.snapgames.framework.io;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.physic.PhysicEngine;
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
import java.util.Map;

import static com.snapgames.framework.utils.Log.debug;

public class InputListener implements KeyListener, Serializable, GSystem {
    private final GameInterface app;
    public boolean[] keys = new boolean[1024];

    public InputListener(GameInterface app) {
        this.app = app;
        debug(InputListener.class, "Start of processing");
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
    public void initialize(GameInterface game) {
    }

    @Override
    public void start(GameInterface game) {

    }

    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        SceneManager sceneManager = SystemManager.get(SceneManager.class);
        Scene scene = sceneManager.getActiveScene();

        PhysicEngine physicEngine = SystemManager.get(PhysicEngine.class);
        if (physicEngine != null) {
            physicEngine.resetForces(scene);
        }

        scene.input(this);
        scene.getEntities().values()
                .forEach(e -> e.getBehaviors()
                        .forEach(b -> b.input(this, e)));
    }

    @Override
    public void stop(GameInterface game) {

    }

    @Override
    public void dispose(GameInterface game) {

    }
}