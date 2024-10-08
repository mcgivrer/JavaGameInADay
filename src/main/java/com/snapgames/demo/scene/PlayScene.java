package com.snapgames.demo.scene;

import com.snapgames.demo.Test001App;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.World;
import com.snapgames.demo.physic.WorldArea;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayScene implements Scene {
    private final Test001App app;
    public Map<String, Entity> entities = new ConcurrentHashMap<String, Entity>();
    private String name;

    @Override
    public Map<String, Entity> getEntities() {
        return entities;
    }

    public World world = new World();

    @Override
    public World getWorld() {
        return world;
    }

    public PlayScene(Test001App app, String name) {
        this.app = app;
        this.name = name;
    }

    @Override
    public void create() {
        Dimension windowSize = app.getWindowSize();
        world = new World("earth", -9.81).setSize(640, 400).setPosition(0, 0);
        Entity player = new Entity("player")
                .setSize(16, 32)
                .setPosition(windowSize.getWidth() * 0.5, windowSize.getHeight() * 0.5)
                .setColor(Color.BLUE)
                .setMass(8)
                .setMaterial(new Material("player_mat", 1.0, 0.998, 0.76));
        add(player);
        generate("star_%d", windowSize, 100, 4, 4);
        WorldArea area1 = (WorldArea) new WorldArea("water")
                .setColor(new Color(0.2f, 0.1f, 0.7f, 0.7f))
                .setSize(world.width, 40)
                .setPosition(0, world.height - 40)
                .addForce(0.2, -6);
        world.addArea(area1);
        add(area1);
    }

    private void generate(String tempateName, Dimension windowSize, int nb, double maxW, double maxH) {
        Material starMat = new Material("star_mat", 1.0, 1.0, 1.0);
        for (int i = 0; i < nb; i++) {
            Entity star = new Entity(tempateName.formatted(i))
                    .setSize(Math.random() * maxW, Math.random() * maxH)
                    .setPosition(windowSize.getWidth() * Math.random(), windowSize.getHeight() * Math.random())
                    .setColor(Color.WHITE)
                    .setMass(0.01)
                    .setMaterial(starMat);
            add(star);
        }
    }

    @Override
    public void add(Entity entity) {
        entities.put(entity.getName(), entity);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void input(InputListener inputListener) {
        double speed = 60.0;
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

    @Override
    public void dispose() {

    }


}