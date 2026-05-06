package utils;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static String readString(Scanner scanner, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Không được để trống. Vui lòng nhập lại.");
            }
        } while (input.isEmpty());
        return input;
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Lỗi: Vui lòng nhập số trong khoảng [" + min + ", " + max + "].");
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số nguyên hợp lệ.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Lỗi: Vui lòng nhập số trong khoảng [" + min + ", " + max + "].");
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số hợp lệ.");
            }
        }
    }

    public static String readDate(Scanner scanner, String prompt) {
        String dateRegex = "^\\d{2}/\\d{2}/\\d{4}$";
        Pattern pattern = Pattern.compile(dateRegex);
        while (true) {
            String input = readString(scanner, prompt);
            if (pattern.matcher(input).matches()) {
                return input;
            }
            System.out.println("Lỗi: Vui lòng nhập ngày tháng theo định dạng dd/MM/yyyy.");
        }
    }
}
