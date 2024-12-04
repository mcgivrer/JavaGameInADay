package entity;

import utils.Node;

import behaviors.Behavior;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Entity extends Node<Entity> {
    // position
    private double x, y;
    // vélocité
    private double dx, dy;
    // la forme et dimension de l’entité
    private Shape shape;

    // propriétés de matériau
    private double elasticity = 1.0;
    private double friction = 1.0;

    // propriété pour le rendu
    private Color color = Color.WHITE;
    private Color fillColor = Color.BLUE;

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private List<Behavior> behaviors = new ArrayList<>();

    public Entity(String name) {
        super(name);
    }

    public double getX() {
        return x;
    }

    public Entity setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public double getY() {
        return y;
    }

    public double getDx() {
        return dx;
    }

    public Entity setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        return this;
    }

    public double getDy() {
        return dy;
    }

    public double getElasticity() {
        return elasticity;
    }

    public Entity setElasticity(double elasticity) {
        this.elasticity = elasticity;
        return this;
    }

    public double getFriction() {
        return friction;
    }

    public Entity setFriction(double friction) {
        this.friction = friction;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Entity setColor(Color color) {
        this.color = color;
        return this;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Entity setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public Shape getShape() {
        return shape;
    }

    public Entity setShape(Shape shape) {
        this.shape = shape;
        return this;
    }


    public <T> Entity setAttribute(String attrKeyName, T attrValue) {
        this.attributes.put(attrKeyName, attrValue);
        return this;
    }

    public <T> T getAttribute(String attrKeyName, T defaultAttrValue) {
        return (T) this.attributes.getOrDefault(attrKeyName, defaultAttrValue);
    }

    public List<Behavior> getBehaviors() {
        return this.behaviors;
    }

    public Entity add(Behavior b) {
        this.behaviors.add(b);
        return this;
    }

}
