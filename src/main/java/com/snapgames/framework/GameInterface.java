package com.snapgames.framework;

/**
 * The GameInterface defines the methods required for managing the basic
 * game state and debugging operations. It provides the necessary
 * functionalities to control game execution flow such as pausing,
 * exiting, and debugging.
 */
public interface GameInterface {
    /**
     * Requests the game to exit by setting an exit flag to true, indicating that the game should
     * terminate. Typically invoked when the player chooses to exit the game, such as by pressing
     * a specific key combination or selecting an exit option in a menu.
     *
     * The actual exit confirmation may involve user interaction, such as displaying a confirmation
     * dialog to ensure that the user indeed intends to exit. If confirmed, the game's main loop
     * will eventually detect the exit flag and terminate accordingly.
     */
    void requestExit();

    /**
     * Sets the debug level of the game. The debug level can be used to control
     * the verbosity of debugging output or to enable/disable certain debugging
     * features within the game.
     *
     * @param i The desired debug level to be set. Acceptable values typically
     *          range from 0 (no debugging) to a maximum predefined level.
     */
    void setDebug(int i);

    /**
     * Retrieves the current debug level of the game. The debug level determines
     * the verbosity of debugging output and debugging features active in the game.
     *
     * @return the current debug level.
     */
    int getDebug();

    /**
     * Checks if the game is currently not in a paused state.
     *
     * @return true if the game is not paused, false otherwise.
     */
    boolean isNotPaused();

    /**
     * Sets the pause state of the game.
     *
     * @param p true to pause the game, false to resume it.
     */
    void setPause(boolean p);

    /**
     * Sets the exit flag, indicating whether the game should terminate.
     *
     * @param b the new value for the exit flag; true to request game termination, false otherwise.
     */
    void setExit(boolean b);

    /**
     * Checks if an exit has been requested.
     *
     * @return true if an exit has been requested, false otherwise.
     */
    boolean isExitRequested();

    /**
     * Checks if the current debug level is greater than the specified debug level.
     *
     * @param debugLevel The debug level to compare against.
     * @return true if the current debug level is greater than the specified debug level, false otherwise.
     */
    boolean isDebugGreaterThan(int debugLevel);
}
