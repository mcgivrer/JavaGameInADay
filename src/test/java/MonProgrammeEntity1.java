import game.TestGame;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class MonProgrammeEntity1 extends TestGame implements KeyListener {
    private String configFilePath = "/demo3.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    /**
     * The coordinate position used in the game for rendering or updating the location of an object.
     */
    private double x, y;

    private double ex, ey;
    /**
     * Represents the velocity of an object.
     */
    private double dx, dy;
    private double edx, edy;
    /**
     * Elasticity coefficient for an object in the simulation.
     * This value determines how "bouncy" an object is during collisions.
     * Values closer to 1 represent high elasticity (more bounce),
     * while values closer to 0 represent low elasticity (less bounce).
     */
    private double elasticity = 0.75;
    private double eElasticity = 0.75;
    /**
     * The friction coefficient used in the simulation. This value affects the rate at
     * which the moving object slows down due to friction. A higher value would mean less
     * friction and thus slower deceleration, while a lower value would result in more
     * friction and faster deceleration.
     */
    private double friction = 0.98;
    private double eFriction = 0.98;
    /**
     * Represents the current speed of an object in the game.
     * This variable is used to control the movement and dynamics
     * of the object within the game loop.
     */
    private double speed = 0.0;
    private double eSpeed = 0.0;

    public MonProgrammeEntity1() {
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

        // Position de départ du player bleu
        x = (int) ((renderingBuffer.getWidth() - 16) * 0.5);
        y = (int) ((renderingBuffer.getHeight() - 16) * 0.5);
        // load physic factors
        speed = (double) config.get("app.physic.entity.player.speed");
        elasticity = (double) config.get("app.physic.entity.player.elasticity");
        friction = (double) config.get("app.physic.entity.player.friction");


        // Position de départ de l'ennemi rouge
        ex = (int) (Math.random() * (renderingBuffer.getWidth() - 16));
        ey = (int) (Math.random() * (renderingBuffer.getHeight() - 16));
        eSpeed = 1.0;
        eElasticity = 0.96;
        eFriction = 0.99;
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
        if (keys[KeyEvent.VK_LEFT]) {
            dx = -speed;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            dx = +speed;

        }
        if (keys[KeyEvent.VK_UP]) {
            dy = -speed;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            dy = +speed;
        }

        // Simulation pour l’ennemi qui suit le player
        if (x+8 != ex+5) {
            edx = Math.min(Math.signum(((x+8) - (ex+5)) * 0.5 * (1 - (eSpeed / ((x+8) - (ex+5))))),2.0);
        }
        if (y != ey) {
            edy = Math.min(Math.signum(((y+8) - (ey+5)) * 0.5 * (1 - (eSpeed / ((y+8) - (ey+5))))),2.0);
        }

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
        x += dx;
        y += dy;

        // application du rebond si collision avec le bord de la zone de jeu
        if (x < -8 || x > renderingBuffer.getWidth() - 8) {
            dx = -dx * elasticity;
        }
        if (y < -8 || y > renderingBuffer.getHeight() - 8) {
            dy = -dy * elasticity;
        }

        // repositionnement dans la zone de jeu si nécessaire
        x = Math.min(Math.max(x, -8), renderingBuffer.getWidth() - 8);
        y = Math.min(Math.max(y, -8), renderingBuffer.getHeight() - 8);

        // application du facteur de friction
        dx *= friction;
        dy *= friction;

        // calcul de la nouvelle position de l'ennemi rouge en fonction de la vitesse courante.
        ex += edx;
        ey += edy;

        // application du rebond si collision avec le bord de la zone de jeu
        if (ex < -5 || ex > renderingBuffer.getWidth() - 5) {
            edx = -edx * eElasticity;
        }
        if (ey < -5 || ey > renderingBuffer.getHeight() - 5) {
            edy = -edy * eElasticity;
        }

        // repositionnement dans la zone de jeu si nécessaire
        ex = Math.min(Math.max(ex, -5), renderingBuffer.getWidth() - 5);
        ey = Math.min(Math.max(ey, -5), renderingBuffer.getHeight() - 5);

        // application du facteur de friction
        edx *= eFriction;
        edy *= eFriction;

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

        // draw player
        g.setColor(Color.BLUE);
        g.fillRect((int) x, (int) y, 16, 16);
        g.setColor(Color.YELLOW);
        g.drawLine((int) x + 8, (int) y + 8, (int) (x + 8 + dx * 4), (int) (y + 8 + dy * 4));

        // draw Ennemi
        g.setColor(Color.RED);
        g.fill(new Ellipse2D.Double((int) ex, (int) ey, 10, 10));
        g.setColor(Color.YELLOW);
        g.drawLine((int) ex + 5, (int) ey + 5, (int) (ex + 5 + edx * 4), (int) (ey + 5 + edy * 4));

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
        MonProgrammeEntity1 prog = new MonProgrammeEntity1();
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