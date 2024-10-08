package com.snapgames.demo;

import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.World;
import com.snapgames.demo.physic.WorldArea;
import com.snapgames.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    private Map<String, Entity> entities = new ConcurrentHashMap<>();


    private InputListener inputListener;

    private World world = new World();

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
        create();
        long startTime = System.nanoTime();
        long endTime = startTime;
        long elapsed = 0;
        while (!exit) {
            elapsed = endTime - startTime;
            startTime = endTime;
            input();
            update(elapsed);
            render();
            endTime = System.nanoTime();
        }
        dispose();
    }

    private void create() {
        world = new World("earth", -9.81).setSize(620, 360).setPosition(10, 20);
        Entity player = new Entity("player")
                .setSize(16, 32)
                .setPosition(windowSize.getWidth() * 0.5, windowSize.getHeight() * 0.5)
                .setColor(Color.BLUE)
                .setMass(80)
                .setMaterial(new Material("player_mat", 1.0, 0.998, 0.1));
        add(player);
        WorldArea area1 = (WorldArea) new WorldArea("water")
                .setColor(new Color(0.2f, 0.1f, 0.7f, 0.7f))
                .setSize(world.width, 40)
                .setPosition(0, world.height - 40);
        world.addArea(area1);
        add(area1);
    }

    private void add(Entity entity) {
        entities.put(entity.getName(), entity);
    }


    public void input() {
        double speed = 120.0;
        Entity player = entities.get("player");
        if (inputListener.isKeyPressed(KeyEvent.VK_UP)) {
            player.addForce(0.0, -speed * 2);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_DOWN)) {
            player.addForce(0.0, speed);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            player.addForce(-speed, 0.0);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.addForce(speed, 0.0);
        }
    }

    public void update(long elapsed) {
        entities.values().forEach(e -> {
            applyWorldEffects(world, e);
            applyPhysicRules(elapsed, e);
            keepEntityIntoWorld(world, e);
        });
    }

    private void applyWorldEffects(World w, Entity e) {
        world.getAreas().forEach(a -> {
            if (a.contains(e)) {
                e.getForces().addAll(a.getForces());
            }
        });
    }

    private void applyPhysicRules(long elapsed, Entity e) {
        e.addForce(0.0, -world.getGravity());
        e.getForces().forEach(f -> {
            e.ax += f.getX();
            e.ay += f.getY();
        });

        e.dx = 0.5 * e.ax / e.getMass();
        e.dy = 0.5 * e.ay / e.getMass();

        e.x += e.dx * (elapsed * 0.0000001);
        e.y += e.dy * (elapsed * 0.0000001);

        e.dx *= e.material.friction;
        e.dy *= e.material.friction;

        e.dx = Math.signum(e.dx) * Math.min(Math.abs(e.dx), 8.0);
        e.dy = Math.signum(e.dy) * Math.min(Math.abs(e.dy), 8.0);

        e.ax = 0.0;
        e.ay = 0.0;
        e.getForces().clear();
    }

    private void keepEntityIntoWorld(World w, Entity e) {
        if (!world.contains(e)) {
            Rectangle2D penetration = world.createIntersection(e);
            if (e.x < w.x) {
                e.x = w.x;
                e.dx = -e.material.elasticity * e.dx;
            }
            if (e.x + e.width > w.width) {
                e.x = w.width - e.width;
                e.dx = -e.material.elasticity * e.dx;
            }
            if (e.y < w.y) {
                e.y = w.y;
                e.dy = -e.material.elasticity * e.dy;
            }
            if (e.y + e.height > w.height) {
                e.y = w.height - e.height;
                e.dy = -e.material.elasticity * e.dy;
            }

        }
    }

    public void render() {
        BufferStrategy bf = window.getBufferStrategy();

        Graphics2D g = (Graphics2D) bf.getDrawGraphics();
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowSize.width, windowSize.height);

        // draw the scene
        entities.values().forEach(e -> {
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

}
