package com.snapgames.demo.scenes;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.behaviors.Behavior;
import com.snapgames.framework.Game;
import com.snapgames.framework.behaviors.WaveWaterSimulator;
import com.snapgames.framework.entity.*;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.io.ResourceManager;
import com.snapgames.framework.physic.Material;
import com.snapgames.framework.physic.PhysicType;
import com.snapgames.framework.physic.World;
import com.snapgames.framework.entity.WorldArea;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.scene.AbstractScene;
import com.snapgames.framework.utils.Config;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

/**
 * The PlayScene class is an implementation of the AbstractScene class, representing
 * the main gameplay scene within the application. This scene includes the player
 * character, interactive elements, and supporting visual components such as
 * a camera, score display, lives counter, energy, and mana gauges.
 *
 * It also initializes environmental elements like a water area and sky,
 * as well as dynamically generated objects such as stars and balls.
 *
 * Key functionalities include:
 *
 * - Setting up the world and its physical properties.
 * - Adding entities such as the player, environmental areas, and UI components.
 * - Attaching behavior logic for interactive entities.
 * - Managing the active camera and assigning it to specific targets.
 */
public class PlayScene extends AbstractScene {

    /**
     * Represents the font used to display score-related information in the PlayScene.
     * This Font object is utilized for rendering text elements specific to the scoring
     * system of the scene, such as player points or game statistics.
     * The font can be customized or initialized via external resources using
     * the ResourceManager class.
     */
    private Font scoreFont, /**
     * Represents the font used for text rendering in the PlayScene.
     * This variable is likely loaded at runtime from external resources using the ResourceManager.
     * It is utilized for displaying textual elements within the scene,
     * ensuring consistent styling and rendering of text objects.
     */
    textFont;

    /**
     * Constructs a new PlayScene instance that represents a playable scene in the game.
     * It is initialized with the specified game application interface and scene name.
     *
     * @param app  the game application interface that provides core game functionalities
     *             such as debugging, pausing, and exiting the game.
     * @param name the name of the scene, used to identify and manage the scene.
     */
    public PlayScene(GameInterface app, String name) {
        super(app, name);
    }

    /**
     * Loads the necessary font resources required for the game scene.
     *
     * This method retrieves specific font files from the configured
     * assets directory using the {@code ResourceManager.get} method.
     * It assigns the retrieved fonts to class-level fields for later use in rendering
     * text within the game scene.
     *
     * Modifies:
     * - Initializes and assigns the {@code scoreFont} and {@code textFont}
     *   fields with their corresponding loaded font resources.
     *
     * Resources loaded:
     * - "/assets/fonts/upheavtt.ttf" for the scoreFont.
     * - "/assets/fonts/Minecraftia-Regular.ttf" for the textFont.
     */
    public void load() {
        scoreFont = ResourceManager.get("/assets/fonts/upheavtt.ttf");
        textFont = ResourceManager.get("/assets/fonts/Minecraftia-Regular.ttf");
    }

