package utils;

import game.Game;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Config class extends HashMap to store configuration parameters for an application
 * defined by GameInterface. It supports loading configuration from a properties
 * file and parsing command-line arguments to modify configuration settings.
 * The class initializes with a set of default values for various application
 * settings such as render options, physics parameters, and debug levels.
 */
public class Config extends HashMap<String, Object> {
    /**
     * Represents an instance of the game application conforming to the
     * GameInterface. This variable provides methods to control game execution,
     * such as starting, pausing, exiting, and managing the game's debug state.
     * It is initialized once and is immutable.
     */
    private final Game app;

    /**
     * Stores the configuration properties for the application.
     * This variable is used to load, parse, and access various configuration settings
     * that are defined in the properties file. These settings include application
     * specific parameters such as rendering options, debugging levels, and physical
     * properties related to game entities.
     * <p>
     * The properties stored in this variable can be accessed and modified within
     * the class to configure the behavior and initialization state of the application.
     */
    private final Properties props = new Properties();

    /**
     * The file path to the configuration properties file used by the application.
     * This path is utilized to locate and load the necessary configuration
     * properties that dictate the application's behavior and setup. The default
     * value points to the "config.properties" file located at the root of the
     * application's classpath.
     */
    private String configFilePath = "/config.properties";

    public Config(Game app) {
        super();
        this.app = app;
        //demo1
        put("app.test", false);
        put("app.debug.level", 0);
        put("app.render.window.title", "Default Title");
        // demo2
        put("app.render.window.size", new Dimension(640, 400));
        put("app.render.buffer.size", new Dimension(320, 200));
        put("app.render.fps", 60);
        // demo3
        put("app.physic.entity.player.speed", 2);
        put("app.physic.entity.player.elasticity", 2);
        put("app.physic.entity.player.friction", 2);
        // camera1
        put("app.render.camera.tween.factor", 0.002);

        // demo4
        put("app.physic.world.play.area.size", new Rectangle2D.Double(0, 0, 640, 400));
        put("app.physic.world.gravity", new Point2D.Double(0, -0.981));
        put("app.scene.default", "");
        put("app.scene.list", "");
    }

    public void load(String filePath) {
        this.configFilePath = filePath;
        load();
    }

    public void load() {
        try {
            System.out.printf("# Load configuration Properties file %s%n", configFilePath);
            props.load(this.getClass().getResourceAsStream(configFilePath));
            props.entrySet().parallelStream().toList().stream()
                    .sorted((a, b) -> ((String) a.getKey()).compareTo((String) b.getKey()))
                    .forEach((k) -> {
                        System.out.printf("- %s=%s%n", (String) k.getKey(), (String) k.getValue());
                    });

            parseAttributes(props.entrySet().parallelStream().collect(Collectors.toList()));
        } catch (IOException e) {
            System.err.printf("Unable to read configuration file: %s", e.getMessage());
        }
    }

    private void parseAttributes(List<Entry<Object, Object>> collect) {
        collect.stream()
                .forEach(e -> {
                    switch (e.getKey().toString()) {
                        case "app.render.window.title" -> {
                            put("app.render.window.title", (String) e.getValue());
                        }
                        case "app.test" -> {
                            put("app.test", Boolean.parseBoolean(props.getProperty("app.test")));
                        }
                        case "app.test.loop.max.count" -> {
                            put("app.test.loop.max.count", Integer.parseInt(props.getProperty("app.test.loop.max.count")));
                        }
                        case "app.debug.level" -> {
                            app.setDebug(Integer.parseInt(props.getProperty("app.debug.level")));
                        }
                        case "app.render.window.size" -> {
                            String[] values = ((String) e.getValue()).split("x");
                            put("app.render.window.size", new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                        }
                        case "app.render.fps" -> {
                            put("app.render.fps", Integer.parseInt(props.getProperty("app.render.fps")));
                        }
                        case "app.render.buffer.strategy" -> {
                            put("app.render.buffer.strategy", Integer.parseInt(props.getProperty("app.render.buffer.strategy")));
                        }
                        case "app.render.buffer.size" -> {
                            String[] values = ((String) e.getValue()).split("x");
                            put("app.render.buffer.size", new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                        }

                        case "app.render.camera.tween.factor" -> {
                            put("app.render.camera.tween.factor", Double.parseDouble(props.getProperty("app.render.camera.tween.factor")));
                        }
                        case "app.physic.entity.player.speed" -> {
                            put("app.physic.entity.player.speed", Double.parseDouble(props.getProperty("app.physic.entity.player.speed")));
                        }
                        case "app.physic.entity.player.elasticity" -> {
                            put("app.physic.entity.player.elasticity", Double.parseDouble(props.getProperty("app.physic.entity.player.elasticity")));
                        }
                        case "app.physic.entity.player.friction" -> {
                            put("app.physic.entity.player.friction", Double.parseDouble(props.getProperty("app.physic.entity.player.friction")));
                        }
                        case "app.physic.entity.enemy.max.speed.ratio" -> {
                            put("app.physic.entity.enemy.max.speed.ratio", Double.parseDouble(props.getProperty("app.physic.entity.enemy.max.speed.ratio")));
                        }
                        default -> {
                            System.out.printf("~ Unknown value for %s=%s%n", e.getKey(), e.getValue());
                        }
                    }
                });
    }

    public void parseArgs(String[] args) {
        List.of(args).forEach(arg -> {
            String[] kv = arg.split("=");
            switch (kv[0]) {
                case "config" -> {
                    configFilePath = kv[1];
                }
                default -> {
                    // nothing to do there !
                }
            }
        });
    }

    public <T> T get(String name) {
        return (T) super.get(name);
    }

    public <T> T get(String name, T defaultValue) {
        return (T) super.getOrDefault(name, defaultValue);
    }
}
