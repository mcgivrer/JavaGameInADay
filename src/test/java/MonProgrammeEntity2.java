import entity.Entity;
import game.TestGame;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a custom game entity that extends functionality from TestGame and
 * implements KeyListener for handling keyboard interactions.
 * <p>
 * Manages the game environment and entities within it, including the main player
 * and enemy entities. Supports initialization, game loop management, keyboard
 * input processing, and updating game state.
 */
public class MonProgrammeEntity2 extends TestGame implements KeyListener {
    private String configFilePath = "/demo3.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    private Map<String, Entity> entities = new HashMap<>();


    /**
     * Constructs a new instance of MonProgrammeEntity2.
     *
     * This constructor performs the following actions upon instantiation:
     * - Outputs a message to the console indicating the start of the program using the class name.
     * - Initializes the configuration object by creating a new Config instance.
     * - Loads configuration settings from the specified configuration file path.
     */
    public MonProgrammeEntity2() {
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

        createScene();
    }

    /**
     * Creates and initializes the scene with player and enemy entities.
     *
     * This method adds a blue-colored player entity at the center of the
     * rendering buffer with physical properties such as elasticity and friction
     * configured from application settings. The player's shape is a rectangle
     * with predefined dimensions and its maximum speed is set to a specified
     * configuration value.
     *
     * Additionally, this method creates multiple red-colored enemy entities
     * and places them at random positions within the rendering buffer. Each
     * enemy has unique elasticity and friction properties assigned randomly,
     * and its shape is defined as an ellipse. The enemies' maximum speed is
     * also assigned randomly but constrained below a certain percentage of
     * the player's maximum speed.
     *
     * The entities are then added to the scene for further interactions and updates.
     */
    private void createScene() {
        // Création du player bleu
        Entity player = new Entity("player")
            .setPosition(
                ((renderingBuffer.getWidth() - 16) * 0.5),
                ((renderingBuffer.getHeight() - 16) * 0.5))
            .setElasticity((double) config.get("app.physic.entity.player.elasticity"))
            .setFriction((double) config.get("app.physic.entity.player.friction"))
            .setFillColor(Color.BLUE)
            .setShape(new Rectangle2D.Double(0, 0, 16, 16))
            .setAttribute("max.speed", 2.0);
        add(player);

        // Création de l’ennemi rouge
        for (int i = 0; i < 10; i++) {
            Entity enemy = new Entity("enemy_%d".formatted(i))
                .setPosition((Math.random() * (renderingBuffer.getWidth() - 16)), (Math.random() * (renderingBuffer.getHeight() - 16)))
                .setElasticity(Math.random())
                .setFriction(Math.random())
                .setFillColor(Color.RED)
                .setShape(new Ellipse2D.Double(0, 0, 10, 10))
                .setAttribute("max.speed", (Math.random() * player.getAttribute("max.speed", 2.0) * 0.90));
            add(enemy);
        }
    }

    /**
     * Adds the specified entity to the collection of entities.
     *
     * @param e the entity to be added
     */
    private void add(Entity e) {
        entities.put(e.getName(), e);
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
     * Executes the main game loop for the application.
     *
     * This method continuously processes game operations such as handling input,
     * updating the game state, and rendering graphics to the screen. The loop
     * continues to run until an exit is requested or, if in test mode, until it
     * has executed a specified maximum number of iterations.
     *
     * It performs the following actions in each iteration:
     * 1. Captures and processes user input.
     * 2. Updates game objects and logic.
     * 3. Renders the updated state to the screen.
     * 4. Waits for a designated frame time to control the frame rate.
     *
     * The frame time is calculated based on a frames-per-second (FPS) value
     * retrieved from the configuration settings.
     *
     * At the conclusion of the loop, a message is printed to the console
     * displaying the total number of loops executed.
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
     * Causes the current thread to sleep for the specified amount of time.
     *
     * @param delayInMs the duration of time in milliseconds for which the
     *                  thread should be put to sleep.
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
        Entity player = entities.get("player");
        double speed = (double) config.get("app.physic.entity.player.speed");

        if (keys[KeyEvent.VK_LEFT]) {
            player.setVelocity(-speed, player.getDy());
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            player.setVelocity(speed, player.getDy());
        }
        if (keys[KeyEvent.VK_UP]) {
            player.setVelocity(player.getDx(), -speed);
        }
        if (keys[KeyEvent.VK_DOWN]) {
            player.setVelocity(player.getDx(), speed);
        }

        // on parcourt les entités en filtrant sur celles dont le nom commence par "enemy_"

        entities.values().stream()
            .filter(e -> e.getName().startsWith("enemy_"))
            .forEach(e -> {
                // new speed will be only a random ratio of the current one (from 50% to 110%)
                double eSpeed = (0.5 + Math.random() * 1.1);

                // Simulation pour les ennemis qui suivent le player sur l'are X,
                // but limited to 'max.speed' attribute's value
                double centerPlayerX = player.getX() + player.getShape().getBounds().width * 0.5;
                double centerEnemyX = e.getX() + e.getShape().getBounds().width * 0.5;
                double directionX = Math.signum(centerPlayerX - centerEnemyX);
                if (directionX != 0.0) {
                    e.setVelocity(
                        Math.min(directionX * eSpeed * e.getAttribute("max.speed", 2.0),
                            e.getAttribute("max.speed", 2.0)),
                        e.getDy());
                }

                // Simulation pour les ennemis qui suivent le player sur l'are Y,
                // but limited to 'max.speed' attribute's value
                double centerPlayerY = player.getY() + player.getShape().getBounds().width * 0.5;
                double centerEnemyY = e.getY() + e.getShape().getBounds().width * 0.5;
                double directionY = Math.signum(centerPlayerY - centerEnemyY);
                if (directionY != 0.0) {
                    e.setVelocity(
                        e.getDx(),
                        Math.min(directionY * eSpeed * e.getAttribute("max.speed", 2.0),
                            e.getAttribute("max.speed", 2.0)));
                }
            });
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
        entities.values().stream().forEach(e -> {
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
        });
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
        entities.values().forEach(e -> {
            g.translate((int) e.getX(), (int) e.getY());
            g.setColor(e.getFillColor());
            g.fill(e.getShape());
            g.setColor(e.getColor());
            g.drawLine((int) (e.getShape().getBounds().width * 0.5), (int) (e.getShape().getBounds().height * 0.5),
                (int) (e.getShape().getBounds().width * 0.5 + e.getDx() * 4), (int) (+e.getShape().getBounds().height * 0.5 + e.getDy() * 4));
            g.translate((int) -e.getX(), (int) -e.getY());
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

    /**
     * Disposes the main application window and outputs a termination message.
     * <p>
     * This method performs the following actions:
     * - Calls the dispose method of the main application window to release resources and close the window.
     * - Prints a message to the console indicating that the current instance of the application has terminated.
     */
    private void dispose() {
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
        MonProgrammeEntity2 prog = new MonProgrammeEntity2();
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
}