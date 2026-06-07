package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.DashboardPage;
import org.pages.settingpage.UserPage;

import static org.common.WebDriverTools.chrome;

/**
 * User - Test Runner untuk HCM > Setting > User Management.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via UserPage
 *  - Fluent interface (method chaining)
 *
 * Skenario:
 *  TC_HCM_USER_PAGE_LOADED      - Halaman User List termuat dengan benar
 *  TC_HCM_USER_PAGE_TITLE       - Judul halaman mengandung "User"
 *  TC_HCM_USER_TABLE_COLUMNS    - Kolom tabel (Action, Username, Full Name, Module Access) tampil
 *  TC_HCM_USER_ADD_BUTTON       - Tombol Add tampil
 *  TC_HCM_USER_TABLE_HAS_DATA   - Tabel memiliki minimal 1 baris data
 *  TC_HCM_USER_ADD_MODAL        - Klik Add membuka modal form
 */
public class User extends BasePage {

    private final UserPage userPage;

    // ==================== Constructor ====================

    public User(WebDriver driver) {
        super(driver);
        this.userPage = new UserPage(driver);
    }

    // ==================== Entry Point ====================

    public static void main(String[] args) {
        runTest("HCM User Management", "User", () -> {
            new DashboardPage(chrome).navigateToUserPage();

            new User(chrome)
                .testPageLoaded()
                .testPageTitle()
                .testTableColumns()
                .testAddButtonVisible()
                .testTableHasData()
                .testAddModalOpens();
        });
    }

    // ==================== TEST METHODS ====================

    /**
     * TC_HCM_USER_PAGE_LOADED - Verifikasi halaman User List termuat.
     */
    public User testPageLoaded() {
        reporter.startTest("TC_HCM_USER_PAGE_LOADED", "Verifikasi Halaman User List Termuat");
        drainLogs();
        try {
            reporter.logStep("Verifikasi halaman User List berhasil dimuat...");
            userPage.verifyPageLoaded();
            inspectNetwork("memuat halaman User");
            reporter.logPass("Halaman User List berhasil ditampilkan.");
        } catch (Throwable e) {
            captureNetworkOnFail("PAGE_LOADED");
            reporter.logFail("Halaman User List gagal dimuat.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_HCM_USER_PAGE_TITLE - Verifikasi judul halaman mengandung "User".
     */
    public User testPageTitle() {
        reporter.startTest("TC_HCM_USER_PAGE_TITLE", "Verifikasi Judul Halaman Mengandung 'User'");
        try {
            reporter.logStep("Verifikasi judul halaman User sesuai...");
            userPage.verifyPageTitle();
            reporter.logPass("Judul halaman User terkonfirmasi.");
        } catch (Throwable e) {
            reporter.logFail("Judul halaman User tidak sesuai.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_HCM_USER_TABLE_COLUMNS - Verifikasi kolom tabel tampil lengkap.
     */
    public User testTableColumns() {
        reporter.startTest("TC_HCM_USER_TABLE_COLUMNS", "Verifikasi Kolom Tabel User (Action, Username, Full Name, Module Access)");
        drainLogs();
        try {
            reporter.logStep("Verifikasi semua kolom tabel User tersedia...");
            userPage.verifyTableColumnsDisplayed();
            reporter.logPass("Semua kolom tabel User tampil.");
        } catch (Throwable e) {
            reporter.logFail("Kolom tabel User tidak lengkap.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_HCM_USER_ADD_BUTTON - Verifikasi tombol Add tampil.
     */
    public User testAddButtonVisible() {
        reporter.startTest("TC_HCM_USER_ADD_BUTTON", "Verifikasi Tombol 'Add' Tampil di Halaman User");
        try {
            reporter.logStep("Verifikasi tombol Add tersedia...");
            userPage.verifyAddButtonDisplayed();
            reporter.logPass("Tombol Add tampil di halaman User.");
        } catch (Throwable e) {
            reporter.logFail("Tombol Add tidak tampil di halaman User.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_HCM_USER_TABLE_HAS_DATA - Verifikasi tabel memiliki minimal 1 baris data.
     */
    public User testTableHasData() {
        reporter.startTest("TC_HCM_USER_TABLE_HAS_DATA", "Verifikasi Tabel User Memiliki Data");
        drainLogs();
        try {
            reporter.logStep("Verifikasi tabel User memiliki minimal 1 baris...");
            userPage.verifyTableHasData();
            inspectNetwork("READ tabel User");
            reporter.logPass("Tabel User memiliki data yang dapat dibaca.");
        } catch (Throwable e) {
            captureNetworkOnFail("TABLE_HAS_DATA");
            reporter.logFail("Tabel User kosong atau gagal dimuat.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_HCM_USER_ADD_MODAL - Verifikasi klik tombol Add membuka modal form.
     */
    public User testAddModalOpens() {
        reporter.startTest("TC_HCM_USER_ADD_MODAL", "Verifikasi Klik 'Add' Membuka Modal Form User");
        drainLogs();
        try {
            reporter.logStep("Klik tombol Add...");
            userPage.clickAddButton();

            reporter.logStep("Verifikasi modal Add User terbuka dengan field input...");
            userPage.verifyAddModalOpened();

            inspectNetwork("membuka modal Add User");
            reporter.logPass("Modal form Add User berhasil terbuka.");

        } catch (Throwable e) {
            captureNetworkOnFail("ADD_MODAL");
            reporter.logFail("Modal Add User gagal terbuka.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }
}
