package behaviors;

import entity.Entity;

public interface CollisionBehavior extends Behavior {

    default void collide(Entity e1, Entity e2) {
    }

}
