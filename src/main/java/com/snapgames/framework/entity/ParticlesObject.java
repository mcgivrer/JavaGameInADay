package com.snapgames.framework.entity;

import com.snapgames.framework.behaviors.ParticleBehavior;

/**
 * A {@link ParticlesObject} is a specific {@link GameObject} that apply the
 * same {@link com.snapgames.framework.behaviors.Behavior} implementations on all its child.
 * <p>
 * It also defines the Max number of child and the life duration for all those children.
 * <p>
 * A specific {@link com.snapgames.framework.behaviors.Behavior} must be applied on this : the {@link ParticleBehavior},
 * it contains an additional method {@link ParticleBehavior#create(Object)}.
 *
 * @author Frédéric Delorme
 * @since 1.0.3
 */
public class ParticlesObject extends Entity<ParticlesObject> {
    private int max = 100;
    private int life = 10000;

    /**
     * Create a new ParticlesObject with its <code>name</code>.
     *
     * @param name the name for this {@link ParticlesObject}.
     */
    public ParticlesObject(String name) {
        super(name);
    }

    /**
     * Create a new {@link ParticlesObject} with a <code>name</code> and max number of child particles.
     *
     * @param name the name for the new {@link ParticlesObject}.
     * @param max  the maximum number of particles for this new {@link ParticlesObject}.
     */
    public ParticlesObject(String name, int max) {
        super(name);
        this.max = max;
    }

    /**
     * Create a new {@link ParticlesObject} with a <code>name</code> and <code>max</code> number
     * of child particles and a maximum <code>life</code> duration.
     *
     * @param name the name for the new {@link ParticlesObject}.
     * @param max  the maximum number of particles for this new {@link ParticlesObject}.
     * @param life the maximum life duration (in millisecond) of all the children particles for this {@link ParticlesObject}.
     */
    public ParticlesObject(String name, int max, int life) {
        super(name);
        this.max = max;
        this.life = life;
    }

    /**
     * Retrieve the current max lifetime.
     *
     * @return the lifetime value for all the new particle.
     */
    public int getLife() {
        return life;
    }

    /**
     * set the maximum lifetime for any new particle for this Particles>{@link Object}.
     *
     * @param life the new lifetime for future particle.
     * @return the updated {@link ParticlesObject}.
     */
    public ParticlesObject setLife(int life) {
        this.life = life;
        return this;
    }

    public int getMaxNbParticles() {
        return max;
    }

    public ParticlesObject setMaxNbParticles(int maxNbParticles) {
        this.max = maxNbParticles;
        return this;
    }

}
