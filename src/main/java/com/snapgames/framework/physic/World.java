package com.snapgames.framework.physic;

import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.entity.WorldArea;
import com.snapgames.framework.physic.math.Vector2d;

public class World extends Entity<World> {
    private Vector2d gravity;

    public World(String name) {
        super(name);
        this.gravity = new Vector2d();
        setRect(0, 0, 640, 400);
    }

    public World(String name, Vector2d gravity) {
        super(name);
        this.gravity = gravity;
    }

    public World setSize(double w, double h) {
        setRect(x, y, w, h);
        return this;
    }

    public World setPosition(double x, double y) {
        setRect(x, y, width, height);
        return this;
    }

    public World addArea(WorldArea a) {
        getChildren().add(a);
        return this;
    }
}
