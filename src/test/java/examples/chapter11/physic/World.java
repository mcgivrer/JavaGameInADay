package examples.chapter11.physic;

import utils.Node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class World extends Node<World> {
    private Vector2 gravity = new Vector2(0, 0);

    public World(Vector2 g, Rectangle2D pa) {
        this.gravity = g;
        this.setRect(pa);
    }

    public Vector2 getGravity() {
        return this.gravity;
    }

    public Rectangle2D getPlayArea() {
        return this.getBounds2D();
    }

    public World setGravity(Point2D g) {
        this.gravity = g;
        return this;
    }

    public World setPlayArea(Rectangle2D pa) {
        this.setRect(pa);
        return this;
    }

}
