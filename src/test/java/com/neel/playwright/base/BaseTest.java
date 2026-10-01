package com.neel.playwright.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Video;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeSuite(alwaysRun = true)
    public void cleanVideosBeforeSuite() {
        Path videoDir = Paths.get("test-results", "videos");
        if (Files.exists(videoDir)) {
            try (Stream<Path> stream = Files.list(videoDir)) {
                stream.filter(Files::isRegularFile)
                      .forEach(file -> {
                          try {
                              Files.deleteIfExists(file);
                          } catch (Exception ignored) {
                          }
                      });
                System.out.println("Cleared previous video files before starting test suite.");
            } catch (Exception e) {
                System.out.println("Could not clear video directory: " + e.getMessage());
            }
        }
    }

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
                                "true"
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

        Video video = (page != null) ? page.video() : null;
        Path originalVideoPath = null;
        if (video != null) {
            try {
                originalVideoPath = video.path();
            } catch (Exception ignored) {
            }
        }

        // Close page and context first to finalize video recording
        if (page != null) {
            try {
                page.close();
            } catch (Exception ignored) {
            }
        }

        if (context != null) {
            try {
                context.close();
            } catch (Exception e) {
                System.out.println("Could not close context: " + e.getMessage());
            }
        }

        // Handle video: keep only on failure, autodelete on success or skip
        if (result.getStatus() == ITestResult.FAILURE) {
            if (video != null) {
                try {
                    String className = (result.getTestClass() != null)
                            ? result.getTestClass().getRealClass().getSimpleName()
                            : "Test";
                    String methodName = (result.getMethod() != null)
                            ? result.getMethod().getMethodName()
                            : "method";
                    Path failedVideo = Paths.get("test-results", "videos", "FAILED_" + className + "_" + methodName + ".webm");
                    video.saveAs(failedVideo);
                    video.delete(); // Delete original random-hash video file
                    System.out.println("Test FAILED - video saved to: " + failedVideo);
                } catch (Exception e) {
                    System.out.println("Could not save failed test video: " + e.getMessage());
                }
            }
        } else {
            // Test PASSED or SKIPPED - delete video immediately
            if (video != null) {
                try {
                    video.delete();
                    System.out.println("Test passed - video deleted successfully.");
                } catch (Exception e) {
                    if (originalVideoPath != null) {
                        try {
                            Files.deleteIfExists(originalVideoPath);
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else if (originalVideoPath != null) {
                try {
                    Files.deleteIfExists(originalVideoPath);
                } catch (Exception ignored) {
                }
            }
        }

        if (browser != null) {
            try {
                browser.close();
            } catch (Exception e) {
                System.out.println("Could not close browser: " + e.getMessage());
            }
        }

        if (playwright != null) {
            try {
                playwright.close();
            } catch (Exception e) {
                System.out.println("Could not close Playwright: " + e.getMessage());
            }
        }
    }
}