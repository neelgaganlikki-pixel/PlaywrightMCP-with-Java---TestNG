package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("OrangeHRM Enterprise Portal")
@Feature("Authentication & Access")
public class LoginTest extends BaseTest {

    @Test(description = "Verify OrangeHRM login with valid admin credentials")
    @Story("Admin Login Flow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies valid authentication and dashboard redirection for OrangeHRM admin credentials.")
    public void verifyOrangeHRMLogin() {
        LoginPage loginPage = new LoginPage(page);

        // Navigate to login page
        loginPage.navigateToLoginPage();

        // Verify login page is displayed
        Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "Login page was not loaded"
        );

        // Enter credentials
        loginPage.enterUsername("Admin");
        loginPage.enterPassword("admin123");

        // Click login
        loginPage.clickLogin();

        // Verify successful login by checking dashboard page
        Assert.assertTrue(
                loginPage.isDashboardPageDisplayed(),
                "Login was not successful - dashboard page not displayed"
        );
    }
}

