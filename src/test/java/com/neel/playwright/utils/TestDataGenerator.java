package com.neel.playwright.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataGenerator {
    private static final String[][] FIRST_NAMES = {
            {"David", "Male"}, {"Robert", "Male"}, {"Michael", "Male"}, {"James", "Male"}, {"Daniel", "Male"},
            {"Emily", "Female"}, {"Sarah", "Female"}, {"Jessica", "Female"}, {"Laura", "Female"}, {"Emma", "Female"}
    };
    private static final String[] MIDDLE_NAMES = {"Alexander", "Thomas", "William", "Grace", "Marie"};
    private static final String[] LAST_NAMES = {"Wilson", "Taylor", "Morgan", "Anderson", "Parker"};

    private TestDataGenerator() {
    }

    public static EmployeeTestData generate() {
        EmployeeTestData data = new EmployeeTestData();
        String[] selectedName = FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
        data.firstName = selectedName[0];
        data.gender = selectedName[1];
        data.middleName = MIDDLE_NAMES[ThreadLocalRandom.current().nextInt(MIDDLE_NAMES.length)];
        data.lastName = LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
        data.employeeId = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
        data.otherId = alphaNumeric(8);
        data.driverLicenseNumber = alphaNumeric(10);
        data.licenseExpiryDate = format(LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(30, 1500)));
        data.dateOfBirth = format(LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(7000, 20000)));
        data.testField = alphaNumeric(8);
        return data;
    }

    private static String format(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-dd-MM"));
    }

    private static String alphaNumeric(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder value = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            value.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }
        return value.toString();
    }
}