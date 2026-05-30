package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.pages.ServicePage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * Service - Test Runner untuk HCM > Setting > Service.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class Service extends BasePage {

    private final ServicePage page;

    /**
     * Constructor untuk inisialisasi driver dan objek halaman ServicePage.
     * State driver dipertahaman agar tidak hilang saat method chaining.
     */
    public Service(WebDriver driver) {
        super(driver);
        this.page = new ServicePage(driver);
    }

    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST SERVICE - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi sekali saja di awal untuk kestabilan sesi SPA
            new DashboardPage(chrome).navigateToServicePage();

            // Memulai method chaining / fluent test execution
            new Service(chrome)
                .testServicePageLoaded()
                .testServicePageTitle()
                .testServiceSubscribedSection()
                .testServiceAvailableSection()
                .testServiceProductTab()
                .testServiceBillingTab()
                .testServiceSearchInput()
                .testServiceRequestButton();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST SERVICE SELESAI");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ERROR !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                System.err.println("URL saat error: " + chrome.getCurrentUrl());
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("c:/Users/LENOVO/SDET/SeleniumVedata/page_source_error.html"),
                    chrome.getPageSource()
                );
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }

    public Service testServicePageLoaded() {
        printTestHeader("Test 1: Verifikasi Halaman Service Tampil");
        try {
            page.verifyPageLoaded();
        } catch (Exception e) {
            printFail("Halaman Service tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServicePageTitle() {
        printTestHeader("Test 2: Verifikasi Judul Halaman Service");
        try {
            page.verifyPageTitle();
        } catch (Exception e) {
            printFail("Judul halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceSubscribedSection() {
        printTestHeader("Test 3: Verifikasi Section 'Produk yang sedang dipakai / subscribe'");
        try {
            page.verifySubscribedSectionDisplayed();
        } catch (Exception e) {
            printFail("Section 'Produk yang sedang dipakai'", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceAvailableSection() {
        printTestHeader("Test 4: Verifikasi Section 'Produk yang tersedia'");
        try {
            page.verifyAvailableSectionDisplayed();
        } catch (Exception e) {
            printFail("Section 'Produk yang tersedia'", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceProductTab() {
        printTestHeader("Test 5: Verifikasi Tab 'Product' Tampil");
        try {
            page.verifyProductTabDisplayed();
        } catch (Exception e) {
            printFail("Tab Product di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceBillingTab() {
        printTestHeader("Test 6: Verifikasi Tab 'Billing' Tampil");
        try {
            page.verifyBillingTabDisplayed();
        } catch (Exception e) {
            printFail("Tab Billing di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceSearchInput() {
        printTestHeader("Test 7: Verifikasi Input Pencarian Layanan Tampil");
        try {
            page.verifySearchInputDisplayed();
        } catch (Exception e) {
            printFail("Input pencarian layanan", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceRequestButton() {
        printTestHeader("Test 8: Verifikasi Tombol 'Request' Tersedia");
        try {
            page.verifyRequestButtonDisplayed();
        } catch (Exception e) {
            printFail("Tombol Request di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
