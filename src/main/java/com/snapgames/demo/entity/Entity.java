package com.snapgames.demo.entity;

import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.PhysicType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Entity<T> extends Rectangle2D.Double {
    private static long index = 0;
    List<Point2D> forces = new ArrayList<>();

    public double ax, ay;
    public double dx, dy;
    private Material material = Material.DEFAULT;
    long id = index++;
    String name = "entity_" + (id);
    Color color = Color.RED;
    private double mass = 1.0;

    private PhysicType physicType = PhysicType.DYNAMIC;

    private boolean active = true;
    private boolean contact = false;

    public Entity() {
    }

    public Entity(String name) {
        setName(name);
    }

    private T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public T setPosition(double x, double y) {
        super.setRect(x, y, width, height);
        return (T) this;
    }

    public T setSize(double w, double h) {
        super.setRect(x, y, w, h);
        return (T) this;
    }

    public T setColor(Color c) {
        this.color = c;
        return (T) this;
    }

    public T setMaterial(Material mat) {
        this.material = mat;
        return (T) this;
    }

    public double getMass() {
        return mass;
    }

    public Material getMaterial() {
        return material;
    }

    public T setMass(double m) {
        this.mass = m;
        return (T) this;
    }

    public T addForce(double fx, double fy) {
        forces.add(new Point2D.Double(fx, fy));
        return (T) this;
    }

    public T addForce(Point2D f) {
        forces.add(f);
        return (T) this;
    }

    public List<Point2D> getForces() {
        return forces;
    }

    public boolean isActive() {
        return active;
    }

    public T setActive(boolean active) {
        this.active = active;
        return (T) this;
    }

    public T setContact(boolean c) {
        this.contact = c;
        return (T) this;
    }

    public boolean hasContact() {
        return contact;
    }


    public PhysicType getPhysicType() {
        return physicType;
    }

    public T setPhysicType(PhysicType physicType) {
        this.physicType = physicType;
        return (T) this;
    }
}
