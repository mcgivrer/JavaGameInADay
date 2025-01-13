package examples.chapter11.physic;

import examples.chapter11.entity.Entity2;

import java.util.ArrayList;
import java.util.List;

public class WorldArea extends Entity2<WorldArea> {
    private boolean isCircular = false;
    private double radius = 0.0;

    public WorldArea(String name) {
        super(name);
    }

    public WorldArea setRadius(double r) {
        this.radius = r;
        return this;
    }

    public WorldArea setAsCircularArea(boolean c) {
        this.isCircular = c;
        return this;
    }


    public boolean isInEffect(Vector2 entityPosition) {
        if (isCircular) {
            // Circular area of effect: check if the entity is within the radius
            return Vector2.distance(entityPosition, this.getPosition()) <= radius;
        } else {
            // Rectangular area of effect: check if the entity is inside the bounds
            return entityPosition.x >= getPosition().x && entityPosition.x <= getPosition().x + width &&
                    entityPosition.y >= getPosition().y && entityPosition.y <= getPosition().y + height;
        }
    }

    // Applies the force to an entity located within the region
    public List<Vector2> getForceAt(Vector2 entityPosition) {
        // If the entity is in the area of effect, return the force
        if (isInEffect(entityPosition)) {
            return getForces();
        }
        // Otherwise, return a zero vector (no force applied)
        return new ArrayList<>();
    }

    public boolean isCircular() {
        return isCircular;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "WorldArea{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", material=" + getMaterial() +
                '}';
    }
}
