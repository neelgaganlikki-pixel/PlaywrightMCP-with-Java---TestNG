package com.neel.playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

import java.util.List;

public class LoginPage extends BasePage {

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private static final String PRIMARY_USERNAME = "input[name='username']";
    private static final List<String> FALLBACK_USERNAME = List.of(
            "input[placeholder='Username']",
            "//input[@name='username']",
            ".oxd-form-row:first-child input"
    );

    private static final String PRIMARY_PASSWORD = "input[name='password']";
    private static final List<String> FALLBACK_PASSWORD = List.of(
            "input[placeholder='Password']",
            "//input[@name='password']",
            "input[type='password']"
    );

    private static final String PRIMARY_LOGIN_BTN = "button[type='submit']";
    private static final List<String> FALLBACK_LOGIN_BTN = List.of(
            "button:has-text('Login')",
            ".orangehrm-login-button",
            "//button[@type='submit']"
    );

    public LoginPage(Page page) {
        super(page);
    }

    public void navigateToLoginPage() {
        page.navigate(
                LOGIN_URL,
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(60000)
        );
        healWaitFor("Username Input", PRIMARY_USERNAME, FALLBACK_USERNAME, 30000);
    }

    public void enterUsername(String username) {
        healFill("Username Field", PRIMARY_USERNAME, FALLBACK_USERNAME, username, 30000);
    }

    public void enterPassword(String password) {
        healFill("Password Field", PRIMARY_PASSWORD, FALLBACK_PASSWORD, password, 30000);
    }

    public void clickLogin() {
        healClick("Login Submit Button", PRIMARY_LOGIN_BTN, FALLBACK_LOGIN_BTN, 30000);
        page.waitForURL(
                "**/dashboard/index**",
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