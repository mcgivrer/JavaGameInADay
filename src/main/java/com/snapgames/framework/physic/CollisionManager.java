package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.scene.Scene;

import static com.snapgames.framework.utils.Log.debug;

public class CollisionManager {

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

    public void dispose() {
        debug(CollisionManager.class, "End of processing");
    }
}
