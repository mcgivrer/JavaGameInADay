package com.snapgames.demo.scenes;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.behaviors.Behavior;
import com.snapgames.framework.behaviors.WaveWaterSimulator;
import com.snapgames.framework.entity.*;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.io.ResourceManager;
import com.snapgames.framework.physic.Material;
import com.snapgames.framework.physic.PhysicType;
import com.snapgames.framework.physic.World;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.scene.AbstractScene;
import com.snapgames.framework.utils.Config;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Represents the main playable scene in the game. This class defines and initializes
 * the various elements of the scene, including the player, environment, UI components,
 * and objects. It handles the game's logic, scene setup, and interactions between
 * entities within the playable area.
 */
public class PlayScene extends AbstractScene {

    private Font scoreFont, textFont;
    private BufferedImage playerImg, tilesImg, moonImg;

    /**
     * Constructs a PlayScene instance, initializing it with the specified game interface
     * and scene name. The PlayScene class is a specific implementation of a game scene
     * that contains and manages game objects, entities, and graphical elements for the
     * gameplay experience.
     *
     * @param app  the GameInterface instance that provides game state management and debugging functionalities.
     * @param name the name of the scene, used as an identifier and to distinguish it from other scenes.
     */
    public PlayScene(GameInterface app, String name) {
        super(app, name);
    }

    /**
     * Loads various resources required for the play scene, including fonts, tile images,
     * player sprite images, and background images. These resources are fetched using
     * the {@code ResourceManager.get} method and cached for use within the scene.
     *
     * This method prepares the graphical assets necessary for rendering elements
     * like text, tiles, and sprites, ensuring they are available when the scene is active.
     *
     * The method retrieves and assigns the following resources:
     * - Fonts used for score display and other textual information.
     * - Tile images for the game environment.
     * - Player sprite for character representation.
     * - The moon image for background or visual effect purposes.
     */
    public void load() {
        scoreFont = ResourceManager.get("/assets/fonts/upheavtt.ttf");
        textFont = ResourceManager.get("/assets/fonts/Minecraftia-Regular.ttf");
        tilesImg = ResourceManager.get("/assets/images/tiles01.png");
        playerImg = ((BufferedImage) ResourceManager.get("/assets/images/sprites01.png")).getSubimage(0, 0, 32, 32);
        moonImg = ResourceManager.get("/assets/images/moon-128/Moon_Phase_1.png");

    }

