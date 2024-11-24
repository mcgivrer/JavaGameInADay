package com.snapgames.framework.gfx;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.*;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.io.ResourceManager;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.snapgames.framework.utils.I18n.getI18n;
import static com.snapgames.framework.utils.Log.error;

public class Renderer implements GSystem, Serializable {
    private final Game app;

    private JFrame window;
    private BufferedImage drawbuffer;
    private Font debugFont;

    private boolean fullScreen = false;

    public Renderer(Game app) {
        this.app = app;
    }

    private void createWindow(String title, Dimension size) {
        newWindow(title, size, false);
        debugFont = window.getGraphics().getFont().deriveFont(9.0f);
    }

    private void newWindow(String title, Dimension size, boolean fullScreen) {
        KeyListener il = null;
        if (window != null && window.isActive()) {
            il = window.getKeyListeners()[0];
            window.dispose();
        }
        window = new JFrame(title);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setFocusTraversalKeysEnabled(true);
        window.setIconImage(ResourceManager.get("/assets/images/thor-hammer.png"));
        if (fullScreen) {
            window.setUndecorated(fullScreen);
            if (il != null) {
                window.addKeyListener(il);
            }
        } else {
            window.setPreferredSize(size);
        }
        window.pack();
        window.createBufferStrategy(3);
        window.setVisible(true);
        if (fullScreen) {
            window.setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            window.setExtendedState(Frame.NORMAL);
        }


        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Action to perform on exit request
                app.requestExit();
            }
        });
    }

    public void setInputListener(InputListener il) {
        window.addKeyListener(il);
    }

    private void render(Scene scene) {

        Graphics2D g = (Graphics2D) drawbuffer.createGraphics();
        g.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g.setRenderingHints(Map.of(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, drawbuffer.getWidth(), drawbuffer.getHeight());

        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            g.translate(-scene.getActiveCamera().x, -scene.getActiveCamera().y);
        }
        // draw the scene
        scene.getEntities().values().stream()
            .filter(e -> !(e instanceof Camera))
            .filter(Entity::isActive)
            .filter(e -> e.getCameraIsStickedTo() == null)
            .sorted(Comparator.comparingInt(Entity::getPriority))
            .forEach(e -> {
                drawEntity(g, scene, e);
                if (app.isDebugGreaterThan(0)) {
                    drawDebugInfoEntity(g, scene, e);
                }
            });

        // draw World borders
        g.setColor(Color.DARK_GRAY);
        g.draw(scene.getWorld());

        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            g.translate(scene.getActiveCamera().x, scene.getActiveCamera().y);
        }
        // draw all entities fixed to the active Camera.
        scene.getEntities().values().stream()
            .filter(e -> !(e instanceof Camera))
            .filter(Entity::isActive)
            .filter(e -> e.getCameraIsStickedTo() != null && e.getCameraIsStickedTo().equals(scene.getActiveCamera()))
            .sorted(Comparator.comparingInt(Entity::getPriority))
            .forEach(e -> {
                drawEntity(g, scene, e);
            });

        g.dispose();

        // copy buffer to window.
        if (window != null) {
            BufferStrategy bf = window.getBufferStrategy();
            if (bf != null) {
                bf.getDrawGraphics().drawImage(drawbuffer, 0, 0, window.getWidth(), window.getHeight(),
                    0, 0, drawbuffer.getWidth(), drawbuffer.getHeight(), null);
                if (!bf.contentsLost()) {
                    bf.show();
                }
            }
        }
    }

    private void drawDebugInfoEntity(Graphics2D g, Scene scene, Entity<?> e) {
        g.setColor(Color.ORANGE);
        g.draw(e);
        g.setFont(debugFont);
        g.drawString("#%d:%s".formatted(e.getId(), e.getName()), (int) (e.getX() + e.getWidth() + 4), (int) e.getY());
        // draw velocity vector
        drawVector(g, (e.x + (e.width * 0.5)), (e.y + (e.height * 0.5)), e.dx * 70, e.dy * 70, Color.CYAN);
        // draw acceleration vector
        drawVector(g, (e.x + (e.width * 0.5)), (e.y + (e.height * 0.5)), e.ax * 40, e.ay * 40, Color.RED);
        // draw forces vector
        e.getForces().forEach(f -> {
            drawVector(g, (e.x + (e.width * 0.5)), (e.y + (e.height * 0.5)), f.getX() * 40, f.getY() * 40, Color.YELLOW);
        });
    }

    private void drawVector(Graphics2D g, double x, double y, double dx, double dy, Color c) {
        g.setColor(c);
        g.drawLine(
            (int) x, (int) y,
            (int) (x + dx), (int) (y + dy));

    }

    public void drawEntity(Graphics2D g, Scene scene, Entity<?> e) {
        switch (e.getClass().getSimpleName()) {
            case "GameObject", "WorldArea" -> {
                drawObject(g, e);
            }
            case "TextObject" -> {
                drawText(g, (TextObject) e);
            }
            case "GridObject" -> {
                drawGrid(g, scene, (GridObject) e);
            }
            case "GaugeObject" -> {
                drawGauge(g, (GaugeObject) e);
            }
            default -> {
                error("Unknown object class %s", e.getClass());
            }
        }
        e.getBehaviors().forEach(b -> b.draw(g, e));
    }

    private static void drawObject(Graphics2D g, Entity<?> e) {
        if (e.getFillColor() != null) {
            g.setColor(e.getFillColor());
            g.fill(e);
        }
    }

    private static void drawText(Graphics2D g, TextObject te) {
        g.setColor(te.getColor());
        if (Optional.ofNullable(te.getFont()).isPresent()) {
            g.setFont(te.getFont());
        }
        g.drawString(te.getText(), (int) te.x, (int) te.y);
    }

    private void drawGauge(Graphics2D g, GaugeObject gg) {
        g.setColor(Color.BLACK);
        g.drawRect((int) gg.getX(), (int) gg.getY(), (int) gg.getWidth(), (int) gg.getHeight());
        g.drawRect((int) gg.getX() + 2, (int) gg.getY() + 2, (int) gg.getWidth() - 4, (int) gg.getHeight() - 4);
        g.setColor(gg.getColor());
        g.drawRect((int) gg.getX() + 1, (int) gg.getY() + 1, (int) gg.getWidth() - 2, (int) gg.getHeight() - 2);
        g.setColor(gg.getFillColor());
        g.fillRect(
            (int) gg.getX() + 3, (int) gg.getY() + 3,
            (int) (gg.getWidth() - 5 * ((gg.getMaxValue() - gg.getMinValue()) / gg.getValue())), (int) gg.getHeight() - 5);
    }

    private void drawGrid(Graphics2D g, Scene scene, GridObject go) {
        g.setColor(go.getColor());
        for (int iy = 0; iy < scene.getWorld().getHeight(); iy += go.getTileWidth()) {
            for (int ix = 0; ix < scene.getWorld().getWidth(); ix += go.getTileWidth()) {
                g.drawRect(ix, iy, go.getTileWidth(), (int) (iy + go.getTileHeight() < scene.getWorld().getHeight()
                    ? go.getTileHeight()
                    : go.getTileHeight() - (scene.getWorld().getHeight() - iy)));
            }
        }
    }

    public void dispose() {
        if (window != null && window.isEnabled() && window.isActive()) {
            window.dispose();
        }
    }

    public JFrame getWindow() {
        return window;
    }

    @Override
    public Collection<Class<?>> getDependencies() {
        return List.of(Config.class, SceneManager.class, PhysicEngine.class, InputListener.class);
    }

    @Override
    public void initialize(GameInterface game) {
        Config config = SystemManager.get(Config.class);
        Dimension bufferSize = config.get("app.render.buffer.size");
        drawbuffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB);

        Dimension windowSize = config.get("app.render.window.size");
        String title = config.get("app.render.window.title");
        createWindow(title, windowSize);

        InputListener inputListener = SystemManager.get(InputListener.class);
        setInputListener(inputListener);
    }

    public void switchFullScreenMode() {
        app.setPause(true);
        fullScreen = !fullScreen;
        JFrame w = getWindow();
        Dimension dim = w.getSize();
        String title = w.getTitle();
        newWindow(title, dim, fullScreen);
        app.setPause(false);
    }

    @Override
    public void start(GameInterface game) {

    }

    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        SceneManager sm = SystemManager.get(SceneManager.class);
        render(sm.getActiveScene());
    }

    @Override
    public void postProcess(GameInterface game) {
    }

    @Override
    public void stop(GameInterface game) {

    }

    @Override
    public void dispose(GameInterface game) {
        dispose();
    }
}