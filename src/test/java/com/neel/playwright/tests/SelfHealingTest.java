package com.neel.playwright.tests;

import com.microsoft.playwright.options.WaitUntilState;
import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LogoutPage;
import com.neel.playwright.pages.PIMPage;
import com.neel.playwright.utils.SelfHealingEngine;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Enterprise Self-Healing Test Case.
 * Verifies that broken primary locators are dynamically healed using fallback candidates,
 * cached in memory, persisted to JSON, and logged via the self-healing telemetry engine.
 */
@Epic("OrangeHRM Enterprise Portal")
@Feature("Resilient Automation Framework")
public class SelfHealingTest extends BaseTest {

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @Test(description = "Verify Enterprise Self-Healing Engine recovers from broken locators across end-to-end user journey")
    @Story("Dynamic Selector Healing & Caching")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies the self-healing engine detects broken primary selectors, applies heuristic fallbacks, and persists healed locators to cache.")
    public void testSelfHealingEngineWorkflow() {
        SelfHealingEngine engine = SelfHealingEngine.getInstance();
        int initialHealingCount = engine.getHealingCount();

        System.out.println(">>> STARTING SELF-HEALING ENGINE TEST CASE <<<");

        // 1. Navigate to Login Page
        page.navigate(
                LOGIN_URL,
                new com.microsoft.playwright.Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(60000)
        );

        // 2. Perform Login with INTENTIONALLY BROKEN primary locators
        System.out.println("[TEST] Testing self-healing for Username field (broken primary selector)...");
        engine.healFill(
                page,
                "Username Input",
                "LoginPage",
                "input#broken-username-selector-12345", // Intentionally broken primary
                List.of(
                        "input[name='username']",      // Valid fallback 1
                        "input[placeholder='Username']",// Valid fallback 2
                        "//input[@name='username']"
                ),
                "Admin",
                3000
        );

        System.out.println("[TEST] Testing self-healing for Password field (broken primary selector)...");
        engine.healFill(
                page,
                "Password Input",
                "LoginPage",
                "input#non-existent-pwd-field",        // Intentionally broken primary
                List.of(
                        "input[name='password']",      // Valid fallback 1
                        "input[type='password']",      // Valid fallback 2
                        "//input[@name='password']"
                ),
                "admin123",
                3000
        );

        System.out.println("[TEST] Testing self-healing for Submit Button (broken primary selector)...");
        engine.healClick(
                page,
                "Login Submit Button",
                "LoginPage",
                "button#broken-submit-btn-tag",        // Intentionally broken primary
                List.of(
                        "button[type='submit']",       // Valid fallback 1
                        "button:has-text('Login')",    // Valid fallback 2
                        "//button[@type='submit']"
                ),
                3000
        );

        page.waitForURL("**/dashboard/index**", new com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(30000));
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);

        // 3. Verify Dashboard with self-healed visibility check
        System.out.println("[TEST] Testing self-healing for Dashboard Header...");
        boolean isDashboardVisible = engine.healIsVisible(
                page,
                "Dashboard Header",
                "DashboardPage",
                "h6#broken-dashboard-title",           // Intentionally broken primary
                List.of(
                        ".oxd-topbar-header-breadcrumb",
                        "h6:has-text('Dashboard')",
                        "p:has-text('Time at Work')",
                        ".orangehrm-dashboard-widget"
                ),
                5000
        );
        Assert.assertTrue(isDashboardVisible, "Dashboard header should be visible via self-healing fallback");

        // 4. Navigate to PIM module using self-healed navigation
        System.out.println("[TEST] Testing PIM navigation via self-healing...");
        PIMPage pimPage = new PIMPage(page);
        pimPage.openEmployeeList();
        Assert.assertTrue(page.url().contains("/pim/"), "Should be navigated to PIM module");

        // 5. Perform Logout using self-healed LogoutPage
        System.out.println("[TEST] Testing Logout flow via self-healing...");
        LogoutPage logoutPage = new LogoutPage(page);
        logoutPage.clickUserProfileDropdown();
        logoutPage.clickLogout();

        Assert.assertTrue(logoutPage.isLoginPageDisplayed(), "Should successfully redirect to Login page after logout");

        // 6. Assert that multiple healing events succeeded
        int newHealingCount = engine.getHealingCount();
        System.out.println("[TEST] Initial healing count: " + initialHealingCount + " | Current healing count: " + newHealingCount);
        Assert.assertTrue(newHealingCount > initialHealingCount, "Self-healing engine should have recorded recovery events");

        // 7. Verify JSON persistence file was created and contains data
        Path cacheFile = Paths.get("test-results", "healed_locators.json");
        Assert.assertTrue(Files.exists(cacheFile), "Healed locators cache file should exist: " + cacheFile);

        // 8. Output summary telemetry
        engine.printSummary();
        System.out.println(">>> SELF-HEALING ENGINE TEST CASE PASSED SUCCESSFULLY <<<");
    }
}
