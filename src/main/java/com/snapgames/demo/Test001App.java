package com.snapgames.demo;

import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.PhysicEngine;
import com.snapgames.demo.scene.PlayScene;
import com.snapgames.demo.scene.Scene;
import com.snapgames.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
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
public class Test001App extends JPanel {
    private final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private final Properties config = new Properties();

    private JFrame window;

    public static boolean exit = false;
    private String title = "Test001";
    private Dimension windowSize = new Dimension(640, 400);


    private InputListener inputListener;
    private PhysicEngine physicEngine;
    private Map<String, Scene> scenes = new HashMap<>();
    private Scene activeScene;

    public Test001App() {
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

        window = new JFrame(title);
        window.setPreferredSize(windowSize);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.addKeyListener(inputListener);
        window.setFocusTraversalKeysEnabled(true);
        window.setContentPane(this);
        window.pack();
        window.createBufferStrategy(3);
        window.setVisible(true);

        physicEngine = new PhysicEngine(this);
        Scene scene = new PlayScene(this, "play");
        addScene(scene);
        activateScene("play");
    }

    private void activateScene(String sceneName) {
        this.activeScene = scenes.get(sceneName);
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
                default -> {
                    Log.error("Configuration|Unknown value %s=%s", e.getKey(), e.getValue());
                }
            }
        });
    }

    private void loop() {
        activeScene.create();
        long startTime = System.nanoTime();
        long endTime = startTime;
        long elapsed = 0;
        while (!exit) {
            elapsed = endTime - startTime;
            startTime = endTime;
            input(activeScene);
            physicEngine.update(activeScene, elapsed);
            render(activeScene);
            endTime = System.nanoTime();
        }
        dispose();
    }


    public void input(Scene scene) {
        activeScene.input(inputListener);
    }

    public void update(Scene scene, long elapsed) {
        physicEngine.update(scene, elapsed);
    }

    public void render(Scene scene) {
        BufferStrategy bf = window.getBufferStrategy();

        Graphics2D g = (Graphics2D) bf.getDrawGraphics();
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowSize.width, windowSize.height);

        // draw the scene
        scene.getEntities().values().forEach(e -> {
            g.setColor(e.getColor());
            g.fill(new Rectangle2D.Double(e.x, e.y, e.width, e.height));
        });

        g.dispose();

        bf.show();
    }

    private void dispose() {
        if (window != null && window.isEnabled() && window.isActive()) {
            window.dispose();
        }
        Log.info("End of application ");
    }

    public static void main(String[] argc) {
        Test001App app = new Test001App();
        app.run(argc);
    }

    public Dimension getWindowSize() {
        return windowSize;
    }
}
