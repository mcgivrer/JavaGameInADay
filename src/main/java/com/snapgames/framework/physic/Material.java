package com.snapgames.framework.physic;

public class Material {
    public static final Material DEFAULT = new Material();
    String name = "default";
    public double density = 1.0;
    public double friction = 1.0;
    public double staticFriction = 1.0;
    public double elasticity = 1.0;

    public Material() {

    }

    public Material(String name, double density, double friction, double elasticity) {
        this.name = name;
        this.density = density;
        this.friction = friction;
        this.staticFriction = friction;
        this.elasticity = elasticity;
    }

    @Override
    public String toString() {
        return "Material{" +
            "name='" + name + '\'' +
            ", density=" + density +
            ", friction=" + friction +
            ", statFriction=" + staticFriction +
            ", elasticity=" + elasticity +
            '}';
    }
}
