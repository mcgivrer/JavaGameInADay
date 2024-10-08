package com.snapgames.demo.scene;

import com.snapgames.demo.Test001App;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.World;
import com.snapgames.demo.physic.WorldArea;

import java.awt.*;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Scene implements Serializable {
    private final Test001App test001App;
    public Map<String, Entity> entities = new ConcurrentHashMap<String, Entity>();
    private String name;

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public World world = new World();

    public World getWorld() {
        return world;
    }

    public Scene(Test001App test001App, String name) {
        this.test001App = test001App;
        this.name = name;
    }

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

    public void add(Entity entity) {
        entities.put(entity.getName(), entity);
    }

    public String getName() {
        return name;
    }
}