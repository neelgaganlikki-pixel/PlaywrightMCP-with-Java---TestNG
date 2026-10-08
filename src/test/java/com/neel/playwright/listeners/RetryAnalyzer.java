package com.neel.playwright.listeners;

import io.qameta.allure.Allure;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Enterprise RetryAnalyzer for transient failure recovery.
 * Automatically retries failed tests up to maxRetryCount times.
 * Default maxRetryCount is 1, configurable via -DretryCount=X.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT;

    static {
        int count = 1; // Default: 1 retry attempt
        String prop = System.getProperty("retryCount");
        if (prop != null) {
            try {
                count = Integer.parseInt(prop.trim());
            } catch (NumberFormatException e) {
                System.err.println("[RETRY] Invalid -DretryCount value: " + prop + ", defaulting to 1");
            }
        }
        MAX_RETRY_COUNT = count;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getRealClass().getSimpleName();

            System.out.println("============================================================");
            System.out.println("[RETRY TRIGGERED] Test failed: " + className + "." + testName);
            System.out.println("[RETRY ATTEMPT] Attempt " + retryCount + " of " + MAX_RETRY_COUNT);
            System.out.println("[RETRY CAUSE] " + (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error"));
            System.out.println("============================================================");

            try {
                Allure.addAttachment(
                        "Retry Log - Attempt " + retryCount,
                        "text/plain",
                        "Test failed and is being automatically retried.\n"
                                + "Test: " + className + "." + testName + "\n"
                                + "Attempt: " + retryCount + " of " + MAX_RETRY_COUNT + "\n"
                                + "Failure Reason: " + (result.getThrowable() != null ? result.getThrowable().toString() : "N/A")
                );
            } catch (Exception ignored) {
            }

            return true;
        }
        return false;
    }

    public static int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
}
