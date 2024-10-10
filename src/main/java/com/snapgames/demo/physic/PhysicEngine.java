package com.snapgames.demo.physic;

import com.snapgames.demo.Game;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.scene.Scene;

import java.io.Serializable;
import java.util.Optional;

public class PhysicEngine implements Serializable {
    private final Game app;

    public PhysicEngine(Game app) {
        this.app = app;
    }

    public void update(Scene scene, long elapsed) {
        scene.getEntities().values().stream()
                .filter(e -> e.isActive() && !(e instanceof WorldArea))
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

    public void applyWorldEffects(Scene scene, Entity<?> e) {
        scene.getWorld().getAreas().forEach(a -> {
            if (a.contains(e) || a.intersects(e)) {
                e.getForces().addAll(a.getForces());
                e.setContact(true);
            }
        });
    }

    public void applyPhysicRules(Scene scene, long elapsed, Entity<?> e) {
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

        // reset acceleration and forces
        e.getForces().clear();
        e.ax = 0;
        e.ay = 0;
    }

    public void keepEntityIntoWorld(Scene scene, Entity<?> e) {
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
}