package com.snapgames.framework.scene;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Camera;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.World;
import com.snapgames.framework.utils.Node;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScene extends Node<AbstractScene> implements Scene {
    protected final Game app;
    protected Map<String, Entity<?>> entities = new ConcurrentHashMap<>();
    protected World world = new World("default");

    protected Camera activeCamera;

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

    public void setActiveCamera(Camera cam) {
        activeCamera = (Camera) entities.values().stream().filter(c -> c.equals(cam)).findFirst().get();
    }


    public Camera getActiveCamera() {
        return activeCamera;
    }

    public List<Camera> getCameras() {
        return entities.values().stream().filter(c -> c instanceof Camera).map(entity -> (Camera) entity).toList();
    }


    public void input(InputListener inputListener) {

    }

    public void load() {
    }

    public abstract void create();

    public void dispose() {
        // end all behaviors.
        getEntities().values().forEach(e -> e.getBehaviors().forEach(b -> b.end(e)));
    }

    public void reset() {
        entities.clear();
        create();
    }

    @Override
    public String toString() {
        return "AbstractScene{" +
                "id=" + id +
                ", name='" + name +
                ", activeCamera=" + activeCamera +
                ", world=" + world +
                '}';
    }
}
