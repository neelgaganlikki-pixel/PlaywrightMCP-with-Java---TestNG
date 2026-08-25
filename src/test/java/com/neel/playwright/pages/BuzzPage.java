package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class BuzzPage {
    public String randomPost;
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
        this.postButton = page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Post").setExact(true));
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
        randomPost = "Automation Test Post - " + System.currentTimeMillis();
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

    public void deleteFirstPost() throws Exception {
        // Get the first post on the page
        Locator firstPost = page.locator("//div[contains(@class, 'oxd-sheet')]").first();
        
        // Scroll to make sure it's visible
        firstPost.scrollIntoViewIfNeeded();
        page.waitForTimeout(1000);
        
        // Hover over post to reveal action buttons
        firstPost.hover();
        page.waitForTimeout(1000);
        
        // Get all buttons in the post and click the action menu (last button usually)
        Locator buttons = firstPost.locator("button");
        int buttonCount = buttons.count();
        
        if (buttonCount > 0) {
            // Click the last button which is usually the action menu
            Locator menuButton = buttons.nth(buttonCount - 1);
            menuButton.click();
            page.waitForTimeout(1500);
        }
        
        // Click delete option - try different selectors
        page.waitForTimeout(300);
        Locator deleteBtn = page.locator("//button[contains(., 'Delete')] | //div[contains(., 'Delete')] | //li[contains(., 'Delete')]").first();
        try {
            deleteBtn.click();
            page.waitForTimeout(1500);
        } catch (Exception e) {
            page.waitForTimeout(500);
        }
        
        // Click confirmation button
        page.waitForTimeout(300);
        Locator confirmBtn = page.locator("//button[contains(., 'Yes')] | //button[contains(@class, 'danger')]").first();
        try {
            confirmBtn.click();
            page.waitForTimeout(2000);
        } catch (Exception e) {
            page.waitForTimeout(1000);
        }
    }
}