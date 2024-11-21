package com.snapgames.framework.entity;

import com.snapgames.framework.behaviors.Behavior;
import com.snapgames.framework.physic.Material;
import com.snapgames.framework.physic.PhysicType;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.utils.Node;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Entity<T> extends Node<T> {


    public Vector2d position = new Vector2d();
    public Vector2d velocity = new Vector2d();
    public Vector2d acceleration = new Vector2d();
    public List<Vector2d> forces = new ArrayList<>();
    private Material material = Material.DEFAULT;
    private double mass = 1.0;

    private PhysicType physicType = PhysicType.DYNAMIC;

    private boolean active = true;
    private boolean contact = false;

    private Color color = Color.RED;
    private Color fillColor = Color.RED;

    private List<Behavior<Entity<?>>> behaviors = new ArrayList<>();
    private int priority = 0;
    private Camera cameraFixedTo;

    public Entity() {
        super();
    }

    public Entity(String name) {
        super(name);
    }


    public T setSize(double w, double h) {
        super.setRect(x, y, w, h);
        return (T) this;
    }

    public T setColor(Color c) {
        this.color = c;
        return (T) this;
    }


    public Color getColor() {
        return color;
    }

    public T setFillColor(Color fc) {
        this.fillColor = fc;
        return (T) this;
    }


    public Color getFillColor() {
        return fillColor;
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
        forces.add(new Vector2d(fx, fy));
        return (T) this;
    }

    public T addForce(Vector2d f) {
        forces.add(f);
        return (T) this;
    }

    public List<Vector2d> getForces() {
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


    public T setPosition(double x, double y) {
        super.setRect(x, y, width, height);
        position.set(x, y);
        return (T) this;
    }


    public T setPosition(Vector2d p) {
        super.setRect(p.getX(), p.getY(), width, height);
        position.set(p.getX(), p.getY());
        return (T) this;
    }

    public Vector2d getPosition() {
        return position;
    }

    public T setVelocity(Vector2d velocity) {
        this.velocity = velocity;
        return (T) this;
    }

    public T setAcceleration(Vector2d acceleration) {
        this.acceleration = acceleration;
        return (T) this;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public Vector2d getAcceleration() {
        return acceleration;
    }

    @Override
    public String toString() {
        return "Entity{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", width=" + width +
            ", height=" + height +
            ", x=" + x +
            ", y=" + y +
            '}';
    }

    public PhysicType getType() {
        return this.physicType;
    }
}
