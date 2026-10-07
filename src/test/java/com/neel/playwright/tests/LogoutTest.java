package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.LogoutPage;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("OrangeHRM Enterprise Portal")
@Feature("Authentication & Access")
public class LogoutTest extends BaseTest {

    @Test(description = "Verify OrangeHRM logout flow via user profile dropdown")
    @Story("User Session Termination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies authenticated session teardown and redirection to login page upon clicking logout.")
    public void verifyOrangeHRMLogout() {

        LoginPage loginPage = new LoginPage(page);
        LogoutPage logoutPage = new LogoutPage(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Verify login page is displayed
        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Login page was not loaded"
        );

        // Enter credentials
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");

        // Login
        loginPage.clickLogin();

        // Verify dashboard
        Assert.assertTrue(
                loginPage.isDashboardPageDisplayed(),
                "Login was not successful - dashboard page not displayed"
        );

        // Open user profile dropdown
        logoutPage.clickUserProfileDropdown();

        // Click logout
        logoutPage.clickLogout();

        // Verify logout
        Assert.assertTrue(
                logoutPage.isLoginPageDisplayed(),
                "Logout was not successful - not redirected to login page"
        );
    }
}