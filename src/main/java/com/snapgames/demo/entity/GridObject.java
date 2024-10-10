package com.snapgames.demo.entity;

/**
 * A Grid object to display a regular grid, mainly used in a visual debug context.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class GridObject extends Entity<GridObject> {
    private int tileWidth = 16, tileHeight = 16;

    public GridObject(String name) {
        super(name);
    }

    public GridObject setTileSize(int tw, int th) {
        this.tileWidth = tw;
        this.tileHeight = th;
        return this;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }
}
