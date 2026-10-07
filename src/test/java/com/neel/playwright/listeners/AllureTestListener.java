package com.neel.playwright.listeners;

import com.microsoft.playwright.Page;
import com.neel.playwright.base.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AllureTestListener for automated test reporting and artifact capture.
 * Automatically attaches full-page screenshots on test failures
 * and attaches self-healed locator artifacts to the Allure report.
 */
public class AllureTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[ALLURE] Starting Test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[ALLURE] Test PASSED: " + result.getMethod().getMethodName());
        attachHealedLocatorsIfPresent();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println("[ALLURE] Test FAILED: " + result.getMethod().getMethodName());
        captureAndAttachScreenshot(result);
        attachHealedLocatorsIfPresent();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[ALLURE] Test SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("[ALLURE] Test Suite Finished. Total Tests: " + context.getAllTestMethods().length);
        attachHealedLocatorsIfPresent();
    }

    /**
     * Captures a full-page screenshot from the active thread's Playwright Page
     * and attaches it directly to the Allure test result.
     */
    private void captureAndAttachScreenshot(ITestResult result) {
        try {
            Page page = BaseTest.getThreadLocalPage();
            if (page != null && !page.isClosed()) {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment(
                        "Failure Screenshot - " + result.getMethod().getMethodName(),
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );
                System.out.println("[ALLURE] Attached full-page failure screenshot to Allure report.");
            }
        } catch (Exception e) {
            System.err.println("[ALLURE] Failed to capture failure screenshot: " + e.getMessage());
        }
    }

    /**
     * Attaches the dynamic healed_locators.json file to the Allure report if present.
     */
    private void attachHealedLocatorsIfPresent() {
        try {
            Path path = Paths.get("test-results", "healed_locators.json");
            if (Files.exists(path)) {
                String content = Files.readString(path);
                if (content != null && !content.trim().isEmpty() && !content.equals("{}")) {
                    Allure.addAttachment("Dynamically Healed Locators", "application/json", content, ".json");
                }
            }
        } catch (Exception ignored) {
        }
    }
}

