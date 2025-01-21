package com.snapgames.framework.physic.collision;

import com.snapgames.framework.entity.Entity;
import com.snapgames.framework.entity.GameObject;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Bounding Box for any object managed by the system.
 *
 * @author Frédéric Delorme
 */
public class BoundingBox {

    /**
     * position for the boundingbox.
     */
    Rectangle2D rect;
    /**
     * diam1 is used for elipse1/circle/capsule BoundingBox type
     */
    Ellipse2D elipse1;
    Ellipse2D elipse2;
    float e1e2Distance;
    /**
     * list of specific points for POINT mode.
     */
    Point[] points;

    BoundingBoxType type;

    public int intersect(BoundingBox b) {
        switch (type) {
            case NONE:
                return 0;
            case RECTANGLE:
                return (b.rect.intersects(rect) ? 1 : 0);
            case CIRCLE:
                return (b.elipse1.intersects(rect) ? 1 : 0);
            case CAPSULE:
                // TODO to be implemented later.
                return 0;
            case POINTS:
                // TODO to be implemented later.
                return 0;
            default:
                return 0;
        }
    }

    /**
     * Update bounding box according to GameObject size and position.
     *
     * @param go
     */
    public void update(Entity<?> go) {
        this.rect = new Rectangle2D.Double(go.getPosition().x, go.getPosition().y, go.width, go.height);
        this.elipse1 = new Ellipse2D.Double(go.getPosition().x, go.getPosition().y, go.width, go.height);
        // TODO compute distance for CAPSULE.
        // this.elipse2 = new Ellipse2D.Float(go.position.x, go.position.y, go.width,
        // go.height);
    }

    /**
     * Create a new Bounding Box.
     *
     * @return
     */
    public BoundingBox builder() {
        return new BoundingBox();
    }

    /**
     * Define the BoundingBoxType for this BoundingBox.
     *
     * @param type type of the bounding box
     * @return this object.
     * @see BoundingBoxType
     */
    public BoundingBox setType(BoundingBoxType type) {
        this.type = type;
        return this;
    }
}