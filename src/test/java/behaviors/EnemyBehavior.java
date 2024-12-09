package behaviors;

import entity.Entity;
import scenes.Scene;

public class EnemyBehavior implements Behavior {
    Scene scene;

    public EnemyBehavior(Scene s) {
        this.scene = s;
    }

    @Override
    public void input(Entity enemy) {
        Entity player = scene.getEntity("player");
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
        // but limited to 'max.speed' attribute’s value
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
}
