package com.snapgames.framework.utils;

import com.snapgames.framework.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Config extends HashMap<String, Object> {
    private final Game app;

    private final Properties props = new Properties();

    public Config(Game app) {
        super();
        this.app = app;
        put("app.window.title", "Test001");
        put("app.window.size", new Dimension(640, 400));
        put("app.render.buffer.size", new Dimension(320, 200));
        put("app.physic.world.play.area.size", new Rectangle2D.Double(0, 0, 640, 400));
        put("app.scene.default", "");
    }

    public void load(String configFilePath) {
        try {
            props.load(this.getClass().getResourceAsStream(configFilePath));
            props.forEach((k, v) -> {
                Log.info(Game.class, "%s=%s", k, v);
            });
            parseAttributes(props.entrySet().parallelStream().collect(Collectors.toList()));
        } catch (IOException e) {
            Log.info(Game.class, "Unable to read configuration file: %s", e.getMessage());
        }
    }

    private void parseAttributes(List<Entry<Object, Object>> collect) {
        collect.forEach(e -> {
            switch (e.getKey().toString()) {
                case "app.window.title" -> {
                    put("app.window.title", (String) e.getValue());
                }
                case "app.exit" -> {
                    app.exit = Boolean.parseBoolean(props.getProperty("app.exit"));
                }
                case "app.window.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    put("app.window.size", new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                }
                case "app.render.buffer.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    put("app.render.buffer.size", new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                }
                case "app.physic.world.play.area.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    put("app.physic.world.play.area.size", new Rectangle2D.Double(0, 0, Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                }
                case "app.scene.default" -> {
                    put("app.scene.default", (String) e.getValue());
                }
                default -> {
                    Log.error(Config.class, "Unknown value %s=%s", e.getKey(), e.getValue());
                }
            }
        });
    }

    public <T> T get(String name) {
        return (T) super.get(name);
    }
}