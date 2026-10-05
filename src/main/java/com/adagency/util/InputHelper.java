package com.adagency.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class InputHelper {
    private static final Scanner SC = new Scanner(System.in);

    private InputHelper() {}

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    public static Integer readOptionalInt(String prompt) {
        String s = readLine(prompt);
        if (s.isBlank()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите целое число или пропустите.");
            return readOptionalInt(prompt);
        }
    }

    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                return new BigDecimal(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число (например, 150000.00).");
            }
        }
    }

    public static BigDecimal readOptionalBigDecimal(String prompt) {
        String s = readLine(prompt);
        if (s.isBlank()) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число или пропустите.");
            return readOptionalBigDecimal(prompt);
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readLine(prompt));
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: формат даты YYYY-MM-DD.");
            }
        }
    }

    public static LocalDate readOptionalDate(String prompt) {
        String s = readLine(prompt);
        if (s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка: формат даты YYYY-MM-DD или пропустите.");
            return readOptionalDate(prompt);
        }
    }
}