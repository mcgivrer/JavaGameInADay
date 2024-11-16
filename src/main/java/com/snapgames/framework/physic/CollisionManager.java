package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import java.util.Collection;
import java.util.List;

public class CollisionManager implements GSystem {

    private final Game app;

    public CollisionManager(Game app) {
        this.app = app;
    }

    public void update(Scene scn, double elapsed) {
        scn.getEntities().values().stream().filter(Entity::isActive).forEach(e1 -> {
            scn.getEntities().values().stream()
                    .filter(e2 -> {
                        return e2.isActive()
                                && !e2.getName().equals(e1.getName());
                    }).forEach(e2 -> {
                        if (e1.intersects(e2)) {
                            e1.getBehaviors().forEach(b -> b.onCollision(e1, e2));
                            e2.getBehaviors().forEach(b -> b.onCollision(e2, e1));
                        }
                    });

        });
    }

    @Override
    public Collection<Class<?>> getDependencies() {

        return List.of(Config.class, PhysicEngine.class,SceneManager.class);
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
    public void stop(Game game) {

    }

    @Override
    public void dispose(Game game) {

    }
}
