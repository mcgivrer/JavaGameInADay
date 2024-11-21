package com.snapgames.framework.system;

import com.snapgames.framework.Game;

import java.util.Collection;
import java.util.Map;

public interface GSystem {

    Collection<Class<?>> getDependencies();

    void initialize(Game game);

    void start(Game game);

    void process(Game game, double elapsed);

    default void postProcess(Game game) {
    }

    void stop(Game game);

    void dispose(Game game);
}
