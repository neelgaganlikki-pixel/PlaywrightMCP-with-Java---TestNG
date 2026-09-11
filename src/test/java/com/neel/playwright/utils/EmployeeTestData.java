package com.neel.playwright.utils;

import java.util.Map;

public class EmployeeTestData {
    public String firstName;
    public String middleName;
    public String lastName;
    public String employeeId;
    public String gender;
    public String otherId;
    public String driverLicenseNumber;
    public String licenseExpiryDate;
    public String nationality;
    public String maritalStatus;
    public String dateOfBirth;
    public String bloodType;
    public String testField;

    public static final String ADMIN_USERNAME = "Admin";
    public static final String ADMIN_PASSWORD = "admin123";
    public static final String EMPLOYEE_USERNAME = "JohnKing0442";
    public static final String EMPLOYEE_PASSWORD = "JkAuto2026X9m4";
    public static final String PHOTO_PATH = "test_data/employee_photo.png";

    public Map<String, Object> asJson() {
        return Map.of(
                "login", Map.of("username", ADMIN_USERNAME, "password", ADMIN_PASSWORD),
                "employee", Map.ofEntries(Map.entry("first_name", firstName), Map.entry("middle_name", middleName),
                        Map.entry("last_name", lastName), Map.entry("employee_id", employeeId), Map.entry("gender", gender),
                        Map.entry("other_id", otherId), Map.entry("driver_license_number", driverLicenseNumber),
                        Map.entry("license_expiry_date", licenseExpiryDate), Map.entry("nationality", nationality),
                        Map.entry("marital_status", maritalStatus), Map.entry("date_of_birth", dateOfBirth)),
                "employee_login", Map.of("create_login_details", true, "username", EMPLOYEE_USERNAME,
                        "status", "Enabled", "password", EMPLOYEE_PASSWORD, "confirm_password", EMPLOYEE_PASSWORD),
                "custom_fields", Map.of("blood_type", bloodType, "test_field", testField),
                "photo", Map.of("path", PHOTO_PATH));
    }
}