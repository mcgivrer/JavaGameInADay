package examples;

import game.TestGame;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * examples.MonProgrammeDemo1 is a demonstration class that extends game.TestGame
 * and implements KeyListener. It initializes a window, manages a
 * game loop, processes user input, and renders graphics.
 */
public class MonProgrammeDemo1 extends TestGame implements KeyListener {
    private String configFilePath = "/demo1.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    private int x, y;

    public MonProgrammeDemo1() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    /**
     * Initializes the application by setting up test mode,
     * maximum loop count, and interface components such as the window and buffer.
     * Also initializes the position of a blue square within the rendering buffer.
     *
     * It performs the following steps:
     * <ul>
     *   <li>Retrieves and sets the test mode from the configuration.</li>
     *   <li>Retrieves and sets the maximum loop count from the configuration.</li>
     *   <li>Prints a message to indicate initialization of the class.</li>
     *   <li>Initializes the application window by calling the createWindow method.</li>
     *   <li>Initializes the rendering buffer by calling the createBuffer method.</li>
     *   <li>Sets the initial position of a blue square to the center of the rendering buffer.</li>
     * </ul>
     */
    public void initialize() {
        testMode = config.get("app.test");
        maxLoopCount = (int) config.get("app.test.loop.max.count");
        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());

        createWindow();
        createBuffer();

        // blue square position initialization.
        x = (int) ((renderingBuffer.getWidth() - 16) * 0.5);
        y = (int) ((renderingBuffer.getHeight() - 16) * 0.5);
    }

    /**
     * Creates and initializes the application window.
     *
     * <ul>
     *     <li>Sets the window title using configuration.</li>
     *     <li>Sets the preferred size of the window using configuration.</li>
     *     <li>Specifies the default close operation to exit the application.</li>
     *     <li>Packs the window to its preferred size.</li>
     *     <li>Makes the window visible.</li>
     *     <li>Adds the current instance as a key listener to the window.</li>
     *     <li>Creates the buffer strategy for rendering using the specified configuration.</li>
     * </ul>
     */
    private void createWindow() {
        // Create the Window
        window = new JFrame((String) config.get("app.render.window.title"));
        window.setPreferredSize(config.get("app.render.window.size"));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        window.addKeyListener(this);
        window.createBufferStrategy((int) config.get("app.render.buffer.strategy"));
    }

    /**
     * Creates a rendering buffer based on the application's configuration settings.
     * The method retrieves the dimensions for the render buffer from the configuration
     * and initializes a new BufferedImage with the specified width, height, and type.
     * The resultant BufferedImage is stored in the renderingBuffer field.
     */
    private void createBuffer() {
        Dimension renderBufferSize = config.get("app.render.buffer.size");
        renderingBuffer = new BufferedImage(
            renderBufferSize.width, renderBufferSize.height,
            BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * The main game loop that continues to run until an exit is requested or
     * a specified number of loops have completed in test mode.
     *
     * The loop operates based on the configured frames per second (FPS),
     * handling user input, updating the game state, rendering the frame, and
     * controlling the loop timing to match the desired frame rate.
     *
     * This method involves the following steps:
     * - Calculates the frame time based on the FPS configuration.
     * - Enters a loop that continues until an exit condition is met.
     * - In each iteration:
     *   - Processes user input.
     *   - Updates the game state.
     *   - Renders the current frame.
     *   - Increments the loop counter.
     *   - Pauses execution to maintain the frame rate.
     * - Once the loop exits, prints the total number of iterations.
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
     * Pauses the execution of the current thread for a specified duration.
     * This method uses Thread.sleep to achieve the delay.
     *
     * @param delayInMs the amount of time, in milliseconds, to pause the execution of the current thread
     */
    private void waitTime(int delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            System.err.println("Unable to wait 16 ms !");
        }
    }

    /**
     * Handles keyboard input to move an object within the rendering buffer.
     *
     * This method updates the object's position based on the state of the arrow keys:
     * - Moves left if the left arrow key is pressed and ensures the object does not move out of the left boundary.
     * - Moves right if the right arrow key is pressed and ensures the object does not move out of the right boundary.
     * - Moves up if the up arrow key is pressed and ensures the object does not move out of the top boundary.
     * - Moves down if the down arrow key is pressed and ensures the object does not move out of the bottom boundary.
     */
    private void input() {
        if (keys[KeyEvent.VK_LEFT]) {
            x = Math.max(x - 2, 0);
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            x = Math.min(x + 2, renderingBuffer.getWidth());

        }
        if (keys[KeyEvent.VK_UP]) {
            y = Math.max(y - 2, 0);
        }
        if (keys[KeyEvent.VK_DOWN]) {
            y = Math.min(y + 2, renderingBuffer.getHeight());
        }

    }

    private void update() {
    }

    /**
     * Renders the current frame by performing the following operations:
     *
     * 1. Creates a Graphics2D object from the rendering buffer.
     * 2. Clears the rendering buffer by filling it with a black color.
     * 3. Draws a blue rectangle at the specified coordinates (x, y).
     * 4. Disposes of the Graphics2D object.
     * 5. Copies the rendering buffer content to the window's buffer strategy.
     * 6. Disposes of the window's graphics object and shows the buffer strategy.
     */
    private void render() {
        Graphics2D g = renderingBuffer.createGraphics();
        // clear rendering buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight());

        // draw something

        g.setColor(Color.BLUE);
        g.fillRect(x, y, 16, 16);

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
     * Disposes of the application window and prints a termination message to the console.
     *
     * This method performs the following actions:
     * - Calls the dispose method on the window object to release all of its resources.
     * - Prints a formatted message to the console indicating that the termination process has completed for the class.
     */
    private void dispose() {
        window.dispose();
        System.out.printf("# %s est terminé.%n", this.getClass().getSimpleName());
    }


    /**
     * Runs the application workflow which includes configuration loading,
     * initialization, main loop execution, and cleanup.
     *
     * @param args command-line arguments that can be used for configuration
     *             or behavior control during the application run.
     */
    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
        initialize();
        loop();
        dispose();
    }


    /**
     * The main entry point of the application.
     *
     * @param args command-line arguments that can be used for configuration
     *             or behavior control during the application run.
     */
    public static void main(String[] args) {
        MonProgrammeDemo1 prog = new MonProgrammeDemo1();
        prog.run(args);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles key press events by updating the internal key state.
     *
     * @param e the KeyEvent generated when a key is pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    /**
     * Handles key release events by updating the internal key state
     * and invoking a method to request application exit if the escape key is released.
     *
     * @param e the KeyEvent generated when a key is released
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.requestExit();
        }
    }
}