package org.test.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage - Base class untuk semua Page Object.
 * Menyediakan method reusable untuk interaksi Selenium.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public BasePage(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // ==================== Element Interaction ====================

    /**
     * Tunggu elemen visible lalu return
     */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Tunggu elemen clickable lalu return
     */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Klik elemen (tunggu sampai clickable)
     */
    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    /**
     * Ketik teks ke input field (clear dulu, lalu ketik)
     */
    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Ambil teks dari elemen
     */
    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    /**
     * Cek apakah elemen tampil (dengan timeout pendek)
     */
    protected boolean isDisplayed(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cek apakah elemen tampil dengan timeout custom
     */
    protected boolean isDisplayed(By locator, int timeoutSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cek apakah elemen ada di DOM (tanpa harus visible/dalam viewport)
     */
    protected boolean isPresent(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cek apakah elemen ada di DOM dengan timeout custom
     */
    protected boolean isPresent(By locator, int timeoutSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Navigasi ke URL
     */
    protected void navigateTo(String url) {
        driver.get(url);
    }

    /**
     * Refresh halaman
     */
    protected void refreshPage() {
        driver.navigate().refresh();
    }

    /**
     * Ambil URL saat ini
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Ambil title halaman
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    // ==================== Session / Auth Helper ====================

    /**
     * Memeriksa apakah URL saat ini adalah Keycloak OAuth2 callback.
     * Ditandai dengan parameter {@code code=} dan {@code session_state=} di URL hash.
     */
    protected boolean isKeycloakCallback() {
        String url = getCurrentUrl();
        return url.contains("code=") && url.contains("session_state=");
    }

    /**
     * Memeriksa apakah halaman saat ini adalah Keycloak login page.
     */
    protected boolean isOnKeycloakLoginPage() {
        String url = getCurrentUrl();
        return url.contains("keycloak") && (url.contains("login") || isDisplayed(By.id("kc-login"), 2));
    }

    /**
     * Navigasi kembali ke halaman URL yang aman (bukan Keycloak callback).
     * Berguna jika SPA gagal memproses callback OAuth.
     *
     * @param safeUrl URL tujuan (misal halaman list/dashboard)
     */
    protected void recoverFromKeycloakRedirect(String safeUrl) {
        System.out.println("  [WARN] Deteksi Keycloak redirect. Navigasi ke: " + safeUrl);
        driver.navigate().to(safeUrl);
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Memeriksa kesehatan sesi dengan menavigasi ke halaman utama.
     * Jika terdeteksi redirect ke Keycloak login, berarti sesi telah habis.
     *
     * @return true jika sesi masih aktif, false jika perlu login ulang
     */
    protected boolean isSessionValid() {
        String currentUrl = driver.getCurrentUrl();
        driver.navigate().to("https://web.vedata.id/");
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        boolean valid = !isOnKeycloakLoginPage();
        // Kembali ke halaman sebelumnya
        driver.navigate().to(currentUrl);
        try { Thread.sleep(4000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return valid;
    }

    // ==================== Test Helper ====================

    /**
     * Assert dan cetak hasil test
     */
    public static void assertCondition(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
        } else {
            System.out.println("  [FAIL] " + testName);
            throw new AssertionError("Test assertion failed: " + testName);
        }
    }

    /**
     * Cetak header section test
     */
    public static void printTestHeader(String testName) {
        System.out.println("--- " + testName + " ---");
    }

    /**
     * Cetak fail message
     */
    public static void printFail(String testName, String message) {
        System.out.println("  [FAIL] " + testName + ": " + message);
        throw new AssertionError("Test failed: " + testName + " - " + message);
    }
}
