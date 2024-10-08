package com.snapgames.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Log class to trace everything standard on the console output.
 *
 * @author Frédéric Delorme
 * @version 1.0.0
 */
public class Log {

    private static int debug = 0;
    private static String debugFilter = "";
    private static String loggerFilter = "ERR,WARN,INFO";

    public static void log(String level, String message, Object... args) {
        if (loggerFilter.contains(level)) {
            String dateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
            System.out.printf(dateFormatted + "|" + level + "|" + message + "%n", args);
        }
    }

    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }

    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }

    public static void error(String message, Object... args) {
        log("ERR", message, args);
    }

    /**
     * Compare the current debug level to the required one
     *
     * @param level the required level
     * @return true if level greater than current debug.
     */
    public static boolean isDebugGreaterThan(int level) {
        return debug < level;
    }

    /**
     * Set the current debug level
     *
     * @param level the required debug level.
     */
    public static void setDebugLevel(int level) {
        debug = level;
    }

    /**
     * Retrieve the current debug level.
     *
     * @return the current int value of the debug level.
     */
    public static int getDebugLevel() {
        return debug;
    }

    /**
     * Define the current level to be output on the console.
     *
     * @param filter a String that can contain all the required level (ERR, INFO, WARN, DEBUG)
     */
    public static void setLoggerFilter(String filter) {
        loggerFilter = filter;
    }
}
