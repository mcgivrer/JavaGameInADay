import game.Game;
import game.TestGame;
import scenes.PlayBehaviorScene;
import scenes.Scene;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * MonProgrammeBehavior1 is a class that extends TestGame and implements KeyListener and Game interfaces.
 *
 * This class manages the initialization and execution of a game application, including configuration
 * loading, scene management, main game loop execution, and handling of user inputs.
 */
public class MonProgrammeBehavior1 extends TestGame implements KeyListener, Game {
    /**
     * Represents the file path to the configuration file used by the application.
     *
     * This variable stores the relative path to the configuration file that
     * contains various settings needed for the initialization and operation
     * of the application. It is typically loaded at the startup of the
     * `MonProgrammeBehavior1` class to configure application-specific properties.
     */
    private String configFilePath = "/behavior1.properties";

    /**
     * Indicates whether the application is currently running in test mode.
     *
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
     *
     * It helps in testing and debugging by providing a finite number of iterations,
     * allowing for observation of the application's behavior over a specific number of loops.
     *
     * When the application is not in test mode, this variable may not affect the loop execution.
     */
    private int maxLoopCount = 1;
    /**
     * Represents the main application window for the MonProgrammeBehavior1 class.
     *
     * This JFrame is used to display the graphical user interface of the application. It is
     * created and initialized with specific settings such as the window title, size,
     * and close operation based on the application's configuration.
     *
     * The window also serves as the primary interface for handling and processing user input,
     * particularly through keyboard interactions. It integrates with the application's event
     * listeners to manage user actions and render updates.
     */
    private JFrame window;
    /**
     * The rendering buffer used for drawing operations.
     * This BufferedImage is initialized based on the application
     * configuration settings, specifically for rendering the
     * current state of the application. It acts as an off-screen
     * drawing area to provide smooth rendering updates.
     */
    private BufferedImage renderingBuffer;

    /**
     * An array representing the state of keyboard keys for the application.
     *
     * Each index in the array corresponds to a specific key code, and
     * the boolean value at that index indicates whether the key is
     * currently pressed (true) or released (false).
     *
     * The array is used to handle and track keyboard input events. It is
     * typically updated in response to key press and release events
     * processed by the application.
     */
    private final boolean[] keys = new boolean[1024];

    /**
     * A collection of scenes managed by the MonProgrammeBehavior1 class.
     *
     * This map stores various scenes, keyed by their unique string identifiers.
     * It allows easy retrieval and management of scenes within the application.
     * The scenes can be added, retrieved, or modified as needed by the
     * application's workflow, with each scene represented by a {@link Scene} object.
     */
    private final Map<String, Scene> scenes = new HashMap<>();
    /**
     * Represents the current scene being rendered and interacted with in the application.
     *
     * The scene object referred to by this variable is responsible for defining the
     * graphical and interactive elements present in the current state of the application.
     * It plays a crucial role in the graphical user interface, handling aspects such as
     * visual rendering, user input, and interactions with other scenes or entities.
     *
     * The currentScene is subject to changes, typically updated to reflect new states in
     * the application, either through user actions or program logic executed during the
     * application's lifecycle.
     */
    private Scene currentScene;

    /**
     * Constructs a new instance of MonProgrammeBehavior1.
     *
     * This constructor performs the following actions:
     * - Outputs a startup message indicating that the MonProgrammeBehavior1 class is starting.
     * - Initializes the configuration for the application by creating a new Config object.
     * - Loads the application configuration from a specified configuration file path.
     */
    public MonProgrammeBehavior1() {
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
        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());
        createWindow();
        createBuffer();

