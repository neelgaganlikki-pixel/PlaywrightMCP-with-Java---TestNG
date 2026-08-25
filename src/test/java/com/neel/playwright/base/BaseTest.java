package com.neel.playwright.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.ITestResult;

public class BaseTest {

protected Playwright playwright;
protected Browser browser;
protected BrowserContext context;
protected Page page;

@BeforeMethod
public void setUp() {
    playwright = Playwright.create();

    browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(1000)
    );

    context = browser.newContext(
        new Browser.NewContextOptions()
    .setViewportSize(1280, 720)
    .setDeviceScaleFactor(1)
    .setRecordVideoSize(1920, 1080)
    .setRecordVideoDir(Paths.get("test-results/videos"))
    );
    page = context.newPage();
}

@AfterMethod
public void tearDown(ITestResult result) {

    Path videoPath = null;

    if (page != null && page.video() != null) {
        videoPath = page.video().path();
    }

    // Close context to finalize the video
    if (context != null) {
        context.close();
    }

    // Delete video if test passed
    if (result.getStatus() == ITestResult.SUCCESS && videoPath != null) {
        try {
            Files.deleteIfExists(videoPath);
            System.out.println("Test passed - video deleted: " + videoPath);
        } catch (Exception e) {
            System.out.println("Could not delete video: " + e.getMessage());
        }
    }

    if (browser != null) {
        browser.close();
    }

    if (playwright != null) {
        playwright.close();
    }
}
}