package game;

import utils.Config;

import java.awt.image.BufferedImage;

public interface Game {
    void requestExit();

    void setDebug(int i);

    int getDebug();

    boolean isDebugGreaterThan(int debugLevel);

    boolean isNotPaused();

    void setPause(boolean p);

    void setExit(boolean e);

    boolean isExitRequested();

    BufferedImage getRenderingBuffer();

    Config getConfig();

    boolean isKeyPressed(int keyCode);
}
