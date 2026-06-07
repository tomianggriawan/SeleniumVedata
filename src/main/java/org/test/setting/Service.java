package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.DashboardPage;
import org.pages.settingpage.ServicePage;

import static org.common.WebDriverTools.chrome;

/**
 * Service - Test Runner untuk HCM > Setting > Service.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via ServicePage
 *  - Fluent interface (method chaining)
 */
public class Service extends BasePage {

    private final ServicePage page;

    public Service(WebDriver driver) {
        super(driver);
        this.page = new ServicePage(driver);
    }

    // ==================== Entry Point ====================

    public static void main(String[] args) {
        runTest("HCM Service", "Service", () -> {
            new DashboardPage(chrome).navigateToServicePage();

            new Service(chrome)
                .testPageLoaded()
                .testPageTitle()
                .testSubscribedSection()
                .testAvailableSection()
                .testProductTab()
                .testBillingTab()
                .testSearchInput()
                .testRequestButton();
        });
    }

    // ==================== TEST METHODS ====================

    /**
     * TC_SERVICE_PAGE_LOADED - Verifikasi halaman Service tampil.
     */
    public Service testPageLoaded() {
        reporter.startTest("TC_SERVICE_PAGE_LOADED", "Verifikasi Halaman Service Tampil");
        drainLogs();
        try {
            reporter.logStep("Verifikasi halaman Service berhasil dimuat...");
            page.verifyPageLoaded();
            inspectNetwork("memuat halaman Service");
            reporter.logPass("Halaman Service berhasil ditampilkan.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Halaman Service gagal dimuat: " + e.getMessage(), e);
            printFail("Halaman Service tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_PAGE_TITLE - Verifikasi judul halaman Service.
     */
    public Service testPageTitle() {
        reporter.startTest("TC_SERVICE_PAGE_TITLE", "Verifikasi Judul Halaman Service");
        try {
            reporter.logStep("Verifikasi judul halaman Service sesuai...");
            page.verifyPageTitle();
            reporter.logPass("Judul halaman Service sesuai.");
        } catch (Exception e) {
            reporter.logFail("Judul halaman Service tidak sesuai: " + e.getMessage(), e);
            printFail("Judul halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_SUBSCRIBED_SECTION - Verifikasi section produk yang sedang dipakai.
     */
    public Service testSubscribedSection() {
        reporter.startTest("TC_SERVICE_SUBSCRIBED_SECTION", "Verifikasi Section 'Produk yang sedang dipakai / subscribe'");
        try {
            reporter.logStep("Verifikasi section produk yang sedang dilanggani tersedia...");
            page.verifySubscribedSectionDisplayed();
            reporter.logPass("Section 'Produk yang sedang dipakai' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Section 'Produk yang sedang dipakai' tidak tampil: " + e.getMessage(), e);
            printFail("Section 'Produk yang sedang dipakai'", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_AVAILABLE_SECTION - Verifikasi section produk yang tersedia.
     */
    public Service testAvailableSection() {
        reporter.startTest("TC_SERVICE_AVAILABLE_SECTION", "Verifikasi Section 'Produk yang tersedia'");
        try {
            reporter.logStep("Verifikasi section produk yang tersedia ditampilkan...");
            page.verifyAvailableSectionDisplayed();
            reporter.logPass("Section 'Produk yang tersedia' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Section 'Produk yang tersedia' tidak tampil: " + e.getMessage(), e);
            printFail("Section 'Produk yang tersedia'", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_PRODUCT_TAB - Verifikasi tab 'Product' tampil.
     */
    public Service testProductTab() {
        reporter.startTest("TC_SERVICE_PRODUCT_TAB", "Verifikasi Tab 'Product' Tampil");
        try {
            reporter.logStep("Verifikasi tab 'Product' tersedia di halaman Service...");
            page.verifyProductTabDisplayed();
            reporter.logPass("Tab 'Product' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Tab Product tidak tampil: " + e.getMessage(), e);
            printFail("Tab Product di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_BILLING_TAB - Verifikasi tab 'Billing' tampil.
     */
    public Service testBillingTab() {
        reporter.startTest("TC_SERVICE_BILLING_TAB", "Verifikasi Tab 'Billing' Tampil");
        try {
            reporter.logStep("Verifikasi tab 'Billing' tersedia di halaman Service...");
            page.verifyBillingTabDisplayed();
            reporter.logPass("Tab 'Billing' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Tab Billing tidak tampil: " + e.getMessage(), e);
            printFail("Tab Billing di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_SEARCH_INPUT - Verifikasi input pencarian layanan tampil.
     */
    public Service testSearchInput() {
        reporter.startTest("TC_SERVICE_SEARCH_INPUT", "Verifikasi Input Pencarian Layanan Tampil");
        try {
            reporter.logStep("Verifikasi field input pencarian layanan tersedia...");
            page.verifySearchInputDisplayed();
            reporter.logPass("Input pencarian layanan berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Input pencarian layanan tidak tampil: " + e.getMessage(), e);
            printFail("Input pencarian layanan", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_SERVICE_REQUEST_BUTTON - Verifikasi tombol 'Request' tersedia.
     */
    public Service testRequestButton() {
        reporter.startTest("TC_SERVICE_REQUEST_BUTTON", "Verifikasi Tombol 'Request' Tersedia");
        drainLogs();
        try {
            reporter.logStep("Verifikasi tombol 'Request' tersedia di halaman Service...");
            page.verifyRequestButtonDisplayed();
            inspectNetwork("verifikasi halaman Service");
            reporter.logPass("Tombol 'Request' berhasil ditemukan di halaman Service.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Tombol Request tidak tersedia: " + e.getMessage(), e);
            printFail("Tombol Request di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
