package com.snapgames.demo.scene;

import com.snapgames.demo.Game;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.World;
import com.snapgames.demo.utils.Node;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScene extends Node<AbstractScene> implements Scene {
    protected final Game app;
    public Map<String, Entity<?>> entities = new ConcurrentHashMap<>();
    public World world = new World();

    public AbstractScene(Game app, String name) {
        super(name);
        this.app = app;
    }

    public Map<String, Entity<?>> getEntities() {
        return entities;
    }

    public World getWorld() {
        return world;
    }

    public void add(Entity<?> entity) {
        entities.put(entity.getName(), entity);
        super.add(entity);
    }

    public void input(InputListener inputListener) {

    }

    public void load() {
    }

    public abstract void create();

    public void dispose() {

    }
}
