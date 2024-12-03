package scenes;

import game.Game;
import entity.Entity;

import java.awt.*;
import java.util.Collection;

public interface Scene {

    String getName();

    // <1>
    void add(Entity entity);

    // <2>
    Collection<Entity> getEntities();

    // <3>
    Entity getEntity(String name);

    // <4>
    default void initialize(Game g) {
    }

    default void create(Game g) {
    }

    // <5>
    default void input(Game g) {
    }

    // <6>
    default void update(Game g) {
    }

    // <7>
    default void dispose(Game g) {
    }

    default void draw(Game game, Graphics2D g) {
    }
}
