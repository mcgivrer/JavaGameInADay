package com.snapgames.framework.utils;

import com.snapgames.framework.Game;
import com.snapgames.framework.GameInterface;
import com.snapgames.framework.physic.math.Vector2d;
import com.snapgames.framework.system.GSystem;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Config extends HashMap<String, Object> implements GSystem {
    private final GameInterface app;

    private final Properties props = new Properties();

    private String configFilePath = "/config.properties";

    public Config(GameInterface app) {
        super();
        this.app = app;
        put("app.test", false);
        put("app.debug.level", 0);
        put("app.render.window.title", "Test001");
        put("app.render.window.size", new Dimension(640, 400));
        put("app.render.buffer.size", new Dimension(320, 200));
        put("app.physic.world.play.area.size", new Rectangle2D.Double(0, 0, 640, 400));
        put("app.physic.world.gravity", new Point2D.Double(0, -0.981));
        put("app.scene.default", "");
        put("app.scene.list", "");
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
                case "app.render.window.title" -> {
                    put("app.render.window.title", (String) e.getValue());
                }
                case "app.exit" -> {
                    app.setExit(Boolean.parseBoolean(props.getProperty("app.exit")));
                }
                case "app.debug.level" -> {
                    app.setDebug(Integer.parseInt(props.getProperty("app.debug.level")));
                }
                case "app.render.window.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    put("app.render.window.size", new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                }
                case "app.render.buffer.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    put("app.render.buffer.size", new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
                }
                case "app.physic.world.play.area.size" -> {
                    String[] values = ((String) e.getValue()).split("x");
                    put("app.physic.world.play.area.size", new Rectangle2D.Double(0, 0, Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                }
                case "app.physic.world.gravity" -> {
                    String[] values = ((String) e.getValue()).substring(((String) e.getValue()).indexOf("(") + 1, ((String) e.getValue()).lastIndexOf(")")).split(",");
                    put("app.physic.world.gravity", new Vector2d(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                }
                case "app.scene.default" -> {
                    put("app.scene.default", (String) e.getValue());
                }
                case "app.scene.list" -> {
                    put("app.scene.list", ((String) e.getValue()).split(","));
                }
                default -> {
                    Log.error(Config.class, "Unknown value %s=%s", e.getKey(), e.getValue());
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

    @Override
    public Collection<Class<?>> getDependencies() {
        return null;
    }

    @Override
    public void initialize(GameInterface game) {
        load(configFilePath);
    }

    @Override
    public void start(GameInterface game) {

    }

    @Override
    public void process(GameInterface game, double elapsed, Map<String, Object> stats) {

    }

    @Override
    public void stop(GameInterface game) {

    }

    @Override
    public void dispose(GameInterface game) {

    }
}
