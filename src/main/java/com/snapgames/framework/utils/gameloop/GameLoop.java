package com.snapgames.framework.utils.gameloop;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.scene.Scene;

public interface GameLoop {
    void process(GameInterface game);

    void input(Scene scene);

    void update(Scene scene, double elapsed);

    void render(Scene scene);

    void setExit(boolean exitRequest);

    void setPause(boolean pauseRequest);
}
