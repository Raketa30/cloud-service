package ru.geekbrains.cloudservice.util;

import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
public class MyLogger {
    private MyLogger() {
    }

    public static void logError(String message) {
        log.log(Level.WARNING, message);
        System.out.println();
        System.out.println();
    }

    public static void logInfo(String message) {
        log.log(Level.INFO, message);
        System.out.println();
        System.out.println();
    }
}
