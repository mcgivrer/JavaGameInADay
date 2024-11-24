package com.snapgames.framework;

import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.CollisionManager;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Log;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static com.snapgames.framework.utils.I18n.getI18n;

/**
 * The Game class represents the core game application, handling initialization, the main game loop,
 * and game termination processes. It extends JPanel and implements the GameInterface.
 */
public class Game extends JPanel implements GameInterface {
    private static final double FPS = 60.0;

    // Game exit request flag.
    public static boolean exit = false;

    // internal Pause flag
    private boolean pause = false;

    // debug level
    private int debug = 1;

    /**
     * Constructs a new Game instance, initializing the application and logging essential startup information.
     * The log includes app name, app version, JDK version, Java home directory, and classpath.
     */
    public Game() {
        super();
        Log.info(Game.class,"Initialization application %s (%s) %n- running on JDK %s %n- at %s %n- with classpath = %s%n",
            getI18n("app.name"),
            getI18n("app.version"),
            System.getProperty("java.version"),
            System.getProperty("java.home"),
            System.getProperty("java.class.path"));
    }

    /**
     * Executes the main game run sequence, consisting of initialization, looping, and disposal.
     *
     * @param args An array of command-line arguments used for initialization.
     */
    public void run(String[] args) {
        init(args);
        loop();
        dispose();
    }

    /**
     * Initializes the game by setting up the necessary systems and parsing command line arguments.
     *
     * @param args An array of command line arguments.
     */
    private void init(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        lArgs.forEach(s -> {
            Log.info(Game.class, String.format("Argument: %s", s));
        });

       SystemManager.setParent(this);

        Config config = new Config(this);
        config.parseArgs(args);
        SystemManager.add(config);

        SystemManager.add(new PhysicEngine(this));
        SystemManager.add(new CollisionManager(this));
        SystemManager.add(new Renderer(this));
        SystemManager.add(new InputListener(this));
        SystemManager.add(new SceneManager(this));

        SystemManager.initialize();

        SystemManager.start(this);
    }

    /**
     * Main game loop that runs continuously, processing and updating subsystems.
     * This loop executes until an exit request is detected.
     *
     * The loop calculates elapsed time for each iteration and performs the following tasks:
     * 1. Calculate the time difference between the current and previous iterations.
     * 2. Invoke the SystemManager to process game subsystems using the elapsed time.
     * 3. Execute post-processing on all subsystems.
     * 4. Control the frame rate to maintain a consistent FPS (Frames Per Second).
     *
     * The loop also handles interruptions during the sleep period by catching
     * InterruptedException and rethrowing it as a RuntimeException.
     */
    private void loop() {

        long startTime = System.currentTimeMillis();
        long endTime = startTime;
        double elapsed = 0;
        while (!isExitRequested()) {
            elapsed = endTime - startTime;
            startTime = endTime;
            SystemManager.process(elapsed);
            SystemManager.postProcess();
            endTime = System.currentTimeMillis();
            try {
                Thread.sleep((long) (elapsed < (1000 / FPS) ? (1000 / FPS) - elapsed : 1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Cleans up resources and terminates the application.
     * <p>
     * This method calls the static {@code dispose()} method of
     * {@code SystemManager} to clean up the game systems and
     * performs logging to indicate the end of the application.
     */
    private void dispose() {
        SystemManager.dispose();
        Log.info(Game.class,"End of application ");
    }

    /**
     * The entry point of the application, initializing and running the game.
     *
     * @param argc An array of command-line arguments passed to the application.
     */
    public static void main(String[] argc) {
        Game app = new Game();
        app.run(argc);
    }

    /**
     * Checks if the current debug level is greater than the specified debug level.
     *
     * @param debugLevel The debug level to compare against.
     * @return true if the current debug level is greater than the specified debug level, false otherwise.
     */
    public boolean isDebugGreaterThan(int debugLevel) {
        return debug > debugLevel;
    }

    public void setDebug(int dl) {
        debug = dl;
    }

    public int getDebug() {
        return debug;
    }

    public void setPause(boolean p) {
        this.pause = p;
    }

    @Override
    public void setExit(boolean e) {
        this.exit = e;
    }

    @Override
    public boolean isExitRequested() {
        return exit;
    }

    public boolean isNotPaused() {
        return !pause;
    }

    public void requestExit() {
        if (confirmExit()) {
            exit = true;
        }
    }

    /**
     * Prompts the user with a confirmation dialog to confirm if they wish to exit the game.
     * Pauses the game while awaiting user input and resumes it after receiving a response.
     *
     * @return true if the user confirms the exit, false otherwise.
     */
    public boolean confirmExit() {
        boolean status = false;
        setPause(true);
        Renderer renderer = SystemManager.get(Renderer.class);
        int response = JOptionPane.showConfirmDialog(renderer.getWindow(),
            getI18n("app.exit.confirm.message"),
            getI18n("app.exit.confirm.title"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            status = true;
        }
        setPause(false);
        return status;
    }

}
