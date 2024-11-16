package com.snapgames.framework.scene;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Camera;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.World;

import java.util.List;
import java.util.Map;

public interface Scene {
    Map<String, Entity<?>> getEntities();

    World getWorld();

    void create();

    void add(Entity<?> entity);

    String getName();

    void input(InputListener inputListener);

    void dispose();

    void load();

    void setActiveCamera(Camera cam);

    List<Camera> getCameras();

    Camera getActiveCamera();

    void reset();

    default void process(Game game, double elapsed) {
    }
}
