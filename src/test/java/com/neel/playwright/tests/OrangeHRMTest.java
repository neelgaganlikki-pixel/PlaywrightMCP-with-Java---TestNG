package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.LogoutPage;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.DeleteBuzzPost;

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

     @Test(priority = 3)
    public void verifyDeleteBuzzPost() {
        LoginPage loginPage = new LoginPage(page);
        BuzzPage buzzPage = new BuzzPage(page);
        DeleteBuzzPost deleteBuzzPost = new DeleteBuzzPost(page);

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
        Assert.assertTrue(loginPage.isDashboardPageDisplayed(),
                "Login was not successful - dashboard page not displayed"
        );

        // Navigate to Buzz page
        buzzPage.navigateToBuzzPage();

        // Verify Buzz page is displayed
        Assert.assertTrue( buzzPage.isBuzzPageDisplayed(),
                "Buzz page was not loaded"
        );

        // Click on "What's on your mind?" text box
        buzzPage.clickWhatsOnYourMindTextBox();

        // Enter a specific buzz post text (to track for deletion)
        String postText = "Test Post for Deletion - " + System.currentTimeMillis();
        buzzPage.enterBuzzPost(postText);

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

        // Verify the post is displayed before deletion
        Assert.assertTrue(
                deleteBuzzPost.isPostDisplayed(postText),
                "Created post is not displayed before deletion"
        );

        // Delete the specific post by its text
        try {
            deleteBuzzPost.deletePostByText(postText);
        } catch (Exception e) {
            Assert.fail("Failed to delete post: " + e.getMessage());
        }

        // Verify the post is no longer displayed
        Assert.assertFalse(
                deleteBuzzPost.isPostDisplayed(postText),
                "Post is still displayed after deletion"
        );
    }
}