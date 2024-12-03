package behaviors;


import entity.Entity;

import java.awt.*;

public interface Behavior {
    default void init(Entity e) {
    }

    default void create(Entity e) {
    }

    default void input(Entity e) {
    }

    default void update(Entity e) {
    }

    default void draw(Graphics2D g, Entity e) {
    }

    default void dispose(Entity e) {
    }

}
