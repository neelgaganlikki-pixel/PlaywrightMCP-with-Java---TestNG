package com.neel.playwright.pages;

import com.microsoft.playwright.Page;

import java.util.List;

public class LogoutPage extends BasePage {

    private static final String PRIMARY_USER_DROPDOWN = "//img[@class='oxd-userdropdown-img']";
    private static final List<String> FALLBACK_USER_DROPDOWN = List.of(
            ".oxd-userdropdown-tab",
            "p.oxd-userdropdown-name",
            "li.oxd-userdropdown",
            ".oxd-userdropdown"
    );

    private static final String PRIMARY_LOGOUT_OPTION = "//a[contains(text(), 'Logout')]";
    private static final List<String> FALLBACK_LOGOUT_OPTION = List.of(
            "a:has-text('Logout')",
            "a[href*='logout']",
            "li:has-text('Logout') a",
            "role=menuitem >> text=Logout"
    );

    public LogoutPage(Page page) {
        super(page);
    }

    public void clickUserProfileDropdown() {
        healClick("User Profile Dropdown", PRIMARY_USER_DROPDOWN, FALLBACK_USER_DROPDOWN, 20000);

        boolean visible = healIsVisible("Logout Menu Option", PRIMARY_LOGOUT_OPTION, FALLBACK_LOGOUT_OPTION, 3000);
        if (!visible) {
            healClick("User Profile Dropdown Fallback", ".oxd-userdropdown-tab", FALLBACK_USER_DROPDOWN, 10000);
            healWaitFor("Logout Menu Option", PRIMARY_LOGOUT_OPTION, FALLBACK_LOGOUT_OPTION, 10000);
        }
    }

    public void clickLogout() {
        healClick("Logout Option", PRIMARY_LOGOUT_OPTION, FALLBACK_LOGOUT_OPTION, 15000);
        page.waitForURL(
                "**/auth/login**",
                new Page.WaitForURLOptions().setTimeout(30000)
        );
    }

    public boolean isLoginPageDisplayed() {
        return page.url().contains("/auth/login");
    }

    public boolean isDashboardPageDisplayed() {
        return page.url().contains("/dashboard/index");
    }
}