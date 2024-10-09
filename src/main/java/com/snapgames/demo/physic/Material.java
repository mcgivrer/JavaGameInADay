package com.snapgames.demo.physic;

public class Material {
    public static final Material DEFAULT = new Material();
    String name = "default";
    public double density = 1.0;
    public double friction = 1.0;
    public double elasticity = 1.0;

    public Material() {

    }

    public Material(String name, double density, double friction, double elasticity) {
        this.name = name;
        this.density = density;
        this.friction = friction;
        this.elasticity = elasticity;
    }

}