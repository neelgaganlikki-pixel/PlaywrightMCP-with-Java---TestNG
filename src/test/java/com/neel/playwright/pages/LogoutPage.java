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

        this.userProfileDropdown =
                page.locator("//img[@class='oxd-userdropdown-img']");

        this.logoutOption =
                page.locator("//a[contains(text(), 'Logout')]");
    }

    public void clickUserProfileDropdown() {

        userProfileDropdown.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        userProfileDropdown.click();

        logoutOption.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(10000)
        );
    }

    public void clickLogout() {

        logoutOption.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(10000)
        );

        logoutOption.click();

        page.waitForURL(
                "**/auth/login**",
                new Page.WaitForURLOptions()
                        .setTimeout(30000)
        );
    }

    public boolean isLoginPageDisplayed() {

        return page.url().contains("/auth/login");
    }

    public boolean isDashboardPageDisplayed() {

        return page.url().contains("/dashboard/index");
    }
}