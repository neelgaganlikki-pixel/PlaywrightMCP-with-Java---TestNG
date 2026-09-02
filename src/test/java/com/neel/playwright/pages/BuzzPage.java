package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;

public class BuzzPage {

    public String randomPost;

    private final Page page;

    // Locators
    private final Locator buzzTab;
    private final Locator whatsOnYourMindTextBox;
    private final Locator postButton;
    private final Locator successMessage;

    private static final String BUZZ_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/buzz/viewBuzz";

    public BuzzPage(Page page) {

        this.page = page;

        this.buzzTab =
                page.locator("//a[contains(@href, '/buzz/')]");

        this.whatsOnYourMindTextBox =
                page.getByPlaceholder("What's on your mind?");

        this.postButton =
                page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions()
                                .setName("Post")
                                .setExact(true)
                );

        this.successMessage =
                page.locator(".oxd-toast");
    }

    public void navigateToBuzzPage() {

        page.navigate(
                BUZZ_URL,
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(60000)
        );

        whatsOnYourMindTextBox.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );
    }

    public void clickBuzzTab() {

        buzzTab.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        buzzTab.click();
    }

    public void clickWhatsOnYourMindTextBox() {

        whatsOnYourMindTextBox.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        whatsOnYourMindTextBox.click();
    }

    public void enterBuzzPost(String text) {

        whatsOnYourMindTextBox.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        whatsOnYourMindTextBox.fill(text);
    }

    public void enterRandomBuzzPost() {

        randomPost =
                "Automation Test Post - "
                        + System.currentTimeMillis();

        enterBuzzPost(randomPost);
    }

    public void clickPostButton() {

        postButton.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        postButton.click();
    }

    public boolean isSuccessMessageDisplayed() {

        try {

            successMessage.waitFor(
                    new Locator.WaitForOptions()
                            .setTimeout(10000)
            );

            return successMessage.isVisible();

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isBuzzPageDisplayed() {

        return page.url().contains("/buzz/");
    }

    public void deleteFirstPost() throws Exception {

        Locator firstPost =
                page.locator(
                        "//div[contains(@class, 'oxd-sheet')]"
                ).first();

        firstPost.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        firstPost.scrollIntoViewIfNeeded();

        firstPost.hover();

        Locator buttons =
                firstPost.locator("button");

        int buttonCount = buttons.count();

        if (buttonCount == 0) {

            throw new Exception(
                    "No action button found on the first Buzz post."
            );
        }

        Locator menuButton =
                buttons.nth(buttonCount - 1);

        menuButton.click();

        Locator deleteButton =
                page.locator(
                        "//button[contains(., 'Delete')]"
                                + " | //div[contains(., 'Delete')]"
                                + " | //li[contains(., 'Delete')]"
                ).first();

        deleteButton.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(10000)
        );

        deleteButton.click();

        Locator confirmButton =
                page.locator(
                        "//button[contains(., 'Yes')]"
                                + " | //button[contains(@class, 'danger')]"
                ).first();

        confirmButton.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(10000)
        );

        confirmButton.click();
    }
}