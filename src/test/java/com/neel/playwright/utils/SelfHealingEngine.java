package com.neel.playwright.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Enterprise Self-Healing Automation Engine for Playwright Java.
 * Dynamically resolves broken locators through sequential candidate evaluation,
 * in-memory caching, and JSON persistence.
 */
public class SelfHealingEngine {

    private static volatile SelfHealingEngine instance;
    private static final ReentrantLock lock = new ReentrantLock();

    private final boolean enabled;
    private final int fallbackTimeoutMs;
    private final boolean saveHealedLocators;
    private final Map<String, String> healedCache = new ConcurrentHashMap<>();
    private final List<HealingRecord> healingRecords = Collections.synchronizedList(new ArrayList<>());
    private final Path cacheFilePath = Paths.get("test-results", "healed_locators.json");
    private final Path summaryFilePath = Paths.get("test-results", "self_healing_summary.json");

    public record HealingRecord(
            String timestamp,
            String pageName,
            String elementName,
            String actionType,
            String originalSelector,
            String healedSelector,
            int attemptNumber,
            String status,
            String errorMessage
    ) {}

    private SelfHealingEngine() {
        this.enabled = Boolean.parseBoolean(System.getProperty("self.healing.enabled", "true"));
        this.fallbackTimeoutMs = Integer.parseInt(System.getProperty("self.healing.fallback.timeout", "3000"));
        this.saveHealedLocators = Boolean.parseBoolean(System.getProperty("self.healing.save", "true"));
        loadPersistedCache();
    }

    public static SelfHealingEngine getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new SelfHealingEngine();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private String cacheKey(String pageName, String elementName) {
        return (pageName + ":" + elementName).trim();
    }

