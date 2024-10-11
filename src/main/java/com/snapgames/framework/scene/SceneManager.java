package com.snapgames.framework.scene;

import com.snapgames.framework.Game;
import com.snapgames.framework.utils.Log;
import com.snapgames.framework.utils.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SceneManager {

    private final Game app;
    // Scene Management
    private Map<String, Scene> scenes = new HashMap<>();
    private Scene activeScene;
    private String defaultSceneName;

    public SceneManager(Game app) {
        this.app = app;
    }

    public void addScene(Scene scene) {
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
        switchScene(defaultSceneName);
    }

    private static void displaySceneTreeOnLog(Node<?> node, String space) {
        String spaces = space + "  ";
        Log.debug(Game.class, "%s |_ Node<%s> named '%s'", spaces, node.getClass().getSimpleName(), node.getName());
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
}
