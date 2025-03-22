package com.snapgames.framework.gfx;

import com.snapgames.framework.GameInterface;
import com.snapgames.framework.entity.*;
import com.snapgames.framework.io.InputListener;
import com.snapgames.framework.io.ResourceManager;
import com.snapgames.framework.physic.PhysicEngine;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.scene.Scene;
import com.snapgames.framework.scene.SceneManager;
import com.snapgames.framework.system.GSystem;
import com.snapgames.framework.system.SystemManager;
import com.snapgames.framework.utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

import static com.snapgames.framework.utils.Log.*;

/**
 * The Renderer class is responsible for rendering game scenes and entities onto a window.
 * It implements the GSystem interface and utilizes various graphics operations to draw
 * the game world, entities, and debug information.
 */
public class Renderer implements GSystem {
    /**
     * Represents a reference to the main game application instance managed by this Renderer.
     * This variable is used to delegate control, access game states, and perform game-specific
     * operations such as handling exit requests or setting debug levels.
     *
     * This object serves as the central interface for the Renderer to interact with the game logic
     * and operations defined by the {@link GameInterface}.
     */
    private final GameInterface app;

    /**
     * The `window` variable represents the primary graphical user interface (GUI)
     * component for the renderer, implemented as a JFrame instance. It serves as
     * the container for rendering the game scenes and provides the backbone for
     * the rendering system's visual output.
     *
     * This variable is used to:
     * - Display the main game window.
     * - Handle user input through key listeners and window interactions.
     * - Manage fullscreen modes and window dimensions.
     * - Serve as the top-level window in the application's rendering hierarchy.
     *
     * The lifecycle of `window` is managed by methods such as `createWindow` and
     * `newWindow`, which initialize and configure its properties based on the current
     * game state and display requirements.
     */
    private JFrame window;
    /**
     * The drawbuffer variable is a BufferedImage that serves as an in-memory
     * drawing surface for rendering graphical elements. It acts as an offscreen
     * buffer where rendering operations can be performed before the final output
     * is displayed on the screen. This provides a mechanism for double buffering,
     * reducing flicker and improving rendering performance during graphical updates.
     */
    private BufferedImage drawbuffer;
    /**
     * Represents the font used for rendering debug information in the Renderer.
     * This font is primarily utilized to draw overlays, debug messages, or
     * additional visual aids for developers during the game's development process.
     * The font can be customized or replaced to suit the stylistic or
     * informational needs of debugging in the game application.
     */
    private Font debugFont;

    /**
     * A flag indicating whether the renderer operates in fullscreen mode.
     * It determines the display mode for the application window.
     *
     * When set to {@code true}, the application will use fullscreen mode,
     * maximizing the window and removing window decorations. When set to
     * {@code false}, the application will display in windowed mode with
     * a specific size and decorations.
     *
     * This variable is typically used in methods that manage
     * creating or switching the window display such as {@code newWindow}.
     */
    private boolean fullScreen = false;

    /**
     * Constructs a new Renderer instance associated with the provided GameInterface.
     * This constructor initializes the Renderer by linking it to the game application
     * and begins the debug logging process.
     *
     * @param app the GameInterface instance that the Renderer will be linked to,
     *            representing the game application that this renderer will manage.
     */
    public Renderer(GameInterface app) {
        this.app = app;
        debug(Renderer.class, "start of processing");
    }

    /**
     * Creates a new window with the specified title and size.
     *
     * @param title the title of the window to be created
     * @param size  the dimensions of the window to be created
     */
    private void createWindow(String title, Dimension size) {
        newWindow(title, size, false);
        debugFont = window.getGraphics().getFont().deriveFont(9.0f);
        debug(Renderer.class, "Window %s created with size of %dx%d", title, size.width, size.height);
    }

