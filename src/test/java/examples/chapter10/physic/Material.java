package examples.chapter10.physic;

/**
 * The Material class represents a physical material characterized by its name,
 * density, elasticity, and friction. This class provides methods to access and
 * modify these physical properties of the material.
 */
public class Material {
    public static final Material DEFAULT = new Material("default", 1.0, 1.0, 1.0);
    private String name;
    private double density;
    private double elasticity;
    private double friction;

    /**
     * Constructs a new Material with specified physical properties.
     *
     * @param name       the name of the material
     * @param density    the density of the material
     * @param elasticity the elasticity of the material
     * @param friction   the friction coefficient of the material
     */
    public Material(String name, double density, double elasticity, double friction) {
        this.name = name;
        this.density = density;
        this.elasticity = elasticity;
        this.friction = friction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public void setElasticity(double elasticity) {
        this.elasticity = elasticity;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getElasticity() {
        return elasticity;
    }

    public double getFriction() {
        return friction;
    }


}
