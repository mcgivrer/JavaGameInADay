package com.snapgames.framework.physic;

/**
 * This enumeration defines the two types of physic the {@link PhysicEngine} can address.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public enum PhysicType {
    /**
     * Any {@link com.snapgames.framework.entity.Entity} that remains static on the display
     */
    STATIC,
    /**
     * Any {@link com.snapgames.framework.entity.Entity} that will move, according to physics rules.
     */
    DYNAMIC,
    NONE
}
