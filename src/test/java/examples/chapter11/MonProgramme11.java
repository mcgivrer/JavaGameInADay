package examples.chapter11;


import com.snapgames.framework.GameInterface;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.utils.Config;
import examples.chapter11.gameloop.StandardGameLoop2;
import examples.chapter11.gfx.Renderer;
import examples.chapter11.physic.PhysicEngine;
import examples.chapter11.scene.SceneManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MonProgrammeCollision1 is a class that extends TestGame and implements KeyListener and Game interfaces.
 * <p>
 * This class manages the initialization and execution of a game application, including configuration
 * loading, scene management, main game loop execution, and handling of user inputs.
 */
public class MonProgramme11 implements GameInterface {

    private static final double FPS = 60.0;

    // Game exit request flag.
    public static boolean exit = false;

    // internal Pause flag
    private boolean pause = false;

    // debug level
    private int debug = 1;
    /**
     * Represents the file path to the configuration file used by the application.
     * <p>
     * This variable stores the relative path to the configuration file that
     * contains various settings needed for the initialization and operation
     * of the application. It is typically loaded at the startup of the
     * `MonProgrammeCollision1` class to configure application-specific properties.
     */
    private String configFilePath = "/engine2.properties";

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


    private Renderer renderEngine;

    private Config config;

    private Map<String, Object> stats = new ConcurrentHashMap<>();

    /**
     * Constructs an instance of the MonProgrammeCamera1 class.
     * <p>
     * This constructor initializes the application by performing the following actions:
     * 1. Outputs a startup message indicating the class name.
     * 2. Creates a configuration object for the application.
     * 3. Loads application settings from a configuration file specified by configFilePath.
     */
    public MonProgramme11() {
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
        inputListener.initialize(this);

        // init scene manager (loaded from config)
        sceneManager = new SceneManager(this, config);
        sceneManager.initialize(this);

        // init physic computation engine for scene entities.
        physicEngine = new PhysicEngine(this, sceneManager);
        physicEngine.initialize(this);

        // init the rendering engine to display all scene entities on screen.
        renderEngine = new Renderer(this, config, sceneManager);
        renderEngine.initialize(this);
        renderEngine.setInputListener(inputListener);

        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());

        sceneManager.switchScene("play");
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
        StandardGameLoop2 standardGameLoop = new StandardGameLoop2(this, config, renderEngine, physicEngine, inputListener, stats);
        standardGameLoop.process(this, sceneManager.getActiveScene());
    }

    /**
     * Disposes the main application window and outputs a termination message.
     * <p>
     * This method performs the following actions:
     * - Calls the dispose method of the main application window to release resources and close the window.
     * - Prints a message to the console indicating that the current instance of the application has terminated.
     */
    private void dispose() {
        Scene currentScene = sceneManager.getActiveScene();
        System.out.printf("# %s se termine:%n", this.getClass().getSimpleName());
        currentScene.getEntities().values().forEach(e -> e.getBehaviors().forEach(b -> b.dispose(e)));
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
        MonProgramme11 prog = new MonProgramme11();
        prog.run(args);
    }

    public void input(Scene scene) {
        scene.input(inputListener);
        scene.getEntities().values()
                .forEach(e -> e.getBehaviors()
                        .forEach(b -> b.input(inputListener, e)));
    }

    @Override
    public void requestExit() {
        setExit(true);
    }

    @Override
    public void setDebug(int i) {

    }

    @Override
    public int getDebug() {
        return debug;
    }

    @Override
    public boolean isNotPaused() {
        return !pause;
    }

    @Override
    public void setPause(boolean p) {
        this.pause = p;
    }

    @Override
    public void setExit(boolean b) {
        this.exit = b;
    }

    @Override
    public boolean isExitRequested() {
        return exit;
    }

    @Override
    public boolean isDebugGreaterThan(int debugLevel) {
        return debug > debugLevel;
    }
}