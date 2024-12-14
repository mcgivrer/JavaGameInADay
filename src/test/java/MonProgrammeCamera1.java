import entity.Camera;
import entity.Entity;
import game.Game;
import game.TestGame;
import physic.World;
import scenes.PlayCameraScene;
import scenes.Scene;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MonProgrammeBehavior1 is a class that extends TestGame and implements KeyListener and Game interfaces.
 * <p>
 * This class manages the initialization and execution of a game application, including configuration
 * loading, scene management, main game loop execution, and handling of user inputs.
 */
public class MonProgrammeCamera1 extends TestGame implements KeyListener, Game {
    /**
     * Represents the file path to the configuration file used by the application.
     * <p>
     * This variable stores the relative path to the configuration file that
     * contains various settings needed for the initialization and operation
     * of the application. It is typically loaded at the startup of the
     * `MonProgrammeBehavior1` class to configure application-specific properties.
     */
    private String configFilePath = "/camera1.properties";

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
     * Represents the main application window for the MonProgrammeBehavior1 class.
     * <p>
     * This JFrame is used to display the graphical user interface of the application. It is
     * created and initialized with specific settings such as the window title, size,
     * and close operation based on the application's configuration.
     * <p>
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
     * <p>
     * Each index in the array corresponds to a specific key code, and
     * the boolean value at that index indicates whether the key is
     * currently pressed (true) or released (false).
     * <p>
     * The array is used to handle and track keyboard input events. It is
     * typically updated in response to key press and release events
     * processed by the application.
     */
    private final boolean[] keys = new boolean[1024];

    /**
     * A collection of scenes managed by the MonProgrammeBehavior1 class.
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


    /**
     * Constructs an instance of the MonProgrammeCamera1 class.
     * <p>
     * This constructor initializes the application by performing the following actions:
     * 1. Outputs a startup message indicating the class name.
     * 2. Creates a configuration object for the application.
     * 3. Loads application settings from a configuration file specified by configFilePath.
     */
    public MonProgrammeCamera1() {
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

        addScene(new PlayCameraScene("play"));
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
     * <p>
     * This method carries out the following actions:
     * - Calls the initialize method of the current scene, passing the current object.
     * - Invokes the create method to set up the scene specifics using the current object.
     * - Iterates over the entities retrieved from the current scene and initializes
     * each behavior associated with those entities.
     */
    private void createScene() {
        currentScene.initialize(this);
        currentScene.create(this);
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.init(e)));
        currentScene.getEntities().forEach(e -> e.getBehaviors().forEach(b -> b.create(e)));
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
        int loopCount = 0;
        int frameTime = 1000 / (int) (config.get("app.render.fps"));
        while (!isExitRequested() && ((testMode && loopCount < maxLoopCount) || !testMode)) {
            input();
            if (isNotPaused()) {
                update();
            }
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
        currentScene.getEntities().stream().filter(e -> !(e instanceof Camera)).forEach(e -> {
            e.setPosition(e.getX() + e.getDx(), e.getY() + e.getDy());

            // repositionnement dans la zone de jeu si nécessaire
            if (!currentScene.getWorld().contains(e)) {

                applyBouncingFactor(currentScene, e);
                e.setPosition(
                        Math.min(Math.max(e.getX(), currentScene.getWorld().getX()), currentScene.getWorld().getWidth() - e.getWidth()),
                        Math.min(Math.max(e.getY(), currentScene.getWorld().getY()), currentScene.getWorld().getHeight() - e.getHeight()));
            }

            // application du facteur de friction
            e.setVelocity(e.getDx() * e.getFriction(), e.getDy() * e.getFriction());
            e.getBehaviors().forEach(b -> b.update(e));
        });
        Optional<Entity> cam = currentScene.getEntities().stream().filter(e -> e instanceof Camera).findFirst();
        cam.ifPresent(entity -> ((Camera) entity).update(16.0));

        currentScene.update(this);
    }

