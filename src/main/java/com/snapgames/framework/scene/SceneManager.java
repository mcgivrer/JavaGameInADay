package com.snapgames.framework.scene;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Log;
import com.snapgames.framework.utils.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.snapgames.framework.utils.Log.debug;

/**
 * Manages the lifecycle, switching, and caching of game scenes. The SceneManager
 * is responsible for initializing, loading, and disposing scenes within a game.
 * It also interacts with the provided GameInterface to ensure scene transitions
 * are smoothly managed.
 */
public class SceneManager implements GSystem {

    private final GameInterface game;
    // Scene Management
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene activeScene;
    private String defaultSceneName;

    /**
     * Constructs a new instance of the SceneManager class, initializing it with the provided
     * GameInterface application. This serves as the entry point for managing scenes within
     * the game, reading the required configurations, and setting up the initial state.
     *
     * @param app the GameInterface instance representing the game application, which provides
     *            the necessary context and configuration for initializing the SceneManager.
     */
    public SceneManager(GameInterface app) {
        this.game = app;
        initialize();
    }

    /**
     * Read all the scenes available in the configuration 'app.scene.list' config key,
     * create corresponding instances and cache these in the internal map.
     */
    private void initialize() {
    }

    /**
     * Create a {@link Scene} instance according to the className and the sceneName.
     *
     * @param sceneName the name for this scene in the {@link SceneManager} cache,
     * @param className the {@link Scene} class name to instantiate.
     * @return the corresponding {@link Scene} instance.
     */
    private Scene createInstance(String sceneName, String className) {
        Scene scene = null;
        try {
            Class<?> sceneClass = Class.forName(className);
            Constructor<?> constructor = sceneClass.getConstructor(GameInterface.class, String.class);
            scene = (Scene) constructor.newInstance(this.game, sceneName);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Log.error(SceneManager.class, "Unable to load Scene class for %s : %s", className, e.getMessage());
        }
        return scene;
    }

    /**
     * @param scene the Scene instance to be added to the list.
     */
    private void addScene(Scene scene) {
        if (scene != null && !scenes.containsKey(scene.getName())) {
            scenes.put(scene.getName(), scene);
        } else {
            Log.error(
                    SceneManager.class,
                    "The SceneManager already contains a scene named '%s': could not add it again.",
                    scene);
        }
    }

    /**
     * Switches the currently active scene to the specified scene by its name. If an active
     * scene exists, it is disposed before switching to the new scene. The new scene is then
     * loaded, created using the global configuration, and initialized with its behaviors
     * and entities.
     *
     * @param sceneName the name of the scene to switch to.
     */
    public void switchScene(String sceneName) {
        Config config = (Config) SystemManager.get(Config.class);
        if (activeScene != null) {
            activeScene.dispose();
        }
        this.activeScene = scenes.get(sceneName);
        activeScene.load();
        activeScene.create(config);
        // start all behaviors
        activeScene.getEntities().values().forEach(e -> e.getBehaviors().forEach(b -> b.start(e)));
        displaySceneTreeOnLog((Node<?>) activeScene, "");
    }

    /**
     * Recursively displays the structure of a scene tree starting from the given node
     * by logging information about each node and its children.
     *
     * @param node  the root node of the scene tree to be displayed
     * @param space the indentation string used for formatting the tree representation
     */
    private static void displaySceneTreeOnLog(Node<?> node, String space) {
        String spaces = space + "  ";
        debug(Game.class, "%s |_ Node<%s> named '%s' : %s", spaces, node.getClass().getSimpleName(), node.getName(), node);
        node.getChildren().forEach(c -> displaySceneTreeOnLog(c, spaces));
    }

    /**
     * Disposes of the currently active scene, if one exists, by invoking its
     * {@code dispose} method to release resources and perform cleanup. This ensures
     * that all resources associated with the active scene, such as memory, textures,
     * or other assets, are properly released to avoid memory leaks and unintended behaviors.
     * <p>
     * After disposing of the active scene, a debug log message is recorded
     * to indicate the completion of the disposal process.
     */
    public void dispose() {
        if (Optional.ofNullable(activeScene).isPresent()) {
            activeScene.dispose();
        }
        debug(SceneManager.class, "End of processing.");
    }

