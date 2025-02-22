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
    /**
     * Represents the target frame rate for the game, defined as frames per second (FPS).
     *
     * This constant is used to control the pacing of the game loop by specifying
     * how many frames should be rendered per second. A higher value results in a
     * smoother experience but requires more processing power, whereas a lower value
     * reduces system demands but may appear less smooth.
     *
     * The value of FPS is primarily utilized in the game loop to calculate the
     * time allocated for each frame and ensure consistent frame timing. The main
     * loop adjusts its sleep duration to maintain this target frame rate, improving
     * the overall experience by avoiding frame drops or excessive performance spikes.
     */
    private static final double FPS = 60.0;

    /**
     * A boolean flag indicating whether the game has been requested to exit.
     *
     * This variable is used as a control mechanism in the game loop to determine
     * if the game should terminate its execution. When set to {@code true}, the
     * main game loop will recognize the exit request and initiate the termination
     * process. It is typically updated through corresponding methods like
     * {@code requestExit()} or {@code setExit(boolean)}.
     */
    public static boolean exit = false;

    /**
     * Represents the game's pause state.
     *
     * This flag determines whether the game is currently paused. When set to true,
     * the game loop will suspend its main execution, pausing updates and rendering.
     * When set to false, the game will resume normal operation. The pause state can
     * be toggled programmatically or through user interaction.
     */
    private boolean pause = false;

    /**
     * Represents the debug level of the game.
     * The debug level controls the verbosity of debug output,
     * with higher levels providing more detailed information
     * about the game's internal state and logic.
     *
     * A debug level of 0 typically indicates that debugging is disabled,
     * while higher values enable various degrees of debugging features.
     * The value of this variable can be adjusted dynamically during runtime
     * through appropriate methods to modify the debugging behavior.
     */
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

    /**
     * Sets the debug level of the game. The debug level controls the verbosity
     * of debugging information and is used to enable or disable specific debugging features.
     *
     * @param dl The desired debug level to set. Acceptable values typically
     *           range from 0 (no debugging) to a maximum predefined level.
     */
    public void setDebug(int dl) {
        debug = dl;
    }

    /**
     * Retrieves the current debug level of the game. The debug level determines
     * the verbosity of debugging output and active debugging features.
     *
     * @return the current debug level as an integer.
     */
    public int getDebug() {
        return debug;
    }

    /**
     * Sets the pause state of the game.
     *
     * @param p true to pause the game, false to resume it.
     */
    public void setPause(boolean p) {
        this.pause = p;
    }

    /**
     * Sets the exit flag for the game, indicating whether the game should terminate.
     *
     * @param e true to request game termination, false otherwise.
     */
    @Override
    public void setExit(boolean e) {
        this.exit = e;
    }

    /**
     * Determines whether an exit has been requested for the game.
     *
     * @return true if an exit has been requested, false otherwise.
     */
    @Override
    public boolean isExitRequested() {
        return exit;
    }

    /**
     * Determines whether the game is currently not in a paused state.
     *
     * @return true if the game is not paused, false otherwise.
     */
    public boolean isNotPaused() {
        return !pause;
    }

    /**
     * Requests the game to exit by setting the exit flag if the user confirms
     * their intention to terminate the game.
     *
     * This method invokes the {@code confirmExit()} method to display a
     * confirmation dialog to the user. If the user confirms the exit, the
     * method sets the {@code exit} flag to {@code true}, indicating that the
     * game's main loop should terminate.
     */
    public void requestExit() {
        if (confirmExit()) {
            exit = true;
        }
    }

    /**
     * Displays a confirmation dialog to the user to confirm if they want to exit the game.
     *
     * This method pauses the game, invokes a confirmation dialog, and resumes the game
     * regardless of the user's choice. If the user confirms exit, it returns {@code true};
     * otherwise, it returns {@code false}.
     *
     * @return {@code true} if the user confirms the exit, {@code false} otherwise.
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
