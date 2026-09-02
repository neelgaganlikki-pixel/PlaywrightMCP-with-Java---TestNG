package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.LogoutPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    @Test
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