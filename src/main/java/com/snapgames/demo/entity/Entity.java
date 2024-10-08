package com.snapgames.demo.entity;

import com.snapgames.demo.physic.Material;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Entity extends Rectangle2D.Double {
    private static long index = 0;
    public double dx, dy;
    public Material material = Material.DEFAULT;
    long id = index++;
    String name = "entity_" + (id);
    Color color = Color.RED;
    private double mass = 1.0;

    public Entity() {
    }

    public Entity(String name) {
        setName(name);
    }

    private Entity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Entity setPosition(double x, double y) {
        super.setRect(x, y, width, height);
        return this;
    }

    public Entity setSize(double w, double h) {
        super.setRect(x, y, w, h);
        return this;
    }

    public Entity setColor(Color c) {
        this.color = c;
        return this;
    }

    public Entity setMaterial(Material mat) {
        this.material = mat;
        return this;
    }

    public double getMass() {
        return mass;
    }

    public Entity setMass(double m) {
        this.mass = m;
        return this;
    }
}
