package scenes;

import game.Game;
import entity.Entity;
import physic.World;

import java.awt.*;
import java.util.Collection;

/**
 * Represents a scene in a game application, consisting of multiple entities and various lifecycle methods
 * to be implemented or overridden by the concrete scene classes.
 */
public interface Scene {

    /**
     * Retrieves the name of the scene.
     *
     * @return the name of the scene as a String.
     */
    String getName();

    /**
     * Adds an entity to the scene.
     *
     * @param entity the entity to be added to the scene
     */
    // <1>
    void add(Entity entity);

    /**
     * Retrieves all entities present within the scene.
     *
     * @return a collection of entities that are part of the scene.
     */
    // <2>
    Collection<Entity> getEntities();

    /**
     * Retrieves an entity with the specified name from the scene.
     *
     * @param name the name of the entity to be retrieved
     * @return the entity with the given name, or null if no such entity exists
     */
    // <3>
    Entity getEntity(String name);

    /**
     * Initializes the scene with the given game context. This method is intended for setting up
     * any necessary configurations or resources prior to the scene being used in the game loop.
     * By default, it performs no actions and is meant to be overridden by classes implementing
     * the Scene interface to provide specific initialization logic.
     *
     * @param g the game context in which the scene is being initialized
     */
    // <4>
    default void initialize(Game g) {
    }

    /**
     * Creates and sets up the entities and initial configurations for the scene
     * within the given game context. By default, this method performs no operations
     * and is meant to be overridden by concrete scene classes to provide specific
     * entity creation and configuration logic.
     *
     * @param g the game context within which the scene entities are to be created
     */
    default void create(Game g) {
    }

    /**
     * Handles input processing for the scene. This method is intended to manage
     * the interactions within the scene based on user input and other game events.
     * By default, it performs no operations and is meant to be overridden by
     * scene implementations to provide specific input handling logic.
     *
     * @param g the game context within which the input processing is to be performed
     */
    // <5>
    default void input(Game g) {
    }

    /**
     * Updates the state or properties of the scene during the game loop. This method
     * is called periodically and should include logic to update the conditions of
     * entities and other elements within the scene. By default, this method performs
     * no actions and is meant to be overridden by scene implementations to provide
     * specific update behavior.
     *
     * @param g the game context providing the necessary information and resources
     *          for the scene update, such as entity states and game settings.
     */
    // <6>
    default void update(Game g) {
    }

    /**
     * Renders the current state of the scene onto the provided Graphics2D context.
     * This method should be overridden to provide specific rendering logic
     * for different scenes.
     *
     * @param game the game context providing information and resources required
     *             during rendering, such as configuration settings or asset management
     * @param g    the Graphics2D context on which the scene elements should be drawn,
     *             allowing manipulation of shapes, colors, images, and fonts for rendering
     */
    default void draw(Game game, Graphics2D g) {
    }

    /**
     * Disposes of the resources associated with the scene. This method is intended
     * for cleanup operations that need to be performed when the scene is no longer
     * needed or when it is being removed from the game loop. By default, this
     * method performs no actions and is meant to be overridden by classes
     * implementing the Scene interface to provide specific disposal logic.
     *
     * @param g the game context from which the scene resources are being disposed
     */
    // <7>
    default void dispose(Game g) {
    }

    /**
     * Retrieves the world associated with this scene.
     *
     * @return the world object representing the environment of the scene,
     *         or null if no world is configured.
     */
    default World getWorld() {
        return null;
    }

}
