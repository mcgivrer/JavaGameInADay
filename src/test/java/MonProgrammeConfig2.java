import utils.Config;

public class MonProgrammeConfig2 extends TestGame {
    private String configFilePath = "/config2.properties";
    private Config config;

    public MonProgrammeConfig2() {
        System.out.printf("# Démarrage de %s%n", this.getClass().getSimpleName());
        config = new Config(this);
        config.load(configFilePath);
    }

    public void run(String[] args) {
        System.out.printf("=> Configuration for title:%s%n", (String) config.get("app.render.window.title"));
    }


    public static void main(String[] args) {
        MonProgrammeConfig2 prog = new MonProgrammeConfig2();
        prog.run(args);
    }
}