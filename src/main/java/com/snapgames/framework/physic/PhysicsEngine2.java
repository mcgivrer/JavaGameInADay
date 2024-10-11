package com.snapgames.framework.physic;

import com.snapgames.framework.Game;
import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.utils.Log;

import java.util.List;
import java.util.Optional;

public class PhysicsEngine2 {

    private final Game app;
    private World world;

    public PhysicsEngine2(Game app) {
        this.app = app;
    }

    public void update(Scene scene, long deltaTime) {
        this.world = scene.getWorld();
        // Mise à jour des entités et application des forces
        scene.getEntities().values().stream()
                .filter(entity -> entity.isActive())
                .forEach(entity -> {
                    if (entity.getPhysicType().equals(PhysicType.DYNAMIC)) {
                        applyForces(entity);
                        updateEntity(entity, deltaTime);
                        keepEntityIntoWorld(scene, entity);
                    }
                    entity.getBehaviors().stream().forEach(b -> b.update(entity, deltaTime));
                });
        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            scene.getActiveCamera().update(deltaTime);
        }
        // Détection et résolution des collisions
        for (Entity<?> entityA : scene.getEntities().values()) {
            if (entityA.isActive() && entityA.getPhysicType() == PhysicType.DYNAMIC) {
                for (Entity<?> entityB : scene.getEntities().values()) {
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

    // Mise à jour de la physique (appelée à chaque frame de jeu)
    private void updateEntity(Entity<?> entity, long deltaTime) {
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
    private Vector2 calculateNetForce(Entity<?> entity) {
        Vector2 netForce = new Vector2(0, 0);
        for (Vector2 force : entity.getForces()) {
            netForce.addInPlace(force);
        }
        return netForce;
    }

    private void applyForces(Entity<?> entity) {
        // Appliquer la force de gravité à l'entité
        entity.applyForce(new Vector2(0, -world.getGravity() * entity.getMass()));

        // Appliquer les forces localisées (comme les vents ou magnétisme)
        world.getChildren().forEach(n -> {
            WorldArea wa = (WorldArea) n;
            if (wa.isInEffect(entity.getPosition())) {
                entity.applyAllForce(wa.getForceAt(entity.getPosition()));
            }
        });
    }

    private void keepEntityIntoWorld(Scene scene, Entity<?> e) {
        World w = scene.getWorld();
        if (!w.contains(e) || w.intersects(e)) {
            if (e.x < w.x) {
                e.x = w.x;
                e.dx *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
            if (e.x + e.width > w.width) {
                e.x = w.width - e.width;
                e.dx *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
            if (e.y < w.y) {
                e.y = w.y;
                e.dy *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
            if (e.y > w.height - e.height) {
                e.y = w.height - e.height;
                e.dy *= -e.getMaterial().elasticity;
                e.setContact(true);
            }
        }
    }

    // Détection de collision entre deux entités
    private boolean detectCollision(Entity<?> entityA, Entity<?> entityB) {
        if (entityA.isRectangle() && entityB.isRectangle()) {
            return detectRectangleCollision(entityA, entityB);
        } else if (entityA.isEllipse() && entityB.isEllipse()) {
            return detectEllipseCollision(entityA, entityB);
        } else {
            return detectEllipseRectangleCollision(entityA, entityB);
        }
    }

    // Détection de collision entre deux rectangles
    private boolean detectRectangleCollision(Entity<?> rectA, Entity<?> rectB) {
        return rectA.intersects(rectB);
    }

    // Détection de collision entre deux ellipses
    private boolean detectEllipseCollision(Entity<?> ellipseA, Entity<?> ellipseB) {
        double distance = Vector2.distance(ellipseA.getPosition(), ellipseB.getPosition());
        double sumRadii = ellipseA.getRadius() + ellipseB.getRadius();
        return distance < sumRadii;
    }

    // Détection de collision entre une ellipse et un rectangle
    private boolean detectEllipseRectangleCollision(Entity<?> ellipse, Entity<?> rect) {
        double closestX = clamp(ellipse.getPosition().x, rect.getPosition().x, rect.getPosition().x + rect.getWidth());
        double closestY = clamp(ellipse.getPosition().y, rect.getPosition().y, rect.getPosition().y + rect.getHeight());

        double distanceX = ellipse.getPosition().x - closestX;
        double distanceY = ellipse.getPosition().y - closestY;

        return (distanceX * distanceX + distanceY * distanceY) < (ellipse.getRadius() * ellipse.getRadius());
    }

    // Résolution de la collision entre deux entités
    private void resolveCollision(Entity<?> entityA, Entity<?> entityB) {
        // Calcul de la réponse à la collision basée sur les masses et l'élasticité des entités
        Vector2 relativeVelocity = entityA.getVelocity().subtract(entityB.getVelocity());
        Vector2 collisionNormal = entityA.getPosition().subtract(entityB.getPosition()).normalized();

        double elasticity = (entityA.getMaterial().getElasticity() + entityB.getMaterial().getElasticity()) / 2;

        double impulse = (-(1.0 + elasticity) * relativeVelocity.dot(collisionNormal)) /
                (entityA.getMassInverse() + entityB.getMassInverse());
        Log.debug(PhysicsEngine2.class, 3,"Collision impusle : %f (%s,%s)", impulse, entityA.getName(), entityB.getName());

        // Appliquer l'impulsion aux deux entités
        entityA.setVelocity(entityA.getVelocity().add(collisionNormal.multiply(impulse * entityA.getMassInverse())));
        entityB.setVelocity(entityB.getVelocity().subtract(collisionNormal.multiply(impulse * entityB.getMassInverse())));

        entityA.getBehaviors().forEach(b -> b.onCollision(entityA, entityB));
        entityB.getBehaviors().forEach(b -> b.onCollision(entityB, entityA));
    }

    // Utilitaire pour "clamp" les valeurs (utile pour la détection ellipse-rectangle)
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public void resetForces(Scene scene) {
        scene.getEntities().values().stream().filter(Entity::isActive).forEach(e -> e.getForces().clear());
    }

    public void dispose() {
    }
}
