package com.snapgames.framework.behaviors;

import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;

import java.awt.*;

/**
 * The {@link Behavior} is an API to implement specific behavior on any object.
 * <p>
 * Mainly used on {@link com.snapgames.framework.entity.Entity} or {@link com.snapgames.framework.scene.Scene},
 * you will be able to modify default processing or add some processing to the existing ones.
 *
 * @param <T> the impacted object type.
 * @author Frederic Delorme
 * @Since 1.0.0
 */
public interface Behavior<T> {
    default void start(T e) {
    }

    default void input(InputListener il, T e) {

    }

    default void update(T e, double elapsed) {

    }

    default void draw(Graphics2D g, T e) {

    }

    default void end(T e) {
    }

    default void onCollision(Entity<?> a, Entity<?> b) {

    }
}
