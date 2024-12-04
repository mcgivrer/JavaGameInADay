import game.TestGame;
import utils.Config;

public class MonProgrammeConfig1 extends TestGame {
    private Config config;

    public MonProgrammeConfig1() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
    }

    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
    }


    public static void main(String[] args) {
        MonProgrammeConfig1 prog = new MonProgrammeConfig1();
        prog.run(args);
    }
}