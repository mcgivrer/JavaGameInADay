package com.snapgames.demo.scene;

import com.snapgames.demo.Game;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.World;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScene implements Scene {
    protected final Game app;
    public Map<String, Entity> entities = new ConcurrentHashMap<String, Entity>();
    public World world = new World();
    protected String name;

    public AbstractScene(Game app, String name) {
        this.app = app;
        this.name = name;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public World getWorld() {
        return world;
    }

    public void add(Entity entity) {
        entities.put(entity.getName(), entity);
    }

    public String getName() {
        return name;
    }

    public void input(InputListener inputListener) {

    }

    public void dispose() {

    }
}
