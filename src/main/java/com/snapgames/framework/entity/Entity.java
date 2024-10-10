package com.snapgames.framework.entity;

import com.snapgames.framework.Behavior;
import com.snapgames.framework.physic.Material;
import com.snapgames.framework.physic.PhysicType;
import com.snapgames.framework.utils.Node;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Entity<T> extends Node<T> {

    List<Point2D> forces = new ArrayList<>();

    public double ax, ay;
    public double dx, dy;
    private Material material = Material.DEFAULT;
    private double mass = 1.0;

    private PhysicType physicType = PhysicType.DYNAMIC;

    private boolean active = true;
    private boolean contact = false;

    private Color color = Color.RED;

    private List<Behavior<Entity<?>>> behaviors = new ArrayList<>();
    private int priority = 0;
    private Camera cameraFixedTo;

    public Entity() {
        super();
    }

    public Entity(String name) {
        super(name);
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

    public T add(Behavior<Entity<?>> b) {
        this.behaviors.add(b);
        return (T) this;
    }

    public List<Behavior<Entity<?>>> getBehaviors() {
        return this.behaviors;
    }

    public T setPriority(int p) {
        this.priority = p;
        return (T) this;
    }

    public int getPriority() {
        return this.priority;
    }

    public Camera getCameraIsStickedTo() {
        return cameraFixedTo;
    }

    public T setFixedToCamera(Camera cam) {
        this.cameraFixedTo = cam;
        return (T) this;
    }
}
