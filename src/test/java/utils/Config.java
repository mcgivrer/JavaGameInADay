package utils;

import com.snapgames.framework.GameInterface;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Config extends HashMap<String, Object> {
    private final com.snapgames.framework.GameInterface app;

    private final Properties props = new Properties();

    private String configFilePath = "/config.properties";

    public Config(GameInterface app) {
        super();
        this.app = app;
        put("app.test", false);
        put("app.debug.level", 0);
        put("app.render.window.title", "Default Title");
        put("app.render.window.size", new Dimension(640, 400));
        put("app.render.buffer.size", new Dimension(320, 200));
        put("app.render.fps", 60);
        put("app.physic.world.play.area.size", new Rectangle2D.Double(0, 0, 640, 400));
        put("app.physic.world.gravity", new Point2D.Double(0, -0.981));
        put("app.scene.default", "");
        put("app.scene.list", "");
        put("app.entity.player.speed", 2);
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
                    case "app.entity.player.speed" -> {
                        put("app.entity.player.speed", Integer.parseInt(props.getProperty("app.entity.player.speed")));
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
}
