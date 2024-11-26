package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.entity.WorldArea;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.snapgames.framework.utils.Log.debug;

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
    private final GameInterface app;
    /**
     * Represents the current time in milliseconds within the PhysicEngine.
     */
    private long currentTime = 0;

    /**
     * Constructs a new PhysicEngine associated with the specified Game application.
     *
     * @param app The Game application that this PhysicEngine will be associated with.
     */
    public PhysicEngine(GameInterface app) {
        this.app = app;

        debug(PhysicEngine.class, "Start of processing");
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
                .forEach(entity -> {
                    World world = scene.getWorld();
                    if (entity.getPhysicType().equals(PhysicType.DYNAMIC)) {
                        applyWorldPhysicRules(entity, world);
                        updatePhysicEntity(entity, world, elapsed);
                    }
                    entity.getBehaviors().forEach(b -> b.update(entity, elapsed));
                    constrainToWorldArea(entity, world);
                });
        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            scene.getActiveCamera().update(elapsed);
        }
    }

    /**
     * Apply all {@link World} rules: apply all world forces on any contained
     * {@link Entity}.
     *
     * @param entity the {@link Entity} to be updated
     * @param world  the {@link World} instance to take into account.
     */
    private void applyWorldPhysicRules(Entity<?> entity, World world) {
        if (world.contains(entity)) {
            // apply all World forces to the PhysicComponent.
            entity.getForces().addAll(world.getForces());
            applyWorldEffects(world, entity);
        }
    }

    /**
     * Applies physical effects of the world on a specified entity.
     * This method iterates through the areas in the world to check if the entity
     * is within or intersects with any area, subsequently applying forces and
     * friction effects from the area to the entity.
     *
     * @param world The world containing multiple areas that may exert forces.
     * @param e     The entity on which the world effects are to be applied.
     */
    private void applyWorldEffects(World world, Entity<?> e) {
        world.getChildren().forEach(a -> {
            if (a.contains(e) || a.intersects(e)) {
                e.getForces().addAll(((WorldArea) a).getForces());
                e.setVelocity(e.getVelocity().multiply(((WorldArea) a).getMaterial().friction));
                e.setContact(true);
            }
        });
    }

    /**
     * Updates the physical state of an entity within the given world using the elapsed time.
     * It adjusts the entity's position, velocity, and acceleration based on its type
     * and applies world constraints and material properties.
     *
     * @param entity  the entity to be updated. Its type determines if it is dynamic or static.
     * @param world   the world in which the entity resides. Used for applying physical constraints.
     * @param elapsed the time elapsed since the last update, in seconds.
     */
    private void updatePhysicEntity(Entity<?> entity, World world, double elapsed) {

        entity.setContact(false);
        switch (entity.getType()) {

            case DYNAMIC -> {

                entity.setAcceleration(new Vector2d().addAll(entity.getForces()).maximize(0.3));
                entity.setVelocity(entity.getVelocity().add(entity.getAcceleration().multiply(0.5 * elapsed)).maximize(0.5));
                entity.setPosition(entity.getPosition().add(entity.getVelocity().multiply(elapsed)));

                entity.getForces().clear();
                constrainToWorldArea(entity, world);

                // apply Material roughness on velocity
                entity.setVelocity(entity.getVelocity().multiply(entity.getMaterial().friction));

            }
            case STATIC -> {
                // TODO define processing for static entity
            }
            default -> {
                // TODO define default processing (if any)
            }
        }
    }


    /**
     * Ensures the given entity remains within the boundaries of the world defined in the scene.
     * If the entity exceeds the world's boundaries, it will be repositioned and its velocity will be
     * modified based on its elasticity to simulate a collision response.
     *
     * @param entity the  {@link Entity} to be updated
     * @param world  the {@link World} instance to take into account.
     */
    private void constrainToWorldArea(Entity<?> entity, World world) {
        Vector2d position = entity.getPosition();
        Vector2d size = new Vector2d(entity.getWidth(), entity.getHeight());

        if (position.x < world.getX()) {
            position.x = world.getX();
        }
        if (position.x + size.x > world.getX() + world.getWidth()) {
            position.x = world.getX() + world.getWidth() - size.x;
        }
        if (position.y < world.getY()) {
            position.y = world.getY();
        }
        if (position.y + size.y > world.getY() + world.getHeight()) {
            position.y = world.getY() + world.getHeight() - size.y;
        }
        entity.setPosition(position);
    }

    /**
     * Disposes of the resources and performs cleanup tasks associated with the PhysicEngine.
     */
    public void dispose() {
        debug(PhysicEngine.class, "End of processing");

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