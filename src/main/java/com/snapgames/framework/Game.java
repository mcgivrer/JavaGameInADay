package com.snapgames.framework;

import com.snapgames.demo.scenes.PlayScene;
import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.CollisionManager;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Log;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.snapgames.framework.utils.I18n.getI18n;

/**
 * Main class for Project test001
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Game extends JPanel {
    private static final double FPS = 60.0;

    // Game exit request flag.
    public static boolean exit = false;

    // internal Pause flag
    private boolean pause = false;

    // debug level
    private int debug = 1;

    // Services
    private Config config;
    private InputListener inputListener;
    private PhysicEngine physicEngine;
    private CollisionManager collisionManager;
    private Renderer renderer;
    private SceneManager scnMgr;


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
        config = new Config(this);
        config.load("/config.properties");

        lArgs.forEach(s -> {
            Log.info(Game.class, String.format("Argument: %s", s));
        });

        inputListener = new InputListener(this);
        physicEngine = new PhysicEngine(this);
        collisionManager = new CollisionManager(this);
        renderer = new Renderer(this, config.get("app.render.buffer.size"));
        renderer.createWindow(config.get("app.window.title"), config.get("app.window.size"));
        renderer.setInputListener(inputListener);
        scnMgr = new SceneManager(this);

        Scene scene = new PlayScene(this, "play");
        scnMgr.addScene(scene);
        scnMgr.setDefaultScene(config.get("app.scene.default"));

        scnMgr.switchScene();
    }

    private void loop() {

        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        long elapsed = 0;
        while (!exit) {
            Scene scene = getSceneManager().getActiveScene();
            elapsed = endTime - startTime;
            startTime = endTime;
            if (!isPaused()) {
                physicEngine.resetForces(scene);
                input(scene);
                update(scene, elapsed);
                render(scene);
            }

            endTime = System.currentTimeMillis();
            try {
                Thread.sleep((long) (elapsed < (1000 / FPS) ? (1000 / FPS) - elapsed : 1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void input(Scene scene) {
        scene.input(inputListener);
        scene.getEntities().values()
                .forEach(e -> e.getBehaviors()
                        .forEach(b -> b.input(inputListener, e)));
    }

    public void update(Scene scene, long elapsed) {
        physicEngine.update(scene, elapsed);
        collisionManager.update(scene, elapsed);
    }

    public void render(Scene scene) {
        renderer.render(scene);
    }

    private void dispose() {
        physicEngine.dispose();
        renderer.dispose();
        scnMgr.dispose();
        Log.info("End of application ");
    }

    public static void main(String[] argc) {
        Game app = new Game();
        app.run(argc);
    }

    public SceneManager getSceneManager() {
        return scnMgr;
    }

    public Config getConfig() {
        return config;
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

    public boolean isPaused() {
        return pause;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
