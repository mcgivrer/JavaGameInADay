package scenes;

import behaviors.Behavior;
import behaviors.EnemyBehavior;
import entity.Camera;
import entity.Entity;
import game.Game;
import physic.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class PlayCameraScene2 extends AbstractScene {
    /**
     * Constructs an instance of AbstractScene with the specified name.
     *
     * @param name the name of the scene to be created
     */
    public PlayCameraScene2(String name) {
        super(name);
    }

    /**
     * Initializes and sets up the game scene by creating the player and enemy entities,
     * as well as configuring the camera.
     *
     * @param app The game application instance that provides access to rendering buffers,
     *            configuration settings, and input states. This parameter is essential to
     *            set up the scene with the necessary configurations and to handle player
     *            inputs and behaviors.
     */
    @Override
    public void create(Game app) {
        setWorld(
                new World(
                        new Point2D.Double(0, -0.981),
                        new Double(0, 0, 30 * 16, 20 * 16)));

        // Création du player bleu
        Entity player = new Entity("player")
                .setPosition(
                        ((getWorld().getWidth() - 16) * 0.5),
                        ((getWorld().getHeight() - 16) * 0.5))
                .setElasticity((double) app.getConfig().get("app.physic.entity.player.elasticity"))
                .setFriction((double) app.getConfig().get("app.physic.entity.player.friction"))
                .setFillColor(Color.BLUE)
                .setShape(new Double(0, 0, 16, 16))
                .setAttribute("max.speed", 2.0)
                // On ajoute le nouveau comportement sur la gestion des entrées
                .add(new Behavior() {
                    @Override
                    public void input(Entity player) {
                        double speed = (double) app.getConfig().get("app.physic.entity.player.speed");

                        if (app.isKeyPressed(KeyEvent.VK_LEFT)) {
                            player.setVelocity(-speed, player.getDy());
                        }
                        if (app.isKeyPressed(KeyEvent.VK_RIGHT)) {
                            player.setVelocity(speed, player.getDy());
                        }
                        if (app.isKeyPressed(KeyEvent.VK_UP)) {
                            player.setVelocity(player.getDx(), -speed);
                        }
                        if (app.isKeyPressed(KeyEvent.VK_DOWN)) {
                            player.setVelocity(player.getDx(), speed);
                        }
                    }
                });
        add(player);

        // add enemies
        generateEntities(app, "enemy_%d", 10, player, new EnemyBehavior(this));

        // Add Camera to the scene.
        Camera cam = new Camera("cam01")
                .setViewport(new Dimension(app.getRenderingBuffer().getWidth(), app.getRenderingBuffer().getHeight()))
                .setTarget(player)
                .setTweenFactor(app.getConfig().get("app.render.camera.tween.factor", 0.002));
        add(cam);
    }

    /**
     * Generates a specified number of enemy entities with random attributes and behavior.
     *
     * @param app            The game application instance used to access rendering buffers
     *                       and configuration settings needed to define entity properties.
     * @param entityBaseName A base name used for naming each entity uniquely by appending
     *                       an index.
     * @param nbEntities     The number of entities to generate.
     * @param entityTarget   Target entity that provides a reference for attribute calculations,
     *                       such as speed.
     * @param behavior       The behavior to be assigned to each entity, defining their actions
     *                       or reactions within the game.
     */
    private void generateEntities(Game app, String entityBaseName, int nbEntities, Entity entityTarget, Behavior behavior) {
        double maxEnemySpeedRatio = app.getConfig().get("app.physic.entity.enemy.max.speed.ratio");
        // Création de l’ennemi rouge
        for (int i = 0; i < nbEntities; i++) {
            Entity enemy = new Entity(entityBaseName.formatted(i))
                    .setPosition((Math.random() * (getWorld().getWidth() - 16)), (Math.random() * (getWorld().getHeight() - 16)))
                    .setElasticity(Math.random())
                    .setFriction(Math.random())
                    .setFillColor(Color.RED)
                    .setShape(new Ellipse2D.Double(0, 0, 10, 10))
                    .setAttribute("max.speed", (Math.random() * entityTarget.getAttribute("max.speed", 2.0) * maxEnemySpeedRatio))
                    // On ajoute le comportement de suivi de l’instance d’Entity "player".
                    .add(behavior);
            add(enemy);
        }
    }
}