    /**
     * Creates a new window with the specified title, size, and fullscreen mode.
     *
     * @param title      the title of the window to be created
     * @param size       the dimensions of the window if not in fullscreen
     * @param fullScreen whether the window should be created in fullscreen mode
     */
    private void newWindow(String title, Dimension size, boolean fullScreen) {
        KeyListener il = null;
        if (window != null && window.isActive()) {
            il = window.getKeyListeners()[0];
            window.dispose();
        }
        window = new JFrame(title);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setFocusTraversalKeysEnabled(true);
        window.setIconImage(ResourceManager.get("/assets/images/thor-hammer.png"));
        if (fullScreen) {
            window.setUndecorated(fullScreen);
            if (il != null) {
                window.addKeyListener(il);
            }
        } else {
            window.setPreferredSize(size);
        }
        window.pack();
        window.createBufferStrategy(3);
        window.setVisible(true);
        if (fullScreen) {
            window.setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            window.setExtendedState(Frame.NORMAL);
        }


        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Action to perform on exit request
                app.requestExit();
            }
        });
    }

    /**
     * Sets an {@link InputListener} instance to the renderer's window, allowing it
     * to handle keyboard input events. This method also logs a debug message when
     * the listener is added as a key listener.
     *
     * @param il the {@link InputListener} instance to be added as a key listener for
     *           handling keyboard input events
     */
    public void setInputListener(InputListener il) {
        window.addKeyListener(il);
        debug(Renderer.class, "adding this %s as a KeyListener", il.getClass());
    }

    /**
     * Renders the current state of the given scene, including its entities and world boundaries.
     * The method also handles graphical transformations for the active camera and draws entities
     * fixed to the camera if applicable.
     *
     * @param scene the Scene object containing the entities, active camera, and world to be rendered
     */
    private void render(Scene scene) {

        Graphics2D g = (Graphics2D) drawbuffer.createGraphics();
        g.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g.setRenderingHints(Map.of(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        // clear display
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, drawbuffer.getWidth(), drawbuffer.getHeight());

        // Create layers
        Map<Integer, RenderLayer> layers = new HashMap<>();
        // dispatch Entities into layers
        scene.getEntities().values().stream()
                .filter(e -> !(e instanceof Camera))
                .filter(Entity::isActive)
                .filter(e -> e.getCameraIsStickedTo() == null)
                .forEach(e -> {
                    if (!layers.containsKey(e.getLayer())) {
                        RenderLayer layer = new RenderLayer("layer_%d".formatted(e.getLayer()));
                        layers.put(e.getLayer(), layer);
                    }
                    layers.get(e.getLayer()).add(e);
                });

        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            g.translate(-scene.getActiveCamera().x, -scene.getActiveCamera().y);
        }
        // draw the scene
        layers.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(l -> l.getValue().getChildren().stream()
                        .filter(e -> !(e instanceof Camera))
                        .filter(e -> ((Entity<?>) e).isActive())
                        .filter(e -> ((Entity<?>) e).getCameraIsStickedTo() == null)
                        .sorted((a, b) -> Integer.compare(((Entity<?>) a).getPriority(), ((Entity<?>) a).getPriority()))
                        .forEach(e -> {
                            drawEntity(g, scene, ((Entity<?>) e));
                            if (app.isDebugGreaterThan(0)) {
                                drawDebugInfoEntity(g, scene, ((Entity<?>) e));
                            }
                        }));

        // draw World borders
        g.setColor(Color.DARK_GRAY);
        g.draw(scene.getWorld());

        if (Optional.ofNullable(scene.getActiveCamera()).isPresent()) {
            g.translate(scene.getActiveCamera().x, scene.getActiveCamera().y);
        }
        // draw all entities fixed to the active Camera.
        scene.getEntities().values().stream()
                .filter(e -> !(e instanceof Camera))
                .filter(Entity::isActive)
                .filter(e -> e.getCameraIsStickedTo() != null && e.getCameraIsStickedTo().equals(scene.getActiveCamera()))
                .sorted(Comparator.comparingInt(Entity::getPriority))
                .forEach(e -> {
                    drawEntity(g, scene, e);
                });

        g.dispose();

        // copy buffer to window.
        if (window != null) {
            BufferStrategy bf = window.getBufferStrategy();
            if (bf != null) {
                bf.getDrawGraphics().drawImage(drawbuffer, 0, 0, window.getWidth(), window.getHeight(),
                        0, 0, drawbuffer.getWidth(), drawbuffer.getHeight(), null);
                if (!bf.contentsLost()) {
                    bf.show();
                }
            }
        }
    }

    /**
     * Draws debugging information for a specified entity within the given scene using the specified
     * {@code Graphics2D} object. This method renders various details such as the entity's ID,
     * position, size, velocity, acceleration, and force vectors.
     *
     * The debugging information includes:
     * - Entity ID and name.
     * - Entity position and size attributes.
     * - Velocity and acceleration vectors in the form of visual arrows.
     * - Applied force vectors on the entity.
     * The severity level of debugging detail displayed is controlled by the debugging level setting.
     *
     * @param g      the {@code Graphics2D} object used for rendering the debug information
     * @param scene  the scene containing the entity for which debug information is being drawn
     * @param e      the entity whose debugging information and visual elements are being drawn
     */
    private void drawDebugInfoEntity(Graphics2D g, Scene scene, Entity<?> e) {
        Vector2d velocity = e.getVelocity();
        Vector2d acc = e.getAcceleration();

        g.setColor(Color.ORANGE);
        g.draw(e);
        g.setFont(debugFont);
        g.drawString("#:%d:%s".formatted(e.getId(), e.getName()), (int) e.getWidth(), 0);
        if (isDebugGreaterThan(3)) {
            g.drawString("p:%3.0f,%3.0f".formatted(e.getX(), e.getY()), (int) e.getWidth(), 10);
            g.drawString("s:%3.2f,%3.2f".formatted(e.getWidth(), e.getHeight()), (int) e.getWidth(), 20);
            g.drawString("av:%3.2f,%3.2f".formatted(e.getVelocity().x, e.getVelocity().y), (int) e.getWidth(), 30);
        }
        // draw velocity vector
        drawVector(g, (e.x + (e.width * 0.5)), (e.y + (e.height * 0.5)), velocity.getX() * 100, velocity.getY() * 100, Color.CYAN);
        // draw acceleration vector
        drawVector(g, (e.x + (e.width * 0.5)), (e.y + (e.height * 0.5)), acc.getX() * 100, acc.getY() * 100, Color.RED);
        // draw forces vector
        e.getForces().forEach(f -> {
            drawVector(g, (e.x + (e.width * 0.5)), (e.y + (e.height * 0.5)), f.getX() * 100, f.getY() * 100, Color.YELLOW);
        });
    }

    /**
     * Draws a vector as a line on the specified {@code Graphics2D} object. The vector is
     * defined by its starting point and direction, and is displayed using the specified color.
     *
     * @param g  the {@code Graphics2D} object used for drawing
     * @param x  the x-coordinate of the starting point of the vector
     * @param y  the y-coordinate of the starting point of the vector
     * @param dx the x-component of the vector's direction
     * @param dy the y-component of the vector's direction
     * @param c  the color used to draw the vector
     */
    private void drawVector(Graphics2D g, double x, double y, double dx, double dy, Color c) {
        g.setColor(c);
        g.drawLine(
                (int) x, (int) y,
                (int) (x + dx), (int) (y + dy));

    }

    /**
     * Renders an entity within the context of a scene using a specified {@code Graphics2D} object.
     * The method determines the specific type of the entity and calls the appropriate rendering
     * method based on its class. Additionally, it processes and draws all the behaviors associated
     * with the entity.
     *
     * @param g      the {@code Graphics2D} object used for rendering the entity
     * @param scene  the scene containing the entity and its associated world
     * @param e      the entity to be rendered
     */
    public void drawEntity(Graphics2D g, Scene scene, Entity<?> e) {
        switch (e.getClass().getSimpleName()) {
            case "GameObject", "WorldArea" -> {
                drawObject(g, e);
            }
            case "ImageObject" -> {
                drawImage(g, (ImageObject) e);
            }
            case "SpriteObject" -> {
                drawSprite(g, (SpriteObject) e);
            }
            case "TextObject" -> {
                drawText(g, (TextObject) e);
            }
            case "GridObject" -> {
                drawGrid(g, scene, (GridObject) e);
            }
            case "GaugeObject" -> {
                drawGauge(g, (GaugeObject) e);
            }
            default -> {
                error(Renderer.class, "Unknown object class %s", e.getClass());
            }
        }
        e.getBehaviors().forEach(b -> b.draw(g, e));
    }

    private void drawSprite(Graphics2D g, SpriteObject e) {
        if (e.getVelocity().x > 0) {
            g.drawImage(e.getImage(), (int) e.x, (int) e.y, null);
        } else {
            g.drawImage(e.getImage(), (int) (e.x + e.getWidth()), (int) e.y, (int) -e.getWidth(), (int) e.getHeight(), null);
        }
    }

    private void drawImage(Graphics2D g, ImageObject e) {
        g.drawImage(e.getImage(), (int) e.x, (int) e.y, null);
    }

    /**
     * Draws the specified entity using the provided {@code Graphics2D} object.
     * If the entity has a fill color defined, it will be filled with the corresponding
     * color.
     *
     * @param g the {@code Graphics2D} object used to draw the entity
     * @param e the entity to be drawn
     */
    private static void drawObject(Graphics2D g, Entity<?> e) {
        if (e.getFillColor() != null) {
            g.setColor(e.getFillColor());
            g.fill(e.getShape());
        }
    }

    /**
     * Draws a {@code TextObject} onto the provided {@code Graphics2D} context.
     * This method sets the color and font specified by the {@code TextObject},
     * then renders its text at the defined position.
     *
     * @param g  the {@code Graphics2D} object used for rendering the text
     * @param te the {@code TextObject} containing the text, position, color, and font to be drawn
     */
    private static void drawText(Graphics2D g, TextObject te) {
        g.setColor(te.getColor());
        if (Optional.ofNullable(te.getFont()).isPresent()) {
            g.setFont(te.getFont());
        }
        g.drawString(te.getText(), (int) te.x, (int) te.y);
    }

    /**
     * Draws a graphical gauge representation on the provided {@code Graphics2D} object
     * based on the properties of the provided {@code GaugeObject}.
     *
     * The gauge consists of a series of nested rectangles representing its border,
     * inner layout, and fill proportionate to the gauge's current value relative
     * to its minimum and maximum values. The fill color and border color are
     * retrieved from the properties of the {@code GaugeObject}.
     *
     * @param g  the {@code Graphics2D} object used for rendering the gauge
     * @param gg the {@code GaugeObject} containing the properties (e.g., position,
     *           dimensions, colors, and value) used to draw the gauge
     */
    private void drawGauge(Graphics2D g, GaugeObject gg) {
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.6f));
        g.drawRect((int) gg.getX(), (int) gg.getY(), (int) gg.getWidth(), (int) gg.getHeight());
        g.drawRect((int) gg.getX() + 2, (int) gg.getY() + 2, (int) gg.getWidth() - 4, (int) gg.getHeight() - 4);
        g.setColor(gg.getColor());
        g.drawRect((int) gg.getX() + 1, (int) gg.getY() + 1, (int) gg.getWidth() - 2, (int) gg.getHeight() - 2);
        g.setColor(gg.getFillColor());
        g.fillRect(
                (int) gg.getX() + 3, (int) gg.getY() + 3,
                (int) (gg.getWidth() - 5 * ((gg.getMaxValue() - gg.getMinValue()) / gg.getValue())), (int) gg.getHeight() - 5);
    }

    /**
     * Draws a grid on the provided {@code Graphics2D} context based on the properties
     * of the given {@code GridObject} and the dimensions of the {@code World} in the
     * provided {@code Scene}. Each grid cell is calculated using the tile width and
     * height of the {@code GridObject}.
     *
     * @param g     the {@code Graphics2D} object used for rendering the grid
     * @param scene the {@code Scene} containing the world whose dimensions are used to
     *              determine the grid boundaries
     * @param go    the {@code GridObject} specifying the tile size and color of the grid
     */
    private void drawGrid(Graphics2D g, Scene scene, GridObject go) {
        g.setColor(go.getColor());
        for (int iy = 0; iy < scene.getWorld().getHeight(); iy += go.getTileWidth()) {
            for (int ix = 0; ix < scene.getWorld().getWidth(); ix += go.getTileWidth()) {
                g.drawRect(ix, iy, go.getTileWidth(), (int) (iy + go.getTileHeight() < scene.getWorld().getHeight()
                        ? go.getTileHeight()
                        : go.getTileHeight() - (scene.getWorld().getHeight() - iy)));
            }
        }
    }

    /**
     * Disposes of resources associated with the renderer, specifically the active window.
     *
     * This method checks if the `window` is not null, enabled, and active. If these conditions
     * are met, it disposes of the `window` and logs a debug message indicating the window has
     * been closed. Additionally, it logs the end of the disposal process for debugging purposes.
     *
     * This method is typically invoked to clean up resources and ensure proper closure of the
     * renderer's window.
     */
    public void dispose() {
        if (window != null && window.isEnabled() && window.isActive()) {
            window.dispose();

            debug(Renderer.class, "windows has been closed.");
        }
        debug(Renderer.class, "End of processing.");
    }

    /**
     * Retrieves the current {@code JFrame} instance associated with the renderer.
     * This window represents the primary graphical interface of the application.
     *
     * @return the {@code JFrame} instance representing the renderer's window
     */
    public JFrame getWindow() {
        return window;
    }

    /**
     * Retrieves the collection of dependencies required for the Renderer to function properly.
     *
     * This method provides a list of core components that the Renderer depends on for its
     * operations, such as configuration management, scene handling, physics processing, and input
     * event listening. The returned collection may include classes essential for rendering and
     * interaction with the game's systems.
     *
     * @return a collection of {@code Class<?>} objects representing the dependencies of the Renderer
     */
    @Override
    public Collection<Class<?>> getDependencies() {
        return List.of(Config.class, SceneManager.class, PhysicEngine.class, InputListener.class);
    }

    /**
     * Initializes the renderer by setting up the necessary configurations, including
     * the rendering buffer, window creation, and input event handling.
     *
     * @param game the GameInterface instance associated with this Renderer,
     *             providing a reference to the core game application
     */
    @Override
    public void initialize(GameInterface game) {
        Config config = SystemManager.get(Config.class);
        Dimension bufferSize = config.get("app.render.buffer.size");
        drawbuffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB);

        Dimension windowSize = config.get("app.render.window.size");
        String title = config.get("app.render.window.title");
        createWindow(title, windowSize);

        InputListener inputListener = SystemManager.get(InputListener.class);
        setInputListener(inputListener);
    }

    /**
     * Toggles the full-screen mode of the renderer's application window.
     *
     * This method pauses the application temporarily, switches the full-screen
     * state, retrieves the current window's dimensions and title, and creates
     * a new window in the desired mode (full-screen or windowed) with the same
     * title and dimensions. Once the new window is created, the application is
     * resumed.
     */
    public void switchFullScreenMode() {
        app.setPause(true);
        fullScreen = !fullScreen;
        JFrame w = getWindow();
        Dimension dim = w.getSize();
        String title = w.getTitle();
        newWindow(title, dim, fullScreen);
        app.setPause(false);
    }

    @Override
    public void start(GameInterface game) {

    }

    /**
     * Processes the current frame of the game, updating the rendering pipeline for the given
     * game context. This method synchronizes with the active scene through the SceneManager,
     * rendering its contents.
     *
     * @param game   the GameInterface instance providing the context for the game being rendered
     * @param elapsed the time elapsed since the last frame in seconds, used for frame calculations
     * @param stats   a map of statistical data or metrics collected during the game execution,
     *                which can be used for debugging or performance analysis
     */
    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
        SceneManager sm = SystemManager.get(SceneManager.class);
        render(sm.getActiveScene());
    }

    @Override
    public void postProcess(GameInterface game) {
    }

    @Override
    public void stop(GameInterface game) {

    }

    @Override
    public void dispose(GameInterface game) {
        dispose();
    }
}