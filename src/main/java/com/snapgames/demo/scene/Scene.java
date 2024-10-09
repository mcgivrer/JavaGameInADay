package com.snapgames.demo.scene;

import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.physic.World;

import java.util.Map;

public interface Scene {
    Map<String, Entity<?>> getEntities();

    World getWorld();

    void create();

    void add(Entity entity);

    String getName();

    void input(InputListener inputListener);

    void dispose();

    void load();
}
