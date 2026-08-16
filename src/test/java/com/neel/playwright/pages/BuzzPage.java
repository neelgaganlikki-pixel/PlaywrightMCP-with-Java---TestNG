package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class BuzzPage {

    private final Page page;

    // Locators
    private final Locator buzzTab;
    private final Locator whatsOnYourMindTextBox;
    private final Locator postButton;
    private final Locator successMessage;

    public BuzzPage(Page page) {
        this.page = page;
        this.buzzTab = page.locator("//a[contains(@href, '/buzz/')]");
        this.whatsOnYourMindTextBox = page.getByPlaceholder("What's on your mind?");
        this.postButton = page.locator("button.oxd-button--main");
        this.successMessage = page.locator(".oxd-toast");
    }

    public void navigateToBuzzPage() {
        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/buzz/viewBuzz");
    }

    public void clickBuzzTab() {
        buzzTab.click();
    }

    public void clickWhatsOnYourMindTextBox() {
        whatsOnYourMindTextBox.click();
    }

    public void enterBuzzPost(String text) {
        whatsOnYourMindTextBox.fill(text);
    }

    public void enterRandomBuzzPost() {
        String randomPost = "Automation Test Post - " + System.currentTimeMillis();
        whatsOnYourMindTextBox.fill(randomPost);
    }

    public void clickPostButton() {
    postButton.click();
}

public boolean isSuccessMessageDisplayed() {
    successMessage.waitFor(
            new Locator.WaitForOptions().setTimeout(5000)
    );

    return successMessage.isVisible();
}

public boolean isBuzzPageDisplayed() {
    return page.url().contains("/buzz/");
}
}