    private void loadPersistedCache() {
        if (!Files.exists(cacheFilePath)) {
            return;
        }
        try {
            String content = Files.readString(cacheFilePath, StandardCharsets.UTF_8).trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                String body = content.substring(1, content.length() - 1);
                for (String line : body.split(",")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim().replace("\"", "");
                        String val = parts[1].trim().replace("\"", "");
                        if (!key.isEmpty() && !val.isEmpty()) {
                            healedCache.put(key, val);
                        }
                    }
                }
                System.out.println("[SELF-HEALING] Loaded " + healedCache.size() + " healed locators from cache.");
            }
        } catch (Exception e) {
            System.out.println("[SELF-HEALING] Notice loading cache: " + e.getMessage());
        }
    }

    private void persistCache() {
        if (!saveHealedLocators) {
            return;
        }
        lock.lock();
        try {
            Files.createDirectories(cacheFilePath.getParent());
            StringBuilder sb = new StringBuilder("{\n");
            int idx = 0;
            for (Map.Entry<String, String> entry : healedCache.entrySet()) {
                sb.append("  \"").append(escapeJson(entry.getKey())).append("\": \"")
                  .append(escapeJson(entry.getValue())).append("\"");
                if (++idx < healedCache.size()) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("}\n");
            Files.writeString(cacheFilePath, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[SELF-HEALING] Could not persist healed locators: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public Locator resolveLocator(
            Page page,
            String elementName,
            String pageName,
            String primary,
            List<String> fallbacks,
            String actionType,
            int timeoutMs
    ) {
        String key = cacheKey(pageName, elementName);
        List<String> candidates = new ArrayList<>();

        if (healedCache.containsKey(key)) {
            candidates.add(healedCache.get(key));
        }
        if (primary != null && !primary.isBlank() && !candidates.contains(primary)) {
            candidates.add(primary);
        }
        if (fallbacks != null) {
            for (String fb : fallbacks) {
                if (fb != null && !fb.isBlank() && !candidates.contains(fb)) {
                    candidates.add(fb);
                }
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No locator candidates provided for " + elementName);
        }

        if (!enabled) {
            return page.locator(candidates.getFirst()).first();
        }

        System.out.println("[SELF-HEALING] Finding element: '" + elementName + "' on " + pageName + " (Action: " + actionType + ")");

        Exception lastException = null;
        for (int i = 0; i < candidates.size(); i++) {
            String candidate = candidates.get(i);
            int attempt = i + 1;
            int waitTime = (i == 0 && candidate.equals(primary)) ? timeoutMs : fallbackTimeoutMs;

            try {
                Locator loc = page.locator(candidate).first();

                // For query operations (is_visible), wait up to waitTime for element to become visible
                if ("is_visible".equalsIgnoreCase(actionType)) {
                    try {
                        loc.waitFor(new Locator.WaitForOptions()
                                .setState(WaitForSelectorState.VISIBLE)
                                .setTimeout(waitTime));
                        if (!candidate.equals(primary)) {
                            recordSuccess(pageName, elementName, actionType, primary, candidate, attempt);
                        }
                        return loc;
                    } catch (Exception notVisible) {
                        continue;
                    }
                }

                // For mutating actions, enforce visibility
                loc.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(waitTime));

                if (!candidate.equals(primary)) {
                    recordSuccess(pageName, elementName, actionType, primary, candidate, attempt);
                }
                return loc;

            } catch (Exception ex) {
                lastException = ex;
                if (attempt == 1) {
                    System.out.println("[SELF-HEALING] Primary locator failed for '" + elementName + "': " + candidate);
                } else {
                    System.out.println("[SELF-HEALING] Attempt " + attempt + " failed for candidate: " + candidate);
                }
            }
        }

        recordFailure(pageName, elementName, actionType, primary, candidates.size(), lastException);
        throw new PlaywrightException("[SELF-HEALING FAILED] All " + candidates.size() +
                " candidates failed for element '" + elementName + "' on " + pageName +
                ". Reason: " + (lastException != null ? lastException.getMessage() : "Not visible"));
    }

    private void recordSuccess(String pageName, String elementName, String actionType,
                               String primary, String healed, int attempt) {
        String key = cacheKey(pageName, elementName);
        healedCache.put(key, healed);
        persistCache();

        HealingRecord record = new HealingRecord(
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                pageName,
                elementName,
                actionType,
                primary,
                healed,
                attempt,
                "HEALED",
                null
        );
        healingRecords.add(record);

        System.out.println("============================================================");
        System.out.println("[SELF-HEALING SUCCESS] '" + elementName + "' healed using fallback: " + healed);
        System.out.println("============================================================");
    }

    private void recordFailure(String pageName, String elementName, String actionType,
                               String primary, int attempts, Exception ex) {
        HealingRecord record = new HealingRecord(
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                pageName,
                elementName,
                actionType,
                primary,
                null,
                attempts,
                "FAILED",
                ex != null ? ex.getMessage() : "Unknown error"
        );
        healingRecords.add(record);
    }

    public void healClick(Page page, String elementName, String pageName,
                          String primary, List<String> fallbacks, int timeoutMs) {
        Locator loc = resolveLocator(page, elementName, pageName, primary, fallbacks, "click", timeoutMs);
        loc.click();
    }

    public void healFill(Page page, String elementName, String pageName,
                         String primary, List<String> fallbacks, String text, int timeoutMs) {
        Locator loc = resolveLocator(page, elementName, pageName, primary, fallbacks, "fill", timeoutMs);
        loc.fill(text);
    }

    public String healGetText(Page page, String elementName, String pageName,
                              String primary, List<String> fallbacks, int timeoutMs) {
        Locator loc = resolveLocator(page, elementName, pageName, primary, fallbacks, "get_text", timeoutMs);
        return loc.innerText().trim();
    }

    public boolean healIsVisible(Page page, String elementName, String pageName,
                                 String primary, List<String> fallbacks, int timeoutMs) {
        try {
            Locator loc = resolveLocator(page, elementName, pageName, primary, fallbacks, "is_visible", timeoutMs);
            return loc.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void healWaitFor(Page page, String elementName, String pageName,
                            String primary, List<String> fallbacks, int timeoutMs) {
        resolveLocator(page, elementName, pageName, primary, fallbacks, "wait_for", timeoutMs);
    }

    public int getHealingCount() {
        return (int) healingRecords.stream().filter(r -> "HEALED".equals(r.status())).count();
    }

    public void printSummary() {
        System.out.println("\n============================================================");
        System.out.println("SELF-HEALING AUTOMATION SUMMARY (Java Engine)");
        System.out.println("============================================================");
        long healed = healingRecords.stream().filter(r -> "HEALED".equals(r.status())).count();
        long failed = healingRecords.stream().filter(r -> "FAILED".equals(r.status())).count();
        System.out.println("Total Healing Events:   " + healingRecords.size());
        System.out.println("Successful Recoveries:  " + healed);
        System.out.println("Failed Recoveries:      " + failed);
        System.out.println("------------------------------------------------------------");
        if (healed == 0) {
            System.out.println("No elements required healing (all primary locators passed).");
        } else {
            System.out.println("Healed Elements Detail:");
            for (HealingRecord r : healingRecords) {
                if ("HEALED".equals(r.status())) {
                    System.out.println("  • [" + r.pageName() + "] " + r.elementName() + " (" + r.actionType() + ")");
                    System.out.println("      Original: " + r.originalSelector());
                    System.out.println("      Healed:   " + r.healedSelector());
                }
            }
        }
        System.out.println("============================================================\n");
    }
}
