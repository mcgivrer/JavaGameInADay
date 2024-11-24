package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;
import com.snapgames.framework.utils.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link PhysicEngine} service compute everything about move and update of all the entities in a {@link Scene}.
 * It will update any object position and animation and also move {@link com.snapgames.framework.entity.Camera} accordingly.
 *
 * <p>Usage:</p>
 * <pre><code>
 * PhysicEngine phy = new PhysicEngine(app);
 * // layer in the game loop:
 * phy.update(currentActiveScene, elapsedTimeSincePreviousCall);
 * </code></pre>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class PhysicEngine implements GSystem {
    /**
     * The Game application instance associated with this PhysicEngine.
     * This application serves as the main context for running the physics simulations
     * and managing game states, including the initialization, update loops, and disposal of resources.
     */
    private final Game app;
    /**
     * Represents the current time in milliseconds within the PhysicEngine.
     */
    private long currentTime = 0;

    /**
     * Constructs a new PhysicEngine associated with the specified Game application.
     *
     * @param app The Game application that this PhysicEngine will be associated with.
     */
    public PhysicEngine(Game app) {
        this.app = app;
    }

    /**
     * Updates the state of the scene and its entities based on the elapsed time.
     * This includes updating the physical states of dynamic entities, keeping entities
     * within the world boundaries, and updating behaviors for all entities.
     * Additionally, the active camera is also updated if present.
     *
     * @param scene   The scene containing the entities and the world they reside in.
     * @param elapsed The time elapsed since the last update, in milliseconds.
     */
    private void update(Scene scene, double elapsed) {
        scene.getEntities().values().stream()
                .filter(Entity::isActive)
                .forEach(e -> {
                    if (e.getPhysicType() == PhysicType.DYNAMIC) {
                        e.setContact(false);
                        applyWorldEffects(scene, e);
                        applyPhysicRules(scene, elapsed, e);
                        keepEntityIntoWorld(scene, e);
                    }
                    e.getBehaviors().forEach(b -> b.update(e, elapsed));
                });
        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            scene.getActiveCamera().update(elapsed);
        }
    }

    /**
     * Applies the world's physical effects to the specified entity within the scene.
     *
     * @param scene The scene containing the world and its children entities.
     * @param e     The entity to which the world effects are to be applied.
     */
    private void applyWorldEffects(Scene scene, Entity<?> e) {
        scene.getWorld().getChildren().forEach(a -> {
            if (a.contains(e) || a.intersects(e)) {
                e.getForces().addAll(((WorldArea) a).getForces());
                e.setVelocity(e.getVelocity().getX() * ((WorldArea) a).getMaterial().friction,
                        e.getVelocity().getY() * ((WorldArea) a).getMaterial().friction);
                e.setContact(true);
            }
        });
    }

    /**
     * Applies the physical rules to a given entity within the specified scene.
     *
     * @param scene   The scene containing the world and other entities.
     * @param elapsed The time elapsed since the last update, in milliseconds.
     * @param e       The entity to which the physical rules will be applied.
     */
    private void applyPhysicRules(Scene scene, double elapsed, Entity<?> e) {
        e.setAcceleration(0, 0);

        e.addForce(0.0, -scene.getWorld().getGravity() / e.getMass());
        e.getForces().forEach(f -> {
            e.setAcceleration(e.getAcceleration().getX() + f.getX(),
                    e.getAcceleration().getY() + f.getY());
        });
        if (e.getName().equals("player") && !e.getForces().isEmpty()) {
            e.getForces().forEach(f -> {
                Log.debug(PhysicEngine.class, "f:%s", e.getAcceleration());
            });
            Log.debug(PhysicEngine.class, "acc:%s", e.getAcceleration());
        }
        e.dx += 0.5 * e.ax * elapsed * elapsed * 0.001;
        e.dy += 0.5 * e.ay * elapsed * elapsed * 0.001;


        e.setPosition(e.getPosition().getX() + e.getVelocity().getX() * elapsed,
                e.getPosition().getY() + e.getVelocity().getY() * elapsed);

        if (e.hasContact()) {
            e.setVelocity(e.getVelocity().getX() * e.getMaterial().friction,
                    e.getVelocity().getY() * e.getMaterial().friction);
        }
        // limit speed to 16.0 max
        e.setVelocity(Math.signum(e.getVelocity().getX()) * Math.min(Math.abs(e.getVelocity().getX()), 16.0),
                Math.signum(e.getVelocity().getY()) * Math.min(Math.abs(e.getVelocity().getY()), 16.0)
        );
    }

    /**
     * Ensures the given entity remains within the boundaries of the world defined in the scene.
     * If the entity exceeds the world's boundaries, it will be repositioned and its velocity will be
     * modified based on its elasticity to simulate a collision response.
     *
     * @param scene The scene containing the world and entities.
     * @param e     The entity to keep within the world boundaries.
     */
    private void keepEntityIntoWorld(Scene scene, Entity<?> e) {
        World w = scene.getWorld();
        if (!w.contains(e) || w.intersects(e)) {
            if (e.x < w.x) {
                e.x = w.x;
                e.dx *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
            if (e.x + e.width > w.width) {
                e.x = w.width - e.width;
                e.dx *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
            if (e.y < w.y) {
                e.y = w.y;
                e.dy *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
            if (e.y > w.height - e.height) {
                e.y = w.height - e.height;
                e.dy *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
        }
    }

    /**
     * Disposes of the resources and performs cleanup tasks associated with the PhysicEngine.
     */
    public void dispose() {

    }

    /**
     * Resets the forces acting on all entities within the specified scene.
     *
     * @param scene The scene containing the entities whose forces need to be reset.
     */
    public void resetForces(Scene scene) {
        scene.getEntities().values().forEach(e -> e.getForces().clear());
    }

    /**
     * Retrieves a list of classes that are dependencies for the PhysicEngine.
     *
     * @return A list containing the classes Config and SceneManager, which are necessary for the PhysicEngine.
     */
    @Override
    public List<Class<?>> getDependencies() {
        return List.of(Config.class, SceneManager.class);
    }

    /**
     * Initializes the PhysicEngine with the specified game instance.
     *
     * @param game The game instance with which this PhysicEngine is to be initialized.
     */
    @Override
    public void initialize(GameInterface game) {

    }

    /**
     * Starts the PhysicEngine with the specified game instance. This method is
     * invoked to initialize and begin the physics processing within the context
     * of the provided game instance.
     *
     * @param game The game interface instance with which this PhysicEngine is to
     *             be started.
     */
    @Override
    public void start(GameInterface game) {

    }

    /**
     * Processes the game state for the given elapsed time and updates the scene if the game is not paused.
     * This includes managing scenes and integrating game-related statistics.
     *
     * @param game    The current game interface instance.
     * @param elapsed The time elapsed since the last update in milliseconds.
     * @param stats   A map containing various statistics and metrics related to the game's state.
     */
    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        if (game.isNotPaused()) {
            SceneManager sm = SystemManager.get(SceneManager.class);
            update(sm.getActiveScene(), elapsed);
        }
    }

    /**
     * Performs post-processing tasks for the PhysicEngine after the main processing cycle.
     * This method resets the forces acting on all entities within the active scene of the game.
     *
     * @param game The current game interface instance.
     */
    @Override
    public void postProcess(GameInterface game) {
    }

    /**
     * Stops the PhysicEngine and performs necessary cleanup tasks.
     *
     * @param game The game interface instance that provides contextual information and control
     *             for the game application.
     */
    @Override
    public void stop(GameInterface game) {

    }

    /**
     * Disposes of the resources and performs cleanup tasks associated with the PhysicEngine.
     *
     * @param game The game interface instance that provides contextual information and control
     *             for the game application.
     */
    @Override
    public void dispose(GameInterface game) {

    }
}