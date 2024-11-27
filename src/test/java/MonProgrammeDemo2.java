import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * MonProgrammeDemo2 is a demonstration program that extends the TestGame
 * framework and implements the KeyListener interface.
 * This class is responsible for initializing a graphical window,
 * handling user input via keyboard events, running the main game loop,
 * and rendering graphics on the screen.
 */
public class MonProgrammeDemo2 extends TestGame implements KeyListener {
    private String configFilePath = "/demo2.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    private int x, y;
    private int dx, dy;

    /**
     * Constructor for the MonProgrammeDemo2 class.
     *
     * This constructor initializes the application by printing a startup message,
     * creating a new Config object with the current instance, and loading the configuration
     * from a specified file path.
     */
    public MonProgrammeDemo2() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    /**
     * Initializes the MonProgrammeDemo2 application.
     * <p>
     * This method performs the initial setup required to run the program,
     * including loading configuration settings, creating the window and
     * rendering buffer, and initializing the position of graphical elements.
     * <p>
     * Specifically, it:
     * <ul>
     *   <li>Loads test mode and maximum loop count settings from the configuration.</li>
     *   <li>Prints a message indicating the initialization of the class.</li>
     *   <li>Creates the application window.</li>
     *   <li>Creates the rendering buffer.</li>
     *   <li>Initializes the starting position of a blue square in the middle of the rendering buffer.</li>
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
     * Creates the main application window.
     *
     * This method initializes the main JFrame window for the application.
     * It sets the window's title, size, and close operation based on the
     * configuration settings. Additionally, it sets this class as the content pane,
     * packs the window's components, makes the window visible, adds a key listener,
     * and sets up the buffer strategy for rendering.
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
     * Creates a rendering buffer for the application.
     *
     * This method initializes the rendering buffer by setting its size and type
     * based on the configuration settings. Specifically, it retrieves the dimensions
     * from the configuration and creates a new {@link BufferedImage} with the specified
     * width, height, and image type {@code BufferedImage.TYPE_INT_ARGB}.
     */
    private void createBuffer() {
        Dimension renderBufferSize = config.get("app.render.buffer.size");
        renderingBuffer = new BufferedImage(
                renderBufferSize.width, renderBufferSize.height,
                BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Executes the main game loop.
     *
     * This method continuously runs the game loop until an exit condition is met. The loop performs the following steps:
     * 1. Calculates the frame time based on the target frames per second (FPS) from the configuration.
     * 2. Checks if the exit has been requested or if it is in test mode and the maximum loop count has been reached.
     * 3. Processes user input.
     * 4. Updates the game state.
     * 5. Renders the game.
     * 6. Increments the loop counter.
     * 7. Waits for the calculated frame time to maintain consistent FPS.
     *
     * After exiting the loop, it prints the total number of game loops executed.
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
     *
     * @param delayInMs the duration for which the thread should pause, in milliseconds.
     */
    private void waitTime(int delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            System.err.println("Unable to wait 16 ms !");
        }
    }

    /**
     * Processes the current state of user input keys and updates the movement deltas.
     *
     * It checks the state of the directional keys (left, right, up, down) and sets
     * the horizontal (dx) and vertical (dy) movement deltas accordingly. After setting
     * the deltas, it scales them down to add some smoothing effect.
     */
    private void input() {
        if (keys[KeyEvent.VK_LEFT]) {
            dx = -2;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            dx = +2;

        }
        if (keys[KeyEvent.VK_UP]) {
            dy = -2;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            dy = +2;
        }
        dx *= 0.6;
        dy *= 0.6;
    }

    /**
     * Updates the current position of graphical elements.
     *
     * This method adjusts the x and y coordinates based on the current movement
     * deltas (dx, dy). The method ensures that the updated position remains within
     * the boundaries of the rendering buffer, clamping the values of x and y to a
     * minimum and maximum value based on the buffer's dimensions.
     */
    private void update() {
        x += dx;
        y += dy;

        x = Math.min(Math.max(x, -8), renderingBuffer.getWidth()-8);
        y = Math.min(Math.max(y, -8), renderingBuffer.getHeight()-8);
    }

    /**
     * Renders the graphical content to the rendering buffer and displays it on the window.
     *
     * This method performs the following steps:
     * 1. Clears the rendering buffer with a black color.
     * 2. Draws a blue rectangle at the position specified by the fields `x` and `y`.
     * 3. Copies the rendered content from the buffer to the main window using the buffer strategy.
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
     * Disposes of the current application window and prints a termination message.
     *
     * This method performs cleanup operations by disposing of the current
     * application window and printing a message indicating that the program
     * has terminated. The message includes the simple name of the class.
     */
    private void dispose() {
        window.dispose();
        System.out.printf("# %s est terminé.%n", this.getClass().getSimpleName());
    }


    /**
     * Runs the main execution of the MonProgrammeDemo2 application.
     *
     * This method starts by displaying the application window title from the configuration,
     * then it proceeds with the initialization, main loop execution, and cleanup disposal.
     *
     * @param args command-line arguments passed to the application.
     */
    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
        initialize();
        loop();
        dispose();
    }


    /**
     * The entry point of the MonProgrammeDemo2 application.
     *
     * This method initializes an instance of the MonProgrammeDemo2 class
     * and invokes its run method.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        MonProgrammeDemo2 prog = new MonProgrammeDemo2();
        prog.run(args);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles the event of a key being pressed.
     *
     * @param e the event object containing details about the key press.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    /**
     * Handles the event of a key being released.
     *
     * @param e the event object containing details about the key release.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.requestExit();
        }
    }
}