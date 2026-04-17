package com.coop.loan.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SocialSecurityNumberService {

    private SocialSecurityNumberService() {}

    public static int extractAge(String personalCode) {
        int centuryDigit = Character.getNumericValue(personalCode.charAt(0));
        String datePart = personalCode.substring(1, 7);

        int centuryPrefix = switch (centuryDigit) {
            case 1, 2 -> 1800;
            case 3, 4 -> 1900;
            case 5, 6 -> 2000;
            default -> throw new IllegalArgumentException("Invalid personal code century digit: " + centuryDigit);
        };

        int year = centuryPrefix + Integer.parseInt(datePart.substring(0, 2));
        int month = Integer.parseInt(datePart.substring(2, 4));
        int day = Integer.parseInt(datePart.substring(4, 6));

        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate today = LocalDate.now();
        int age = today.getYear() - birthDate.getYear();
        if (birthDate.withYear(today.getYear()).isAfter(today)) {
            age--;
        }
        return age;
    }

    public static boolean isValid(String personalCode) {
        if (personalCode == null || personalCode.length() != 11) return false;
        if (!personalCode.matches("\\d{11}")) return false;

        int[] weights1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        int[] weights2 = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};

        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(personalCode.charAt(i)) * weights1[i];
        }
        int remainder = sum % 11;

        if (remainder == 10) {
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(personalCode.charAt(i)) * weights2[i];
            }
            remainder = sum % 11;
            if (remainder == 10) remainder = 0;
        }

        return remainder == Character.getNumericValue(personalCode.charAt(10));
    }
}
