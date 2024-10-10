package com.snapgames.framework.physic;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class World extends Rectangle2D.Double {
    private String name;
    private double gravity;
    private List<WorldArea> areas = new ArrayList<>();

    public World() {
        this.gravity = 0.0;
        setRect(0, 0, 640, 400);
    }

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

    public Iterable<WorldArea> getAreas() {
        return areas;
    }

    public World addArea(WorldArea a) {
        areas.add(a);
        return this;
    }
}