        addScene(new PlayBehaviorScene("play"));
        createScene();
    }

    /**
     * Adds a new scene to the collection of scenes. If the scenes collection is
     * currently empty, the provided scene will also be set as the current scene.
     *
     * @param s the Scene object to be added to the collection. Its name will be
     *          used as the key for storage within the collection.
     */
    private void addScene(Scene s) {
        if (this.scenes.isEmpty()) {
            this.currentScene = s;
        }
        this.scenes.put(s.getName(), s);
    }

    /**
     * Initializes and creates the current scene and its entities.
     *
     * This method carries out the following actions:
     * - Calls the initialize method of the current scene, passing the current object.
     * - Invokes the create method to set up the scene specifics using the current object.
     * - Iterates over the entities retrieved from the current scene and initializes
     *   each behavior associated with those entities.
     */
    private void createScene() {
        currentScene.initialize(this);
        currentScene.create(this);
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.init(e)));
    }

    /**
     * Creates and initializes the main application window.
     * <p>
     * The window's title, size, and buffer strategy are configured based on the
     * application settings obtained from the config object.
     * <p>
     * This method sets the default close operation to exit the application when
     * the window is closed.
     * <p>
     * Adds the current instance as a key listener to the window to handle keyboard input.
     */
    private void createWindow() {
        // Create the Window
        window = new JFrame((String) config.get("app.render.window.title"));
        window.setPreferredSize(config.get("app.render.window.size"));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(this);
        window.pack();
        window.setVisible(true);
        window.addKeyListener(this);
        window.createBufferStrategy((int) config.get("app.render.buffer.strategy"));
    }

    /**
     * Creates a rendering buffer used for drawing operations.
     * <p>
     * The render buffer size is obtained from the application configuration,
     * specifically the "app.render.buffer.size" property. The rendering buffer
     * is initialized as a BufferedImage with the dimensions specified in the
     * configuration and with the image type set to TYPE_INT_ARGB.
     */
    private void createBuffer() {
        Dimension renderBufferSize = config.get("app.render.buffer.size");
        renderingBuffer = new BufferedImage(
                renderBufferSize.width, renderBufferSize.height,
                BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Executes the main application loop for the game.
     *
     * This method contains the core loop for running the game's logic. It performs
     * the following steps repeatedly until an exit is requested or a test mode condition is met:
     * 1. Handles input by capturing keyboard events.
     * 2. Updates the game state, including object positions and velocities based on current inputs and physics.
     * 3. Renders the current game state to the screen.
     * 4. Records the number of game loops executed for testing or debugging purposes.
     * 5. Waits for a calculated frame time to maintain a consistent frame rate as configured.
     *
     * The loop operates at a frame rate determined by the configuration setting "app.render.fps".
     * In test mode, the loop will execute a pre-defined number of times specified by maxLoopCount.
     * Outputs the total number of game loops executed upon termination.
     */
    public void loop() {
        int loopCount = 0;
        int frameTime = 1000 / (int) (config.get("app.render.fps"));
        while (!isExitRequested() && ((testMode && loopCount < maxLoopCount) || !testMode)) {
            input();
            update();
            render();
            loopCount++;
            waitTime(frameTime);
        }
        System.out.printf("=> Game loops %d times%n", loopCount);
    }

    /**
     * Pauses the execution of the current thread for the specified amount of time.
     *
     * @param delayInMs the time to wait, in milliseconds. This value determines how long the
     *                  thread will sleep. If interrupted during sleep, an error message will
     *                  be printed to the standard error stream.
     */
    private void waitTime(int delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            System.err.println("Unable to wait 16 ms !");
        }
    }

    /**
     * Handles keyboard input for controlling the movement of an object.
     * <p>
     * This method updates the object's movement direction based on the
     * state of the arrow keys:
     * - Left arrow key decreases the horizontal speed.
     * - Right arrow key increases the horizontal speed.
     * - Up arrow key decreases the vertical speed.
     * - Down arrow key increases the vertical speed.
     */
    private void input() {
        currentScene.input(this);
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.input(e)));
    }

    /**
     * Updates the position and velocity of an object within the game area.
     * <p>
     * This method performs the following operations:
     * - Calculates the new position based on the current velocity.
     * - Applies bounce effect if a collision with the game area's edge is detected.
     * - Repositions the object within the game area if necessary.
     * - Applies a friction factor to the object's velocity.
     */
    private void update() {
        // calcul de la position du player bleu en fonction de la vitesse courante.
        currentScene.getEntities().forEach(e -> {
            e.setPosition(e.getX() + e.getDx(), e.getY() + e.getDy());
            // application du rebond si collision avec le bord de la zone de jeu
            if (e.getX() < -8 || e.getX() > renderingBuffer.getWidth() - 8) {
                e.setVelocity(-e.getDx() * e.getElasticity(), e.getDy());
            }
            if (e.getY() < -8 || e.getY() > renderingBuffer.getHeight() - 8) {
                e.setVelocity(e.getDx(), -e.getDy() * e.getElasticity());
            }

            // repositionnement dans la zone de jeu si nécessaire
            e.setPosition(Math.min(Math.max(e.getX(), -8), renderingBuffer.getWidth() - 8),
                    Math.min(Math.max(e.getY(), -8), renderingBuffer.getHeight() - 8));

            // application du facteur de friction
            e.setVelocity(e.getDx() * e.getFriction(), e.getDy() * e.getFriction());
            e.getBehaviors().forEach(b -> b.update(e));
        });
        currentScene.update(this);
    }

    /**
     * Renders the current state of the application to the screen.
     * <p>
     * This method performs the following steps:
     * - Clears the rendering buffer and sets it to a black background.
     * - Draws a blue rectangle at the current position (x, y).
     * - Disposes of the graphics context used for drawing.
     * - Copies the contents of the rendering buffer to the screen using the window's buffer strategy.
     * - Disposes of the graphics context used for drawing to the screen.
     * - Shows the buffer strategy to display the rendered image.
     */
    private void render() {
        Graphics2D g = renderingBuffer.createGraphics();
        // clear rendering buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight());

        // draw entities
        currentScene.getEntities().forEach(e -> {
            g.translate((int) e.getX(), (int) e.getY());
            g.setColor(e.getFillColor());
            g.fill(e.getShape());
            g.setColor(e.getColor());
            g.drawLine((int) (e.getShape().getBounds().width * 0.5), (int) (e.getShape().getBounds().height * 0.5),
                    (int) (e.getShape().getBounds().width * 0.5 + e.getDx() * 4), (int) (+e.getShape().getBounds().height * 0.5 + e.getDy() * 4));
            g.translate((int) -e.getX(), (int) -e.getY());
            // Exécuter les comportements de dessin pour cette instance d'Entity.
            e.getBehaviors().forEach(b -> b.draw(g, e));
        });
        currentScene.draw(this, g);

        g.dispose();

        // copy buffer to window.
        BufferStrategy bs = window.getBufferStrategy();
        Graphics gw = bs.getDrawGraphics();
        gw.drawImage(renderingBuffer, 0, 0, window.getWidth(), window.getHeight(),
                0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight()
                , null);

        gw.dispose();
        bs.show();

    }

    /**
     * Disposes the main application window and outputs a termination message.
     * <p>
     * This method performs the following actions:
     * - Calls the dispose method of the main application window to release resources and close the window.
     * - Prints a message to the console indicating that the current instance of the application has terminated.
     */
    private void dispose() {
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.dispose(e)));

        currentScene.dispose(this);
        window.dispose();
        System.out.printf("# %s est terminé.%n", this.getClass().getSimpleName());
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
        MonProgrammeBehavior1 prog = new MonProgrammeBehavior1();
        prog.run(args);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles the key press events to update the state of keys in the application.
     *
     * @param e The KeyEvent that triggered the method call. Used to determine which key was pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    /**
     * Handles the key release events to update the state of keys in the application.
     *
     * @param e The KeyEvent that triggered the method call. Used to determine which key was released.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.requestExit();
        }
    }


    /**
     * Determines if the specified key is currently pressed.
     *
     * @param keyCode the code of the key to check, corresponding to a standard key code.
     * @return true if the key specified by keyCode is pressed, false otherwise.
     */
    @Override
    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    /**
     * Retrieves the current rendering buffer used for drawing operations.
     *
     * @return a BufferedImage object representing the rendering buffer.
     */
    public BufferedImage getRenderingBuffer() {
        return this.renderingBuffer;
    }
}