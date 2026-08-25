package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.DeleteBuzzPost;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteBuzzPostTest extends BaseTest {

    @Test
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