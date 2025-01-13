package examples.chapter11.physic;

import entity.Entity;

public record CollisionEvent(Entity e1, Entity e2) {
}
