package com.snapgames.framework.gfx;

import com.snapgames.framework.Game;
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
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static com.snapgames.framework.utils.Log.*;
import static com.snapgames.framework.utils.Log.isDebugGreaterThan;

/**
 * The Renderer class is responsible for rendering game scenes and entities onto a window.
 * It implements the GSystem interface and utilizes various graphics operations to draw
 * the game world, entities, and debug information.
 */
public class Renderer implements GSystem {
    /**
     * The {@code app} field represents the {@link GameInterface} instance associated with this
     * {@code Renderer}. It acts as the primary connection between the rendering system and the
     * game application logic, facilitating game state management, debugging processes, and
     * interaction control.
     *
     * This field is initialized during the creation of a {@code Renderer} instance and remains
     * constant throughout its lifecycle. It is used by the renderer to access game-specific
     * methods and configurations critical for rendering operations and game flow control.
     */
    private final GameInterface app;

    /**
     * Represents the main application window used by the renderer.
     * This {@code JFrame} serves as the primary graphical user interface
     * container for displaying rendered content and handling user interactions.
     * It is initialized and managed by methods within the {@code Renderer} class,
     * such as window creation and fullscreen toggling.
     */
    private JFrame window;
    /**
     * An off-screen {@code BufferedImage} used as a drawing surface during rendering operations.
     * This image serves as a back buffer where graphical elements are drawn before being
     * rendered onto the main display. Utilizing a draw buffer helps in minimizing flickering
     * and achieving smoother rendering by performing all graphical operations off-screen
     * and then displaying the resulting image in a single operation.
     */
    private BufferedImage drawbuffer;
    /**
     * The {@code debugFont} field represents the font used for rendering
     * debugging information in the renderer. This font is typically utilized
     * in visual debug overlays to display textual data such as entity
     * properties, performance metrics, or other diagnostics during development
     * and debugging sessions.
     */
    private Font debugFont;

    /**
     * Indicates whether the Renderer operates in full-screen mode.
     * This flag is used to control and toggle between full-screen
     * and windowed display modes within the application.
     *
     * By default, the value is {@code false}, meaning the application
     * starts in windowed mode unless explicitly set to full-screen.
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
        debugFont = window.getGraphics().getFont().deriveFont(8.0f);
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
     * Associates an {@code InputListener} with the application's primary window to handle
     * keyboard input events. This method adds the given {@code InputListener} instance
     * as a {@code KeyListener} to the window, enabling dynamic interactions between
     * user inputs and the game environment.
     *
     * @param il the {@code InputListener} instance to be registered as a key event listener
     *           for the window. This object processes specific key events and game interactions.
     */
    public void setInputListener(InputListener il) {
        window.addKeyListener(il);

        debug(Renderer.class, "adding this %s as a KeyListener", il.getClass());
    }

