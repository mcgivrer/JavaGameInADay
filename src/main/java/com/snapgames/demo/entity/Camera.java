package com.snapgames.demo.entity;

public class Camera extends Entity<Camera> {

    protected Entity<?> target;
    protected double tween;

    public Camera(String name) {
        super(name);
    }

    public Camera setViewPort(double width, double height) {
        this.setSize(width, height);
        return this;
    }

    public Camera setTarget(Entity<?> target) {
        this.target = target;
        return this;
    }

    public Camera setTWeen(double tween) {
        this.tween = tween;
        return this;
    }

    public void update(double elapsed) {
        this.x = target.x - ((this.width - target.width) * 0.5);
        this.y = target.y - ((this.height - target.height) * 0.5);
    }
}
