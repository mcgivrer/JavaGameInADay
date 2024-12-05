package scenes;

import entity.Entity;
import game.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * The PlayScene class represents a specific scene in a game where the main gameplay occurs.
 * It manages the creation and interactions of entities such as the player and enemies within
 * the scene. This class extends the AbstractScene to leverage basic entity management
 * operations.
 */
public class PlayScene extends AbstractScene{

    /**
     * Constructs a new PlayScene with the specified name.
     *
     * @param name the name of the scene to be created
     */
    public PlayScene(String name){
        super(name);
    }

    /**
     * Initializes and adds entities to the game scene. This method creates the
     * blue player entity positioned in the middle of the rendering buffer and
     * multiple red enemy entities with random positions and parameters.
     *
     * @param app the Game instance which provides access to configuration and rendering buffer.
     */
    public void create(Game app){
        // Création du player bleu
        Entity player = new Entity("player")
                .setPosition(
                        ((app.getRenderingBuffer().getWidth() - 16) * 0.5),
                        ((app.getRenderingBuffer().getHeight() - 16) * 0.5))
                .setElasticity((double) app.getConfig().get("app.physic.entity.player.elasticity"))
                .setFriction((double) app.getConfig().get("app.physic.entity.player.friction"))
                .setFillColor(Color.BLUE)
                .setShape(new Rectangle2D.Double(0, 0, 16, 16))
                .setAttribute("max.speed", 2.0);
        add(player);

        // Création de l’ennemi rouge
        for (int i = 0; i < 10; i++) {
            Entity enemy = new Entity("enemy_%d".formatted(i))
                    .setPosition((Math.random() * (app.getRenderingBuffer().getWidth() - 16)), (Math.random() * (app.getRenderingBuffer().getHeight() - 16)))
                    .setElasticity(Math.random())
                    .setFriction(Math.random())
                    .setFillColor(Color.RED)
                    .setShape(new Ellipse2D.Double(0, 0, 10, 10))
                    .setAttribute("max.speed", (Math.random() * player.getAttribute("max.speed", 2.0) * 0.90));
            add(enemy);
        }
    }


    /**
     * Handles player input for movement and updates the velocities of both the player
     * and enemy entities based on key presses and the player's position.
     *
     * @param app the Game instance which provides access to input states and configuration.
     *            It is used to check if certain keys are pressed and to retrieve configuration
     *            settings such as player speed.
     */
    public void input(Game app){
        Entity player = getEntity("player");
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

        // on parcourt les entités en filtrant sur celles dont le nom commence par "enemy_"
        getEntities().stream()
                .filter(e -> e.getName().startsWith("enemy_"))
                .forEach(e -> {
                    // new speed will be only a random ratio of the current one (from 50% to 110%)
                    double eSpeed = (0.5 + Math.random() * 1.1);

                    // Simulation pour les ennemis qui suivent le player sur l’are X,
                    // but limited to 'max.speed' attribute's value
                    double centerPlayerX = player.getX() + player.getShape().getBounds().width * 0.5;
                    double centerEnemyX = e.getX() + e.getShape().getBounds().width * 0.5;
                    double directionX = Math.signum(centerPlayerX - centerEnemyX);
                    if (directionX != 0.0) {
                        e.setVelocity(
                                Math.min(directionX * eSpeed * e.getAttribute("max.speed", 2.0),
                                        e.getAttribute("max.speed", 2.0)),
                                e.getDy());
                    }

                    // Simulation pour les ennemis qui suivent le player sur l’axe Y,
                    // but limited to 'max.speed' attribute's value
                    double centerPlayerY = player.getY() + player.getShape().getBounds().width * 0.5;
                    double centerEnemyY = e.getY() + e.getShape().getBounds().width * 0.5;
                    double directionY = Math.signum(centerPlayerY - centerEnemyY);
                    if (directionY != 0.0) {
                        e.setVelocity(
                                e.getDx(),
                                Math.min(directionY * eSpeed * e.getAttribute("max.speed", 2.0),
                                        e.getAttribute("max.speed", 2.0)));
                    }
                });
    }
}
