package com.snapgames.framework.system;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;

import java.util.Collection;
import java.util.Map;

public interface GSystem {

    Collection<Class<?>> getDependencies();

    void initialize(GameInterface game);

    void start(GameInterface game);

    void process(GameInterface game, double elapsed, Map<String, Object> stats);

    default void postProcess(GameInterface game) {
    }

    void stop(GameInterface game);

    void dispose(GameInterface game);
}
