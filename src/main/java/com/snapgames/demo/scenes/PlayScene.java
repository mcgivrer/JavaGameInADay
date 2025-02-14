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
 * The PlayScene class extends the AbstractScene and represents a specific game scene
 * where the main gameplay elements and logic are implemented. This scene includes
 * various entities like a player, camera, UI elements, world areas, and dynamically
 * generated objects.
 */
public class PlayScene extends AbstractScene {

    private Font scoreFont, textFont;
    private BufferedImage playerImg, tilesImg, moonImg;

    public PlayScene(GameInterface app, String name) {
        super(app, name);
    }

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
                .setFillColor(new Color(0.1f, 0.1f, 0.7f, 0.8f))
                .setColor(Color.BLUE)
                .setSize(world.width, 64)
                .setPosition(0, world.height - 64)
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
                .setSize(world.width, world.height - 64)
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