    /**
     * Sets the default scene for the {@code SceneManager}.
     * This method updates the internal reference to the default scene by its name.
     * The specified default scene will act as the fallback or starting scene
     * when the game initializes or when no active scene is explicitly set.
     *
     * @param defaultSceneName the name of the scene to be designated as the default.
     */
    public void setDefaultScene(String defaultSceneName) {
        this.defaultSceneName = defaultSceneName;
    }

    /**
     * Retrieves the currently active scene managed by the SceneManager.
     * The active scene represents the scene that is currently in use or being displayed
     * during the game execution. This method allows the caller to access the active scene
     * to perform operations such as state checks, updates, or interaction management.
     *
     * @return the active {@link Scene} instance currently managed by the SceneManager,
     * or null if no scene is currently active.
     */
    public Scene getActiveScene() {
        return activeScene;
    }

    /**
     * Retrieves a collection of dependencies required by the SceneManager.
     * Dependencies represent the necessary classes or configurations needed
     * for the proper functionality of the SceneManager.
     *
     * @return a collection of classes representing the dependencies of the SceneManager.
     */
    @Override
    public Collection<Class<?>> getDependencies() {
        return List.of(Config.class);
    }

    /**
     * Initializes the SceneManager by configuring it with available scenes
     * using the game's configuration and setting up the internal scene structure.
     * This method reads the scene configuration from the application's system manager
     * and instantiates the scenes accordingly.
     *
     * @param game the GameInterface instance that provides the context and access
     *             to the application's settings and functionalities.
     */
    @Override
    public void initialize(GameInterface game) {
        Config config = SystemManager.get(Config.class);
        String[] scenesList = config.get("app.scene.list");
        Arrays.stream(scenesList).forEach(sceneItem -> {
            String[] kv = sceneItem.split(":");
            Scene scene = createInstance(kv[0], kv[1]);
            addScene(scene);
        });
    }

    /**
     * Starts the scene management process by retrieving the default scene configuration
     * and switching to it. This method serves as the entry point for initializing
     * and activating the default scene when the game starts.
     *
     * @param game the {@link GameInterface} instance representing the game application,
     *             providing the context and configuration necessary for initializing
     *             and starting the scene management process.
     */
    @Override
    public void start(GameInterface game) {
        Config config = SystemManager.get(Config.class);
        defaultSceneName = config.get("app.scene.default");
        switchScene(defaultSceneName);
    }

    /**
     * Processes the currently active scene if it exists, updating its state based on the elapsed time
     * and the provided game context. This method delegates the processing logic to the active scene,
     * ensuring that the game state progresses as intended.
     *
     * @param game    the {@link GameInterface} instance representing the current game context. It provides
     *                access to the core game functionalities and state management.
     * @param elapsed the time elapsed since the last processing update, in seconds. This value is used
     *                to calculate time-based updates for the active scene.
     * @param stats   a map containing key-value pairs representing additional statistical or contextual
     *                information that may be used during processing.
     */
    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        if (Optional.ofNullable(this.activeScene).isPresent()) {
            activeScene.process(game, elapsed);
        }
    }

    /**
     * Stops the scene management process for the given game instance. This method is typically
     * called to halt all scene-related processing and clean up any resources or states associated
     * with the scene manager, ensuring a proper shutdown or pause of the game.
     *
     * @param game the {@link GameInterface} instance representing the game application for which
     *             the scene management process should be stopped. This provides the necessary
     *             context for halting operations related to the game's scenes.
     */
    @Override
    public void stop(GameInterface game) {
        // TODO stop the contained scenes and possible entities/resources requiring a stop action.
    }

    /**
     * Disposes resources and performs cleanup operations for the game or scene management to ensure
     * proper release of occupied resources. This method is invoked when the SceneManager or the game
     * is no longer required, preventing memory leaks or unintended behavior.
     *
     * @param game the {@link GameInterface} instance representing the game application to which
     *             the cleanup or disposal operations are applied.
     */
    @Override
    public void dispose(GameInterface game) {
        // TODO stop the contained scenes and possible entities/resources requiring a disposing action.
    }
}
