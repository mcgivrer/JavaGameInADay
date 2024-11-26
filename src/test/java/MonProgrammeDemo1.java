import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class MonProgrammeDemo1 extends TestGame implements KeyListener {
    private String configFilePath = "/demo1.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;
    private BufferedImage renderingBuffer;

    private boolean[] keys = new boolean[1024];

    private int x, y;

    public MonProgrammeDemo1() {
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
    }

    private void createWindow() {
        // Create the Window
        window = new JFrame((String) config.get("app.render.window.title"));
        window.setPreferredSize(config.get("app.render.window.size"));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
            x = Math.max(x - 2, 0);
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            x = Math.min(x + 2, renderingBuffer.getWidth());

        }
        if (keys[KeyEvent.VK_UP]) {
            y = Math.max(y - 2, 0);
        }
        if (keys[KeyEvent.VK_DOWN]) {
            y = Math.min(y + 2, renderingBuffer.getHeight());
        }

    }

    private void update() {
    }

    private void render() {
        Graphics2D g = renderingBuffer.createGraphics();
        // clear rendering buffer to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, renderingBuffer.getWidth(), renderingBuffer.getHeight());

        // draw something

        g.setColor(Color.BLUE);
        g.fillRect(x, y, 16, 16);

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
        MonProgrammeDemo1 prog = new MonProgrammeDemo1();
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