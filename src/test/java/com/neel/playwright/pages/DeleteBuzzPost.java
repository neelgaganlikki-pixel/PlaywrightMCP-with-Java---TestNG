package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class DeleteBuzzPost {

    private final Page page;

    // Locators
    private final Locator postContainer;
    private final Locator deleteOption;
    private final Locator confirmDeleteButton;

    public DeleteBuzzPost(Page page) {

        this.page = page;

        postContainer =
                page.locator(
                        "//div[contains(@class,'oxd-sheet') "
                                + "and contains(@class,'orangehrm-buzz')]"
                );

        deleteOption =
                page.getByText(
                        "Delete Post",
                        new Page.GetByTextOptions()
                                .setExact(true)
                );

        confirmDeleteButton =
                page.getByText(
                        "Yes, Delete",
                        new Page.GetByTextOptions()
                                .setExact(true)
                );
    }

    public void deletePostByText(String postText)
            throws Exception {

        Locator targetPost =
                postContainer
                        .filter(
                                new Locator.FilterOptions()
                                        .setHasText(postText)
                        )
                        .first();

        targetPost.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(15000)
        );

        targetPost.scrollIntoViewIfNeeded();

        targetPost.hover();

        Locator postActionMenu =
                targetPost.locator(
                        "button.oxd-icon-button:has(i.bi-three-dots)"
                ).first();

        if (!postActionMenu.isVisible()) {

            Locator buttons =
                    targetPost.locator("button");

            int buttonCount = buttons.count();

            if (buttonCount == 0) {

                throw new Exception(
                        "No action button found for post: "
                                + postText
                );
            }

            postActionMenu =
                    buttons.nth(buttonCount - 1);
        }

        postActionMenu.click();

        deleteOption.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(10000)
        );

        deleteOption.click();

        confirmDeleteButton.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(10000)
        );

        confirmDeleteButton.click();

        try {

            targetPost.waitFor(
                    new Locator.WaitForOptions()
                            .setState(
                                    WaitForSelectorState.DETACHED
                            )
                            .setTimeout(15000)
            );

        } catch (Exception ignored) {
            // Allow the application time to update the feed.
        }
    }

    public boolean isPostDisplayed(String postText) {

        try {

            Locator post =
                    postContainer
                            .filter(
                                    new Locator.FilterOptions()
                                            .setHasText(postText)
                            )
                            .first();

            return post.isVisible();

        } catch (Exception e) {

            return false;
        }
    }
}