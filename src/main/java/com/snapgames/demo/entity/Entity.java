package com.snapgames.demo.entity;

import com.snapgames.demo.physic.Material;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Entity extends Rectangle2D.Double {
    private static long index = 0;
    List<Point2D> forces = new ArrayList<>();

    public double ax, ay;
    public double dx, dy;
    private Material material = Material.DEFAULT;
    long id = index++;
    String name = "entity_" + (id);
    Color color = Color.RED;
    private double mass = 1.0;

    private boolean active = true;
    private boolean contact = false;

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

    public Material getMaterial() {
        return material;
    }

    public Entity setMass(double m) {
        this.mass = m;
        return this;
    }

    public Entity addForce(double fx, double fy) {
        forces.add(new Point2D.Double(fx, fy));
        return this;
    }

    public Entity addForce(Point2D f) {
        forces.add(f);
        return this;
    }

    public List<Point2D> getForces() {
        return forces;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Entity setContact(boolean c) {
        this.contact = c;
        return this;
    }

    public boolean hasContact() {
        return contact;
    }
}
