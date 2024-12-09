package entity;

import behaviors.Behavior;
import game.Game;

import java.awt.*;

/**
 * The Camera class represents a camera entity within a game or graphical application,
 * allowing for the tracking of a target entity and adjusting its viewport
 * based on a tweening factor and rotation.
 */
public class Camera extends Entity {

    private Entity target = null;
    private double tweenFactor = 1.0;
    private double rotation = 0.0;

    /**
     * Constructs a new Camera object with a specified name and initializes its shape.
     *
     * @param name the name to assign to the Camera entity
     */
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

    /**
     * Updates the position of the camera based on the target's position, the defined tween factor,
     * and the elapsed time, allowing for smooth transitions.
     *
     * @param elapsed the time elapsed since the last update call, influencing the amount of movement
     */
    public void update(double elapsed) {
        this.x = this.x + (((target.x - this.x) - (this.getBounds2D().getWidth() - target.width) * 0.5) * tweenFactor * elapsed);
        this.y = this.y + (((target.y - this.y) - (this.getBounds2D().getHeight() - target.height) * 0.5) * tweenFactor * elapsed);

    }

    /**
     * Sets the viewport dimensions of the camera.
     *
     * @param vp the Dimension object containing the width and height for the viewport.
     * @return the Camera instance with the updated viewport.
     */
    public Camera setViewport(Dimension vp) {
        this.setRect(x, y, vp.width, vp.height);
        return this;
    }
}
