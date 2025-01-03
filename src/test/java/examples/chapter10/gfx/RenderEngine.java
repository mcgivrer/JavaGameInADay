package examples.chapter10.gfx;

import entity.Camera;
import entity.Entity;
import examples.chapter10.io.InputListener;
import game.Game;
import physic.World;
import scenes.Scene;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;

import static com.snapgames.framework.utils.Log.isDebugGreaterThan;

/**
 * The {@link RenderEngine} introduce all the drawing process for every {@link Entity} on a {@link Scene}.
 *
 * @author Frédéric Delorme
 * @since 1.0.10
 */
public class RenderEngine {

    /**
     * The parent {@link Game} instance.
     */
    private final Game app;
    /**
     * Represents the main application window for the MonProgrammeCollision1 class.
     * <p>
     * This JFrame is used to display the graphical user interface of the application. It is
     * created and initialized with specific settings such as the window title, size,
     * and close operation based on the application's configuration.
     * <p>
     * The window also serves as the primary interface for handling and processing user input,
     * particularly through keyboard interactions. It integrates with the application's event
     * listeners to manage user actions and render updates.
     */
    private JFrame window;
    /**
     * The rendering buffer used for drawing operations.
     * This BufferedImage is initialized based on the application
     * configuration settings, specifically for rendering the
     * current state of the application. It acts as an off-screen
     * drawing area to provide smooth rendering updates.
     */
    private BufferedImage renderingBuffer;

    public RenderEngine(Game app, InputListener inputListener) {
        this.app = app;
        createWindow(inputListener);
        createBuffer(app.getConfig());
    }


    /**
     * Creates and initializes the main application window.
     * <p>
     * The window's title, size, and buffer strategy are configured based on the
     * application settings obtained from the config object.
     * <p>
     * This method sets the default close operation to exit the application when
     * the window is closed.
     * <p>
     * Adds the current instance as a key listener to the window to handle keyboard input.
     */
    private void createWindow(InputListener inputListener) {
        Config config = app.getConfig();
        // Create the Window
        window = new JFrame((String) config.get("app.render.window.title"));
        window.setPreferredSize(config.get("app.render.window.size"));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane((JPanel) app);
        window.pack();
        window.setVisible(true);
        window.addKeyListener(inputListener);
        window.createBufferStrategy((int) config.get("app.render.buffer.strategy"));
    }

