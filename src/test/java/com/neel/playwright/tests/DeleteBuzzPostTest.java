package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.BuzzPage;
import com.neel.playwright.pages.DeleteBuzzPost;
import com.neel.playwright.pages.LoginPage;

import com.microsoft.playwright.options.LoadState;

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
public class DeleteBuzzPostTest extends BaseTest {

    @Test(description = "Verify deleting specific post on OrangeHRM Buzz feed")
    @Story("Delete Buzz Post")
    @Severity(SeverityLevel.NORMAL)
    @Description("Creates a dedicated test post, locates its contextual action menu, triggers post deletion, and verifies feed removal.")
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

        // Allow feed state to settle after navigation
        page.waitForTimeout(3000);

        // Open post box
        buzzPage.clickWhatsOnYourMindTextBox();

        // Create post using BuzzPage standard generator
        buzzPage.enterRandomBuzzPost();
        String postText = buzzPage.getRandomPost();

        System.out.println("Creating Buzz post for deletion: " + postText);

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