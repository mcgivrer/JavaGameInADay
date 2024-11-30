import entity.Entity;
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

public class MonProgrammeEntity2 extends TestGame implements KeyListener {
    private String configFilePath = "/demo3.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    private Map<String, Entity> entities = new HashMap<>();


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

        // Création du player bleu
        Entity player = new Entity("player")
                .setPosition(
                        ((renderingBuffer.getWidth() - 16) * 0.5),
                        ((renderingBuffer.getHeight() - 16) * 0.5))
                .setElasticity((double) config.get("app.physic.entity.player.elasticity"))
                .setFriction((double) config.get("app.physic.entity.player.friction"))
                .setFillColor(Color.BLUE)
                .setShape(new Rectangle2D.Double(0, 0, 16, 16));
        add(player);

        // Création de l’ennemi rouge
        for (int i = 0; i < 10; i++) {
            Entity enemy = new Entity("enemy_%d".formatted(i))
                    .setPosition((Math.random() * (renderingBuffer.getWidth() - 16)), (Math.random() * (renderingBuffer.getHeight() - 16)))
                    .setElasticity(Math.random())
                    .setFriction(Math.random())
                    .setFillColor(Color.RED)
                    .setShape(new Ellipse2D.Double(0, 0, 10, 10));
            add(enemy);
        }
    }

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
                    double eSpeed = (0.5 + Math.random() * 1.5);
                    // Simulation pour l’ennemi qui suit le player
                    if (player.getX() + player.getShape().getBounds().width * 0.5 != e.getX() + 5) {
                        e.setVelocity(Math.min(Math.signum(((player.getX()
                                + player.getShape().getBounds().width * 0.5)
                                - (e.getX() + e.getShape().getBounds().width * 0.5)) * 0.5
                                * (1 - (eSpeed / ((player.getX() + 8) - (e.getX() + e.getShape().getBounds().width * 0.5))))), 2.0), e.getDy());
                    }
                    if (player.getY() + player.getShape().getBounds().width * 0.5 != e.getY() + e.getShape().getBounds().width * 0.5) {
                        e.setVelocity(
                                e.getDx(), Math.min(
                                        Math.signum(
                                        ((player.getY() + player.getShape().getBounds().width * 0.5) - (e.getY() + e.getShape().getBounds().width * 0.5)) * 0.5
                                            * (1 - (eSpeed / ((player.getY() + player.getShape().getBounds().width * 0.5) - (e.getY() + e.getShape().getBounds().width * 0.5))))),
                                        2.0));
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