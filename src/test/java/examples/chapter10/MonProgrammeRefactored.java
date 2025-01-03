package examples.chapter10;

import examples.chapter10.io.InputListener;
import examples.chapter10.physic.PhysicEngine;
import examples.chapter10.gfx.RenderEngine;
import examples.chapter10.scene.SceneManager;
import game.Game;
import game.TestGame;
import scenes.Scene;
import utils.Config;
import utils.gameloop.StandardGameLoop;

import java.awt.image.BufferedImage;

/**
 * MonProgrammeCollision1 is a class that extends TestGame and implements KeyListener and Game interfaces.
 * <p>
 * This class manages the initialization and execution of a game application, including configuration
 * loading, scene management, main game loop execution, and handling of user inputs.
 */
public class MonProgrammeRefactored extends TestGame implements Game {
    /**
     * Represents the file path to the configuration file used by the application.
     * <p>
     * This variable stores the relative path to the configuration file that
     * contains various settings needed for the initialization and operation
     * of the application. It is typically loaded at the startup of the
     * `MonProgrammeCollision1` class to configure application-specific properties.
     */
    private String configFilePath = "/refactor1.properties";

    /**
     * Indicates whether the application is currently running in test mode.
     * <p>
     * When set to true, the application will operate under conditions
     * suitable for testing, such as executing a predefined number of
     * iterations or using mock data. This mode is typically used to facilitate
     * automated tests or to simulate scenarios in a controlled environment.
     * By default, testMode is set to false, meaning that the application
     * will run in its normal operational mode.
     */
    private boolean testMode = false;
    /**
     * Specifies the maximum number of iterations the main application loop can execute
     * in test mode. This variable is used to limit the loop execution when the application
     * is running in a controlled test environment.
     * <p>
     * It helps in testing and debugging by providing a finite number of iterations,
     * allowing for observation of the application's behavior over a specific number of loops.
     * <p>
     * When the application is not in test mode, this variable may not affect the loop execution.
     */
    private int maxLoopCount = 1;


    /**
     * Input manager instance.
     */
    private InputListener inputListener;
    /**
     * Scene manager instance.
     */
    private SceneManager sceneManager;

    /**
     * physic computation engine for Scene entities.
     */
    private PhysicEngine physicEngine;


    private RenderEngine renderEngine;

    /**
     * Constructs an instance of the MonProgrammeCamera1 class.
     * <p>
     * This constructor initializes the application by performing the following actions:
     * 1. Outputs a startup message indicating the class name.
     * 2. Creates a configuration object for the application.
     * 3. Loads application settings from a configuration file specified by configFilePath.
     */
    public MonProgrammeRefactored() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    /**
     * Initializes the application.
     * <p>
     * This method sets up the application by:
     * - Configuring test mode using values from the configuration.
     * - Setting the maximum loop count from the configuration.
     * - Outputting a message indicating initialization.
     * - Creating the main application window.
     * - Creating the rendering buffer.
     * - Initializing the position of the blue square at the center of the rendering buffer.
     * - Loading physical properties (speed, elasticity, and friction) from the configuration.
     */
    public void initialize() {
        testMode = config.get("app.test");
        maxLoopCount = (int) config.get("app.test.loop.max.count");

        // Initialization de services
        // init Input service manager
        inputListener = new InputListener(this);
        // init scene manager (loaded from config)
        sceneManager = new SceneManager(this);
        // init physic computation engine for scene entities.
        physicEngine = new PhysicEngine(this);
        // init the rendering engine to display all scene entities on screen.
        renderEngine = new RenderEngine(this, inputListener);

        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());

        sceneManager.switchTo("play");
    }


    /**
     * Executes the main application loop for the game.
     * <p>
     * This method contains the core loop for running the game's logic. It performs
     * the following steps repeatedly until an exit is requested or a test mode condition is met:
     * 1. Handles input by capturing keyboard events.
     * 2. Updates the game state, including object positions and velocities based on current inputs and physics.
     * 3. Renders the current game state to the screen.
     * 4. Records the number of game loops executed for testing or debugging purposes.
     * 5. Waits for a calculated frame time to maintain a consistent frame rate as configured.
     * <p>
     * The loop operates at a frame rate determined by the configuration setting "app.render.fps".
     * In test mode, the loop will execute a pre-defined number of times specified by maxLoopCount.
     * Outputs the total number of game loops executed upon termination.
     */
    public void loop() {
        StandardGameLoop standardGameLoop = new StandardGameLoop(this);
        standardGameLoop.process(this);
    }

    /**
     * Delegate scene computation to {@link PhysicEngine} instance.
     *
     * @param scene the current active Scene to be updated.
     */
    public void update(Scene scene) {
        physicEngine.update(scene);
    }


    public void render(Scene scene) {
        renderEngine.render(scene);
    }

    /**
     * Disposes the main application window and outputs a termination message.
     * <p>
     * This method performs the following actions:
     * - Calls the dispose method of the main application window to release resources and close the window.
     * - Prints a message to the console indicating that the current instance of the application has terminated.
     */
    private void dispose() {
        Scene currentScene = sceneManager.getCurrentScene();
        System.out.printf("# %s se termine:%n", this.getClass().getSimpleName());
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.dispose(e)));
        System.out.printf("- all %s entities' behaviors are disposed.%n", currentScene.getEntities().size());
        sceneManager.dispose(this);
        System.out.printf("- Scene '%s' is disposed.%n", currentScene.getName());
        renderEngine.dispose();
        System.out.printf("=> L'exécution de %s est terminée.%n", this.getClass().getSimpleName());
    }

    /**
     * Runs the application.
     * <p>
     * This method performs the following steps:
     * - Prints the application window title from the configuration.
     * - Initializes the application.
     * - Enters the main application loop.
     * - Disposes resources upon exit.
     *
     * @param args Command-line arguments passed to the application.
     */
    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
        initialize();
        loop();
        dispose();
    }


    /**
     * Entry point for the application.
     * <p>
     * This method creates an instance of MonProgrammeDemo3 and invokes its run method
     * to start the application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        MonProgrammeRefactored prog = new MonProgrammeRefactored();
        prog.run(args);
    }

    /**
     * Determines if the specified key is currently pressed.
     *
     * @param keyCode the code of the key to check, corresponding to a standard key code.
     * @return true if the key specified by keyCode is pressed, false otherwise.
     */
    @Override
    public boolean isKeyPressed(int keyCode) {
        return inputListener.isKeyPressed(keyCode);
    }

    @Override
    public void input(Scene scene) {
        scene.input(this);
        scene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.input(e)));
    }

    /**
     * Retrieves the current rendering buffer used for drawing operations.
     *
     * @return a BufferedImage object representing the rendering buffer.
     */
    public BufferedImage getRenderingBuffer(){
        return renderEngine.getRenderingBuffer();
    }


    public Scene getCurrentScene() {
        return sceneManager.getCurrentScene();
    }

}