package examples.chapter11.entity;


import com.snapgames.framework.physic.PhysicType;
import examples.chapter11.behaviors.Behavior2;
import examples.chapter11.physic.Material;
import examples.chapter11.physic.Vector2;
import utils.Node;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Entity2<T> extends Node<T> {


    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public Vector2 acceleration = new Vector2();
    public List<Vector2> forces = new ArrayList<>();
    private Material material = Material.DEFAULT;
    private double mass = 1.0;

    private Shape shape = new Rectangle2D.Double();

    private PhysicType physicType = PhysicType.DYNAMIC;

    private boolean active = true;
    private boolean contact = false;

    private Color color = Color.RED;
    private Color fillColor = Color.RED;

    private List<Behavior2<Entity2>> behaviors = new ArrayList<>();
    private int priority = 0;
    private Camera cameraFixedTo;

    public Entity2() {
        super();
    }

    public Entity2(String name) {
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
        forces.add(new Vector2(fx, fy));
        return (T) this;
    }

    public T applyForce(Vector2 f) {
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

    public T setVelocity(double dx, double dy) {
        this.velocity.set(dx, dy);
        return (T) this;
    }

    public T setAcceleration(double ax, double ay) {
        this.acceleration.set(ax, ay);
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

    public T add(Behavior2 b) {
        this.behaviors.add(b);
        return (T) this;
    }

    public List<Behavior2<Entity2>> getBehaviors() {
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

    public T updateBox() {
        super.setRect(x, y, width, height);
        shape = this;
        return (T) this;
    }


    public T setPosition(Vector2 p) {
        super.setRect(p.x, p.y, width, height);
        position.set(p.x, p.y);
        return (T) this;
    }

    public Vector2 getPosition() {
        return position;
    }

    public T setVelocity(Vector2 velocity) {
        this.velocity = velocity;
        return (T) this;
    }

    public T setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
        return (T) this;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getAcceleration() {
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

    public boolean isRectangle() {
        return true;
    }

    public boolean isEllipse() {
        return false;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public double getRadius() {
        return width;
    }

    public void applyAllForce(List<Vector2> forceAt) {
        forces.addAll(forceAt);
    }

    public double getMassInverse() {
        return 1.0 / mass;
    }
}
