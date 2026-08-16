package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.LogoutPage;
import com.neel.playwright.pages.BuzzPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrangeHRMTest extends BaseTest {

   @Test(priority = 1)
    public void verifyOrangeHRMLogin() {

        LoginPage loginPage = new LoginPage(page);

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

        // Click Login
        loginPage.clickLogin();

        // Verify successful login
        Assert.assertTrue(
                loginPage.isDashboardPageDisplayed(),
                "Login was not successful - dashboard page not displayed"
        );
    }

    @Test(priority = 2)
    public void verifyOrangeHRMLogout() {

        LoginPage loginPage = new LoginPage(page);
        LogoutPage logoutPage = new LogoutPage(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Login
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLogin();

        // Verify dashboard
        Assert.assertTrue(
                loginPage.isDashboardPageDisplayed(),
                "Dashboard page was not displayed"
        );

        // Click logout button
        logoutPage.clickUserProfileDropdown();
        logoutPage.clickLogout();

        // Verify login page after logout
        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Logout was not successful"
        );
    }

    @Test(priority = 3)
    public void verifyCreateBuzzPost() {

        LoginPage loginPage = new LoginPage(page);
        BuzzPage buzzPage = new BuzzPage(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Login
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLogin();

        // Verify dashboard
        Assert.assertTrue(
                loginPage.isDashboardPageDisplayed(),
                "Login was not successful"
        );

        // Navigate to Buzz page
        buzzPage.navigateToBuzzPage();

        // Verify Buzz page
        Assert.assertTrue(
                buzzPage.isBuzzPageDisplayed(),
                "Buzz page was not loaded"
        );

        // Click What's on your mind
        buzzPage.clickWhatsOnYourMindTextBox();

        // Enter random Buzz post
        buzzPage.enterRandomBuzzPost();

        // Click Post
        buzzPage.clickPostButton();

        // Verify success message
        Assert.assertTrue(
                buzzPage.isSuccessMessageDisplayed(),
                "Success message was not displayed after posting"
        );
    }
}