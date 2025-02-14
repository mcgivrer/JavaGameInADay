package com.snapgames.framework.entity;

import java.awt.image.BufferedImage;

public class SpriteObject extends Entity<SpriteObject> {
    private BufferedImage image;

    /**
     * Constructs an ImageObject instance with the specified name.
     *
     * @param name the name of the ImageObject to be created, which is inherited from the parent Entity class.
     */
    public SpriteObject(String name) {
        super(name);
        addAttribute("energy", 100.0);
    }

    @Override
    public void update(double elapsed) {
        if (!isAlive()) {
            addAttribute("energy", 0.0);
        }
    }

    public SpriteObject setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void hit(double impact) {
        double energy = getAttribute("energy", 0.0);
        energy -= impact;
        addAttribute("energy", energy);
    }

    public SpriteObject setEnergy(double e) {
        addAttribute("energy", e);
        return this;
    }
}
