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
    /**
     * A thread-safe cache that maps resource identifiers (strings representing file paths
     * or keys) to corresponding objects (such as images, fonts, or any other resources).
     * This map serves as the central storage for loaded and cached resources in the
     * application, ensuring that resources are reused and not repeatedly loaded from disk.
     * <p>
     * Resources are loaded into this map using the {@code load(String pathToResource)}
     * method, which supports specific file types (e.g., PNG, JPG, TTF) and logs errors
     * if loading fails or if the file type is unsupported.
     * <p>
     * The {@code resources} map is used internally for efficient retrieval of
     * resources via the {@code get(String pathToResource)} method or for manual
     * removal of cached resources using the {@code remove(String s)} method.
     */
    private static Map<String, Object> resources = new ConcurrentHashMap<>();

    /**
     * Retrieves a resource from the cache associated with the specified path. If the resource is not
     * already loaded, it will be loaded and cached for future use.
     *
     * @param pathToResource the path to the resource to retrieve or load. This resource can be
     *                       an image, font, or any other supported file type.
     * @param <T>            the type of the resource to be retrieved.
     * @return the resource object of type T loaded and cached from the specified path.
     */
    public static <T> T get(String pathToResource) {
        T object = null;

        if (!resources.containsKey(pathToResource)) {
            load(pathToResource);
        }
        object = (T) resources.get(pathToResource);
        return (T) object;
    }

    /**
     * Loads a resource from the specified path and caches it for future use.
     * Supports image files with extensions such as "PNG" and "JPG" and font files with the "TTF" extension.
     * Logs an error for unsupported file types or when unable to load the specified resource.
     *
     * @param pathToResource The path to the resource file to be loaded and cached.
     */
    private static void load(String pathToResource) {

        String ext = pathToResource.substring(pathToResource.lastIndexOf(".") + 1).toUpperCase();
        switch (ext) {
            case "PNG", "JPG" -> {
                try {
                    resources.put(pathToResource, ImageIO.read(ResourceManager.class.getResourceAsStream(pathToResource)));
                } catch (IOException e) {
                    Log.error(ResourceManager.class, "Unable to read image resource at %s", e.getMessage());
                }
            }
            case "TTF" -> {
                try {
                    resources.put(pathToResource, Font.createFont(
                            Font.TRUETYPE_FONT,
                            ResourceManager.class.getResourceAsStream(pathToResource)));
                } catch (FontFormatException | IOException e) {
                    Log.warn(ResourceManager.class, "Unable to read Font resource at %s", e.getMessage());
                }
            }
            default -> {
                Log.error(ResourceManager.class, "Unknown loaded resource as %s", pathToResource);
            }
        }
    }

    /**
     * Removes a resource from the cache associated with the specified key.
     *
     * @param s the key of the resource to be removed from the cache
     */
    public static void remove(String s) {
        resources.entrySet().stream().filter((e) -> e.getKey().equals(s)).toList().forEach(e -> resources.remove(e.getKey()));
    }
}