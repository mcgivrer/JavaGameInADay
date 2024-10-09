package com.snapgames.demo.gfx;

import com.snapgames.demo.Game;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serializable;

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

        // draw a background grid.
        drawGrid(g, scene.getWorld(), 16, 16, new Color(0.2f, 0.2f, 0.2f));

        // draw the scene
        scene.getEntities().values().stream().filter(Entity::isActive).forEach(e -> {
            drawEntity(g, e);
        });

        g.dispose();
        bf.getDrawGraphics().drawImage(drawbuffer, 0, 0, app.getWindowSize().width, app.getWindowSize().height,
                0, 0, drawbuffer.getWidth(), drawbuffer.getHeight(), null);
        bf.show();
    }

    private void drawGrid(Graphics2D g, Rectangle2D windowSize, int tileW, int tileH, Color color) {
        g.setColor(color);
        for (int iy = 0; iy < windowSize.getWidth(); iy += tileH) {
            for (int ix = 0; ix < windowSize.getWidth(); ix += tileW) {
                g.drawRect(ix, iy, tileW, tileH);
            }
        }
    }

    public void drawEntity(Graphics2D g, Entity e) {
        g.setColor(e.getColor());
        g.fill(new Rectangle2D.Double(e.x, e.y, e.width, e.height));
    }

    public void dispose() {
        if (window != null && window.isEnabled() && window.isActive()) {
            window.dispose();
        }
    }
}