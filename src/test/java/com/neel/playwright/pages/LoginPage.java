package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

public class LoginPage {

    private final Page page;

    // Locators
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    public LoginPage(Page page) {
        this.page = page;

        this.usernameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.loginButton = page.locator("button[type='submit']");
    }

    public void navigateToLoginPage() {

        page.navigate(
                LOGIN_URL,
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(60000)
        );

        usernameInput.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );
    }

    public void enterUsername(String username) {

        usernameInput.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        usernameInput.fill(username);
    }

    public void enterPassword(String password) {

        passwordInput.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        passwordInput.fill(password);
    }

    public void clickLogin() {

        loginButton.waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(30000)
        );

        loginButton.click();

        page.waitForURL(
                "**/dashboard/index**",
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