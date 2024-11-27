import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class MonProgrammeDemo3 extends TestGame implements KeyListener {
    private String configFilePath = "/demo3.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    private double x, y;
    private double dx, dy;
    private double elasticity = 0.75;
    private double friction = 0.98;
    private double speed = 0.0;

    public MonProgrammeDemo3() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    public void initialize() {
        testMode = config.get("app.test");
        maxLoopCount = (int) config.get("app.test.loop.max.count");
        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());
        createWindow();
        createBuffer();

        // blue square position initialization.
        x = (int) ((renderingBuffer.getWidth() - 16) * 0.5);
        y = (int) ((renderingBuffer.getHeight() - 16) * 0.5);

        // load physic factors
        speed = (double)config.get("app.physic.entity.player.speed");
        elasticity = (double)config.get("app.physic.entity.player.elasticity");
        friction = (double)config.get("app.physic.entity.player.friction");
    }

    private void createWindow() {
        // Create the Window
        window = new JFrame((String) config.get("app.render.window.title"));
        window.setPreferredSize(config.get("app.render.window.size"));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(this);
        window.pack();
        window.setVisible(true);
        window.addKeyListener(this);
        window.createBufferStrategy((int) config.get("app.render.buffer.strategy"));
    }

    private void createBuffer() {
        Dimension renderBufferSize = config.get("app.render.buffer.size");
        renderingBuffer = new BufferedImage(
                renderBufferSize.width, renderBufferSize.height,
                BufferedImage.TYPE_INT_ARGB);
    }

    public void loop() {
        int loopCount = 0;
        int frameTime = 1000 / (int) (config.get("app.render.fps"));
        while (!isExitRequested() && ((testMode && loopCount < maxLoopCount) || !testMode)) {
            input();
            update();
            render();
            loopCount++;
            waitTime(frameTime);
        }
        System.out.printf("=> Game loops %d times%n", loopCount);
    }

    private void waitTime(int delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            System.err.println("Unable to wait 16 ms !");
        }
    }

    private void input() {
        if (keys[KeyEvent.VK_LEFT]) {
            dx = -speed;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            dx = +speed;

        }
        if (keys[KeyEvent.VK_UP]) {
            dy = -speed;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            dy = +speed;
        }
    }

    private void update() {
        // calcul de la position en fonction de la vitesse courante.
        x += dx;
        y += dy;

        // application du rebond si collision avec le bord de la zone de jeu
        if (x < -8 || x > renderingBuffer.getWidth() - 8) {
            dx = -dx * elasticity;
        }
        if (y < -8 || y > renderingBuffer.getHeight() - 8) {
            dy = -dy * elasticity;
        }

        // repositionnement dans la zone de jeu si nécessaire
        x = Math.min(Math.max(x, -8), renderingBuffer.getWidth() - 8);
        y = Math.min(Math.max(y, -8), renderingBuffer.getHeight() - 8);

        // application du facteur de friction
        dx *= friction;
        dy *= friction;
    }

    private void render() {
        Graphics2D g = renderingBuffer.createGraphics();
        // clear rendering buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight());

        // draw something

        g.setColor(Color.BLUE);
        g.fillRect((int) x, (int) y, 16, 16);

        g.dispose();

        // copy buffer to window.
        BufferStrategy bs = window.getBufferStrategy();
        Graphics gw = bs.getDrawGraphics();
        gw.drawImage(renderingBuffer, 0, 0, window.getWidth(), window.getHeight(),
                0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight()
                , null);

        gw.dispose();
        bs.show();

    }

    private void dispose() {
        window.dispose();
        System.out.printf("# %s est terminé.%n", this.getClass().getSimpleName());
    }


    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
        initialize();
        loop();
        dispose();
    }


    public static void main(String[] args) {
        MonProgrammeDemo3 prog = new MonProgrammeDemo3();
        prog.run(args);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.requestExit();
        }
    }
}