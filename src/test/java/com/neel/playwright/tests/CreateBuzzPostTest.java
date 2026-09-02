package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.LoginPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateBuzzPostTest extends BaseTest {

    @Test
    public void verifyCreateBuzzPost() {

        LoginPage loginPage =
                new LoginPage(page);

        BuzzPage buzzPage =
                new BuzzPage(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Verify login page
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

        // Navigate to Buzz page
        buzzPage.navigateToBuzzPage();

        // Verify Buzz page
        Assert.assertTrue(
                buzzPage.isBuzzPageDisplayed(),
                "Buzz page was not loaded"
        );

        // Open post box
        buzzPage.clickWhatsOnYourMindTextBox();

        // Enter random post
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