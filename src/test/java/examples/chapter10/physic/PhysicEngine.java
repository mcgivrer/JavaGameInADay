package examples.chapter10.physic;

import behaviors.CollisionBehavior;
import entity.Camera;
import entity.Entity;
import game.Game;
import physic.CollisionEvent;
import physic.World;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhysicEngine {

    private final Game app;

    /**
     * The list of all detected collisions, composed of CollisionEvent.
     */
    private List<CollisionEvent> collisions = new ArrayList<>();

    public PhysicEngine(Game app) {
        this.app = app;
    }

    /**
     * Updates the position and velocity of an object within the game area.
     * <p>
     * This method performs the following operations:
     * - Calculates the new position based on the current velocity.
     * - Applies bounce effect if a collision with the game area's edge is detected.
     * - Repositions the object within the game area if necessary.
     * - Applies a friction factor to the object's velocity.
     */
    public void update(Scene currentScene) {
        currentScene.getEntities().stream().filter(e -> !(e instanceof Camera)).forEach(e -> {
            // Calcul de la nouvelle position en tenant compte de l'objet World issue de la scène active.
            World world = currentScene.getWorld();
            e.setPosition(
                    e.getX() + e.getDx() - (world.getGravity().getX()),
                    e.getY() + e.getDy() - (world.getGravity().getY()));

            // repositionnement dans la zone de jeu si nécessaire
            if (!world.contains(e)) {
                applyBouncingFactor(world, e);
                e.setPosition(
                        Math.min(Math.max(e.getX(), world.getX()), world.getWidth() - e.getWidth()),
                        Math.min(Math.max(e.getY(), world.getY()), world.getHeight() - e.getHeight()));
            }

            // Application du facteur de friction
            e.setVelocity(e.getDx() * e.getFriction(), e.getDy() * e.getFriction());
            e.getBehaviors().forEach(b -> b.update(e));
        });

        // TODO delegate collision detection and processing to a dedicated service.
        detectCollision(currentScene);
        resolveCollision(collisions);

        Optional<Entity> cam = currentScene.getEntities().stream().filter(e -> e instanceof Camera).findFirst();
        cam.ifPresent(entity -> ((Camera) entity).update(16.0));

        currentScene.update(app);
    }

    private void resolveCollision(List<physic.CollisionEvent> collisions) {
        collisions.forEach(ec -> {
            Entity e1 = ec.e1();
            Entity e2 = ec.e2();

            e1.getBehaviors().forEach(b -> {
                if (b instanceof CollisionBehavior) {
                    ((CollisionBehavior) b).collide(ec.e1(), ec.e2());
                }
            });
            e1.getBehaviors().forEach(b -> {
                if (b instanceof CollisionBehavior) {
                    ((CollisionBehavior) b).collide(ec.e2(), ec.e1());
                }
            });

        });
    }

    private void detectCollision(Scene scene) {
        collisions.clear();
        scene.getEntities().stream()
                .filter(e -> !(e instanceof Camera))
                .forEach(e1 -> {
                    scene.getEntities().stream()
                            .filter(e -> !(e instanceof Camera))
                            .forEach(e2 -> {
                                if (!e1.getName().equals(e2.getName())) {
                                    if (e1.intersects(e2)) {
                                        physic.CollisionEvent ce = new CollisionEvent(e1, e2);
                                        collisions.add(ce);
                                    }
                                }
                            });
                });
    }

    private void applyBouncingFactor(World world, Entity e) {
        // application du rebond si collision avec le bord de la zone de jeu
        if (e.getX() < world.getX()
                || e.getX() + e.getWidth() > e.getWidth() + world.getWidth()) {
            e.setVelocity(-e.getDx() * e.getElasticity(), e.getDy());
        }
        if (e.getY() < world.getY()
                || e.getY() + e.getHeight() > world.getHeight()) {
            e.setVelocity(e.getDx(), -e.getDy() * e.getElasticity());
        }
    }

}