    /**
     * Initializes and creates the play scene with the specified configuration.
     * This method sets up the game world, player, environment, camera, HUD elements
     * (e.g., score, lives, energy, mana), and objects within the scene.
     * It also defines behaviors for certain elements like the player and environmental effects.
     *
     * @param config the configuration object containing parameters such as window size,
     *               play area dimensions, and scene-specific settings.
     */
    @Override
    public void create(Config config) {

        Dimension windowSize = config.get("app.window.size");
        Rectangle2D playArea = config.get("app.physic.world.play.area.size");


        setWorld(new World("earth", new Vector2d(0, -0.981))
                .setSize(800, 600)
                .setPosition(0, 0));

        GridObject go = new GridObject("grid").setTileSize(16, 16).setColor(Color.DARK_GRAY).setPriority(1);
        add(go);

        GameObject player = new GameObject("player")
                .setSize(16, 32)
                .setPosition(world.getWidth() * 0.5, world.getHeight() * 0.5)
                .setColor(Color.BLUE)
                .setMass(8)
                .setMaterial(new Material("player_mat", 1.0, 0.92, 0.66))
                .setPriority(10)
                .add(new Behavior<Entity<?>>() {
                    @Override
                    public void input(InputListener inputListener, Entity<?> player) {
                        double speed = 0.05;
                        if (inputListener.isKeyPressed(KeyEvent.VK_UP)) {
                            player.addForce(0.0, -speed * 2);
                        }
                        if (inputListener.isKeyPressed(KeyEvent.VK_DOWN)) {
                            player.addForce(0.0, speed);
                        }
                        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
                            player.addForce(-speed, 0.0);
                        }
                        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                            player.addForce(speed, 0.0);
                        }
                    }
                });
        add(player);

        Camera camera = new Camera("cam01").setViewPort(320, 200).setTween(0.2).setTarget(player);
        add(camera);


        TextObject score = new TextObject("score")
                .setPosition(10, 32)
                .setFont(scoreFont.deriveFont(18.0f))
                .setText("00000")
                .setColor(Color.WHITE)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(score);

        TextObject lives = new TextObject("lives")
                .setPosition(camera.getWidth() - 30, 32)
                .setFont(scoreFont.deriveFont(17.0f))
                .setText("3")
                .setColor(Color.WHITE)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(lives);

        GaugeObject energy = new GaugeObject("energy")
                .setPosition(camera.getWidth() - 78, 20)
                .setSize(40, 7)
                .setColor(Color.LIGHT_GRAY)
                .setFillColor(Color.RED)
                .setMinValue(0).setMaxValue(100).setValue(100)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(energy);

        GaugeObject mana = new GaugeObject("mana")
                .setPosition(camera.getWidth() - 78, 27)
                .setSize(40, 7)
                .setColor(Color.LIGHT_GRAY)
                .setFillColor(Color.BLUE)
                .setMinValue(0).setMaxValue(100).setValue(100)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(mana);

        generate("star_%d", world, 20, 2, 2,
                Color.WHITE, 100000000,
                Material.DEFAULT,
                PhysicType.STATIC,
                5);
        generate("ball_%d", world, 5, 20, 20,
                Color.RED, 5.0,
                new Material("ball_mat", 1.0, 0.7, 0.8),
                PhysicType.DYNAMIC, 5);

        WorldArea water = (WorldArea) new WorldArea("water")
                .setFillColor(new Color(0.1f, 0.1f, 0.7f, 0.8f))
                .setColor(Color.BLUE)
                .setSize(world.width, 64)
                .setPosition(0, world.height - 64)
                .setPhysicType(PhysicType.STATIC)
                .setMaterial(new Material("water", 1.0, 0.67, 0.32))
                .addForce(0.02, -0.21)
                .setPriority(20)
                .add(new WaveWaterSimulator());
        getWorld().add(water);
        add(water);

        WorldArea sky = (WorldArea) new WorldArea("sky")
                .setFillColor(new Color(0.0f, 0.1f, 0.3f, 0.9f))
                .setColor(null)
                .setSize(world.width, world.height - 64)
                .setPosition(0, 0)
                .addForce(0.01, 0.0)
                .setPriority(2)
                .setPhysicType(PhysicType.STATIC)
                .add(new Behavior<Entity<?>>() {
                    double cumul = 0;

                    @Override
                    public void update(Entity<?> e, double elapsed) {
                        // change the wind direction every random ms
                        cumul -= elapsed;
                        if (cumul <= 0) {
                            e.getForces().clear();
                            e.addForce(0.05 - Math.random() * 0.1, 0);
                            cumul = Math.random() * 1000;
                        }
                    }
                });
        getWorld().add(sky);
        add(sky);
        //activate our camera as the default one.
        setActiveCamera(camera);
    }

    /**
     * Generates and adds a specified number of game objects to the scene with configurable properties.
     *
     * @param templateName the template name used as a base for naming the generated objects. Each
     *                     object's name will be formatted with an index appended to the template name.
     * @param windowSize   the dimensions of the window or area within which the game objects will
     *                     be positioned randomly.
     * @param nb           the number of game objects to generate and add to the scene.
     * @param maxW         the maximum width of the generated objects. Each object's width will be
     *                     randomly determined up to this value.
     * @param maxH         the maximum height of the generated objects. Each object's height will be
     *                     randomly determined up to this value.
     * @param color        the fill color of the generated objects.
     * @param mass         the mass value assigned to the generated objects, affecting their physics
     *                     behavior.
     * @param mat          the material assigned to the generated objects, which includes properties
     *                     such as density, friction, and elasticity.
     * @param pt           the physics type of the generated objects, specifying whether objects are
     *                     static, dynamic, or have no physics applied.
     * @param priority     the priority level of the generated objects, which can influence their
     *                     processing order or importance within the scene.
     */
    private void generate(String templateName, Rectangle2D windowSize,
                          int nb, double maxW, double maxH,
                          Color color,
                          double mass,
                          Material mat,
                          PhysicType pt,
                          int priority) {
        for (int i = 0; i < nb; i++) {
            GameObject star = new GameObject(templateName.formatted(i))
                    .setSize(maxW * Math.random(), maxH * Math.random())
                    .setPosition(windowSize.getWidth() * Math.random(), windowSize.getHeight() * Math.random())
                    .setFillColor(color)
                    .setColor(null)
                    .setMass(mass)
                    .setMaterial(mat)
                    .setPhysicType(pt)
                    .setPriority(priority);
            add(star);
        }
    }
}