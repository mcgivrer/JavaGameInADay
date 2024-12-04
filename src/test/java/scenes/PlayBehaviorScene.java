package scenes;

import behaviors.Behavior;
import entity.Entity;
import game.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class PlayBehaviorScene extends AbstractScene {

    public PlayBehaviorScene(String name) {
        super(name);
    }

    public void create(Game app) {
        // Création du player bleu
        Entity player = new Entity("player")
                .setPosition(
                        ((app.getRenderingBuffer().getWidth() - 16) * 0.5),
                        ((app.getRenderingBuffer().getHeight() - 16) * 0.5))
                .setElasticity((double) app.getConfig().get("app.physic.entity.player.elasticity"))
                .setFriction((double) app.getConfig().get("app.physic.entity.player.friction"))
                .setFillColor(Color.BLUE)
                .setShape(new Rectangle2D.Double(0, 0, 16, 16))
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

        // Création de l’ennemi rouge
        for (int i = 0; i < 10; i++) {
            Entity enemy = new Entity("enemy_%d".formatted(i))
                    .setPosition((Math.random() * (app.getRenderingBuffer().getWidth() - 16)), (Math.random() * (app.getRenderingBuffer().getHeight() - 16)))
                    .setElasticity(Math.random())
                    .setFriction(Math.random())
                    .setFillColor(Color.RED)
                    .setShape(new Ellipse2D.Double(0, 0, 10, 10))
                    .setAttribute("max.speed", (Math.random() * player.getAttribute("max.speed", 2.0) * 0.90))
                    // On ajoute le comportement de suivi de l'instance d'Entity "player"
                    .add(new Behavior() {
                        @Override
                        public void input(Entity enemy) {
                            Entity player = getEntity("player");
                            double eSpeed = (0.5 + Math.random() * 1.1);

                            // Simulation pour les ennemis qui suivent le player sur l’are X,
                            // but limited to 'max.speed' attribute's value
                            double centerPlayerX = player.getX() + player.getShape().getBounds().width * 0.5;
                            double centerEnemyX = enemy.getX() + enemy.getShape().getBounds().width * 0.5;
                            double directionX = Math.signum(centerPlayerX - centerEnemyX);
                            if (directionX != 0.0) {
                                enemy.setVelocity(
                                        Math.min(directionX * eSpeed * enemy.getAttribute("max.speed", 2.0),
                                                enemy.getAttribute("max.speed", 2.0)),
                                        enemy.getDy());
                            }

                            // Simulation pour les ennemis qui suivent le player sur l’axe Y,
                            // but limited to 'max.speed' attribute's value
                            double centerPlayerY = player.getY() + player.getShape().getBounds().width * 0.5;
                            double centerEnemyY = enemy.getY() + enemy.getShape().getBounds().width * 0.5;
                            double directionY = Math.signum(centerPlayerY - centerEnemyY);
                            if (directionY != 0.0) {
                                enemy.setVelocity(
                                        enemy.getDx(),
                                        Math.min(directionY * eSpeed * enemy.getAttribute("max.speed", 2.0),
                                                enemy.getAttribute("max.speed", 2.0)));
                            }
                        }
                    });
            add(enemy);
        }
    }
}
