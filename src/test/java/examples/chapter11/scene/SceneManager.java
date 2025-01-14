package examples.chapter11.scene;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Log;
import com.snapgames.framework.utils.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.snapgames.framework.utils.Log.debug;

public class SceneManager {

    private final GameInterface game;
    // Scene Management
    private final Map<String, Scene> scenes = new HashMap<>();
    private final Config config;
    private Scene activeScene;
    private String defaultSceneName;

    public SceneManager(GameInterface app, Config config) {
        this.game = app;
        this.config = config;
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
            scene = (Scene) constructor.newInstance( this.game, sceneName);
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
        activeScene.create(config);
        // start all behaviors
        activeScene.getEntities().values().forEach(e -> e.getBehaviors().forEach(b -> b.start(e)));

        displaySceneTreeOnLog((Node<?>) activeScene, "");
    }

    public void switchScene() {
    }

    private static void displaySceneTreeOnLog(Node<?> node, String space) {
        String spaces = space + "  ";
        debug(SceneManager.class, "%s |_ Node<%s> named '%s' : %s", spaces, node.getClass().getSimpleName(), node.getName(), node);
        node.getChildren().forEach(c -> displaySceneTreeOnLog(c, spaces));
    }

    public void dispose(GameInterface app) {
        if (Optional.ofNullable(activeScene).isPresent()) {
            activeScene.dispose();
        }
        debug(SceneManager.class, "End of processing.");
    }

    public void setDefaultScene(String defaultSceneName) {
        this.defaultSceneName = defaultSceneName;
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public void initialize(GameInterface game) {
        String[] scenesList = config.get("app.scene.list");
        Arrays.stream(scenesList).forEach(sceneItem -> {
            String[] kv = sceneItem.split(":");
            Scene scene = createInstance(kv[0], kv[1]);
            addScene(scene);
        });
    }

}
