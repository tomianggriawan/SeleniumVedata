package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.settingpage.AccessRightsPage;
import org.pages.DashboardPage;

import static org.common.WebDriverTools.chrome;

/**
 * AccessRights - Test Runner untuk HCM > Setting > Access Rights (Role Menu).
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via AccessRightsPage
 *  - Fluent interface (method chaining)
 */
public class AccessRights extends BasePage {

    private final AccessRightsPage page;

    public AccessRights(WebDriver driver) {
        super(driver);
        this.page = new AccessRightsPage(driver);
    }

    // ==================== Entry Point ====================

    public static void main(String[] args) {
        runTest("HCM AccessRights", "AccessRights", () -> {
            new DashboardPage(chrome).navigateToAccessRightsPage();

            new AccessRights(chrome)
                .testPageLoaded()
                .testPageTitle()
                .testTableColumns()
                .testModuleDropdown()
                .testCheckboxes()
                .testSettingMenuInTable()
                .testDefaultModuleFilter();
        });
    }

    // ==================== TEST METHODS ====================

    /**
     * TC_ACCESS_RIGHTS_PAGE_LOADED - Verifikasi halaman Access Rights tampil.
     */
    public AccessRights testPageLoaded() {
        reporter.startTest("TC_ACCESS_RIGHTS_PAGE_LOADED", "Verifikasi Halaman Access Rights Tampil");
        drainLogs();
        try {
            reporter.logStep("Verifikasi halaman Access Rights berhasil dimuat...");
            page.verifyPageLoaded();
            inspectNetwork("memuat halaman Access Rights");
            reporter.logPass("Halaman Access Rights berhasil ditampilkan.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Halaman Access Rights gagal dimuat: " + e.getMessage(), e);
            printFail("Halaman Access Rights tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_ACCESS_RIGHTS_PAGE_TITLE - Verifikasi judul halaman Access Rights.
     */
    public AccessRights testPageTitle() {
        reporter.startTest("TC_ACCESS_RIGHTS_PAGE_TITLE", "Verifikasi Judul Halaman Access Rights");
        drainLogs();
        try {
            reporter.logStep("Verifikasi judul halaman Access Rights...");
            page.verifyPageTitle();
            reporter.logPass("Judul halaman Access Rights sesuai.");
        } catch (Exception e) {
            reporter.logFail("Judul halaman Access Rights tidak sesuai: " + e.getMessage(), e);
            printFail("Judul halaman Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_ACCESS_RIGHTS_TABLE_COLUMNS - Verifikasi kolom tabel.
     */
    public AccessRights testTableColumns() {
        reporter.startTest("TC_ACCESS_RIGHTS_TABLE_COLUMNS", "Verifikasi Kolom Tabel (Roles/Menu List, SUPER ADMIN, STAFF, ADMIN)");
        drainLogs();
        try {
            reporter.logStep("Verifikasi kolom-kolom tabel Access Rights tersedia...");
            page.verifyTableColumnsDisplayed();
            reporter.logPass("Semua kolom tabel Access Rights tampil.");
        } catch (Exception e) {
            reporter.logFail("Kolom tabel Access Rights tidak lengkap: " + e.getMessage(), e);
            printFail("Kolom tabel Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_ACCESS_RIGHTS_MODULE_DROPDOWN - Verifikasi dropdown filter module tampil.
     */
    public AccessRights testModuleDropdown() {
        reporter.startTest("TC_ACCESS_RIGHTS_MODULE_DROPDOWN", "Verifikasi Dropdown Filter Module Tampil");
        drainLogs();
        try {
            reporter.logStep("Verifikasi dropdown filter module tersedia di halaman...");
            page.verifyModuleDropdownDisplayed();
            reporter.logPass("Dropdown filter module berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Dropdown filter module tidak tampil: " + e.getMessage(), e);
            printFail("Dropdown filter module", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_ACCESS_RIGHTS_CHECKBOXES - Verifikasi checkbox permission tersedia.
     */
    public AccessRights testCheckboxes() {
        reporter.startTest("TC_ACCESS_RIGHTS_CHECKBOXES", "Verifikasi Checkbox Permission Tersedia");
        drainLogs();
        try {
            reporter.logStep("Verifikasi checkbox permission di tabel tersedia...");
            page.verifyCheckboxesDisplayed();
            reporter.logPass("Checkbox permission di tabel Access Rights berhasil ditemukan.");
        } catch (Exception e) {
            reporter.logFail("Checkbox permission tidak tersedia: " + e.getMessage(), e);
            printFail("Checkbox permission di tabel Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_ACCESS_RIGHTS_SETTING_MENU - Verifikasi baris 'Setting' ada di menu list.
     */
    public AccessRights testSettingMenuInTable() {
        reporter.startTest("TC_ACCESS_RIGHTS_SETTING_MENU", "Verifikasi Baris 'Setting' Ada di Menu List");
        drainLogs();
        try {
            reporter.logStep("Verifikasi baris 'Setting' tersedia pada kolom Menu List...");
            page.verifySettingMenuInTable();
            reporter.logPass("Baris 'Setting' berhasil ditemukan di tabel Access Rights.");
        } catch (Exception e) {
            reporter.logFail("Baris 'Setting' tidak ditemukan di tabel: " + e.getMessage(), e);
            printFail("Baris Setting di tabel Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    /**
     * TC_ACCESS_RIGHTS_DEFAULT_FILTER - Verifikasi filter module default adalah 'HCM'.
     */
    public AccessRights testDefaultModuleFilter() {
        reporter.startTest("TC_ACCESS_RIGHTS_DEFAULT_FILTER", "Verifikasi Filter Module Default adalah 'HCM'");
        drainLogs();
        try {
            reporter.logStep("Verifikasi bahwa filter module default adalah 'HCM'...");
            page.verifyDefaultModuleFilter();
            inspectNetwork("verifikasi halaman Access Rights");
            reporter.logPass("Filter module default 'HCM' terkonfirmasi.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Filter module default bukan 'HCM': " + e.getMessage(), e);
            printFail("Filter module default HCM", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
