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

/**
 * Thread-safe BaseTest supporting parallel execution using ThreadLocal.
 * Manages independent Playwright, Browser, Context, and Page lifecycles per thread.
 */
public class BaseTest {

    private static final ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> tlContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();

    // Preserved for direct field access compatibility across test classes
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    public Page getPage() {
        return tlPage.get();
    }

    public static Page getThreadLocalPage() {
        return tlPage.get();
    }

    public BrowserContext getContext() {
        return tlContext.get();
    }

    public Browser getBrowser() {
        return tlBrowser.get();
    }

    public Playwright getPlaywright() {
        return tlPlaywright.get();
    }

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
        Playwright pw = Playwright.create();
        tlPlaywright.set(pw);
        this.playwright = pw;

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        Browser br = pw.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(headless)
                        .setSlowMo(0)
        );
        tlBrowser.set(br);
        this.browser = br;

        BrowserContext ctx = br.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1280, 720)
                        .setDeviceScaleFactor(1)
                        .setRecordVideoSize(1920, 1080)
                        .setRecordVideoDir(Paths.get("test-results", "videos"))
        );
        tlContext.set(ctx);
        this.context = ctx;

        Page pg = ctx.newPage();
        pg.setDefaultTimeout(30000);
        pg.setDefaultNavigationTimeout(60000);
        tlPage.set(pg);
        this.page = pg;
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        Page pg = tlPage.get();
        BrowserContext ctx = tlContext.get();
        Browser br = tlBrowser.get();
        Playwright pw = tlPlaywright.get();

        Video video = (pg != null) ? pg.video() : null;
        Path originalVideoPath = null;
        if (video != null) {
            try {
                originalVideoPath = video.path();
            } catch (Exception ignored) {
            }
        }

        // Close page and context first to finalize video recording
        if (pg != null) {
            try {
                pg.close();
            } catch (Exception ignored) {
            }
        }

        if (ctx != null) {
            try {
                ctx.close();
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

        if (br != null) {
            try {
                br.close();
            } catch (Exception e) {
                System.out.println("Could not close browser: " + e.getMessage());
            }
        }

        if (pw != null) {
            try {
                pw.close();
            } catch (Exception e) {
                System.out.println("Could not close Playwright: " + e.getMessage());
            }
        }

        // Clear ThreadLocal variables to prevent memory leaks across threads
        tlPage.remove();
        tlContext.remove();
        tlBrowser.remove();
        tlPlaywright.remove();
    }
}