    /**
     * Renders the given scene by processing and drawing its entities, layers, and world boundaries
     * onto a graphics context. Handles entity layering, camera transformations,
     * and debugging information if enabled.
     *
     * @param scene the Scene object to be rendered, containing entities and camera settings
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
     * Draws debugging information for a given entity onto the provided graphics context.
     * The method displays visual representations and textual data, such as position,
     * size, velocity, acceleration, and forces, depending on the debug level.
     *
     * @param g      the {@code Graphics2D} object used for rendering the debug information
     * @param scene  the {@code Scene} containing the entity being debugged
     * @param e      the {@code Entity} object for which the debug information is to be drawn
     */
    private void drawDebugInfoEntity(Graphics2D g, Scene scene, Entity<?> e) {
        Vector2d velocity = e.getVelocity();
        Vector2d acc = e.getAcceleration();

        g.setColor(Color.ORANGE);
        g.draw(e);
        g.setFont(debugFont);
        g.translate(e.getX() + e.getWidth(), e.getY());
        g.drawString("#:%d:%s".formatted(e.getId(), e.getName()), 0, 0);
        if (isDebugGreaterThan(3)) {
            g.drawString("p:%3.0f,%3.0f".formatted(e.getX(), e.getY()), 0, 10);
            g.drawString("s:%3.2f,%3.2f".formatted(e.getWidth(), e.getHeight()), 0, 20);
            g.drawString("av:%3.2f,%3.2f".formatted(e.getVelocity().x, e.getVelocity().y), 0, 30);
        }
        g.translate(-(e.getX() + e.getWidth()), -(e.getY()));
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
     * Draws a vector represented by its starting point, direction, and color
     * on the provided {@code Graphics2D} object.
     *
     * @param g   the {@code Graphics2D} object to use for rendering the vector
     * @param x   the x-coordinate of the starting point of the vector
     * @param y   the y-coordinate of the starting point of the vector
     * @param dx  the horizontal displacement (direction and magnitude) of the vector
     * @param dy  the vertical displacement (direction and magnitude) of the vector
     * @param c   the {@code Color} used to draw the vector
     */
    private void drawVector(Graphics2D g, double x, double y, double dx, double dy, Color c) {
        g.setColor(c);
        g.drawLine(
                (int) x, (int) y,
                (int) (x + dx), (int) (y + dy));

    }

    /**
     * Draws a specific entity onto the provided graphics context. The method determines
     * the type of the entity and calls the appropriate rendering logic for it. Additionally,
     * it applies behaviors associated with the entity, allowing custom drawing operations.
     *
     * @param g     the {@code Graphics2D} object used for rendering the entity
     * @param scene the {@code Scene} containing the entity, used for context-specific rendering
     * @param e     the {@code Entity} to be drawn, whose type determines the specific rendering logic
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

    /**
     * Draws a sprite entity onto the provided {@code Graphics2D} context. The method
     * determines the sprite's rendering logic based on its velocity. If the velocity's x-component
     * is positive, the sprite is drawn normally. Otherwise, the sprite is rendered flipped horizontally.
     *
     * @param g the {@code Graphics2D} object used for rendering the sprite
     * @param e the {@code SpriteObject} to be drawn, containing the image and positional data
     */
    private void drawSprite(Graphics2D g, SpriteObject e) {
        if (e.getVelocity().x > 0) {
            g.drawImage(e.getImage(), (int) e.x, (int) e.y, null);
        } else {
            g.drawImage(e.getImage(), (int) (e.x + e.getWidth()), (int) e.y, (int) -e.getWidth(), (int) e.getHeight(), null);
        }
    }

    /**
     * Draws the specified {@code ImageObject} onto the provided {@code Graphics2D} context.
     * This method uses the image data and positional information stored in the
     * {@code ImageObject} to render it onto the graphics context at the desired coordinates.
     *
     * @param g the {@code Graphics2D} object used to render the image
     * @param e the {@code ImageObject} containing the image and positional data
     */
    private void drawImage(Graphics2D g, ImageObject e) {
        g.drawImage(e.getImage(), (int) e.x, (int) e.y, null);
    }

    /**
     * Draws an entity onto the provided {@code Graphics2D} context. If the entity has
     * a defined fill color, the method sets the color and fills the entity's shape.
     *
     * @param g the {@code Graphics2D} object used to render the entity
     * @param e the {@code Entity} whose shape and fill color are used for drawing
     */
    private static void drawObject(Graphics2D g, Entity<?> e) {
        if (e.getFillColor() != null) {
            g.setColor(e.getFillColor());
            g.fill(e.getShape());
        }
    }

    /**
     * Draws the specified {@code TextObject} onto the provided {@code Graphics2D} context.
     * This method sets the color and font (if defined) from the {@code TextObject},
     * and renders the text at the given coordinates.
     *
     * @param g  the {@code Graphics2D} object used for rendering the text
     * @param te the {@code TextObject} containing the text, position, font, and color to be drawn
     */
    private static void drawText(Graphics2D g, TextObject te) {
        g.setColor(te.getColor());
        if (Optional.ofNullable(te.getFont()).isPresent()) {
            g.setFont(te.getFont());
        }
        g.drawString(te.getText(), (int) te.x, (int) te.y);
    }

    /**
     * Draws a graphical gauge representation on the provided {@code Graphics2D} context.
     * The gauge is visually constructed using rectangles to represent the frame,
     * borders, and filled area. The fill level reflects the current value of the
     * {@code GaugeObject} in relation to its minimum and maximum values.
     *
     * @param g  the {@code Graphics2D} object used for rendering the gauge
     * @param gg the {@code GaugeObject} defining the gauge properties, such as position,
     *           dimensions, color, minimum value, maximum value, and current value
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
     * Draws a grid on the scene using the specified {@code Graphics2D} context. The grid
     * is defined by the properties of the provided {@code GridObject}, and adapts to the
     * dimensions of the scene's world and grid object settings.
     *
     * @param g    the {@code Graphics2D} object used for rendering the grid
     * @param scene the {@code Scene} object containing the world settings and dimensions
     * @param go   the {@code GridObject} specifying the grid's properties, such as color, tile width, and tile height
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
     * Releases resources held by the renderer, particularly the associated window,
     * and performs cleanup actions required for proper disposal.
     *
     * This method checks if the window is valid, enabled, and active, and then
     * disposes of it to free up system resources. Additionally, it logs debug
     * messages to indicate the status of the window disposal and the end of the
     * disposal process.
     */
    public void dispose() {
        if (window != null && window.isEnabled() && window.isActive()) {
            window.dispose();

            debug(Renderer.class, "windows has been closed.");
        }
        debug(Renderer.class, "End of processing.");
    }

    /**
     * Retrieves the primary application window managed by the {@code Renderer}.
     *
     * @return the {@code JFrame} instance representing the main application window.
     */
    public JFrame getWindow() {
        return window;
    }

    /**
     * Retrieves a collection of classes that the Renderer depends on for its functionality.
     * These dependencies represent essential components required by the Renderer to operate
     * correctly, such as configuration, scene management, physics processing, and input handling.
     *
     * @return a collection of {@code Class<?>} objects representing the dependencies of the Renderer.
     */
    @Override
    public Collection<Class<?>> getDependencies() {
        return List.of(Config.class, SceneManager.class, PhysicEngine.class, InputListener.class);
    }

    /**
     * Initializes the Renderer by setting up the rendering buffer, creating the main application
     * window, and associating an input listener. This method retrieves configuration properties
     * needed for setting up the buffer size, window size, and title from the system configuration.
     * It also assigns an input listener to handle keyboard events.
     *
     * @param game the {@code GameInterface} instance that this renderer is associated with,
     *             representing the game application to be rendered.
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
     * Toggles the full-screen mode of the application.
     *
     * This method switches the application's display mode between full-screen and windowed mode.
     * It temporarily pauses the application during the transition, updates the current window instance,
     * and reinitializes the window with the desired display mode.
     *
     * The current window size and title are preserved while transitioning modes.
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