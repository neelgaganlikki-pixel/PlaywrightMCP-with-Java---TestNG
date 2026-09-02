package com.neel.playwright.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeMethod
    public void setUp() {

        playwright = Playwright.create();

        /*
         * Default: headed mode.
         *
         * Local headed:
         * mvn clean test -Dsurefire.suiteXmlFiles=testng.xml -Dheadless=false
         *
         * Jenkins:
         * mvn clean test -Dsurefire.suiteXmlFiles=testng.xml -Dheadless=true
         */

        boolean headless =
                Boolean.parseBoolean(
                        System.getProperty(
                                "headless",
                                "false"
                        )
                );

        browser =
                playwright.chromium().launch(
                        new BrowserType.LaunchOptions()
                                .setHeadless(headless)
                                .setSlowMo(0)
                );

        context =
                browser.newContext(
                        new Browser.NewContextOptions()
                                .setViewportSize(1280, 720)
                                .setDeviceScaleFactor(1)
                                .setRecordVideoSize(1920, 1080)
                                .setRecordVideoDir(
                                        Paths.get(
                                                "test-results/videos"
                                        )
                                )
                );

        page = context.newPage();

        // Default locator/action timeout
        page.setDefaultTimeout(30000);

        // Default navigation timeout
        page.setDefaultNavigationTimeout(60000);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {

        Path videoPath = null;

        if (page != null && page.video() != null) {

            try {

                videoPath = page.video().path();

            } catch (Exception e) {

                System.out.println(
                        "Could not get video path: "
                                + e.getMessage()
                );
            }
        }

        // Close context to finalize video
        if (context != null) {

            try {

                context.close();

            } catch (Exception e) {

                System.out.println(
                        "Could not close context: "
                                + e.getMessage()
                );
            }
        }

        // Delete video for passed tests
        if (result.getStatus() == ITestResult.SUCCESS
                && videoPath != null) {

            try {

                Files.deleteIfExists(videoPath);

                System.out.println(
                        "Test passed - video deleted: "
                                + videoPath
                );

            } catch (Exception e) {

                System.out.println(
                        "Could not delete video: "
                                + e.getMessage()
                );
            }
        }

        if (browser != null) {

            try {

                browser.close();

            } catch (Exception e) {

                System.out.println(
                        "Could not close browser: "
                                + e.getMessage()
                );
            }
        }

        if (playwright != null) {

            try {

                playwright.close();

            } catch (Exception e) {

                System.out.println(
                        "Could not close Playwright: "
                                + e.getMessage()
                );
            }
        }
    }
}