package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import java.util.List;
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
    private final Game app;
    private long currentTime = 0;

    public PhysicEngine(Game app) {
        this.app = app;

        debug(PhysicEngine.class, "Start of processing");
    }

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

    private void updatePhysicEntity(Entity<?> entity, World world, double elapsed) {

        entity.setContact(false);
        switch (entity.getType()) {

            case DYNAMIC -> {

                entity.setAcceleration(new Vector2d().addAll(entity.getForces()).maximize(0.5));
                entity.setVelocity(entity.getVelocity().add(entity.getAcceleration().multiply(0.5 * elapsed)).maximize(1.0));
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
        }
    }

    /**
     * Keep all {@link Entity} position into the
     * {@link World}'s play area.
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

    public void dispose() {
        debug(PhysicEngine.class, "End of processing");

    }

    public void resetForces(Scene scene) {
        scene.getEntities().values().forEach(e -> e.getForces().clear());
    }

    @Override
    public List<Class<?>> getDependencies() {
        return List.of(Config.class, SceneManager.class);
    }

    @Override
    public void initialize(Game game) {

    }

    @Override
    public void start(Game game) {

    }

    @Override
    public void process(Game game, double elapsed) {
        if (game.isNotPaused()) {
            SceneManager sm = SystemManager.get(SceneManager.class);
            update(sm.getActiveScene(), elapsed);
        }
    }

    @Override
    public void postProcess(Game game) {
        SceneManager sm = SystemManager.get(SceneManager.class);
        resetForces(sm.getActiveScene());
    }

    @Override
    public void stop(Game game) {

    }

    @Override
    public void dispose(Game game) {

    }
}