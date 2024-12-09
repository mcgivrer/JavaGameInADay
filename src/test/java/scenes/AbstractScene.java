package scenes;

import entity.Entity;
import physic.World;
import utils.Node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An abstract class representing a scene in a game, extending the Node class and implementing the Scene interface.
 * This class provides basic functionality for managing entities within a scene. Concrete scene implementations should
 * extend this class to leverage the basic entity management operations provided here.
 */
public abstract class AbstractScene extends Node<Scene> implements Scene {

    /**
     * Represents the physical environment within a scene, defined by gravity and a play area.
     * This instance is primarily used to manage physics-related simulations, such as object movement
     * influenced by gravity within the designated play space. The gravity is set to a downward vector,
     * simulating real-world gravity, while the play area confines the movement and interactions of entities.
     */
    protected World world = new World(new Point2D.Double(0, -0.981), new Rectangle2D.Double(0, 0, 640, 200));

    /**
     * Constructs an instance of AbstractScene with the specified name.
     *
     * @param name the name of the scene to be created
     */
    public AbstractScene(String name) {
        super(name);
    }

    /**
     * Adds an entity to the scene. This method delegates the addition of the
     * entity to the superclass to ensure that the entity is added to the
     * hierarchy managed by the superclass.
     *
     * @param entity the entity to be added to the scene
     */
    // <1>
    public void add(Entity entity) {
        super.add(entity);
    }

    /**
     * Retrieves a collection of all entities present in the scene.
     *
     * @return a collection of entities contained in the scene
     */
    // <2>
    public Collection<Entity> getEntities() {
        return getChildren().stream()
                .filter(Entity.class::isInstance) // Filtrer les objets de type Entity
                .map(Entity.class::cast)         // Les convertir en Entity
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an entity by its specified name from the children of the scene.
     *
     * @param name the name of the entity to be retrieved
     * @return the entity with the specified name, if present
     */
    // <3>
    public Entity getEntity(String name) {
        return (Entity) getChildren().stream().filter(c -> c.getName().equals(name)).findFirst().get();
    }

    /**
     * Retrieves the World object associated with the current scene.
     *
     * @return the World instance representing the physical environment of the scene
     * including its gravity and play area settings.
     */
    public World getWorld(){
        return this.world;
    }
}
