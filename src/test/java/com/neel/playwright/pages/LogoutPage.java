package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LogoutPage {

    private final Page page;

    // Locators
    private final Locator userProfileDropdown;
    private final Locator logoutOption;

    public LogoutPage(Page page) {
        this.page = page;
        this.userProfileDropdown = page.locator("//img[@class='oxd-userdropdown-img']");
        this.logoutOption = page.locator("//a[contains(text(), 'Logout')]");
    }

    public void clickUserProfileDropdown() {
        userProfileDropdown.click();
    }

    public void clickLogout() {
        logoutOption.click();
    }

    public boolean isLoginPageDisplayed() {
        return page.url().contains("/auth/login");
    }

    public boolean isDashboardPageDisplayed() {
        return page.url().contains("/dashboard/index");
    }
}
