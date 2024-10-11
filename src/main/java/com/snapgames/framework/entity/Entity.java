package com.snapgames.framework.entity;

import com.snapgames.framework.behaviors.Behavior;
import com.snapgames.framework.physic.Material;
import com.snapgames.framework.physic.PhysicType;
import com.snapgames.framework.physic.Vector2;
import com.snapgames.framework.utils.Node;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Entity<T> extends Node<T> {

    List<Vector2> forces = new CopyOnWriteArrayList<>();

    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public Vector2 acceleration = new Vector2();

    public double ax, ay;
    public double dx, dy;
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
    private double radius = 0;
    private boolean isEllipse;
    private boolean isRectangle = true;

    public Entity() {
        super();
    }

    public Entity(String name) {
        super(name);
    }

    public T setPosition(double x, double y) {
        super.setRect(x, y, width, height);
        this.position.x = x;
        this.position.y = y;
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
        forces.add(new Vector2(fx, fy));
        return (T) this;
    }

    public T addForce(Vector2 f) {
        forces.add(f);
        return (T) this;
    }

    public List<Vector2> getForces() {
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


    // Méthode pour appliquer une force à l'entité
    public void applyForce(Vector2 force) {
        forces.add(force);
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

    public Vector2 getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public double getMassInverse() {
        return 1.0 / mass;
    }

    public T setVelocity(Vector2 v) {
        this.velocity = v;
        return (T) this;
    }

    public boolean isRectangle() {
        return isRectangle;
    }

    public boolean isEllipse() {
        return isEllipse;
    }

    public void applyAllForce(List<Vector2> fList) {
        forces.addAll(fList);
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public void updateBox() {
        setRect(position.x, position.y, width, height);
        ax = acceleration.x;
        ay = acceleration.y;
        dx = velocity.x;
        dy = velocity.y;
    }
}
