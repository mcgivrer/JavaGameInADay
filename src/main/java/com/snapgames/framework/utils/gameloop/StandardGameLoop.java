package com.snapgames.framework.utils.gameloop;


import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

public class StandardGameLoop implements GameLoop {
    private final GameInterface game;

    public StandardGameLoop(GameInterface game) {
        this.game = game;
    }


    /**
     * Executes the game loop for the game.
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
    @Override
    public void process(GameInterface game) {
        SceneManager scm = SystemManager.get(SceneManager.class);
        Config config = SystemManager.get(Config.class);
        Scene scene = scm.getActiveScene();
        int loopCount = 0;
        int frameTime = 1000 / (int) (config.get("app.render.fps"));
        long elapsed = 0;
        long startLoop = System.currentTimeMillis();
        long endLoop = startLoop;
        while (!game.isExitRequested()
                && ((game.isTestMode()
                && loopCount < game.getMaxLoopCount()) || !game.isTestMode())) {
            elapsed = endLoop - startLoop;
            startLoop = endLoop;
            SystemManager.process(elapsed);
            SystemManager.postProcess();
            loopCount++;
            waitTime(frameTime);
            endLoop = System.currentTimeMillis();
        }
        System.out.printf("=> Game loops %d times%n", loopCount);
    }

    @Override
    public void input(Scene scene) {

    }

    /**
     * Pauses the execution of the current thread for the specified amount of time.
     *
     * @param delayInMs the time to wait, in milliseconds. This value determines how long the
     *                  thread will sleep. If interrupted during sleep, an error message will
     *                  be printed to the standard error stream.
     */
    public void waitTime(int delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            System.err.println("Unable to wait 16 ms !");
        }
    }

    @Override
    public void update(Scene scene, double elapsed) {

    }

    @Override
    public void render(Scene scene) {

    }

    @Override
    public void setExit(boolean exitRequest) {
        game.setExit(exitRequest);
    }

    @Override
    public void setPause(boolean pauseRequest) {
        game.setPause(pauseRequest);
    }
}