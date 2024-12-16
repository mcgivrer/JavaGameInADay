package examples;

import game.TestGame;
import utils.Config;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MonProgrammeWinAndLoop2 extends TestGame implements KeyListener {
    private String configFilePath = "/win-config-2.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;
    private JFrame window;

    public MonProgrammeWinAndLoop2() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    public void initialize() {
        testMode = config.get("app.test");
        maxLoopCount = (int) config.get("app.test.loop.max.count");
        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());
        createWindow();
    }

    private void createWindow() {
        // Create the Window
        window = new JFrame((String) config.get("app.render.window.title"));
        window.setPreferredSize(config.get("app.render.window.size"));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        window.addKeyListener(this);
    }

    public void loop() {
        int loopCount = 0;
        while (!isExitRequested() && ((testMode && loopCount < maxLoopCount) || !testMode)) {
            input();
            update();
            render();
            loopCount++;
            waitTime(16);
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
    }

    private void update() {
    }

    private void render() {
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
        MonProgrammeWinAndLoop2 prog = new MonProgrammeWinAndLoop2();
        prog.run(args);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.requestExit();
        }
    }
}