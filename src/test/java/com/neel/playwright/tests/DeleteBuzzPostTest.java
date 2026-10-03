package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.DeleteBuzzPost;
import com.neel.playwright.pages.LoginPage;

import com.microsoft.playwright.options.LoadState;

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

        System.out.println("Creating Buzz post: " + postText);

        buzzPage.enterBuzzPost(postText);

        // Click Post
        buzzPage.clickPostButton();

        // Verify post creation success message
        Assert.assertTrue(
                buzzPage.isSuccessMessageDisplayed(),
                "Success message was not displayed after posting"
        );

        System.out.println("Buzz post created successfully");
        System.out.println("Checking if newly created post is visible: " + postText);

        boolean postDisplayed = false;

        // Verify post presence in feed; reload periodically if read replica has not synced yet
        for (int attempt = 1; attempt <= 20; attempt++) {
            if (deleteBuzzPost.isPostDisplayed(postText)) {
                postDisplayed = true;
                System.out.println("Created post is visible: " + postText);
                break;
            }
            if (attempt % 4 == 0) {
                System.out.println("Post not yet visible, refreshing Buzz feed (attempt " + attempt + "/20)...");
                page.reload();
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } else {
                page.waitForTimeout(1000);
            }
        }

        Assert.assertTrue(
                postDisplayed,
                "Created post is not displayed before deletion: "
                        + postText
        );

        // Delete specific post
        System.out.println(
                "Starting deletion of post: "
                        + postText
        );

        try {

            deleteBuzzPost.deletePostByText(postText);

            System.out.println(
                    "Delete action completed for post: "
                            + postText
            );

        } catch (Exception e) {

            Assert.fail(
                    "Failed to delete post: "
                            + postText
                            + ". Reason: "
                            + e.getMessage()
            );
        }

        // Verify post is no longer displayed
        System.out.println(
                "Verifying post has been deleted..."
        );

        boolean postStillDisplayed = true;

        for (int attempt = 1; attempt <= 20; attempt++) {

            System.out.println(
                    "Checking deletion - attempt "
                            + attempt
                            + "/20"
            );

            if (!deleteBuzzPost.isPostDisplayed(postText)) {

                postStillDisplayed = false;

                System.out.println(
                        "Post is no longer displayed"
                );

                break;
            }

            page.waitForTimeout(1000);
        }

        Assert.assertFalse(
                postStillDisplayed,
                "Post is still displayed after deletion: "
                        + postText
        );

        System.out.println(
                "================================"
        );

        System.out.println(
                "Buzz post deletion verified successfully"
        );

        System.out.println(
                "Deleted post: " + postText
        );

        System.out.println(
                "================================"
        );
    }
}