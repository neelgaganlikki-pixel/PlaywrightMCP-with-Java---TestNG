package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.DeleteBuzzPost;
import com.neel.playwright.pages.LoginPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteBuzzPostTest extends BaseTest {

    @Test
    public void verifyDeleteBuzzPost() {

        LoginPage loginPage =
                new LoginPage(page);

        BuzzPage buzzPage =
                new BuzzPage(page);

        DeleteBuzzPost deleteBuzzPost =
                new DeleteBuzzPost(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Verify login page
        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Login page was not loaded"
        );

        // Login
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");
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

        // Create unique post
        String postText =
                "Test Post for Deletion - "
                        + System.currentTimeMillis();

        buzzPage.enterBuzzPost(postText);

        // Click Post
        buzzPage.clickPostButton();

        // Verify post creation
        Assert.assertTrue(
                buzzPage.isSuccessMessageDisplayed(),
                "Success message was not displayed after posting"
        );

        // Wait for newly created post to appear
        boolean postDisplayed = false;

        for (int attempt = 0; attempt < 10; attempt++) {

            if (deleteBuzzPost.isPostDisplayed(postText)) {

                postDisplayed = true;
                break;
            }

            page.waitForTimeout(1000);
        }

        Assert.assertTrue(
                postDisplayed,
                "Created post is not displayed before deletion: "
                        + postText
        );

        // Delete specific post
        try {

            deleteBuzzPost.deletePostByText(postText);

        } catch (Exception e) {

            Assert.fail(
                    "Failed to delete post: "
                            + postText
                            + ". Reason: "
                            + e.getMessage()
            );
        }

        // Verify post is no longer displayed
        boolean postStillDisplayed = true;

        for (int attempt = 0; attempt < 10; attempt++) {

            if (!deleteBuzzPost.isPostDisplayed(postText)) {

                postStillDisplayed = false;
                break;
            }

            page.waitForTimeout(1000);
        }

        Assert.assertFalse(
                postStillDisplayed,
                "Post is still displayed after deletion: "
                        + postText
        );
    }
}