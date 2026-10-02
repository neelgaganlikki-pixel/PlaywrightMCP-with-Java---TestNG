package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PIMPage {
    private final Page page;
    private final Locator toast;

    public PIMPage(Page page) {
        this.page = page;
        this.toast = page.locator(".oxd-toast:visible");
    }

    public void deleteUserIfExists(String username) {
        try {
            System.out.println("Checking if user '" + username + "' already exists in Admin -> Users...");
            page.locator("a[href='/web/index.php/admin/viewAdminModule']").click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            try {
                page.locator(".oxd-form-loader").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(5000));
            } catch (Exception ignored) {
            }
            Locator usernameInput = page.locator(".oxd-input-group").filter(new Locator.FilterOptions().setHasText("Username")).locator("input");
            usernameInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            usernameInput.fill(username);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            page.waitForTimeout(1500);
            try {
                page.locator(".oxd-form-loader").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(5000));
            } catch (Exception ignored) {
            }
            Locator row = page.locator("div[role='row']").filter(new Locator.FilterOptions().setHasText(username)).first();
            if (row.count() > 0 && row.isVisible()) {
                System.out.println("Existing user '" + username + "' found. Deleting...");
                row.locator(".bi-trash").click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, Delete")).click();
                try {
                    page.locator(".oxd-toast:visible").waitFor(new Locator.WaitForOptions().setTimeout(5000));
                } catch (Exception ignored) {
                }
                page.waitForTimeout(1500);
                System.out.println("Existing user '" + username + "' deleted successfully.");
            } else {
                System.out.println("No existing user found for: " + username);
            }
        } catch (Exception e) {
            System.out.println("deleteUserIfExists completed with notice: " + e.getMessage());
        }
    }

    public void openEmployeeList() {
        if (!page.url().contains("/pim/viewEmployeeList")) {
            page.locator("a[href='/web/index.php/pim/viewPimModule']").click();
        }
        page.getByText("Employee Information", new Page.GetByTextOptions().setExact(true)).waitFor();
    }

    public void openAddEmployee() {
        page.getByText("Add Employee", new Page.GetByTextOptions().setExact(true)).click();
        page.locator("input[name='firstName']").waitFor();
    }

    public void fillAddEmployee(String firstName, String middleName, String lastName, String employeeId, Path photo) {
        page.locator("input[name='firstName']").fill(firstName);
        page.locator("input[name='middleName']").fill(middleName);
        page.locator("input[name='lastName']").fill(lastName);
        inputByLabel("Employee Id").fill(employeeId);
        page.locator("input[type='file']").setInputFiles(photo);
    }

    public void createLoginDetails(String username, String password) {
        page.locator(".oxd-switch-wrapper span").click();
        inputByLabel("Username").fill(username);
        page.locator(".oxd-radio-wrapper").filter(new Locator.FilterOptions().setHasText("Enabled")).click();
        inputByLabel("Password").fill(password);
        inputByLabel("Confirm Password").fill(password);
    }

    public void save() {
        System.out.println("Clicking Save button on URL: " + page.url());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save")).last().click();
        page.waitForTimeout(1000);
        Locator errors = page.locator(".oxd-input-field-error-message");
        if (errors.count() > 0) {
            System.err.println("Validation errors found after save: " + errors.allInnerTexts());
        }
    }

    public void verifySuccessToast(String expectedText) {
        System.out.println("Waiting for toast... current URL: " + page.url());
        Locator errors = page.locator(".oxd-input-field-error-message");
        if (errors.count() > 0) {
            System.err.println("Validation errors present: " + errors.allInnerTexts());
        }
        try {
            toast.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            String text = toast.innerText().toLowerCase();
            boolean valid = text.contains("success") && text.contains(expectedText.toLowerCase());
            System.out.println("OrangeHRM toast [" + (valid ? "VALID" : "INVALID") + "]: " + text);
            if (!valid) {
                throw new AssertionError("Unexpected OrangeHRM toast: " + text);
            }
        } catch (Exception e) {
            System.err.println("Failed waiting for toast. Current URL: " + page.url());
            if (errors.count() > 0) {
                System.err.println("Validation errors preventing save: " + errors.allInnerTexts());
            }
            throw e;
        }
    }

    public void waitForPersonalDetails() {
        try {
            page.waitForURL("**/pim/viewPersonalDetails/**", new Page.WaitForURLOptions().setTimeout(15000));
        } catch (Exception ignored) {
        }
        try {
            page.locator(".oxd-form-loader").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(10000));
        } catch (Exception ignored) {
        }
        fieldContainer("Other Id").locator("input").waitFor(new Locator.WaitForOptions().setTimeout(15000));
    }

    public String selectRandomByLabel(String label) {
        Locator dropdown = fieldContainer(label).locator(".oxd-select-text");
        if (dropdown.count() == 0) {
            return "Unavailable in current OrangeHRM UI";
        }
        dropdown.click();
        Locator options = page.locator(".oxd-select-option:visible");
        List<String> values = new ArrayList<>();
        for (String value : options.allInnerTexts()) {
            String trimmed = value.trim();
            if (!trimmed.isBlank() && !trimmed.startsWith("--")) {
                values.add(trimmed);
            }
        }
        if (values.isEmpty()) {
            throw new AssertionError("No valid options found for " + label);
        }
        String selected = values.get(ThreadLocalRandom.current().nextInt(values.size()));
        options.filter(new Locator.FilterOptions().setHasText(selected)).first().click();
        return selected;
    }

    public void fillPersonalDetails(String employeeId, String otherId, String license, String expiry, String dob) {
        try {
            page.locator(".oxd-form-loader").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(5000));
        } catch (Exception ignored) {
        }
        inputByLabel("Employee Id").fill(employeeId);
        inputByLabel("Other Id").fill(otherId);
        inputByLabel("Driver's License Number").fill(license);
        inputByLabel("License Expiry Date").fill(expiry);
        inputByBirth("Date of Birth", dob);
    }

    private void inputByBirth(String label, String value) {
        inputByLabel(label).fill(value);
    }

    public void selectByLabel(String label, String value) {
        fieldContainer(label).locator(".oxd-select-text").click();
        page.locator(".oxd-select-option:visible").filter(new Locator.FilterOptions().setHasText(value)).first().click();
    }

    public void selectGender(String value) {
        String radioValue = "Male".equals(value) ? "1" : "2";
        page.locator(".oxd-form-loader").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        fieldContainer("Gender").locator("input[type='radio'][value='" + radioValue + "']")
                .locator("..").locator("span").click();
    }

    public void fillTestField(String value) {
        inputByLabel("Test_Field").fill(value);
    }

    public void searchEmployee(String employeeId) {
        inputByLabel("Employee Id").fill(employeeId);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
    }

    public Locator employeeRow(String employeeId) {
        return page.locator("div[role='row']").filter(new Locator.FilterOptions().setHasText(employeeId)).first();
    }

    public void deleteEmployee(String employeeId) {
        Locator row = employeeRow(employeeId);
        if (!row.isVisible()) {
            throw new AssertionError("Employee row was not found for deletion: " + employeeId);
        }
        row.locator(".bi-trash").click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, Delete")).click();
    }

    public boolean employeeExists(String employeeId) {
        return employeeRow(employeeId).count() > 0;
    }

    private Locator fieldContainer(String label) {
        Locator container = page.locator(".oxd-input-group").filter(new Locator.FilterOptions().setHasText(label)).first();
        if (container.count() == 0) {
            String alt = label.contains("'") ? label.replace("'", "’") : label.replace("’", "'");
            container = page.locator(".oxd-input-group").filter(new Locator.FilterOptions().setHasText(alt)).first();
        }
        if (container.count() == 0 && label.toLowerCase().contains("license")) {
            container = page.locator(".oxd-input-group").filter(new Locator.FilterOptions().setHasText("License Number")).first();
        }
        return container;
    }

    private Locator inputByLabel(String label) {
        return fieldContainer(label).locator("input");
    }
}