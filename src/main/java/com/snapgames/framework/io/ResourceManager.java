package com.snapgames.framework.io;

import com.snapgames.framework.utils.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>A service dedicated to store resources in cahce</p>
 * <p>Any loaded file resource will be loaded in cache in the correct java object to be directly used
 * in the scene.</p>
 * <p>USAGE:</p>
 * <PRE>
 * Font f = ResourceManager.get("path/to/my/resource.tff");
 * BufferedImage i = ResourceManager.get("path/to/my/resource.png");
 * </PRE>
 *
 * @author Frédéric Delorme
 * @since 1.0.9
 */
public class ResourceManager {
    private static Map<String, Object> resources = new ConcurrentHashMap<>();

    public static <T> T get(String pathToResource) {
        T object = null;

        if (!resources.containsKey(pathToResource)) {
            load(pathToResource);
        }
        object = (T) resources.get(pathToResource);
        return (T) object;
    }

    private static void load(String pathToResource) {

        String ext = pathToResource.substring(pathToResource.lastIndexOf(".") + 1).toUpperCase();
        switch (ext) {
            case "PNG", "JPG" -> {
                try {
                    resources.put(pathToResource, ImageIO.read(ResourceManager.class.getResourceAsStream(pathToResource)));
                } catch (IOException e) {
                    Log.error(ResourceManager.class, "Unable to read Icon: %s", e.getMessage());
                }
            }
            case "TTF" -> {
                try {
                    resources.put(pathToResource, Font.createFont(
                            Font.TRUETYPE_FONT,
                            ResourceManager.class.getResourceAsStream(pathToResource)));
                } catch (FontFormatException | IOException e) {
                    Log.warn(ResourceManager.class, "Unable to read Font: %s", e.getMessage());
                }
            }
            default -> {
                Log.error(ResourceManager.class, "Unknown loader resource %s", pathToResource);
            }
        }
    }

    public static void remove(String s) {
        resources.entrySet().stream().filter((e) -> e.getKey().equals(s)).toList().forEach(e -> resources.remove(e.getKey()));
    }
}