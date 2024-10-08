package com.snapgames.demo.physic;

import java.awt.geom.Rectangle2D;

public class World extends Rectangle2D.Double {
    private String name;
    private double gravity;

    public World(String name, double gravity) {

        this.name = name;
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
}
