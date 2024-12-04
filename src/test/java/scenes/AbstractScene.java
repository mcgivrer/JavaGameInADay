package scenes;

import entity.Entity;
import utils.Node;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractScene extends Node<Scene> implements Scene {

    public AbstractScene(String name) {
        super(name);
    }

    // <1>
    public void add(Entity entity) {
        super.add(entity);
    }

    // <2>
    public Collection<Entity> getEntities() {
        return getChildren().stream()
                .filter(Entity.class::isInstance) // Filtrer les objets de type Entity
                .map(Entity.class::cast)         // Les convertir en Entity
                .collect(Collectors.toList());
    }

    // <3>
    public Entity getEntity(String name) {
        return (Entity) getChildren().stream().filter(c -> c.getName().equals(name)).findFirst().get();
    }

}
