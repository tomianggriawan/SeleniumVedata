package org.test.inventory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.WebDriverTools;
import org.pages.LoginPage;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

/**
 * BaseTest - Setup & Teardown WebDriver untuk seluruh test suite Category.
 *
 * Siklus Hidup:
 * - @BeforeSuite : Login ke VEDATA, maximize window, tunggu dashboard siap.
 * - @AfterSuite  : Quit WebDriver.
 *
 * Driver diakses dari WebDriverTools.chrome (Singleton shared driver).
 */
public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;

    protected static final String BASE_URL              = "https://web.vedata.id/";
    protected static final String INVENTORY_SETTING_URL = "https://web.vedata.id/inventory/setting?tab=item";
    protected static final String ITEM_FORM_URL         = "https://web.vedata.id/inventory/setting/item/form";

    // Kredensial login — idealnya dibaca dari config/environment
    private static final String LOGIN_USERNAME = "tomi@tester.com";
    private static final String LOGIN_PASSWORD = "1234";

    // ==================== Suite Setup ====================

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() throws InterruptedException {
        System.out.println("=================================================");
        System.out.println("[SUITE START] Category DDT Test Suite");
        System.out.println("=================================================");

        driver = WebDriverTools.chrome;
        wait   = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.get(BASE_URL);
        Thread.sleep(3000);

        doLogin();

        System.out.println("  [INFO] Navigasi ke Category Setting...");
        driver.get(INVENTORY_SETTING_URL);
        Thread.sleep(4000);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("leftSidebar")));
            System.out.println("  [INFO] Halaman Category Setting berhasil dimuat.");
        } catch (Exception e) {
            System.out.println("  [WARN] leftSidebar tidak terdeteksi, lanjut...");
        }

        System.out.println("[SUITE READY] Driver siap, sesi login aktif.\n");
    }

    // ==================== Suite Teardown ====================

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        System.out.println("\n=================================================");
        System.out.println("[SUITE END] Category DDT Test Suite selesai.");
        System.out.println("=================================================");
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("  [WARN] Driver quit gagal: " + e.getMessage());
            }
        }
    }

    // ==================== Helpers ====================

    /**
     * Melakukan login ke VEDATA.
     * Jika sesi sudah aktif (URL bukan Keycloak), login dilewati.
     */
    private void doLogin() throws InterruptedException {
        String currentUrl = driver.getCurrentUrl();
        boolean isAlreadyLoggedIn = currentUrl.contains("web.vedata.id")
            && !currentUrl.contains("keycloak")
            && !currentUrl.contains("kc-login");

        if (isAlreadyLoggedIn) {
            System.out.println("  [INFO] Sesi tampaknya sudah aktif, skip login.");
            return;
        }

        System.out.println("  [INFO] Melakukan login sebagai: " + LOGIN_USERNAME);
        new LoginPage(driver).login(LOGIN_USERNAME, LOGIN_PASSWORD);
        Thread.sleep(5000);

        if (driver.getCurrentUrl().contains("keycloak")) {
            System.out.println("  [WARN] Masih di Keycloak setelah login, tunggu...");
            Thread.sleep(5000);
        }

        System.out.println("  [INFO] Login selesai. URL: " + driver.getCurrentUrl());
    }

    /**
     * Navigasi ke Category Setting dan tunggu tab selesai dimuat.
     */
    protected void navigateToInventorySetting() throws InterruptedException {
        driver.get(INVENTORY_SETTING_URL);
        Thread.sleep(3000);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-tab")));
        } catch (Exception e) {
            System.out.println("  [WARN] Tab inventory tidak terdeteksi, lanjut...");
        }
    }

    /**
     * Sleep ringkas yang menelan InterruptedException.
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Menghasilkan string timestamp (milisecond) untuk Code/SKU unik.
     */
    protected static String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
}
