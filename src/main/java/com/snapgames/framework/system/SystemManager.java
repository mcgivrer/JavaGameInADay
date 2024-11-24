package com.snapgames.framework.system;

import com.snapgames.framework.Game;
import com.snapgames.framework.utils.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.snapgames.framework.utils.Log.debug;

/**
 * The SystemManager class is responsible for managing game systems, including their
 * initialization, starting, processing, and disposal. It maintains a collection of
 * systems and ensures they are executed in the correct order, respecting dependencies.
 */
public class SystemManager {
    /**
     * Singleton instance of SystemManager.
     * This static instance ensures that only one instance of SystemManager exists throughout the application.
     */
    private static SystemManager instance = new SystemManager();
    /**
     * The parent Game instance used by the SystemManager to manage and interact
     * with various game systems.
     */
    private static Game parent;
    /**
     * A thread-safe map that holds references to all the registered game systems.
     * The map's keys are the classes of the game systems, and the values are the
     * instances of the corresponding game systems.
     * <p>
     * This variable is used to manage and interact with various game systems
     * in the game framework, allowing for lifecycle management, dependency
     * resolution, and execution of game systems in a concurrent environment.
     */
    private static final Map<Class<? extends GSystem>, GSystem> systems = new ConcurrentHashMap<>();
    /**
     * A ConcurrentHashMap that holds various statistics and metrics for the SystemManager.
     * The keys are statistic names, while the values are the corresponding metrics.
     */
    private static Map<String, Object> stats = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent instantiation from other classes.
     * Initializes the SystemManager and logs a startup message.
     */
    private SystemManager() {
        Log.info(SystemManager.class,"Start SystemManager");
    }

    /**
     * Sets the parent game instance for the SystemManager.
     *
     * @param game the Game instance to be set as the parent
     */
    public static void setParent(Game game) {
        parent = game;
    }

    /**
     * Adds a game system to the system manager. This method registers the system
     * so it can be managed and executed in conjunction with other systems.
     *
     * @param system the GSystem instance to be added to the system manager
     */
    public static void add(GSystem system) {
        systems.put(system.getClass(), system);
    }

    /**
     * Retrieves the instance of a game system associated with the specified class.
     *
     * @param className the class of the system to retrieve
     * @param <T>       the type of the system extending GSystem
     * @return the instance of the system associated with the specified class, or null if no system is found
     */
    public static <T extends GSystem> T get(Class<?> className) {
        return (T) systems.get(className);
    }

    /**
     * Disposes of all the registered game systems managed by the SystemManager.
     * This method iterates over all the game systems within the manager and calls
     * their respective {@code dispose} methods, passing the parent game instance.
     */
    public static void dispose() {
        systems.values().forEach(s -> s.dispose(parent));
    }

    /**
     * Processes the game logic for all registered systems using the specified elapsed time.
     * The systems are processed in a specific order based on their dependencies.
     *
     * @param elapsed The time elapsed since the last frame or update.
     */
    public static void process(double elapsed) {
        stats.put("elapsed", elapsed);
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> {
                    s.process(parent, elapsed, stats);
                });
    }

    /**
     * Executes the post-processing step for all registered game systems in the system manager.
     * <p>
     * This method sorts the systems based on their dependencies and then calls the
     * postProcess method on each system. Systems with dependencies are processed
     * before the systems they depend on.
     */
    public static void postProcess() {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.postProcess(parent));
    }

    /**
     * Initializes all registered game systems in the SystemManager.
     * <p>
     * This method sorts the game systems based on their dependencies and initializes them
     * in an order where systems with dependencies are initialized before the systems they
     * depend on. The initialization is performed using the parent game instance.
     */
    public static void initialize() {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.initialize(parent));
    }

    /**
     * Starts all registered game systems in the SystemManager.
     * <p>
     * This method sorts the game systems based on their dependencies and starts them
     * in an order where systems with dependencies are started before the systems they
     * depend on. The starting process is performed using the provided game instance.
     *
     * @param game the Game instance used to start the systems
     */
    public static void start(Game game) {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.start(parent));
    }

    /**
     * Retrieves a map containing various statistics and metrics.
     *
     * @return a map where the keys are statistic names and the values are
     * the corresponding metrics.
     */
    public Map<String, Object> getStats() {
        return stats;
    }
}
