package com.snapgames.demo.scenes;

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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

public class PlayScene extends AbstractScene {

    private Font scoreFont, textFont;

    public PlayScene(Game app, String name) {
        super(app, name);
    }

    public void load() {
        scoreFont = ResourceManager.get("/assets/fonts/upheavtt.ttf");
        textFont = ResourceManager.get("/assets/fonts/Minecraftia-Regular.ttf");
    }

    @Override
    public void create() {

        Dimension windowSize = getConfig().get("app.window.size");
        Rectangle2D playArea = getConfig().get("app.physic.world.play.area.size");


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