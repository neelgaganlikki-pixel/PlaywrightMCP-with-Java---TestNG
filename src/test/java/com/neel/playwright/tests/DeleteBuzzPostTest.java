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
        System.out.println("Waiting for Buzz feed to update...");

        // Give OrangeHRM time to process the post
        page.waitForTimeout(2000);

        // Refresh the Buzz feed
        System.out.println("Refreshing Buzz feed...");

        page.reload();

        page.waitForLoadState(
                LoadState.DOMCONTENTLOADED
        );

        System.out.println("Buzz page reloaded");

        // Wait for Buzz page to become visible again
        Assert.assertTrue(
                buzzPage.isBuzzPageDisplayed(),
                "Buzz page was not displayed after reload"
        );

        // Wait for the newly created post
        System.out.println(
                "Waiting for newly created post to appear: "
                        + postText
        );

        boolean postDisplayed = false;

        for (int attempt = 1; attempt <= 30; attempt++) {

            System.out.println(
                    "Checking for created post - attempt "
                            + attempt
                            + "/30"
            );

            if (deleteBuzzPost.isPostDisplayed(postText)) {

                postDisplayed = true;

                System.out.println(
                        "Created post is now visible: "
                                + postText
                );

                break;
            }

            page.waitForTimeout(1000);
        }

        Assert.assertTrue(
                postDisplayed,
                "Created post is not displayed after feed refresh: "
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