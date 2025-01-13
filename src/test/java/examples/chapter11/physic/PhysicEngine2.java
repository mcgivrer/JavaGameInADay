package examples.chapter11.physic;

import com.snapgames.framework.physic.PhysicType;
import com.snapgames.framework.utils.Log;
import examples.chapter11.behaviors.Behavior2;
import examples.chapter11.entity.Entity2;
import examples.chapter11.scene.Scene;
import game.Game;

import java.awt.geom.Rectangle2D;
import java.util.Optional;

public class PhysicEngine2 {

    private final Game app;
    private World world;

    public PhysicEngine2(Game app) {
        this.app = app;
    }

    public void update(Scene scene, double deltaTime) {
        this.world = scene.getWorld();
        // Mise à jour des entités et application des forces
        scene.getEntities().stream()
                .filter(entity -> entity.isActive())
                .forEach(entity -> {
                    if (entity.getPhysicType().equals(PhysicType.DYNAMIC)) {
                        applyForces(entity);
                        updateEntity(entity, deltaTime);
                        keepEntityIntoWorld(scene, entity);
                    }
                    entity.getBehaviors().forEach(b -> ((Behavior2<Entity2>) b).update(entity, deltaTime));
                });
        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            scene.getActiveCamera().update(deltaTime);
        }
        // Détection et résolution des collisions
        for (Entity2<?> entityA : scene.getEntities()) {
            if (entityA.isActive() && entityA.getPhysicType() == PhysicType.DYNAMIC) {
                for (Entity2<?> entityB : scene.getEntities()) {
                    if (entityB.isActive() && entityB.getPhysicType() == PhysicType.DYNAMIC) {
                        if (!entityA.getName().equals(entityB.getName())
                                && detectCollision(entityA, entityB)) {
                            resolveCollision(entityA, entityB);
                        }
                    }
                }
            }
        }
    }

    private void keepEntityIntoWorld(Scene scene, Entity2<?> entity) {
        Rectangle2D sceneBounds = scene.getActiveCamera().getBounds2D();
        if (sceneBounds.contains(entity)) {
            if (entity.getPosition().x < 0) {
                entity.getPosition().x = 0;
                entity.setVelocity(entity.getVelocity().multiply(-entity.getMaterial().getElasticity()));
            }
            if (entity.getPosition().y < 0) {
                entity.getPosition().y = 0;
                entity.setVelocity(entity.getVelocity().multiply(-entity.getMaterial().getElasticity()));
            }
            if (entity.getPosition().x + entity.width > sceneBounds.getWidth()) {
                entity.getPosition().x = sceneBounds.getWidth() - entity.width;
                entity.setVelocity(entity.getVelocity().multiply(-entity.getMaterial().getElasticity()));
            }
            if (entity.getPosition().y + entity.width > sceneBounds.getHeight()) {
                entity.getPosition().y = sceneBounds.getHeight() - entity.height;
                entity.setVelocity(entity.getVelocity().multiply(-entity.getMaterial().getElasticity()));
            }
        }
    }

    // Mise à jour de la physique (appelée à chaque frame de jeu)
    private void updateEntity(Entity2<?> entity, double deltaTime) {
        // Calcul de la somme des forces
        Vector2 netForce = calculateNetForce(entity);

        // Calcul de l'accélération : a = F / m
        entity.acceleration = netForce.divide(entity.getMass()).clamp(2);

        // Mise à jour de la vélocité : v = v + a * deltaTime
        entity.velocity = entity.velocity.add(entity.getAcceleration().multiply(deltaTime)).clamp(8);

        // Application de la friction (simplification)
        entity.velocity = entity.velocity.multiply(entity.getMaterial().getFriction());

        // Mise à jour de la position : p = p + v * deltaTime
        entity.position = entity.position.add(entity.getVelocity().multiply(deltaTime));

        entity.updateBox();

        // Réinitialisation des forces pour le prochain frame
        //forces.clear();
    }


    // Calcul de la somme des forces appliquées à l'entité
    private Vector2 calculateNetForce(Entity2<?> entity) {
        Vector2 netForce = new Vector2(0, 0);
        for (Vector2 force : entity.getForces()) {
            netForce.addInPlace(force);
        }
        return netForce;
    }

    private void applyForces(Entity2<?> entity) {
        // Appliquer la force de gravité à l'entité
        entity.applyForce(world.getGravity().multiply(-entity.getMass()));

        // Appliquer les forces localisées (comme les vents ou magnétisme)
        world.getChildren().forEach(n -> {
            WorldArea wa = (WorldArea) n;
            if (wa.isInEffect(entity.getPosition())) {
                entity.applyAllForce(wa.getForceAt(entity.getPosition()));
            }
        });
    }


    // Détection de collision entre deux entités
    private boolean detectCollision(Entity2<?> entityA, Entity2<?> entityB) {
        if (entityA.isRectangle() && entityB.isRectangle()) {
            return detectRectangleCollision(entityA, entityB);
        } else if (entityA.isEllipse() && entityB.isEllipse()) {
            return detectEllipseCollision(entityA, entityB);
        } else {
            return detectEllipseRectangleCollision(entityA, entityB);
        }
    }

    // Détection de collision entre deux rectangles
    private boolean detectRectangleCollision(Entity2<?> rectA, Entity2<?> rectB) {
        return rectA.intersects(rectB);
    }

    // Détection de collision entre deux ellipses
    private boolean detectEllipseCollision(Entity2<?> ellipseA, Entity2<?> ellipseB) {
        double distance = Vector2.distance(ellipseA.getPosition(), ellipseB.getPosition());
        double sumRadii = ellipseA.getRadius() + ellipseB.getRadius();
        return distance < sumRadii;
    }

    // Détection de collision entre une ellipse et un rectangle
    private boolean detectEllipseRectangleCollision(Entity2<?> ellipse, Entity2<?> rect) {
        double closestX = clamp(ellipse.getPosition().x, rect.getPosition().x, rect.getPosition().x + rect.getWidth());
        double closestY = clamp(ellipse.getPosition().y, rect.getPosition().y, rect.getPosition().y + rect.getHeight());

        double distanceX = ellipse.getPosition().x - closestX;
        double distanceY = ellipse.getPosition().y - closestY;

        return (distanceX * distanceX + distanceY * distanceY) < (ellipse.getRadius() * ellipse.getRadius());
    }

    // Résolution de la collision entre deux entités
    private void resolveCollision(Entity2<?> entityA, Entity2<?> entityB) {
        // Calcul de la réponse à la collision basée sur les masses et l'élasticité des entités
        Vector2 relativeVelocity = entityA.getVelocity().subtract(entityB.getVelocity());
        Vector2 collisionNormal = entityA.getPosition().subtract(entityB.getPosition()).normalized();

        double elasticity = (entityA.getMaterial().getElasticity() + entityB.getMaterial().getElasticity()) / 2;

        double impulse = (-(1.0 + elasticity) * relativeVelocity.dot(collisionNormal)) /
                (entityA.getMassInverse() + entityB.getMassInverse());
        Log.debug(PhysicEngine2.class, "Collision impulse : %f (%s,%s)", impulse, entityA.getName(), entityB.getName());

        // Appliquer l'impulsion aux deux entités
        entityA.setVelocity(entityA.getVelocity().add(collisionNormal.multiply(impulse * entityA.getMassInverse())));
        entityB.setVelocity(entityB.getVelocity().subtract(collisionNormal.multiply(impulse * entityB.getMassInverse())));

        entityA.getBehaviors().stream()
                .forEach(b -> b.collide(entityA, entityB));
        entityB.getBehaviors().stream()
                .forEach(b -> b.collide(entityB, entityA));
    }

    // Utilitaire pour "clamp" les valeurs (utile pour la détection ellipse-rectangle)
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public void resetForces(Scene scene) {
        scene.getEntities().stream().filter(Entity2::isActive).forEach(e -> e.getForces().clear());
    }

    public void dispose() {
    }
}

