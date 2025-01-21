package com.snapgames.framework.physic.collision;

/**
 * Interface to managed Collision with CollisionManager and QuadTree.
 *
 * @author Frédéric Delorme
 * @see QuadTree
 * @see CollisionManager
 */
public interface Collidable {

    BoundingBox getBoundingBox();

    void addCollider(Collidable c);
}
