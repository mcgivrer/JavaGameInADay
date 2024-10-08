package com.snapgames.demo;

import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.World;
import com.snapgames.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
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
public class Test001App extends JPanel implements KeyListener {
    private final ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private final Properties config = new Properties();

    private JFrame window;

    private static boolean exit = false;
    private boolean[] keys = new boolean[1024];
    private String title = "Test001";
    private Dimension windowSize = new Dimension(640, 400);

    private Map<String, Entity> entities = new ConcurrentHashMap<>();

    private World world = new World("earth", -0.981).setSize(620, 360).setPosition(10, 20);

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


        window = new JFrame(title);
        window.setPreferredSize(windowSize);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.addKeyListener(this);
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
        Entity player = new Entity("player")
                .setSize(16, 16)
                .setPosition(windowSize.getWidth() * 0.5, windowSize.getHeight() * 0.5)
                .setColor(Color.BLUE)
                .setMass(10)
                .setMaterial(new Material("player_mat", 1.0, 0.998, 0.1));
        add(player);
    }

    private void add(Entity entity) {
        entities.put(entity.getName(), entity);
    }


    private void input() {
        Entity player = entities.get("player");
        if (isKeyPressed(KeyEvent.VK_UP)) {
            player.dy = -2.0;
        }
        if (isKeyPressed(KeyEvent.VK_DOWN)) {
            player.dy = 2.0;
        }
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            player.dx = -2.0;
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.dx = 2.0;
        }
    }

    private void update(long elapsed) {
        entities.values().forEach(e -> {
            e.x += e.dx * (elapsed * 0.0000001);
            e.y += (e.dy - (world.getGravity()*(e.getMass()))) * (elapsed * 0.0000001);

            keepEntityIntoWorld(world, e);

            e.dx *= e.material.friction;
            e.dy *= e.material.friction;
        });
    }

    private void keepEntityIntoWorld(World w, Entity e) {
        if (!world.contains(e)) {
            if (e.x > w.width) {
                e.x = w.width - e.width;
                e.dy = -e.material.elasticity * e.dy;
            }
            if (e.y > w.height) {
                e.y = w.height - e.height;
                e.dy = -e.material.elasticity * e.dy;
            }
            if (e.x < w.x) {
                e.x = w.x;
                e.dx = -e.material.elasticity * e.dx;
            }
            if (e.y < w.y) {
                e.y = w.y;
                e.dy = -e.material.elasticity * e.dy;
            }
        }
    }

    private void render() {
        BufferStrategy bf = window.getBufferStrategy();

        Graphics2D g = (Graphics2D) bf.getDrawGraphics();
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowSize.width, windowSize.height);

        // draw the scene
        entities.values().forEach(e -> {
            g.setColor(e.getColor());
            g.fill(new Ellipse2D.Double(e.x, e.y, e.width, e.height));
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


    private boolean isKeyPressed(int keyCode) {
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
            exit = true;
        }
        keys[e.getKeyCode()] = false;
    }
}
