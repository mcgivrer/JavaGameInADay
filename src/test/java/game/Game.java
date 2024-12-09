package game;

import utils.Config;

import java.awt.image.BufferedImage;

/**
 * Represents a generic game interface that defines the basic structure
 * and functionalities for a game.
 */
public interface Game {
    /**
     * Requests to exit the game by setting an internal exit flag.
     * This method is typically called in response to specific user actions, such as pressing
     * the escape key during gameplay. It sets a boolean field to indicate that an exit
     * has been requested, allowing the game loop or main application logic to perform the
     * necessary actions to terminate gracefully.
     */
    void requestExit();

    /**
     * Sets the debug level for the game.
     *
     * @param i the debug level to set. A higher value generally means more detailed
     *          debugging information is enabled.
     */
    void setDebug(int i);

    /**
     * Retrieves the current debug level for the game.
     *
     * @return the current debug level, where a higher value generally means more detailed
     * debugging information is enabled.
     */
    int getDebug();

    /**
     * Checks if the current debug level is greater than the specified debug level.
     *
     * @param debugLevel the debug level to compare against
     * @return true if the current debug level is greater than the specified debug level, false otherwise
     */
    boolean isDebugGreaterThan(int debugLevel);

    /**
     * Determines if the game is currently not paused.
     *
     * @return true if the game is not paused, false otherwise.
     */
    boolean isNotPaused();

    /**
     * Sets the pause state of the game.
     *
     * @param p true to pause the game, false to resume it
     */
    void setPause(boolean p);

    /**
     * Sets the exit flag for the game.
     * This flag indicates whether an exit request has been made, allowing the game
     * to handle cleanup operations and terminate gracefully if necessary.
     *
     * @param e true to set the exit flag, indicating an exit request, or false to clear
     *          the exit flag, canceling the exit request.
     */
    void setExit(boolean e);

    /**
     * Checks if an exit request has been made for the game.
     *
     * @return true if an exit has been requested, false otherwise.
     */
    boolean isExitRequested();

    /**
     * Retrieves the rendering buffer for the game. This buffer is used for graphical
     * operations, allowing the game to perform custom rendering.
     *
     * @return a BufferedImage representing the current rendering buffer.
     */
    BufferedImage getRenderingBuffer();

    /**
     * Retrieves the game configuration settings.
     *
     * @return a Config object containing the current configuration settings for the game.
     */
    Config getConfig();

    /**
     * Checks if the specified key is currently pressed.
     *
     * @param keyCode the integer code of the key to check, typically one of the
     *                constants defined in {@code java.awt.event.KeyEvent}.
     * @return true if the key corresponding to the specified keyCode is pressed, false otherwise.
     */
    boolean isKeyPressed(int keyCode);
}