    private void applyBouncingFactor(Scene currentScene, Entity e) {
        // application du rebond si collision avec le bord de la zone de jeu
        if (e.getX() < currentScene.getWorld().getX()
                || e.getX() + e.getWidth() > e.getWidth() + currentScene.getWorld().getWidth()) {
            e.setVelocity(-e.getDx() * e.getElasticity(), e.getDy());
        }
        if (e.getY() < currentScene.getWorld().getY()
                || e.getY() + e.getHeight() > currentScene.getWorld().getHeight()) {
            e.setVelocity(e.getDx(), -e.getDy() * e.getElasticity());
        }
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
        // configure rendering pipeline
        g.setRenderingHints(
                Map.of(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        // clear rendering buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight());

        Optional<Entity> cam = currentScene.getEntities().stream().filter(e -> e instanceof Camera).findFirst();

        cam.ifPresent(entity -> g.translate((int) -entity.getX(), (int) -entity.getY()));
        drawWorldLimit(g, currentScene.getWorld(), 16, 16);
        if (cam.isPresent() && isDebugGreaterThan(1)) {
            drawDebugCamera(g, (Camera) cam.get());
        }
        cam.ifPresent(entity -> g.translate((int) entity.getX(), (int) entity.getY()));


        // draw entities
        currentScene.getEntities().stream()
                .filter(e -> !(e instanceof Camera))
                .forEach(e -> {
                    cam.ifPresent(entity -> g.translate((int) -entity.getX(), (int) -entity.getY()));
                    drawEntity(e, g);
                    cam.ifPresent(entity -> g.translate((int) entity.getX(), (int) entity.getY()));
                    currentScene.draw(this, g);

                    // Exécuter les comportements de dessin pour cette instance d'Entity.
                    e.getBehaviors().forEach(b -> b.draw(g, e));
                });
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

    private static void drawDebugCamera(Graphics2D g, Camera camera) {
        g.setFont(g.getFont().deriveFont(9.0f));
        g.setColor(Color.yellow);
        Rectangle2D drawCamera = new Rectangle2D.Double(
                camera.getBounds2D().getX() + 20, camera.getBounds2D().getY() + 30,
                camera.getBounds2D().getWidth() - 40, camera.getBounds2D().getHeight() - 40);
        g.draw(drawCamera);
        g.drawString("#%d:%s".formatted(camera.getId(), camera.getName()),
                (int) (camera.getBounds2D().getX() + 20 + camera.getBounds2D().getWidth() * 0.70),
                (int) (camera.getBounds2D().getY() + camera.getBounds2D().getHeight() - 14));
    }

    private void drawWorldLimit(Graphics2D g, World world, int tileWidth, int tileHeight) {
        // draw the world limit.
        g.setColor(Color.GRAY);
        for (int ix = 0; ix < world.getWidth(); ix += tileWidth) {
            for (int iy = 0; iy < world.getHeight(); iy += tileHeight) {
                g.drawRect(ix, iy,
                        tileWidth, tileHeight);
            }
        }
        g.setColor(Color.DARK_GRAY);
        g.draw(world);
    }

    private void drawEntity(Entity e, Graphics2D g) {
        g.translate((int) e.getX(), (int) e.getY());
        g.setColor(e.getFillColor());
        g.fill(e.getShape());
        // draw velocity vector
        g.setColor(e.getColor());
        g.drawLine((int) (e.getShape().getBounds().width * 0.5), (int) (e.getShape().getBounds().height * 0.5),
                (int) (e.getShape().getBounds().width * 0.5 + e.getDx() * 4), (int) (+e.getShape().getBounds().height * 0.5 + e.getDy() * 4));
        drawDebugEntity(g, e);
        g.translate((int) -e.getX(), (int) -e.getY());
    }

    private void drawDebugEntity(Graphics2D g, Entity e) {
        if (isDebugGreaterThan(1)) {
            g.setStroke(new BasicStroke(0.5f));
            g.setColor(Color.ORANGE);
            g.draw(e.getShape());
            g.setFont(g.getFont().deriveFont(9.0f));
            g.drawString("#:%d:%s".formatted(e.getId(), e.getName()), (int) e.getWidth(), 0);
            if (isDebugGreaterThan(2)) {
                g.drawString("pos:%.0f,%.0f".formatted(e.getX(), e.getY()), (int) e.getWidth(), 10);
                g.drawString("siz:%.2f,%.2f".formatted(e.getWidth(), e.getHeight()), (int) e.getWidth(), 20);
                g.drawString("vel:%.2f,%.2f".formatted(e.getDx(), e.getDy()), (int) e.getWidth(), 30);
            }
        }
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
        MonProgrammeCamera1 prog = new MonProgrammeCamera1();
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
        if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {
            setDebug(getDebug() + 1 < 6 ? getDebug() + 1 : 0);
        }
        if (e.getKeyCode() == KeyEvent.VK_P && e.isControlDown()) {
            setPause(isNotPaused());
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