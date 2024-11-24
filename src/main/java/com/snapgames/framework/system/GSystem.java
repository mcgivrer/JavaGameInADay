package com.snapgames.framework.system;

import com.snapgames.framework.GameInterface;

import java.util.Collection;
import java.util.Map;

/**
 * The GSystem interface defines the lifecycle and dependency management
 * methods for a game system.
 */
public interface GSystem {

    /**
     * Retrieves the collection of system dependencies.
     *
     * @return a collection of classes that this system depends on.
     */
    Collection<Class<?>> getDependencies();

    /**
     * Initializes the game system with the provided game instance.
     *
     * @param game the game instance to initialize the system with
     */
    void initialize(GameInterface game);

    /**
     * Starts the game system using the provided game instance.
     *
     * @param game the game instance used to start the system
     */
    void start(GameInterface game);


    /**
     * Performs any necessary preprocessing before the main processing step of the game system.
     *
     * @param game the game instance on which to perform preprocessing
     */
    default void preProcess(GameInterface game) {
    }

    /**
     * Processes the game logic for a specific game system.
     *
     * @param game    the game instance on which processing is performed
     * @param elapsed the time elapsed since the last frame or update
     * @param stats   a map containing various statistics and metrics for the game
     */
    void process(GameInterface game, double elapsed, Map<String, Object> stats);

    /**
     * Executes any necessary post-processing tasks after the main processing
     * step of the game system.
     *
     * @param game the game instance on which to perform post-processing
     */
    default void postProcess(GameInterface game) {
    }

    /**
     * Stops the game system associated with the provided game instance.
     *
     * @param game the game instance to stop the system for
     */
    void stop(GameInterface game);

    /**
     * Disposes of the resources and performs cleanup for the game system
     * associated with the provided game instance.
     *
     * @param game the game instance for which the system resources are to be disposed
     */
    void dispose(GameInterface game);
}
