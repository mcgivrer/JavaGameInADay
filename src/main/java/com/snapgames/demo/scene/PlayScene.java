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
    private final Test001App test001App;
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

    public PlayScene(Test001App test001App, String name) {
        this.test001App = test001App;
        this.name = name;
    }

    @Override
    public void create() {
        world = new World("earth", -9.81).setSize(620, 360).setPosition(10, 20);
        Entity player = new Entity("player")
                .setSize(16, 32)
                .setPosition(test001App.getWindowSize().getWidth() * 0.5, test001App.getWindowSize().getHeight() * 0.5)
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
        double speed = 120.0;
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