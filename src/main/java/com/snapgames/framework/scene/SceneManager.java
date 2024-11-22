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

public class SceneManager implements GSystem {

    private final GameInterface game;
    // Scene Management
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene activeScene;
    private String defaultSceneName;

    public SceneManager(Game app) {
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
            Constructor<?> constructor = sceneClass.getConstructor(Game.class, String.class);
            scene = (Scene) constructor.newInstance(this.game, sceneName);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Log.error("Unable to load Scene class for %s : %s", className, e.getMessage());
        }
        return scene;
    }

    /**
     * @param scene the Scene instance to be added to the list.
     */
    private void addScene(Scene scene) {
        if (!scenes.containsKey(scene.getName())) {
            scenes.put(scene.getName(), scene);
        } else {
            Log.error(SceneManager.class, "The SceneManager already contains a scene named '%s': could not add it again.", scene.getName());
        }
    }

    public void switchScene(String sceneName) {
        if (activeScene != null) {
            activeScene.dispose();
        }
        this.activeScene = scenes.get(sceneName);
        activeScene.load();
        activeScene.create();
        // start all behaviors
        activeScene.getEntities().values().forEach(e -> e.getBehaviors().forEach(b -> b.start(e)));

        displaySceneTreeOnLog((Node<?>) activeScene, "");
    }

    public void switchScene() {
    }

    private static void displaySceneTreeOnLog(Node<?> node, String space) {
        String spaces = space + "  ";
        Log.debug(Game.class, "%s |_ Node<%s> named '%s' : %s", spaces, node.getClass().getSimpleName(), node.getName(), node);
        node.getChildren().forEach(c -> displaySceneTreeOnLog(c, spaces));
    }

    public void dispose() {
        if (Optional.ofNullable(activeScene).isPresent()) {
            activeScene.dispose();
        }
    }

    public void setDefaultScene(String defaultSceneName) {
        this.defaultSceneName = defaultSceneName;
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    @Override
    public Collection<Class<?>> getDependencies() {
        return List.of(Config.class);
    }

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

    @Override
    public void start(GameInterface game) {
        Config config = SystemManager.get(Config.class);
        defaultSceneName = config.get("app.scene.default");
        switchScene(defaultSceneName);
    }

    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        if (Optional.ofNullable(this.activeScene).isPresent()) {
            activeScene.process(game, elapsed);
        }
    }

    @Override
    public void stop(GameInterface game) {

    }

    @Override
    public void dispose(GameInterface game) {

    }
}
