package com.snapgames.demo.physic;

import com.snapgames.demo.Test001App;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.scene.Scene;

import java.io.Serializable;

public class PhysicEngine implements Serializable {
    private final Test001App app;

    public PhysicEngine(Test001App app) {
        this.app = app;
    }

    public void update(Scene scene, long elapsed) {
        scene.getEntities().values().forEach(e -> {
            applyWorldEffects(scene, e);
            applyPhysicRules(scene, elapsed, e);
            keepEntityIntoWorld(scene, e);
        });
    }

    public void applyWorldEffects(Scene scene, Entity e) {
        scene.getWorld().getAreas().forEach(a -> {
            if (a.contains(e)) {
                e.getForces().addAll(a.getForces());
            }
        });
    }

    public void applyPhysicRules(Scene scene, long elapsed, Entity e) {
        e.addForce(0.0, -scene.getWorld().getGravity());
        e.getForces().forEach(f -> {
            e.ax += f.getX();
            e.ay += f.getY();
        });

        e.dx = 0.5 * e.ax / e.getMass();
        e.dy = 0.5 * e.ay / e.getMass();

        e.x += e.dx * (elapsed * 0.0000001);
        e.y += e.dy * (elapsed * 0.0000001);

        e.dx *= e.material.friction;
        e.dy *= e.material.friction;

        e.dx = Math.signum(e.dx) * Math.min(Math.abs(e.dx), 8.0);
        e.dy = Math.signum(e.dy) * Math.min(Math.abs(e.dy), 8.0);

        e.ax = 0.0;
        e.ay = 0.0;
        e.getForces().clear();
    }

    public void keepEntityIntoWorld(Scene scene, Entity e) {
        World w = scene.getWorld();
        if (!w.contains(e)) {
            if (e.x < w.x) {
                e.x = w.x;
                e.dx = -e.material.elasticity * e.dx;
            }
            if (e.x + e.width > w.width) {
                e.x = w.width - e.width;
                e.dx = -e.material.elasticity * e.dx;
            }
            if (e.y < w.y) {
                e.y = w.y;
                e.dy = -e.material.elasticity * e.dy;
            }
            if (e.y + e.height > w.height) {
                e.y = w.height - e.height;
                e.dy = -e.material.elasticity * e.dy;
            }
        }
    }
}