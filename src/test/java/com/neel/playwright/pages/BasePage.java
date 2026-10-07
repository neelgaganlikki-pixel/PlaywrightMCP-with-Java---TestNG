package com.neel.playwright.pages;

import com.microsoft.playwright.Page;
import com.neel.playwright.utils.SelfHealingEngine;

import java.util.List;

/**
 * Base Page Object providing unified Playwright actions and self-healing primitives.
 */
public abstract class BasePage {

    protected final Page page;
    protected final SelfHealingEngine healingEngine;

    public BasePage(Page page) {
        this.page = page;
        this.healingEngine = SelfHealingEngine.getInstance();
    }

    public String getPageName() {
        return getClass().getSimpleName();
    }

    public void healClick(String elementName, String primary, List<String> fallbacks) {
        healClick(elementName, primary, fallbacks, 15000);
    }

    public void healClick(String elementName, String primary, List<String> fallbacks, int timeoutMs) {
        healingEngine.healClick(page, elementName, getPageName(), primary, fallbacks, timeoutMs);
    }

    public void healFill(String elementName, String primary, List<String> fallbacks, String text) {
        healFill(elementName, primary, fallbacks, text, 15000);
    }

    public void healFill(String elementName, String primary, List<String> fallbacks, String text, int timeoutMs) {
        healingEngine.healFill(page, elementName, getPageName(), primary, fallbacks, text, timeoutMs);
    }

    public String healGetText(String elementName, String primary, List<String> fallbacks) {
        return healGetText(elementName, primary, fallbacks, 15000);
    }

    public String healGetText(String elementName, String primary, List<String> fallbacks, int timeoutMs) {
        return healingEngine.healGetText(page, elementName, getPageName(), primary, fallbacks, timeoutMs);
    }

    public boolean healIsVisible(String elementName, String primary, List<String> fallbacks) {
        return healIsVisible(elementName, primary, fallbacks, 5000);
    }

    public boolean healIsVisible(String elementName, String primary, List<String> fallbacks, int timeoutMs) {
        return healingEngine.healIsVisible(page, elementName, getPageName(), primary, fallbacks, timeoutMs);
    }

    public void healWaitFor(String elementName, String primary, List<String> fallbacks) {
        healWaitFor(elementName, primary, fallbacks, 20000);
    }

    public void healWaitFor(String elementName, String primary, List<String> fallbacks, int timeoutMs) {
        healingEngine.healWaitFor(page, elementName, getPageName(), primary, fallbacks, timeoutMs);
    }

    public Page getPage() {
        return page;
    }
}

