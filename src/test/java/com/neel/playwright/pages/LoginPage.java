package com.neel.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {

private final Page page;

// Locators
private final Locator usernameInput;
private final Locator passwordInput;
private final Locator loginButton;

public LoginPage(Page page) {
this.page = page;
this.usernameInput = page.locator("input[name='username']");
this.passwordInput = page.locator("input[name='password']");
this.loginButton = page.locator("button[type='submit']");
}

public void navigateToLoginPage() {
page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
}

public void enterUsername(String username) {
usernameInput.fill(username);
}

public void enterPassword(String password) {
passwordInput.fill(password);
}

public void clickLogin() {

loginButton.waitFor(
    new Locator.WaitForOptions()
        .setTimeout(10000)
);
loginButton.click();

page.waitForURL(
"**/dashboard/index",
new Page.WaitForURLOptions()
    .setTimeout(10000)
);
}

public boolean isLoginPageDisplayed() {
return page.url().contains("/auth/login");
}

public boolean isDashboardPageDisplayed() {
return page.url().contains("/dashboard/index");
}
} 