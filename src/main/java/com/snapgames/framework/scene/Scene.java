package com.snapgames.framework.scene;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Camera;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.World;
import com.snapgames.framework.utils.Config;

import java.util.List;
import java.util.Map;

/**
 * The Scene interface represents a fundamental structure for managing the components
 * of a game environment, including entities, world, cameras, and input interactions.
 * It provides a contract for handling initialization, updates, and cleanup of a game scene.
 */
public interface Scene {
    /**
     * Retrieves a map of entities associated with this scene. The map contains
     * key-value pairs where the key is a unique string identifier for each entity,
     * and the value is the corresponding {@link Entity} object.
     *
     * @return a map of entities in this scene, with their string identifiers as keys
     *         and their respective {@link Entity} objects as values.
     */
    Map<String, Entity<?>> getEntities();

    /**
     * Retrieves the {@link World} object associated with this Scene.
     * The World represents the physical environment and spatial configuration
     * of the scene, including properties like gravity and spatial boundaries.
     *
     * @return the {@link World} object representing the physical environment
     *         of the scene.
     */
    World getWorld();

    /**
     * Initializes and sets up the scene using the provided configuration settings.
     *
     * @param config the configuration object containing key-value pairs used
     *               for setting up the scene. This object may include parameters
     *               such as window size, render options, physics settings, and
     *               default scene preferences.
     */
    void create(Config config);

    /**
     * Adds the specified entity to the scene. This method integrates the entity
     * into the scene's management system, enabling it to interact within the
     * scene's environment and participate in updates.
     *
     * @param entity the {@link Entity} to be added to the scene. This object
     *               contains properties such as position, velocity, acceleration,
     *               and behaviors that determine its interaction within the scene.
     */
    void add(Entity<?> entity);

    /**
     * Retrieves the name of the scene.
     *
     * @return the name of the scene as a String, which serves as a unique identifier
     *         for the scene within the application.
     */
    String getName();

    /**
     * Processes and handles input events for the scene using the specified input listener.
     * This method allows the scene to react to user input such as keyboard, mouse,
     * or other input device interactions captured by the provided {@code InputListener}.
     *
     * @param inputListener the {@link InputListener} instance responsible for capturing
     *                       and managing input events to be processed by the scene.
     */
    void input(InputListener inputListener);

    /**
     * Releases all resources used by the scene and performs necessary cleanup.
     * This method is typically invoked before the scene is discarded or replaced
     * to ensure all allocated resources, such as memory, textures, or other assets,
     * are properly released and do not cause memory leaks.
     *
     * It also ensures that all active entities and other components within the scene
     * are disposed or reset to avoid unintended behaviors.
     */
    void dispose();

    /**
     * Loads the scene, preparing it for use by initializing the necessary resources and
     * setting up required entities or configurations. This method ensures that all
     * dependencies and assets needed for the scene to function properly are ready.
     *
     * Commonly called before activating or rendering the scene to ensure it is
     * fully operational.
     */
    void load();

    /**
     * Sets the active camera for the scene.
     * This method updates the currently active {@link Camera} that will be used
     * to render the scene and calculate the view transformation.
     *
     * @param cam the {@link Camera} to be activated for this scene. The specified camera
     *            determines the viewport and target settings for rendering within
     *            the scene.
     */
    void setActiveCamera(Camera cam);

    /**
     * Retrieves a list of cameras associated with the scene.
     * Each camera in the list represents a viewport through which
     * the scene can be rendered or displayed.
     *
     * @return a list of {@link Camera} objects present in the scene.
     */
    List<Camera> getCameras();

    /**
     * Retrieves the currently active {@link Camera} for the scene.
     * The active camera is used to render the scene and determines the view transformation.
     *
     * @return the {@link Camera} object that is currently active for the scene.
     */
    Camera getActiveCamera();

    /**
     * Resets the scene to its initial state.
     * This method clears any existing state, entities, and settings associated
     * with the scene, effectively preparing it for reuse or reinitialization.
     * It ensures that all components, resources, and configurations are returned
     * to their default values or disposed of as needed.
     */
    void reset();

    /**
     * Executes processing logic for the scene using the provided game interface and elapsed time.
     * This method typically performs updates to the scene's state, entities, and components
     * based on the elapsed time and the current game state.
     *
     * @param game   the {@link GameInterface} instance representing the game context. It provides
     *               methods to interact with the game's state, such as checking pause status or
     *               debugging features.
     * @param elapsed the time elapsed since the last frame or update, in seconds. This value is used
     *                to calculate proportional updates to entities and other time-dependent processes
     *                within the scene.
     */
    default void process(GameInterface game, double elapsed) {
    }
}
