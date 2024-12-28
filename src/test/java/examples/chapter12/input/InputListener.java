package examples.chapter12.input;

import game.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The InputListener class implements everything about managing
 * Keyboard, mouse or GamePad and Joystick inputs.
 *
 * @author Frédéric Delorme
 * @version 1.0.12
 */
public class InputListener implements KeyListener {
    private final Game app;
    public boolean[] keys = new boolean[1024];

    public InputListener(Game app) {
        this.app = app;
        initialize(app);
        System.out.printf("=> InputListener : created%n");
    }

    private void initialize(Game app) {
        /* Get the available controllers */
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        Arrays.stream(controllers).forEach(controller -> {
            System.out.printf("~ controller : %s => %s%n", controller.getName(), controller.getType().toString());
        });
    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    public void keyTyped(KeyEvent e) {

    }

    /**
     * Manage possible input from joystick/gamepad via JInput.
     *
     * @param app the parent Game implementation.
     */
    public void update(Game app) {
        // TODO implements the GamePad (controler) input processing
    }


    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        if (isKeyPressed(KeyEvent.VK_Q) || isKeyPressed(KeyEvent.VK_ESCAPE)) {
            app.requestExit();
        }
        if (e.isControlDown()) {

            if (isKeyPressed(KeyEvent.VK_P) || isKeyPressed(KeyEvent.VK_PAUSE)) {
                app.setPause(app.isNotPaused());
            }
            if (isKeyPressed(KeyEvent.VK_D)) {
                app.setDebug(app.getDebug() + 1 < 6 ? app.getDebug() + 1 : 0);
            }
        }
        keys[e.getKeyCode()] = false;
    }

    public void dispose(Game game) {

    }
}