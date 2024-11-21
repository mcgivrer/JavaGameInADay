package com.snapgames.framework.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Log class to trace everything standard on the console output.
 *
 * @author Frédéric Delorme
 * @version 1.0.0
 */
public class Log {

    public enum LogLevel {
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARN("WARN"),
        ERROR("ERROR"),
        FATAL("FATAL");

        String value;

        LogLevel(String value) {
            this.value = value;
        }

        String getValue() {
            return this.value;
        }
    }

    private static final String separator = "\t";
    private static final String lineFeed = "%n";
    private static int debug = 0;
    private static String debugFilter = "";
    private static String loggerFilter = "ERR,WARN,INFO,DEBUG";

    public static void log(LogLevel level, String message, Object... args) {
        String dateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        if (loggerFilter.contains(level.getValue())) {
            System.out.printf(dateFormatted + separator + level + separator + message + lineFeed, args);
        }
        if ((level.equals(LogLevel.ERROR) || level.equals(LogLevel.FATAL))) {
            System.err.printf(dateFormatted + separator + level + separator + message + lineFeed, args);
        }
    }

    public static void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    public static void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    public static void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    public static void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }

    public static void debug(Class<?> className, String message, Object... args) {
        log(LogLevel.DEBUG, className.getCanonicalName() + separator + message, args);
    }

    public static void info(Class<?> className, String message, Object... args) {
        log(LogLevel.INFO, className.getCanonicalName() + separator + message, args);
    }

    public static void warn(Class<?> className, String message, Object... args) {
        log(LogLevel.WARN, className.getCanonicalName() + separator + message, args);
    }

    public static void error(Class<?> className, String message, Object... args) {
        log(LogLevel.ERROR, className.getCanonicalName() + separator + message, args);
    }

    public static void fatal(Class<?> className, String message, Object... args) {
        log(LogLevel.FATAL, className.getCanonicalName() + separator + message, args);
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
