package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DeleteBuzzPost {

    private final Page page;

    // Locators
    private final Locator postContainer;
    private final Locator actionMenuButton;
    private final Locator deleteOption;
    private final Locator confirmDeleteButton;

    public DeleteBuzzPost(Page page) {
        this.page = page;
        // Post container locator - will be parameterized with post text
        this.postContainer = page.locator(
                "//div[contains(@class,'oxd-sheet') and contains(@class,'orangehrm-buzz')]");
        // Action menu button (three dots)
        this.actionMenuButton = page.locator(
                "button[aria-label='More actions'], button:has(i.oxd-icon.bi-three-dots-vertical), button:has(i.bi-three-dots-vertical)");
        // Delete option in dropdown
        this.deleteOption = page.getByText("Delete Post", new Page.GetByTextOptions().setExact(true));
        // Confirm delete button
        this.confirmDeleteButton = page.getByText("Yes, Delete", new Page.GetByTextOptions().setExact(true));
    }

    /**
     * Deletes a specific post by its text content.
     * Finds the post, opens its action menu (three dots), clicks Delete, and
     * confirms.
     */
    public void deletePostByText(String postText) throws Exception {
        // Find the post container that contains the specific text
        Locator targetPost = postContainer
                .filter(new Locator.FilterOptions().setHasText(postText)).first();

        // Wait for the post to be visible
        targetPost.waitFor(new Locator.WaitForOptions().setTimeout(5000));

        // Scroll to make sure it's visible
        targetPost.scrollIntoViewIfNeeded();
        page.waitForTimeout(1000);

        // Hover over post to reveal action buttons
        targetPost.hover();

        page.waitForTimeout(1000);

        // Find the 3-dot action button ONLY inside the specific post
        Locator postActionMenu = targetPost.locator("button.oxd-icon-button:has(i.bi-three-dots)").first();

        // If 3-dot button is not found, use the last button inside the target post
        if (!postActionMenu.isVisible()) {

            Locator buttons = targetPost.locator("button");

            int buttonCount = buttons.count();

            if (buttonCount > 0) {
                postActionMenu = buttons.nth(buttonCount - 1);
            } else {
                throw new Exception(
                        "No action button found for post: " + postText);
            }
        }

        // Click the 3-dot menu
        postActionMenu.click();
        page.waitForTimeout(1000);

        // Click Delete from the dropdown
        // deleteOption.waitFor(new Locator.WaitForOptions().setTimeout(5000));

        deleteOption.click();

        page.waitForTimeout(1000);

        // Click "Yes, Delete" in the confirmation popup
        confirmDeleteButton.waitFor(new Locator.WaitForOptions().setTimeout(2000));

        confirmDeleteButton.click();

        page.waitForTimeout(1000);
    }

    /**
     * Verifies if a post with the given text is displayed on the page.
     */
    public boolean isPostDisplayed(String postText) {
        try {
            page.waitForTimeout(500);

            // Try multiple ways to find the post
            Locator post = null;

            // First try: direct text match
            try {
                post = page.locator("//*[contains(text(), '" + postText + "')]").first();
                post.waitFor(new Locator.WaitForOptions().setTimeout(1000));
                if (post.isVisible()) {
                    return true;
                }
            } catch (Exception e) {
                // Continue to next attempt
            }

            // Second try: partial text match in div
            try {
                post = page.locator("//div[contains(., '" + postText + "')]").first();
                post.waitFor(new Locator.WaitForOptions().setTimeout(1000));
                if (post.isVisible()) {
                    return true;
                }
            } catch (Exception e) {
                // Post not found
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}