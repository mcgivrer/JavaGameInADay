package com.snapgames.framework.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A utility class that provides logging capabilities with different levels of severity.
 * <p>
 * The supported log levels are ERR, WARN, INFO, and DEBUG. Messages are logged with a
 * timestamp and can optionally include additional arguments.
 * <p>
 * Log filtering can be controlled through the logger filter, and a debug level can be set
 * to control the verbosity of debug messages.
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

    /**
     * Logs a message at a specified log level.
     * <p>
     * The message is timestamped and formatted according to the specified log level and
     * provided arguments. Logging will only be performed if the specified log level is
     * included in the logger filter.
     *
     * @param level   the severity level of the log (e.g., ERR, WARN, INFO, DEBUG)
     * @param message the log message to be recorded
     * @param args    additional arguments to be included in the log message
     */
    public static void log(Class<?> className, LogLevel level, String message, Object... args) {

        if ((level.equals(LogLevel.ERROR) || level.equals(LogLevel.FATAL))) {
            System.out.printf("%s %s %s %s %s %s %s%n", LocalDateTime.now(), separator, level, separator, className.getCanonicalName(), separator,
                    message.formatted(args));
        }
    }

    /**
     * Logs a message at the DEBUG log level.
     *
     * @param message the log message to be recorded
     * @param args    additional arguments to be included in the log message
     */
    @Deprecated
    public static void debug(String message, Object... args) {
        log(null, LogLevel.DEBUG, message, args);
    }

    /**
     * Logs a message at the INFO log level.
     *
     * @param message the log message to be recorded
     * @param args    additional arguments to be included in the log message
     */
    @Deprecated
    public static void info(String message, Object... args) {
        log(null, LogLevel.INFO, message, args);
    }

    /**
     * Logs a message at the WARN log level.
     *
     * @param message the log message to be recorded
     * @param args    additional arguments to be included in the log message
     */
    @Deprecated
    public static void warn(String message, Object... args) {
        log(null, LogLevel.WARN, message, args);
    }

    /**
     * Logs a message at the ERROR log level.
     *
     * @param message the log message to be recorded
     * @param args    additional arguments to be included in the log message
     */
    @Deprecated
    public static void error(String message, Object... args) {
        log(null, LogLevel.ERROR, message, args);
    }

    /**
     * Logs a message at the DEBUG log level, specifically including the name of the class
     * from which the logging call was made.
     *
     * @param className the class object from which the logging call is made
     * @param message   the log message to be recorded
     * @param args      additional arguments to be included in the log message
     */
    public static void debug(Class<?> className, String message, Object... args) {
        log(className, LogLevel.DEBUG, message, args);
    }

    /**
     * Logs a message at the INFO log level, specifically including the name of the class
     * from which the logging call was made.
     *
     * @param className the class object from which the logging call is made
     * @param message   the log message to be recorded
     * @param args      additional arguments to be included in the log message
     */
    public static void info(Class<?> className, String message, Object... args) {
        log(className, LogLevel.INFO, message, args);
    }

    /**
     * Logs a message at the WARN log level, specifically including the name of the class
     * from which the logging call was made.
     *
     * @param className the class object from which the logging call is made
     * @param message   the log message to be recorded
     * @param args      additional arguments to be included in the log message
     */
    public static void warn(Class<?> className, String message, Object... args) {
        log(className, LogLevel.WARN, message, args);
    }

    /**
     * Logs a message at the ERROR log level, specifically including the name of the class
     * from which the logging call was made.
     *
     * @param className the class object from which the logging call is made
     * @param message   the log message to be recorded
     * @param args      additional arguments to be included in the log message
     */
    public static void error(Class<?> className, String message, Object... args) {
        log(className, LogLevel.ERROR, message, args);
    }

    public static void fatal(Class<?> className, String message, Object... args) {
        log(className, LogLevel.FATAL, message, args);
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
