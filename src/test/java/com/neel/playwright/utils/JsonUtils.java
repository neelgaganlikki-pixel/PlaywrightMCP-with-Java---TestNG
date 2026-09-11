package com.neel.playwright.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static void writeEmployeeData(EmployeeTestData data) throws IOException {
        Path output = Path.of("test_data", "user_data1.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, toJson(data.asJson(), 0) + System.lineSeparator(), StandardCharsets.UTF_8);
    }

    public static void writeEmployeeDataText(EmployeeTestData data) throws IOException {
        Path output = Path.of("test-results", "employee_test_data.txt");
        Files.createDirectories(output.getParent());
        String content = "Admin username: " + EmployeeTestData.ADMIN_USERNAME + System.lineSeparator()
                + "Admin password: " + EmployeeTestData.ADMIN_PASSWORD + System.lineSeparator()
                + "First name: " + data.firstName + System.lineSeparator()
                + "Middle name: " + data.middleName + System.lineSeparator()
                + "Last name: " + data.lastName + System.lineSeparator()
                + "Employee ID: " + data.employeeId + System.lineSeparator()
                + "Gender: " + data.gender + System.lineSeparator()
                + "Other ID: " + data.otherId + System.lineSeparator()
                + "Driver's license number: " + data.driverLicenseNumber + System.lineSeparator()
                + "License expiry date: " + data.licenseExpiryDate + System.lineSeparator()
                + "Nationality: " + data.nationality + System.lineSeparator()
                + "Marital status: " + data.maritalStatus + System.lineSeparator()
                + "Date of birth: " + data.dateOfBirth + System.lineSeparator()
                + "Employee username: " + EmployeeTestData.EMPLOYEE_USERNAME + System.lineSeparator()
                + "Employee status: Enabled" + System.lineSeparator()
                + "Employee password: " + EmployeeTestData.EMPLOYEE_PASSWORD + System.lineSeparator()
                + "Confirm password: " + EmployeeTestData.EMPLOYEE_PASSWORD + System.lineSeparator()
                + "Blood type: " + data.bloodType + System.lineSeparator()
                + "Test_Field: " + data.testField + System.lineSeparator()
                + "Photo: " + EmployeeTestData.PHOTO_PATH + System.lineSeparator();
        Files.writeString(output, content, StandardCharsets.UTF_8);
    }

    private static String toJson(Object value, int level) {
        if (value instanceof Map<?, ?> map) {
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append(System.lineSeparator()).append("  ".repeat(level + 1));
                json.append('"').append(escape(entry.getKey().toString())).append("\": ");
                json.append(toJson(entry.getValue(), level + 1));
                first = false;
            }
            if (!map.isEmpty()) {
                json.append(System.lineSeparator()).append("  ".repeat(level));
            }
            return json.append("}").toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        return "\"" + escape(value.toString()) + "\"";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}