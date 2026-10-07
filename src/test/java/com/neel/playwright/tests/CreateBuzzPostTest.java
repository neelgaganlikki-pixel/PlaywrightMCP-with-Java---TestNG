package com.neel.playwright.tests;

import com.microsoft.playwright.Locator;
import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.LoginPage;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("OrangeHRM Enterprise Portal")
@Feature("Buzz Social Feed")
public class CreateBuzzPostTest extends BaseTest {

    @Test(description = "Verify publishing new post on OrangeHRM Buzz feed")
    @Story("Publish Buzz Post")
    @Severity(SeverityLevel.NORMAL)
    @Description("Creates and publishes a timestamped status update to the Buzz newsfeed and asserts post visibility.")
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

        // Verify post creation success
        Assert.assertTrue(
                buzzPage.isSuccessMessageDisplayed(),
                "Success message or posted content was not displayed after posting"
        );
    }
}