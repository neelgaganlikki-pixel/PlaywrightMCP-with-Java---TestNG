package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.BuzzPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateBuzzPostTest extends BaseTest {

    @Test
    public void verifyCreateBuzzPost() {
        LoginPage loginPage = new LoginPage(page);
        BuzzPage buzzPage = new BuzzPage(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Verify login page is displayed
        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Login page was not loaded"
        );

        // Enter credentials and login
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLogin();

        // Verify successful login by checking dashboard page
        Assert.assertTrue(
                loginPage.isDashboardPageDisplayed(),
                "Login was not successful - dashboard page not displayed"
        );

        // Navigate to Buzz page
        buzzPage.navigateToBuzzPage();

        // Verify Buzz page is displayed
        Assert.assertTrue(
                buzzPage.isBuzzPageDisplayed(),
                "Buzz page was not loaded"
        );

        // Click on "What's on your mind?" text box
        buzzPage.clickWhatsOnYourMindTextBox();

        // Enter random buzz post text
        buzzPage.enterRandomBuzzPost();

        // Click Post button
        buzzPage.clickPostButton();

        // Wait for success message to appear
        page.waitForSelector(
                "//div[contains(@class, 'oxd-toast')]",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setTimeout(5000)
        );

        // Verify success message is displayed
        Assert.assertTrue(
                buzzPage.isSuccessMessageDisplayed(),
                "Success message was not displayed after posting"
        );
    }
}