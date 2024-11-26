package com.snapgames.framework.behaviors;

public interface ParticleBehavior<T> extends Behavior<T> {
    default void create(T parent) {
    }
}
