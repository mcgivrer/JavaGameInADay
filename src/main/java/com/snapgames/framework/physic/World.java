package com.snapgames.framework.physic;

import com.snapgames.framework.entity.Entity;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class World extends Entity<World> {
    private double gravity;

    public World(String name) {
        super(name);
        this.gravity = 0.0;
        setRect(0, 0, 640, 400);
    }

    public World(String name, double gravity) {
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

    public double getGravity() {
        return gravity;
    }

    public World addArea(WorldArea a) {
        getChildren().add(a);
        return this;
    }
}