    /**
     * Initializes the play scene by creating and configuring various game objects,
     * UI elements, and environmental components based on the provided configuration.
     *
     * @param config The configuration object used to set up the scene, which includes
     *               information such as window size, play area dimensions, and other
     *               relevant parameters.
     */
    @Override
    public void create(Config config) {

        Rectangle2D playArea = config.get("app.physic.world.play.area.size");

        setWorld(new World("earth", new Vector2d(0, -0.981))
                .setSize(playArea.getWidth(), playArea.getHeight())
                .setPosition(0, 0));

        GridObject go = new GridObject("grid").setTileSize(16, 16).setColor(Color.DARK_GRAY).setPriority(1);
        add(go);

        SpriteObject player = (SpriteObject) new SpriteObject("player")
                .setEnergy(100.0)
                .setImage(playerImg)
                .setSize(32, 32)
                .setPosition(world.getWidth() * 0.5, world.getHeight() * 0.5)
                .setColor(Color.BLUE)
                .setShape(new Rectangle(0, 0, 16, 32))
                .setMass(8)
                .setLayer(2)
                .setMaterial(new Material("player_mat", 1.0, 0.92, 0.66))
                .setPriority(10)
                .addAttribute("stepSpeed", 0.005)
                .addAttribute("jumpSpeed", 0.025)
                .add(new Behavior<Entity<?>>() {
                    @Override
                    public void input(InputListener inputListener, Entity<?> player) {
                        double speed = player.getAttribute("stepSpeed", 0.005);
                        double jump = player.getAttribute("jumpSpeed", 0.025);
                        if (inputListener.isKeyPressed(KeyEvent.VK_UP)) {
                            player.addForce(0.0, -jump);
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

        ImageObject moon = new ImageObject("moon")
                .setImage(moonImg)
                .setSize(128, 128)
                .setPosition(world.getWidth() * 0.5, world.getHeight() * 0.08)
                .setPhysicType(PhysicType.STATIC)
                .setPriority(9)
                .setLayer(1);
        add(moon);

        Camera camera = new Camera("cam01")
                .setViewPort(320, 200)
                .setTween(0.2)
                .setTarget(player);
        add(camera);

        // add background stars
        generate("star_%d", world, 20, 2, 2,
                Color.WHITE, 100000000,
                Material.DEFAULT,
                PhysicType.STATIC,
                5,
                1);

        // add enemy's ball
        generate("ball_%d", world, 5, 20, 20,
                Color.ORANGE, 5.0,
                new Material("ball_mat", 1.0, 0.7, 0.8),
                PhysicType.DYNAMIC, 5, 1);

        // add World specific Area
        WorldArea water = (WorldArea) new WorldArea("water")
                .setFillColor(new Color(0.3f, 0.4f, 0.7f, 0.8f))
                .setColor(Color.BLUE)
                .setSize(world.width, 32)
                .setPosition(0, world.height - 32)
                .setPhysicType(PhysicType.STATIC)
                .setMaterial(new Material("water", 1.0, 0.67, 0.90))
                .addForce(0.02, -0.21)
                .setPriority(20)
                .setLayer(0)
                .add(new WaveWaterSimulator());
        getWorld().add(water);
        add(water);
        // add sky
        WorldArea sky = (WorldArea) new WorldArea("sky")
                .setFillColor(new Color(0.0f, 0.1f, 0.7f))
                .setColor(null)
                .setSize(world.width, world.height - 32)
                .setPosition(0, 0)
                .addForce(0.01, 0.0)
                .setPriority(2)
                .setLayer(0)
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

                    @Override
                    public void draw(Graphics2D g, Entity<?> e) {
                        g.setColor(e.getFillColor());
                        g.fill(e);
                    }
                });
        getWorld().add(sky);
        add(sky);


        // draw HUD
        TextObject score = new TextObject("score")
                .setPosition(10, 32)
                .setFont(scoreFont.deriveFont(18.0f))
                .setText("00000")
                .setColor(Color.WHITE)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(score);

        ImageObject heart = new ImageObject("heart")
                .setImage(tilesImg.getSubimage(0, 6 * 16, 16, 16))
                .setPosition(camera.getWidth() - 40, 16)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(99);
        add(heart);


        TextObject lives = new TextObject("lives")
                .setPosition(camera.getWidth() - 30, 32)
                .setFont(scoreFont.deriveFont(14.0f))
                .setText("3")
                .setColor(Color.WHITE)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(lives);

        GaugeObject energy = new GaugeObject("energy")
                .setPosition((camera.getWidth() * 0.5) - 30, 20)
                .setSize(60, 6)
                .setColor(Color.LIGHT_GRAY)
                .setFillColor(Color.RED)
                .setMinValue(0).setMaxValue(100).setValue(100)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(energy);

        GaugeObject mana = new GaugeObject("mana")
                .setPosition((camera.getWidth() * 0.5) - 30, 27)
                .setSize(60, 6)
                .setColor(Color.LIGHT_GRAY)
                .setFillColor(new Color(0.3f, 0.7f, 0.9f))
                .setMinValue(0).setMaxValue(100).setValue(100)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(mana);


        //activate our camera as the default one.
        setActiveCamera(camera);
    }

    /**
     * Generates and adds a specified number of game objects to the scene. Each object is
     * created based on the provided template name, graphical and physical properties,
     * and scene configuration. The objects have randomized sizes and positions within
     * the specified window size constraints.
     *
     * @param templateName the base name for each game object, which will be formatted with an index.
     * @param windowSize   the dimensions of the area within which the game objects will be placed.
     * @param nb           the number of game objects to be generated.
     * @param maxW         the maximum width for the randomly generated game objects.
     * @param maxH         the maximum height for the randomly generated game objects.
     * @param color        the fill color of the game objects.
     * @param mass         the mass of each game object.
     * @param mat          the material specifying the physical properties of the objects.
     * @param pt           the physics type of the game objects, either static, dynamic, or none.
     * @param priority     the priority level of the game objects, affecting their rendering order.
     * @param layer        the layer index to position the objects in the scene hierarchy.
     */
    private void generate(String templateName, Rectangle2D windowSize,
                          int nb, double maxW, double maxH,
                          Color color,
                          double mass,
                          Material mat,
                          PhysicType pt,
                          int priority,
                          int layer) {
        for (int i = 0; i < nb; i++) {
            GameObject star = new GameObject(templateName.formatted(i))
                    .setShape(new Ellipse2D.Double())
                    .setSize(maxW * Math.random(), maxH * Math.random())
                    .setPosition(windowSize.getWidth() * Math.random(), windowSize.getHeight() * Math.random())
                    .setFillColor(color)
                    .setColor(null)
                    .setMass(mass)
                    .setMaterial(mat)
                    .setPhysicType(pt)
                    .setPriority(priority)
                    .setLayer(layer);
            add(star);
        }
    }
}