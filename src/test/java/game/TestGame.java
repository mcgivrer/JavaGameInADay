package game;

import com.snapgames.framework.GameInterface;
import utils.Config;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class TestGame extends JPanel implements GameInterface, Game{
    private boolean exit = false;
    private boolean pause = false;
    private int debug = 0;
    protected Config config;

    protected BufferedImage renderingBuffer;

    @Override
    public void requestExit() {
        this.exit = true;
    }

    @Override
    public void setDebug(int i) {
        this.debug = i;
    }

    @Override
    public int getDebug() {
        return debug;
    }

    @Override
    public boolean isDebugGreaterThan(int debugLevel) {
        return debug > debugLevel;
    }

    @Override
    public boolean isNotPaused() {
        return pause;
    }

    @Override
    public void setPause(boolean p) {
        this.pause = p;
    }

    @Override
    public void setExit(boolean e) {
        this.exit = e;
    }

    @Override
    public boolean isExitRequested() {
        return exit;
    }

    @Override
    public BufferedImage getRenderingBuffer() {
        return this.renderingBuffer;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return false;
    }
}