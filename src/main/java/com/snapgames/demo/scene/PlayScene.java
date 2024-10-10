package com.snapgames.demo.scene;

import com.snapgames.demo.Behavior;
import com.snapgames.demo.Game;
import com.snapgames.demo.entity.*;
import com.snapgames.demo.io.InputListener;
import com.snapgames.demo.io.ResourceManager;
import com.snapgames.demo.physic.Material;
import com.snapgames.demo.physic.PhysicType;
import com.snapgames.demo.physic.World;
import com.snapgames.demo.physic.WorldArea;

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
        Dimension windowSize = app.getWindowSize();


        world = new World("earth", -0.981)
                .setSize(800, 600)
                .setPosition(0, 0);

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
                        double speed = 0.2;
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
                .setPosition(camera.getWidth() - 30, 38)
                .setFont(textFont.deriveFont(8.0f))
                .setText("3")
                .setColor(Color.RED)
                .setPhysicType(PhysicType.STATIC)
                .setFixedToCamera(camera)
                .setPriority(100);
        add(lives);
        generate("star_%d", world, 20, 1, 1,
                Color.WHITE, 100000000,
                Material.DEFAULT,
                PhysicType.STATIC,
                2);
        generate("ball_%d", world, 5, 20, 20,
                Color.RED, 5.0,
                new Material("ball_mat", 1.0, 0.7, 0.8),
                PhysicType.DYNAMIC, 5);


        WorldArea area1 = (WorldArea) new WorldArea("water")
                .setColor(new Color(0.1f, 0.1f, 0.7f, 0.8f))
                .setSize(world.width, 64)
                .setPosition(0, world.height - 64)
                .setPhysicType(PhysicType.STATIC)
                .addForce(0.02, -0.16)
                .setPriority(20);
        world.addArea(area1);
        add(area1);
        WorldArea sky = (WorldArea) new WorldArea("sky")
                .setColor(new Color(0.2f, 0.6f, 1.0f, 0.9f))
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
        world.addArea(sky);
        add(sky);
        //activate our camera as the default one.
        setActiveCamera(camera);
    }

    private void generate(String tempateName, Rectangle2D windowSize,
                          int nb, double maxW, double maxH,
                          Color color,
                          double mass,
                          Material mat,
                          PhysicType pt,
                          int priority) {
        for (int i = 0; i < nb; i++) {
            GameObject star = new GameObject(tempateName.formatted(i))
                    .setSize(maxW * Math.random(), maxH * Math.random())
                    .setPosition(windowSize.getWidth() * Math.random(), windowSize.getHeight() * Math.random())
                    .setColor(color)
                    .setMass(mass)
                    .setMaterial(mat)
                    .setPhysicType(pt)
                    .setPriority(priority);
            add(star);
        }
    }
}