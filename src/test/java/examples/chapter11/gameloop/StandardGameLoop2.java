package examples.chapter11.gameloop;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.utils.Config;
import examples.chapter11.gfx.Renderer;
import examples.chapter11.physic.PhysicEngine;

import java.util.Map;

public class StandardGameLoop2 implements GameLoop {
    private final GameInterface game;
    private final Config config;
    private final Renderer render;
    private final PhysicEngine physicEngine;
    private final InputListener inputListener;
    private final Map<String, Object> stats;

    public StandardGameLoop2(GameInterface game,
                             Config config,
                             Renderer render,
                             PhysicEngine physicEngine,
                             InputListener inputListener,
                             Map<String, Object> stats) {
        this.game = game;
        this.config = config;
        this.render = render;
        this.physicEngine = physicEngine;
        this.inputListener = inputListener;
        this.stats = stats;

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
    public void process(GameInterface game, Scene scene) {
        int loopCount = 0;
        int frameTime = 1000 / (int) (config.get("app.render.fps"));
        long elapsed = 0;
        long startLoop = System.currentTimeMillis();
        long endLoop = startLoop;
        while (!game.isExitRequested()) {
            elapsed = endLoop - startLoop;
            startLoop = endLoop;
            input(scene);
            if (game.isNotPaused()) {
                update(scene, elapsed);
            }
            render(scene, elapsed);
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
        scene.input(inputListener);
        scene.getEntities().values().stream()
                .filter(Entity::isActive)
                .forEach(e -> e.getBehaviors()
                        .forEach(b ->
                                b.input(inputListener, e)));
    }

    @Override
    public void update(Scene scene, double elapsed) {
        physicEngine.process(game, elapsed, stats);
    }

    @Override
    public void render(Scene scene, double elapsed) {
        render.process(game, elapsed, stats);
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