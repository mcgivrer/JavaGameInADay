package utils;

import com.snapgames.framework.entity.Entity;

public interface Behavior {
    default void init(Entity e) {
    }

    default void create(Entity e) {
    }

    default void input(Entity e) {
    }

    default void update(Entity e) {
    }

    default void draw(Entity e) {
    }

    default void dispose(Entity e) {
    }

}
