package com.snapgames.demo.gfx;

import com.snapgames.demo.Test001App;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.io.Serializable;

public class Renderer implements Serializable {
    private final Test001App app;


    private JFrame window;

    public Renderer(Test001App app) {
        this.app = app;
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

        Graphics2D g = (Graphics2D) bf.getDrawGraphics();
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, app.getWindowSize().width, app.getWindowSize().height);

        // draw the scene
        scene.getEntities().values().stream().filter(Entity::isActive).forEach(e -> {
            drawEntity(g, e);
        });

        g.dispose();

        bf.show();
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