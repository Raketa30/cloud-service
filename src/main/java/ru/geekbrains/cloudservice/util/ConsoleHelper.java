package ru.geekbrains.cloudservice.util;

import java.util.Scanner;

public class ConsoleHelper {
    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static String getSource() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
