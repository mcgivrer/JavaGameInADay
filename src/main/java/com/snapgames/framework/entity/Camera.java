package com.snapgames.framework.entity;

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

    public Camera setTween(double tween) {
        this.tween = tween;
        return this;
    }

    public void update(double elapsed) {
        //this.x += this.x + ((target.x - this.x) * tween * elapsed);
        //this.y += this.y  + ((target.y - this.y) * tween * elapsed);
        this.x = target.x - ((this.width - target.width) * 0.5);
        this.y = target.y - ((this.height - target.height) * 0.5);
    }

    @Override
    public String toString() {
        return "Camera{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", target='" + target.getName()+"'" +
                ", tween=" + tween +
                '}';
    }
}
