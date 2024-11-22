package com.snapgames.framework.physic;

import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.entity.WorldArea;
import com.snapgames.framework.physic.math.Vector2d;

public class World extends Entity<World> {

    private Vector2d gravity = new Vector2d();

    public World(String name) {
        super(name);
        setRect(0, 0, 640, 400);
    }

    public World(String name, Vector2d gravity) {
        super(name);
        this.gravity = gravity;
        getForces().add(gravity.negate().multiply(0.01));
    }

    public World setSize(double w, double h) {
        setRect(x, y, w, h);
        return this;
    }

    public World add(WorldArea a) {
        getChildren().add(a);
        return this;
    }
}
