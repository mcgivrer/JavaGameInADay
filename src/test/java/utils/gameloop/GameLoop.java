package utils.gameloop;

import game.Game;
import scenes.Scene;

public interface GameLoop {
    void process(Game game);

    void input(Scene scene);

    void update(Scene scene, double elapsed);

    void render(Scene scene);

    void setExit(boolean exitRequest);

    void setPause(boolean pauseRequest);
}
