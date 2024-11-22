import java.util.Properties;

public class MonProgramme3 {
    private String configurationFilePath = "/config.properties";
    private Properties config = new Properties();
    private boolean exit = false;

    public MonProgramme3() {
        System.out.println("Démarrage de mon Programme2");
    }

    public void run(String[] args) {
        init(args);
    }

    private void init(String[] args) {
        System.out.println("nb args:" + args.length);
        for (String arg : args) {
            System.out.println("CLI: argument : " + arg);
        }
        try {
            config.load(this.getClass().getResourceAsStream(configurationFilePath));
            config.entrySet().stream().forEach(e -> {
                {
                    System.out.printf("Configuration: config %s = %s ", e.getKey(), e.getValue());
                }
            });
            exit = Boolean.parseBoolean(config.getProperty("app.exit", "false"));
        } catch (Exception e) {
            System.err.printf("Unable to read configuration file %s : %s%n", configurationFilePath, e.getMessage());
        }
    }

    public static void main(String[] args) {
        MonProgramme3 prog = new MonProgramme3();
        prog.run(args);
    }
}