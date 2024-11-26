package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.snapgames.framework.utils.Log.debug;

public class CollisionManager implements GSystem {

    private final Game app;

    public CollisionManager(Game app) {
        this.app = app;
        debug(CollisionManager.class, "Start of processing");

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
    public void initialize(GameInterface game) {

    }

    @Override
    public void start(GameInterface game) {

    }

    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        if (game.isNotPaused()) {
            SceneManager sm = SystemManager.get(SceneManager.class);
            update(sm.getActiveScene(), elapsed);
        }
    }

    @Override
    public void stop(GameInterface game) {

    }

    @Override
    public void dispose(GameInterface game) {

    }
}
