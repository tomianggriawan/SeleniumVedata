package org.test.setting;

import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.pages.ServicePage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * Service - Test Runner untuk HCM > Setting > Service.
 * Menggunakan POM + fluent interface (method chaining).
 *
 * Skenario Test:
 *   1. Verifikasi halaman Service tampil
 *   2. Verifikasi judul halaman
 *   3. Verifikasi section "Produk yang sedang dipakai / subscribe" tampil
 *   4. Verifikasi section "Produk yang tersedia" tampil
 *   5. Verifikasi tab "Product" tampil
 *   6. Verifikasi tab "Billing" tampil
 *   7. Verifikasi input pencarian layanan tampil
 *   8. Verifikasi tombol "Request" tersedia
 */
public class Service {

    private static LoginPage loginPage;

    public static void main(String[] args) {
        try {
            loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST SERVICE - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi sekali saja di awal untuk kestabilan sesi SPA
            ServicePage servicePage = new DashboardPage(chrome).navigateToServicePage();

            testServicePageLoaded(servicePage);
            testServicePageTitle(servicePage);
            testServiceSubscribedSection(servicePage);
            testServiceAvailableSection(servicePage);
            testServiceProductTab(servicePage);
            testServiceBillingTab(servicePage);
            testServiceSearchInput(servicePage);
            testServiceRequestButton(servicePage);

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

    public static void testServicePageLoaded(ServicePage page) {
        BasePage.printTestHeader("Test 1: Verifikasi Halaman Service Tampil");
        try {
            page.verifyPageLoaded();
        } catch (Exception e) {
            BasePage.printFail("Halaman Service tampil", e.getMessage());
        }
        System.out.println();
    }

    public static void testServicePageTitle(ServicePage page) {
        BasePage.printTestHeader("Test 2: Verifikasi Judul Halaman Service");
        try {
            page.verifyPageTitle();
        } catch (Exception e) {
            BasePage.printFail("Judul halaman Service", e.getMessage());
        }
        System.out.println();
    }

    public static void testServiceSubscribedSection(ServicePage page) {
        BasePage.printTestHeader("Test 3: Verifikasi Section 'Produk yang sedang dipakai / subscribe'");
        try {
            page.verifySubscribedSectionDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Section 'Produk yang sedang dipakai'", e.getMessage());
        }
        System.out.println();
    }

    public static void testServiceAvailableSection(ServicePage page) {
        BasePage.printTestHeader("Test 4: Verifikasi Section 'Produk yang tersedia'");
        try {
            page.verifyAvailableSectionDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Section 'Produk yang tersedia'", e.getMessage());
        }
        System.out.println();
    }

    public static void testServiceProductTab(ServicePage page) {
        BasePage.printTestHeader("Test 5: Verifikasi Tab 'Product' Tampil");
        try {
            page.verifyProductTabDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Tab Product di halaman Service", e.getMessage());
        }
        System.out.println();
    }

    public static void testServiceBillingTab(ServicePage page) {
        BasePage.printTestHeader("Test 6: Verifikasi Tab 'Billing' Tampil");
        try {
            page.verifyBillingTabDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Tab Billing di halaman Service", e.getMessage());
        }
        System.out.println();
    }

    public static void testServiceSearchInput(ServicePage page) {
        BasePage.printTestHeader("Test 7: Verifikasi Input Pencarian Layanan Tampil");
        try {
            page.verifySearchInputDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Input pencarian layanan", e.getMessage());
        }
        System.out.println();
    }

    public static void testServiceRequestButton(ServicePage page) {
        BasePage.printTestHeader("Test 8: Verifikasi Tombol 'Request' Tersedia");
        try {
            page.verifyRequestButtonDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Tombol Request di halaman Service", e.getMessage());
        }
        System.out.println();
    }
}
