package entity;

import behaviors.Behavior;
import game.Game;

import java.awt.*;

public class Camera extends Entity {

    private Entity target = null;
    private double tweenFactor = 1.0;
    private double rotation = 0.0;
    private Dimension viewport = new Dimension(320, 200);

    public Camera(String name) {
        super(name);
    }


    public Entity getTarget() {
        return target;
    }

    public Camera setTarget(Entity target) {
        this.target = target;
        return this;
    }

    public double getTweenFactor() {
        return tweenFactor;
    }

    public Camera setTweenFactor(double tweenFactor) {
        this.tweenFactor = tweenFactor;
        return this;
    }

    public double getRotation() {
        return rotation;
    }

    public Camera setRotation(double rotation) {
        this.rotation = rotation;
        return this;
    }

    public void update(Game app, double elapsed) {
        setPosition(target.getX(), target.getY());
    }

    public Camera setViewport(Dimension vp) {
        this.viewport = vp;
        return this;
    }
}
