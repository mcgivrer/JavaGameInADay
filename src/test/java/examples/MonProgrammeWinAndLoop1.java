package examples;

import game.TestGame;
import utils.Config;

public class MonProgrammeWinAndLoop1 extends TestGame {
    private String configFilePath = "/config-loop.properties";
    private Config config;

    private boolean testMode = false;
    private int maxLoopCount = 1;

    public MonProgrammeWinAndLoop1() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    public void initialize() {
        testMode = config.get("app.test");
        maxLoopCount = (int) config.get("app.test.loop.max.count");
        System.out.printf("# %s est initialisé%n", this.getClass().getSimpleName());
    }

    public void loop() {
        int loopCount = 0;
        while (!isExitRequested() && ((testMode && loopCount < maxLoopCount) || !testMode)) {
            input();
            update();
            render();
            loopCount++;
        }
        System.out.printf("=> Game loops %d times%n", loopCount);
    }

    private void input() {
    }

    private void update() {
    }

    private void render() {
    }

    private void dispose() {
        System.out.printf("# %s est terminé.%n", this.getClass().getSimpleName());
    }


    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
        initialize();
        loop();
        dispose();
    }


    public static void main(String[] args) {
        MonProgrammeWinAndLoop1 prog = new MonProgrammeWinAndLoop1();
        prog.run(args);
    }
}