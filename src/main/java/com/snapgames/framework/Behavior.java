package com.snapgames.framework;

import com.snapgames.framework.io.InputListener;

import java.awt.*;

public interface Behavior<T> {
    default void input(InputListener il, T e) {

    }

    default void update(T e, double elapsed) {

    }

    default void draw(Graphics2D g, T e) {

    }
}
