package com.snapgames.framework;

import com.snapgames.demo.scenes.PlayScene;
import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.CollisionManager;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Log;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static com.snapgames.framework.utils.I18n.getI18n;

/**
 * Main class for Project test001
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Game extends JPanel implements GameInterface {
    private static final double FPS = 60.0;

    // Game exit request flag.
    public static boolean exit = false;

    // internal Pause flag
    private boolean pause = false;

    // debug level
    private int debug = 1;

    public Game() {
        super();
        Log.info("Initialization application %s (%s) %n- running on JDK %s %n- at %s %n- with classpath = %s%n",
            getI18n("app.name"),
            getI18n("app.version"),
            System.getProperty("java.version"),
            System.getProperty("java.home"),
            System.getProperty("java.class.path"));
    }

    public void run(String[] args) {
        init(args);
        loop();
        dispose();
    }

    private void init(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        lArgs.forEach(s -> {
            Log.info(Game.class, String.format("Argument: %s", s));
        });

        SystemManager.setParent(this);

        Config config = new Config(this);
        config.parseArgs(args);
        SystemManager.add(config);

        SystemManager.add(new PhysicEngine(this));
        SystemManager.add(new CollisionManager(this));
        SystemManager.add(new Renderer(this));
        SystemManager.add(new InputListener(this));
        SystemManager.add(new SceneManager(this));

        SystemManager.initialize();

        SystemManager.start(this);
    }

    private void loop() {

        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        double elapsed = 0;
        while (!exit) {
            elapsed = endTime - startTime;
            startTime = endTime;
            SystemManager.process(elapsed);
            SystemManager.postProcess();
            endTime = System.currentTimeMillis();
            try {
                Thread.sleep((long) (elapsed < (1000 / FPS) ? (1000 / FPS) - elapsed : 1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void dispose() {
        SystemManager.dispose();
        Log.info("End of application ");
    }

    public static void main(String[] argc) {
        Game app = new Game();
        app.run(argc);
    }

    public boolean isDebugGreaterThan(int debugLevel) {
        return debug > debugLevel;
    }

    public void setDebug(int dl) {
        debug = dl;
    }

    public int getDebug() {
        return debug;
    }

    public void setPause(boolean p) {
        this.pause = p;
    }

    @Override
    public void setExit(boolean e) {
        this.exit = e;
    }

    public boolean isNotPaused() {
        return !pause;
    }

    public void requestExit() {
        if (confirmExit()) {
            exit = true;
        }
    }

    public boolean confirmExit() {
        boolean status = false;
        setPause(true);
        Renderer renderer = SystemManager.get(Renderer.class);
        int response = JOptionPane.showConfirmDialog(renderer.getWindow(),
            getI18n("app.exit.confirm.message"),
            getI18n("app.exit.confirm.title"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            status = true;
        }
        setPause(false);
        return status;
    }

}
