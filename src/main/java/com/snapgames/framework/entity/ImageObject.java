package com.snapgames.framework.entity;

import java.awt.image.BufferedImage;

/**
 * The ImageObject class represents an entity that contains image data. It extends the
 * Entity class, inheriting all its properties and behavior, while adding specific
 * functionality to manage an image.
 */
public class ImageObject extends Entity<ImageObject> {
    private BufferedImage image;

    /**
     * Constructs an ImageObject instance with the specified name.
     *
     * @param name the name of the ImageObject to be created, which is inherited from the parent Entity class.
     */
    public ImageObject(String name) {
        super(name);
    }

    public ImageObject setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "ImageObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", velocity=" + velocity +
                ", acceleration=" + acceleration +
                '}';
    }
}
