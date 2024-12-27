package utils.gameloop;

import examples.MonProgrammeGameLoop1;
import game.Game;
import scenes.Scene;

import java.io.Serializable;

public class StandardGameLoop implements GameLoop {
    private final MonProgrammeGameLoop1 game;

    public StandardGameLoop(MonProgrammeGameLoop1 monProgrammeGameLoop1) {
        this.game = monProgrammeGameLoop1;
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
    public void process(Game game) {
        Scene scene = game.getCurrentScene();
        int loopCount = 0;
        int frameTime = 1000 / (int) (game.getConfig().get("app.render.fps"));
        long elapsed = 0;
        long startLoop = System.currentTimeMillis();
        long endLoop = startLoop;
        while (!game.isExitRequested()
                && ((game.isTestMode()
                && loopCount < game.getMaxLoopCount()) || !game.isTestMode())) {
            elapsed = endLoop - startLoop;
            startLoop = endLoop;
            input(scene);
            if (game.isNotPaused()) {
                update(scene, elapsed);
            }
            render(scene);
            loopCount++;
            waitTime(frameTime);
            endLoop = System.currentTimeMillis();
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
    public void waitTime(int delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            System.err.println("Unable to wait 16 ms !");
        }
    }

    @Override
    public void input(Scene scene) {
        game.input(scene);
    }

    @Override
    public void update(Scene scene, double elapsed) {
        game.update(scene);
    }

    @Override
    public void render(Scene scene) {
        game.render(scene);
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