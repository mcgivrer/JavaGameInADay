package com.snapgames.framework;

public interface GameInterface {
    void requestExit();

    void setDebug(int i);

    int getDebug();

    boolean isNotPaused();

    void setPause(boolean p);

    void setExit(boolean b);
}
