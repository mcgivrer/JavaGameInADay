package com.snapgames.framework.gfx;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.*;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.io.ResourceManager;
import com.snapgames.framework.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import static com.snapgames.framework.utils.I18n.getI18n;
import static com.snapgames.framework.utils.Log.error;

public class Renderer implements Serializable {
    private final Game app;

    private JFrame window;
    private BufferedImage drawbuffer;
    private Font debugFont;

    public Renderer(Game app, Dimension bufferSize) {
        this.app = app;
        drawbuffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB);
    }

    public void createWindow(String title, Dimension size) {
        newWindow(title, size, false);
        debugFont = window.getGraphics().getFont().deriveFont(9.0f);
    }

    public void newWindow(String title, Dimension size, boolean fullScreen) {
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
        }


        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Action to perform on exit request
                confirmExit();
            }
        });
    }

    private void confirmExit() {
        app.setPause(true);
        int response = JOptionPane.showConfirmDialog(window,
                getI18n("app.exit.confirm.message"),
                getI18n("app.exit.confirm.title"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            dispose(); // Ferme la fenêtre
        }
        app.setPause(false);
    }

    public void setInputListener(InputListener il) {
        window.addKeyListener(il);
    }

    public void render(Scene scene) {

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

                g.setColor(e.getFillColor());
                g.fill(new Rectangle2D.Double(e.x, e.y, e.width, e.height));
            }
            case "TextObject" -> {
                TextObject te = (TextObject) e;
                g.setColor(te.getColor());
                if (Optional.ofNullable(te.getFont()).isPresent()) {
                    g.setFont(te.getFont());
                }
                g.drawString(te.getText(), (int) e.x, (int) e.y);
            }
            case "GridObject" -> {
                GridObject go = (GridObject) e;
                drawGrid(g, scene.getWorld(), go.getTileWidth(), go.getTileHeight(), go.getColor());
            }

            default -> {
                error("Unknown object class %s", e.getClass());
            }
        }

        e.getBehaviors().forEach(b -> b.draw(g, e));
    }

    private void drawGrid(Graphics2D g, Rectangle2D playArea, int tileW, int tileH, Color color) {
        g.setColor(color);
        for (int iy = 0; iy < playArea.getHeight(); iy += tileH) {
            for (int ix = 0; ix < playArea.getWidth(); ix += tileW) {
                g.drawRect(ix, iy, tileW, (int) (iy + tileH < playArea.getHeight() ? tileH : tileH - (playArea.getHeight() - iy)));
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
}