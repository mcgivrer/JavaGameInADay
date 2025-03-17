package com.snapgames.framework.scene;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Camera;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.World;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Node;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The AbstractScene class is an abstract implementation of the Scene interface, providing
 * a foundational structure for managing entities, cameras, and world objects within a game scene.
 * It extends the Node class to enable hierarchical relationships between scenes.
 */
public abstract class AbstractScene extends Node<AbstractScene> implements Scene {
    protected final GameInterface app;
    protected Map<String, Entity<?>> entities = new ConcurrentHashMap<>();
    protected World world = new World("default");
    Config config;

    protected Camera activeCamera;

    /**
     * Constructs an AbstractScene instance with the specified GameInterface implementation
     * and scene name.
     *
     * @param app  the GameInterface instance that provides game state management and debugging functionalities.
     * @param name the name of the scene, used as an identifier and for hierarchical scene relationships.
     */
    public AbstractScene(GameInterface app, String name) {
        super(name);
        this.app = app;
    }

    public Map<String, Entity<?>> getEntities() {
        return entities;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World w) {
        this.world = w;
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

    public void create(Config config) {
        this.config = config;
    }

    ;

    public void dispose() {
        // end all behaviors.
        getEntities().values().forEach(e -> e.getBehaviors().forEach(b -> b.end(e)));
    }

    public void reset() {
        entities.clear();
        create(config);
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
