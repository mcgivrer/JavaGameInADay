package behaviors;


import entity.Entity;

import java.awt.*;

/**
 * The Behavior interface defines a contract for implementing various behaviors
 * that can be applied to entities in a graphical application. It provides a
 * set of default methods which can be overridden to define custom behavior
 * during different stages of an entity's lifecycle and interaction within the
 * application.
 */
public interface Behavior {
    /**
     * Initializes the entity when the behavior is first applied. This method
     * can be overridden to set up initial states or configurations specific to
     * the behavior for the given entity.
     *
     * @param e the entity to initialize with this behavior
     */
    default void init(Entity e) {
    }

    /**
     * Creates or initializes additional properties or settings for the given
     * entity. This method is intended to be overridden to perform operations
     * that prepare the entity to interact within the graphical application,
     * potentially establishing default values, resources, or connections
     * needed by the entity.
     *
     * @param e the entity for which the create operation is performed
     */
    default void create(Entity e) {
    }

    /**
     * Processes input for the specified entity. This method can be overridden to
     * define custom input handling behavior for the entity within the application.
     *
     * @param e the entity for which input is being processed
     */
    default void input(Entity e) {
    }

    /**
     * Updates the state of the specified entity. This method can be overridden to
     * implement custom update logic for the entity, typically called during each
     * update cycle of the application to reflect changes or progression in the entity's state.
     *
     * @param e the entity for which the update operation is performed
     */
    default void update(Entity e) {
    }

    /**
     * Renders the specified entity onto the graphical context. This method can be
     * overridden to customize the visual representation of the entity within the
     * application.
     *
     * @param g the Graphics2D context used for drawing the entity
     * @param e the entity to be drawn on the graphical context
     */
    default void draw(Graphics2D g, Entity e) {
    }

    /**
     * Disposes of resources associated with the specified entity. This method can be
     * overridden to free any resources or perform cleanup operations needed when the
     * behavior is no longer applied to the entity.
     *
     * @param e the entity for which resources are being disposed
     */
    default void dispose(Entity e) {
    }

}