    /**
     * Creates a rendering buffer used for drawing operations.
     * <p>
     * The render buffer size is obtained from the application configuration,
     * specifically the "app.render.buffer.size" property. The rendering buffer
     * is initialized as a BufferedImage with the dimensions specified in the
     * configuration and with the image type set to TYPE_INT_ARGB.
     */
    private void createBuffer(Config config) {
        Dimension renderBufferSize = config.get("app.render.buffer.size");
        renderingBuffer = new BufferedImage(
                renderBufferSize.width, renderBufferSize.height,
                BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Renders the current state of the application to the screen.
     * <p>
     * This method performs the following steps:
     * - Clears the rendering buffer and sets it to a black background.
     * - Draws a blue rectangle at the current position (x, y).
     * - Disposes of the graphics context used for drawing.
     * - Copies the contents of the rendering buffer to the screen using the window's buffer strategy.
     * - Disposes of the graphics context used for drawing to the screen.
     * - Shows the buffer strategy to display the rendered image.
     */
    public void render(Scene currentScene) {

        World world = currentScene.getWorld();
        Graphics2D g = renderingBuffer.createGraphics();
        // Configure rendering pipeline
        g.setRenderingHints(
                Map.of(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        // clear rendering buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight());

        Optional<Entity> cam = currentScene.getEntities().stream().filter(e -> e instanceof Camera).findFirst();
        Camera camera = (Camera) cam.orElse(null);

        if (camera != null) {
            g.translate((int) -camera.getX(), (int) -camera.getY());
        }
        drawWorldLimit(g, world, 16, 16);
        if (camera != null) {
            if (isDebugGreaterThan(1)) {
                drawDebugCamera(g, camera);
            }
            g.translate((int) camera.getX(), (int) camera.getY());
        }


        // draw entities
        currentScene.getEntities().stream()
                .filter(e -> !(e instanceof Camera))
                .forEach(e -> {
                    if (camera != null) {
                        g.translate((int) -camera.getX(), (int) -camera.getY());
                    }
                    drawEntity(e, world, g);
                    if (camera != null) {
                        g.translate((int) camera.getX(), (int) camera.getY());
                    }
                    currentScene.draw(app, g);

                    // Exécuter les comportements de dessin pour cette instance d'Entity.
                    e.getBehaviors().forEach(b -> b.draw(g, e));
                });
        g.dispose();

        // copy buffer to window.
        BufferStrategy bs = window.getBufferStrategy();
        Graphics2D gw = (Graphics2D) bs.getDrawGraphics();
        // configure rendering pipeline
        gw.setRenderingHints(
                Map.of(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        gw.drawImage(renderingBuffer, 0, 0, window.getWidth(), window.getHeight(),
                0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight()
                , null);

        gw.dispose();
        bs.show();

    }

    private static void drawDebugCamera(Graphics2D g, Camera camera) {
        g.setFont(g.getFont().deriveFont(9.0f));
        g.setColor(Color.yellow);
        Rectangle2D drawCamera = new Rectangle2D.Double(
                camera.getBounds2D().getX() + 20, camera.getBounds2D().getY() + 30,
                camera.getBounds2D().getWidth() - 40, camera.getBounds2D().getHeight() - 40);
        g.draw(drawCamera);
        g.drawString("#%d:%s".formatted(camera.getId(), camera.getName()),
                (int) (camera.getBounds2D().getX() + 20 + camera.getBounds2D().getWidth() * 0.70),
                (int) (camera.getBounds2D().getY() + camera.getBounds2D().getHeight() - 14));
    }

    private void drawWorldLimit(Graphics2D g, World world, int tileWidth, int tileHeight) {
        // draw the world limit.
        g.setColor(Color.GRAY);
        for (int ix = 0; ix < world.getWidth(); ix += tileWidth) {
            for (int iy = 0; iy < world.getHeight(); iy += tileHeight) {
                g.drawRect(ix, iy,
                        tileWidth, tileHeight);
            }
        }
        g.setColor(Color.DARK_GRAY);
        g.draw(world);
    }

    private void drawEntity(Entity e, World world, Graphics2D g) {
        g.translate((int) e.getX(), (int) e.getY());
        g.setColor(e.getFillColor());
        g.fill(e.getShape());
        // draw velocity vector
        g.setColor(e.getColor());
        g.drawLine((int) (e.getShape().getBounds().width * 0.5), (int) (e.getShape().getBounds().height * 0.5),
                (int) (e.getShape().getBounds().width * 0.5 + e.getDx() * 4), (int) (+e.getShape().getBounds().height * 0.5 + e.getDy() * 4));
        drawDebugEntity(g, world, e);
        g.translate((int) -e.getX(), (int) -e.getY());
    }

    private void drawDebugEntity(Graphics2D g, World world, Entity e) {
        if (isDebugGreaterThan(1)) {
            g.setStroke(new BasicStroke(0.5f));
            g.setColor(Color.ORANGE);
            g.draw(e.getShape());
            if (isDebugGreaterThan(2)) {
                g.setFont(g.getFont().deriveFont(9.0f));
                g.drawString("#:%d:%s".formatted(e.getId(), e.getName()), (int) e.getWidth(), 0);
                if (isDebugGreaterThan(3)) {
                    g.drawString("p:%3.0f,%3.0f".formatted(e.getX(), e.getY()), (int) e.getWidth(), 10);
                    g.drawString("s:%3.2f,%3.2f".formatted(e.getWidth(), e.getHeight()), (int) e.getWidth(), 20);
                    g.drawString("av:%3.2f,%3.2f".formatted(e.getDx(), e.getDy()), (int) e.getWidth(), 30);
                    g.drawString("g:%3.2f,%3.2f".formatted(world.getGravity().getX(), world.getGravity().getY()), (int) e.getWidth(), 40);
                }
            }
        }
    }

    /**
     * Release all reserved resources for the {@link RenderEngine}.
     */
    public void dispose() {
        window.dispose();
        renderingBuffer = null;
    }

    public BufferedImage getRenderingBuffer() {
        return renderingBuffer;
    }
}
