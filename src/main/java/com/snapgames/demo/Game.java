package com.snapgames.demo;

import com.snapgames.demo.gfx.Renderer;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.PhysicEngine;
import com.snapgames.demo.scene.PlayScene;
import com.snapgames.demo.scene.Scene;
import com.snapgames.demo.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class for Project test001
 *
 * @author Frédéric Delorme frederic.delorme@gmail.com
 * @since 1.0.0
 */
public class Game extends JPanel {
    private static final double FPS = 60.0;
    private final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private final Properties config = new Properties();

    // Configuration values
    public static boolean exit = false;
    private String title = "Test001";
    private Dimension windowSize = new Dimension(640, 400);
    private Dimension bufferSize = new Dimension(320, 200);
    private Rectangle2D.Double playArea = new Rectangle2D.Double(0, 0, 640, 400);

    // Services
    private InputListener inputListener;
    private PhysicEngine physicEngine;
    private Renderer renderer;

    // Scene Management
    private Map<String, Scene> scenes = new HashMap<>();
    private Scene activeScene;
    private String defaultSceneName;

    public Game() {
        super();
        Log.info("Initialization application %s (%s) %n- running on JDK %s %n- at %s %n- with classpath = %s%n",
                messages.getString("app.name"),
                messages.getString("app.version"),
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
        try {
            config.load(this.getClass().getResourceAsStream("/config.properties"));
            config.forEach((k, v) -> {
                Log.info("Configuration|%s=%s", k, v);
            });
            parseAttributes(config.entrySet().parallelStream().collect(Collectors.toList()));
        } catch (IOException e) {
            Log.info("Configuration|Unable to read configuration file: %s", e.getMessage());
        }
        lArgs.forEach(s -> {
            Log.info(String.format("Configuration|Argument: %s", s));
        });

        inputListener = new InputListener(this);
        physicEngine = new PhysicEngine(this);
        renderer = new Renderer(this, bufferSize);
        renderer.createWindow(title, windowSize);
        renderer.setInputListener(inputListener);

        Scene scene = new PlayScene(this, "play");
        addScene(scene);

        switchScene(defaultSceneName);
    }

    public void switchScene(String sceneName) {
        if (activeScene != null) {
            activeScene.dispose();
        }
        this.activeScene = scenes.get(sceneName);
        activeScene.load();
        activeScene.create();
    }

    private void addScene(Scene scene) {
        scenes.put(scene.getName(), scene);
    }

    private void parseAttributes(List<Map.Entry<Object, Object>> collect) {
        collect.forEach(e -> {
            switch (e.getKey().toString()) {
                case "app.window.title" -> {
                    this.title = (String) e.getValue();
                }
                case "app.exit" -> {
                    exit = Boolean.parseBoolean(config.getProperty("app.exit"));
                }
                case "app.window.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    windowSize = new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
                }
                case "app.render.buffer.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    bufferSize = new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
                }
                case "app.physic.world.play.area.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    playArea = new Rectangle2D.Double(0, 0, Double.parseDouble(values[0]), Double.parseDouble(values[1]));
                }
                case "app.scene.default" -> {
                    defaultSceneName = (String) e.getValue();
                }
                default -> {
                    Log.error("Configuration|Unknown value %s=%s", e.getKey(), e.getValue());
                }
            }
        });
    }

    private void loop() {

        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        long elapsed = 0;
        while (!exit) {
            elapsed = endTime - startTime;
            startTime = endTime;
            input(activeScene);
            update(activeScene, elapsed);
            render(activeScene);
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
    }

    public void update(Scene scene, long elapsed) {
        physicEngine.update(scene, elapsed);
    }

    public void render(Scene scene) {
        renderer.render(scene);
    }

    private void dispose() {
        renderer.dispose();
        Log.info("End of application ");
    }

    public static void main(String[] argc) {
        Game app = new Game();
        app.run(argc);
    }

    public Dimension getWindowSize() {
        return windowSize;
    }

    public String getDefaultScene() {
        return defaultSceneName;
    }
}
