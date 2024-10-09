package com.snapgames.demo.scene;

import com.snapgames.demo.Game;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.PhysicType;
import com.snapgames.demo.physic.World;
import com.snapgames.demo.physic.WorldArea;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayScene extends AbstractScene {

    public PlayScene(Game app, String name) {
        super(app, name);
    }

    @Override
    public void create() {
        Dimension windowSize = app.getWindowSize();
        world = new World("earth", -0.981)
                .setSize(320, 200)
                .setPosition(0, 0);
        Entity player = new Entity("player")
                .setSize(16, 32)
                .setPosition(world.getWidth() * 0.5, world.getHeight() * 0.5)
                .setColor(Color.BLUE)
                .setMass(8)
                .setMaterial(new Material("player_mat", 1.0, 0.92, 0.66));
        add(player);

        generate("star_%d", world, 100, 1, 1,
                Color.WHITE, 100000000,
                Material.DEFAULT,
                PhysicType.STATIC);
        generate("ball_%d", world, 10, 20, 20,
                Color.RED, 5.0,
                new Material("ball_mat", 1.0, 0.7, 0.8),
                PhysicType.DYNAMIC);


        WorldArea area1 = (WorldArea) new WorldArea("water")
                .setColor(new Color(0.1f, 0.1f, 0.7f, 0.8f))
                .setSize(world.width, 64)
                .setPosition(0, world.height - 64)
                .addForce(0.02, -0.16);
        world.addArea(area1);
        add(area1);

    }

    private void generate(String tempateName, Rectangle2D windowSize,
                          int nb, double maxW, double maxH,
                          Color color,
                          double mass,
                          Material mat,
                          PhysicType pt) {
        for (int i = 0; i < nb; i++) {
            Entity star = new Entity(tempateName.formatted(i))
                    .setSize(maxW * Math.random(), maxH * Math.random())
                    .setPosition(windowSize.getWidth() * Math.random(), windowSize.getHeight() * Math.random())
                    .setColor(color)
                    .setMass(mass)
                    .setMaterial(mat)
                    .setPhysicType(pt);
            add(star);
        }
    }

    @Override
    public void input(InputListener inputListener) {
        double speed = 0.2;
        Entity player = getEntities().get("player");
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
}