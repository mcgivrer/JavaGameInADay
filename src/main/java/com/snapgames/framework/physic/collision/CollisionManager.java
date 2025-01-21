package com.snapgames.framework.physic.collision;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.snapgames.framework.utils.Log.debug;

public class CollisionManager implements GSystem {

    private final Game app;
    private QuadTree entities;

    public CollisionManager(Game app) {
        this.app = app;
        Config config = SystemManager.get(Config.class);
        Rectangle2D playArea = config.get("app.physic.world.play.area.size");
        this.entities = new QuadTree(playArea.getWidth(), playArea.getHeight());
        this.entities.setMaxLevels(5);
        this.entities.setMaxLevels(5);

        debug(CollisionManager.class, "Start of processing");
    }

    public void update(Scene scn, double elapsed) {
        //Insert all entities in the quadtree.
        getQuadTree().clear();
        scn.getEntities().values().stream().filter(Entity::isActive).forEach(e -> getQuadTree().insert(e));

        // proceed to collision detection.
        scn.getEntities().values().stream().filter(Entity::isActive).forEach(e1 -> {
            getQuadTree().retrieve((List<Collidable>) new ArrayList<Collidable>(), e1).stream()
                    .filter(e2 -> {
                        return ((Entity<?>) e2).isActive()
                                && !((Entity<?>) e2).getName().equals(e1.getName());
                    }).forEach(e2 -> {
                        if (e1.intersects(((Entity<?>) e2))) {
                            e1.getBehaviors().forEach(b -> b.onCollision(e1, ((Entity<?>) e2)));
                            ((Entity<?>) e2).getBehaviors().forEach(b -> b.onCollision(((Entity<?>) e2), e1));
                        }
                    });

        });
    }

    public QuadTree getQuadTree() {
        return entities;
    }

    @Override
    public Collection<Class<?>> getDependencies() {

        return List.of(Config.class, PhysicEngine.class, SceneManager.class);
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
