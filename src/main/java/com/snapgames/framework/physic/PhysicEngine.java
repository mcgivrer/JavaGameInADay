package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.entity.WorldArea;
import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.scene.Scene;

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
public class PhysicEngine {
    private final Game app;

    public PhysicEngine(Game app) {
        this.app = app;

        debug(PhysicEngine.class, "Start of processing");
    }

    public void update(Scene scene, long elapsed) {
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

    private void applyWorldEffects(Scene scene, Entity<?> e) {
        scene.getWorld().getChildren().forEach(a -> {
            if (a.contains(e) || a.intersects(e)) {
                e.getForces().addAll(((WorldArea) a).getForces());
                e.dx *= ((WorldArea) a).getMaterial().friction;
                e.dy *= ((WorldArea) a).getMaterial().friction;
                e.setContact(true);
            }
        });
    }

    private void applyPhysicRules(Scene scene, long elapsed, Entity<?> e) {
        e.ax = 0;
        e.ay = 0;
        e.addForce(0.0, -scene.getWorld().getGravity() / e.getMass());
        e.getForces().forEach(f -> {
            e.ax += f.getX();
            e.ay += f.getY();
        });

        e.dx += 0.5 * e.ax * elapsed * elapsed * 0.001;
        e.dy += 0.5 * e.ay * elapsed * elapsed * 0.001;

        e.x += e.dx * (elapsed);
        e.y += e.dy * (elapsed);

        if (e.hasContact()) {
            e.dx *= e.getMaterial().friction;
            e.dy *= e.getMaterial().friction;
        }
        e.dx = Math.signum(e.dx) * Math.min(Math.abs(e.dx), 16.0);
        e.dy = Math.signum(e.dy) * Math.min(Math.abs(e.dy), 16.0);
    }

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

    public void dispose() {
        debug(PhysicEngine.class, "End of processing");

    }

    public void resetForces(Scene scene) {
        scene.getEntities().values().forEach(e -> e.getForces().clear());
    }
}