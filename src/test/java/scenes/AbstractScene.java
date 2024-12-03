package scenes;

import entity.Entity;
import utils.Node;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScene extends Node<Scene> implements Scene {

    private String name = "";
    private Map<String, Entity> entities = new ConcurrentHashMap<>();

    public AbstractScene(String name) {
        this.name = name;
    }

    // <1>
    public void add(Entity entity) {
        this.entities.put(entity.getName(), entity);
    }

    // <2>
    public Collection<Entity> getEntities() {
        return entities.values();
    }

    // <3>
    public Entity getEntity(String name) {
        return entities.get(name);
    }

}
