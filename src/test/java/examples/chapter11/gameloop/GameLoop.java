package examples.chapter11.gameloop;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.gfx.Renderer;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.scene.Scene;

public interface GameLoop {
    void process(GameInterface game, Scene scene);

    void input(Scene scene);

    void update(Scene scene, double elapsed);

    void render(Scene scene, double elapsed);

    void setExit(boolean exitRequest);

    void setPause(boolean pauseRequest);
}
