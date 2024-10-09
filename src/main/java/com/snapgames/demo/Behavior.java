package com.snapgames.demo;

import com.snapgames.demo.entity.GameObject;
import com.snapgames.demo.io.InputListener;

import java.awt.*;

public interface Behavior<T> {
    default void input(InputListener il, T e) {

    }

    default void update(T e, double elapsed) {

    }

    default void draw(Graphics2D g, T e) {

    }
}
