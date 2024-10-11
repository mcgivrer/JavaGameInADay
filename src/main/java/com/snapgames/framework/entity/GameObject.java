package com.snapgames.framework.entity;

public class GameObject extends Entity<GameObject> {
    public GameObject(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", x=" + x +
                ", y=" + y +
                ", material=" + getMaterial() +
                '}';
    }
}
