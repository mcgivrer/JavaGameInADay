package game;

import scenes.Scene;
import utils.Config;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * The {@code TestGame} class represents a basic framework for a game application,
 * implementing both {@code GameInterface} and {@code Game}.
 * It extends {@code JPanel} to provide a graphical surface for rendering.
 * <p>
 * This class includes functionality for managing game states such as exit and pause,
 * as well as debugging options. It also provides access to configuration settings
 * and a rendering buffer for the graphical output.
 */
public class TestGame extends JPanel implements Game {
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
        return !pause;
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

    /**
     * Checks if the specified key is currently pressed.
     *
     * @param keyCode the integer code of the key to check, typically one of the
     *                constants defined in {@code java.awt.event.KeyEvent}.
     * @return true if the key corresponding to the specified keyCode is pressed, false otherwise.
     */
    @Override
    public boolean isKeyPressed(int keyCode) {
        return false;
    }

    @Override
    public void input(Scene scene) {

    }
}