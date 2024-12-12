package physic;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class World extends Rectangle2D.Double {
    private Point2D gravity = new Point2D.Double(0, 0);

    public World(Point2D g, Rectangle2D pa) {
        this.gravity = g;
        this.setRect(pa);
    }

    public Point2D getGravity() {
        return this.gravity;
    }

    public Rectangle2D getPlayArea() {
        return this.getBounds2D();
    }
}
