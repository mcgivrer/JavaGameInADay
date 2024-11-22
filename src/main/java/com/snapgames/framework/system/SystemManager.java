package com.snapgames.framework.system;

import com.snapgames.framework.Game;
import com.snapgames.framework.utils.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SystemManager {
    private static SystemManager instance = new SystemManager();
    private static Game parent;
    private static final Map<Class<? extends GSystem>, GSystem> systems = new ConcurrentHashMap<>();

    private SystemManager() {
        Log.info("Start SystemManager");
    }

    public static void setParent(Game game) {
        parent = game;
    }

    public static void add(GSystem system) {
        systems.put(system.getClass(), system);
    }

    public static <T extends GSystem> T get(Class<?> className) {
        return (T) systems.get(className);
    }

    public static void dispose() {
        systems.values().forEach(s -> s.dispose(parent));
    }

    public static void process(double elapsed) {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.process(parent,elapsed));
    }

    public static void postProcess() {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.postProcess(parent));
    }

    public static void initialize() {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.initialize(parent));
    }

    public static void start(Game game) {
        systems.values().stream().sorted(
                        (s1, s2) -> s2.getDependencies() != null && s2.getDependencies().contains(s1.getClass()) ? -1 : 1)
                .forEach(s -> s.start(parent));
    }
}
