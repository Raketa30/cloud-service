package ru.geekbrains.cloudservice.util;

import java.util.logging.Level;

public class MyLogger {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(MyLogger.class.getName());

    public static void logError(String message) {
        log.log(Level.WARNING, message);
        System.out.println();
        System.out.println();
    }
}
