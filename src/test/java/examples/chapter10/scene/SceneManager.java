package examples.chapter10.scene;

import examples.chapter10.MonProgrammeRefactored;
import game.Game;
import scenes.Scene;
import utils.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SceneManager {
    private final Game app;

    /**
     * A collection of scenes managed by the MonProgrammeCollision1 class.
     * <p>
     * This map stores various scenes, keyed by their unique string identifiers.
     * It allows easy retrieval and management of scenes within the application.
     * The scenes can be added, retrieved, or modified as needed by the
     * application's workflow, with each scene represented by a {@link Scene} object.
     */
    private final Map<String, Scene> scenes = new HashMap<>();
    /**
     * Represents the current scene being rendered and interacted with in the application.
     * <p>
     * The scene object referred to by this variable is responsible for defining the
     * graphical and interactive elements present in the current state of the application.
     * It plays a crucial role in the graphical user interface, handling aspects such as
     * visual rendering, user input, and interactions with other scenes or entities.
     * <p>
     * The currentScene is subject to changes, typically updated to reflect new states in
     * the application, either through user actions or program logic executed during the
     * application's lifecycle.
     */
    private Scene currentScene;


    public SceneManager(Game app) {
        this.app = app;
        load(app.getConfig());
    }

    private void load(Config config) {
        if (config.containsKey("app.scene.list") && config.containsKey("app.scene.default")) {
            String[] scenesList = ((String[]) config.get("app.scene.list"));
            Arrays.asList(scenesList).stream().forEach(sceneItem -> {
                String[] keyClass = sceneItem.split(":");
                if (Optional.ofNullable(keyClass[0]).isPresent() && !keyClass[0].equals("")) {
                    if (Optional.ofNullable(keyClass[1]).isPresent() && !keyClass[1].equals("")) {
                        try {
                            Class<?> sceneClazz = Class.forName(keyClass[1]);
                            Constructor<?> constructor = sceneClazz.getConstructor(String.class);
                            Scene scene = (Scene) constructor.newInstance(keyClass[0]);
                            addScene(scene);
                        } catch (ClassNotFoundException e) {
                            System.out.printf("!! The class %s is unknown.%n", keyClass[1]);
                        } catch (NoSuchMethodException e) {
                            System.out.printf("!! The class %s constructor(Game,String) is unknown.%n", keyClass[1]);
                        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                            System.out.printf("!! Unable to create instance for %s !!%n", keyClass[1]);
                        }
                    } else {
                        System.out.printf("!! The canonical class name %s is malformed.%n", keyClass[1]);
                    }
                } else {
                    System.out.printf("!! The scene name is incorrect !!%n");
                }
            });
        }
    }


    /**
     * Adds a new scene to the collection of scenes. If the scenes collection is
     * currently empty, the provided scene will also be set as the current scene.
     *
     * @param s the Scene object to be added to the collection. Its name will be
     *          used as the key for storage within the collection.
     */
    public void addScene(Scene s) {
        this.scenes.put(s.getName(), s);
    }


    /**
     * Initializes and creates the current scene and its entities.
     * <p>
     * This method carries out the following actions:
     * - Calls the initialize method of the current scene, passing the current object.
     * - Invokes the create method to set up the scene specifics using the current object.
     * - Iterates over the entities retrieved from the current scene and initializes
     * each behavior associated with those entities.
     */
    public void create(Game app) {

        System.out.printf("=> Load scene '%s' (%s)%n", currentScene.getName(), currentScene.getClass());
        currentScene.initialize(app);
        System.out.printf("- Scene '%s' (%s) initialized%n", currentScene.getName(), currentScene.getClass());
        currentScene.create(app);
        System.out.printf("- Scene '%s' (%s) created %d entities%n", currentScene.getName(), currentScene.getClass(), currentScene.getEntities().size());
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.init(e)));
        System.out.printf("- All %d entities' behaviors from Scene %s (%s) initialized%n",
                currentScene.getEntities().size(),
                currentScene.getName(),
                currentScene.getClass());
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.create(e)));
        System.out.printf("- All %d entities' behaviors from Scene %s (%s) created%n",
                currentScene.getEntities().size(),
                currentScene.getName(),
                currentScene.getClass());
    }

    /**
     * Initializes and creates the current scene and its entities.
     * <p>
     * This method carries out the following actions:
     * - Calls the initialize method of the current scene, passing the current object.
     * - Invokes the create method to set up the scene specifics using the current object.
     * - Iterates over the entities retrieved from the current scene and initializes
     * each behavior associated with those entities.
     *
     * @param name the name of the Scene instance to be activated.
     */
    public void switchTo(String name) {
        if (Optional.ofNullable(currentScene).isPresent()) {
            currentScene.dispose(app);
        }
        currentScene = scenes.get(name);
        // Initialise et créé la Scene courante.
        create(app);
    }

    public Scene getCurrentScene() {
        return this.currentScene;
    }

    public void dispose(Game app) {
        if (!scenes.isEmpty()) {
            scenes.values().forEach(scene -> scene.dispose(app));
        }
    }
}
