package entity;

import behaviors.Behavior;
import game.Game;

import java.awt.*;

public class Camera extends Entity {

    private Entity target = null;
    private double tweenFactor = 1.0;
    private double rotation = 0.0;

    public Camera(String name) {
        super(name);
        setShape(new Rectangle(0, 0, 320, 200));
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

    public void update(double elapsed) {
        this.x = this.x + (((target.x - this.x) - (this.getBounds2D().getWidth() - target.width) * 0.5) * tweenFactor * elapsed);
        this.y = this.y + (((target.y - this.y) - (this.getBounds2D().getHeight() - target.height) * 0.5) * tweenFactor * elapsed);

    }

    public Camera setViewport(Dimension vp) {
        this.setRect(x, y, vp.width, vp.height);
        return this;
    }
}
