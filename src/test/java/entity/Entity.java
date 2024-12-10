package entity;

import behaviors.Behavior;

import physic.Material;
import utils.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Entity class represents a physical entity within a simulation
 * or a graphical application. It extends the Node class and provides
 * additional attributes specific to physical entities such as position,
 * velocity, shape, material properties, rendering properties,
 * and behaviors.
 * <p>
 * This class allows the manipulation of attributes related to the position,
 * velocity, and appearance of the entity. It also supports the addition
 * and management of behaviors that define specific actions or reactions
 * of the entity within the system.
 * <p>
 * Attributes:
 * - Position: Defined by x and y coordinates.
 * - Velocity: Defined by dx and dy, representing the rate of change of position.
 * - Shape: The physical shape and dimensions of the entity.
 * - Material Properties: Includes elasticity and friction, which affect how the entity
 * interacts with other entities.
 * - Rendering Properties: Includes color and fillColor for visual representation.
 * - Attributes Map: A collection of arbitrary attributes defined by key-value pairs.
 * - Behaviors: A list of behaviors that can be added to define the entity's actions.
 * <p>
 * Methods in the Entity class allow retrieval and mutation of the above attributes,
 * supporting the fluent interface design pattern for easy configuration of entity
 * properties and behaviors.
 */
public class Entity extends Node<Entity> {
    /**
     * Represents the change in velocity along the x-axis for this entity.
     * This variable is used to determine how the entity's position changes
     * over time in the horizontal direction.
     */
    // vélocité
    private double dx, dy;
    // la forme et dimension de l’entité
    private Shape shape;

    // propriétés de matériau
    private Material material = Material.DEFAULT;

    // propriété pour le rendu
    private Color color = Color.WHITE;
    private Color fillColor = Color.BLUE;

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private List<Behavior> behaviors = new ArrayList<>();

    public Entity(String name) {
        super(name);
    }

    /**
     * Sets the position of this Entity to the specified coordinates.
     *
     * @param x the x-coordinate to set for this Entity
     * @param y the y-coordinate to set for this Entity
     * @return the current Entity instance with updated position
     */
    public Entity setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Retrieves the change in the x-coordinate velocity for this entity.
     *
     * @return the current x-direction velocity as a double.
     */
    public double getDx() {
        return dx;
    }

    /**
     * Sets the velocity of this Entity by specifying the changes in the x and y directions.
     *
     * @param dx the change in velocity along the x-axis
     * @param dy the change in velocity along the y-axis
     * @return the current Entity instance with updated velocity
     */
    public Entity setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        return this;
    }

    /**
     * Retrieves the change in the y-coordinate velocity for this entity.
     *
     * @return the current y-direction velocity as a double.
     */
    public double getDy() {
        return dy;
    }

    /**
     * Assigns a Material to the Entity and updates its material property.
     *
     * @param mat the Material object to set for this Entity
     * @return the current Entity instance with the updated material
     */
    public Entity setMaterial(Material mat) {
        this.material = mat;
        return this;
    }

    /**
     * Retrieves the material associated with the entity.
     *
     * @return the Material object representing the entity's material properties.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Retrieves the elasticity coefficient of the entity.
     *
     * @return the elasticity value as a double.
     */
    public double getElasticity() {
        return material.getElasticity();
    }

    /**
     * Sets the elasticity coefficient for this Entity. The elasticity defines how bouncy
     * the entity is when it collides with other objects.
     *
     * @param elasticity the elasticity value to set for this Entity
     * @return the current Entity instance with updated elasticity
     */
    public Entity setElasticity(double elasticity) {
        this.material.setElasticity(elasticity);
        return this;
    }

    /**
     * Retrieves the friction coefficient of the entity. The friction value
     * affects how quickly the entity slows down when moving.
     *
     * @return the friction coefficient as a double.
     */
    public double getFriction() {
        return material.getFriction();
    }

    /**
     * Sets the friction coefficient for this Entity. The friction value affects
     * how quickly the entity slows down when moving.
     *
     * @param friction the friction coefficient to set for this Entity
     * @return the current Entity instance with updated friction
     */
    public Entity setFriction(double friction) {
        this.material.setFriction(friction);
        return this;
    }

    /**
     * Retrieves the current color associated with the entity.
     *
     * @return the current color of the entity.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color attribute for this Entity.
     *
     * @param color the color to be set for this Entity
     * @return the current Entity instance with updated color
     */
    public Entity setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Retrieves the fill color of the entity.
     *
     * @return the fill color as a Color object.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Sets the fill color attribute for the Entity.
     *
     * @param fillColor the Color to set as the fill color for the Entity
     * @return the current Entity instance with the updated fill color
     */
    public Entity setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    /**
     * Retrieves the shape associated with the entity.
     *
     * @return the current Shape object representing the entity's shape.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the shape of this Entity.
     *
     * @param shape the Shape object to set for this Entity
     * @return the current Entity instance with the updated shape
     */
    public Entity setShape(Shape shape) {
        this.shape = shape;
        this.width = shape.getBounds().width;
        this.height = shape.getBounds().height;
        return this;
    }


    /**
     * Sets a custom attribute for the entity using a key-value pair.
     *
     * @param <T>         the type of the attribute value
     * @param attrKeyName the key for the attribute
     * @param attrValue   the value to associate with the attribute key
     * @return the current Entity instance with the updated attribute
     */
    public <T> Entity setAttribute(String attrKeyName, T attrValue) {
        this.attributes.put(attrKeyName, attrValue);
        return this;
    }

    /**
     * Retrieves the value of a custom attribute associated with the entity, using the provided key.
     * If the attribute with the specified key is not found, the default value is returned.
     *
     * @param <T>              the type of the attribute value
     * @param attrKeyName      the key name of the attribute to retrieve
     * @param defaultAttrValue the default value to return if the attribute is not found
     * @return the current value of the attribute associated with the key, or the default value if the attribute does not exist
     */
    public <T> T getAttribute(String attrKeyName, T defaultAttrValue) {
        return (T) this.attributes.getOrDefault(attrKeyName, defaultAttrValue);
    }

    /**
     * Retrieves the list of behaviors associated with this entity.
     *
     * @return a list of Behavior objects associated with the entity.
     */
    public List<Behavior> getBehaviors() {
        return this.behaviors;
    }

    /**
     * Adds a behavior to the entity's list of behaviors.
     *
     * @param b the behavior to be added to this entity
     * @return the current Entity instance with the new behavior added
     */
    public Entity add(Behavior b) {
        this.behaviors.add(b);
        return this;
    }

}
