package com.snapgames.demo.gfx;

import com.snapgames.demo.Game;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.entity.GameObject;
import com.snapgames.demo.entity.GridObject;
import com.snapgames.demo.entity.TextObject;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

import static com.snapgames.demo.utils.Log.error;

public class Renderer implements Serializable {
    private final Game app;


    private JFrame window;

    private BufferedImage drawbuffer;

    public Renderer(Game app, Dimension bufferSize) {
        this.app = app;
        drawbuffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB);
    }

    public void createWindow(String title, Dimension size) {
        window = new JFrame(title);
        window.setPreferredSize(size);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setFocusTraversalKeysEnabled(true);
        window.pack();
        window.createBufferStrategy(3);
        window.setVisible(true);
    }

    public void setInputListener(InputListener il) {
        window.addKeyListener(il);
    }


    public void render(Scene scene) {
        BufferStrategy bf = window.getBufferStrategy();

        Graphics2D g = (Graphics2D) drawbuffer.createGraphics();
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, drawbuffer.getWidth(), drawbuffer.getHeight());

        // draw the scene
        scene.getEntities().values().stream()
                .filter(Entity::isActive)
                .sorted(Comparator.comparingInt(Entity::getPriority))
                .forEach(e -> {
                    drawEntity(g, scene, e);
                });

        g.dispose();
        bf.getDrawGraphics().drawImage(drawbuffer, 0, 0, app.getWindowSize().width, app.getWindowSize().height,
                0, 0, drawbuffer.getWidth(), drawbuffer.getHeight(), null);
        bf.show();
    }


    public void drawEntity(Graphics2D g, Scene scene, Entity<?> e) {
        switch (e.getClass().getSimpleName()) {
            case "GameObject", "WorldArea" -> {

                g.setColor(e.getColor());
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

    private void drawGrid(Graphics2D g, Rectangle2D windowSize, int tileW, int tileH, Color color) {
        g.setColor(color);
        for (int iy = 0; iy < windowSize.getWidth(); iy += tileH) {
            for (int ix = 0; ix < windowSize.getWidth(); ix += tileW) {
                g.drawRect(ix, iy, tileW, tileH);
            }
        }
    }

    public void dispose() {
        if (window != null && window.isEnabled() && window.isActive()) {
            window.dispose();
        }
    }
}