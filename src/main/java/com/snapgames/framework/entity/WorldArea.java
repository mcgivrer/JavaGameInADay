package com.snapgames.framework.entity;

public class WorldArea extends Entity<WorldArea> {
    public WorldArea(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "WorldArea{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", material=" + getMaterial() +
                '}';
    }
}
