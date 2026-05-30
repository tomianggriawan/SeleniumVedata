package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.pages.AccessRightsPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * AccessRights - Test Runner untuk HCM > Setting > Access Rights (Role Menu).
 * Menggunakan POM + fluent interface (method chaining).
 */
public class AccessRights extends BasePage {

    private final AccessRightsPage page;

    /**
     * Constructor untuk inisialisasi driver dan objek halaman AccessRightsPage.
     * State driver dipertahankan agar tidak hilang saat method chaining.
     */
    public AccessRights(WebDriver driver) {
        super(driver);
        this.page = new AccessRightsPage(driver);
    }

    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST ACCESS RIGHTS - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi sekali saja di awal untuk kestabilan sesi SPA
            new DashboardPage(chrome).navigateToAccessRightsPage();

            // Memulai method chaining / fluent test execution
            new AccessRights(chrome)
                .testAccessRightsPageLoaded()
                .testAccessRightsPageTitle()
                .testAccessRightsTableColumns()
                .testAccessRightsModuleDropdown()
                .testAccessRightsCheckboxes()
                .testAccessRightsSettingMenuInTable()
                .testAccessRightsDefaultModuleFilter();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST ACCESS RIGHTS SELESAI");
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

    public AccessRights testAccessRightsPageLoaded() {
        printTestHeader("Test 1: Verifikasi Halaman Access Rights Tampil");
        try {
            page.verifyPageLoaded();
        } catch (Exception e) {
            printFail("Halaman Access Rights tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public AccessRights testAccessRightsPageTitle() {
        printTestHeader("Test 2: Verifikasi Judul Halaman Access Rights");
        try {
            page.verifyPageTitle();
        } catch (Exception e) {
            printFail("Judul halaman Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public AccessRights testAccessRightsTableColumns() {
        printTestHeader("Test 3: Verifikasi Kolom Tabel (Roles/Menu List, SUPER ADMIN, STAFF, ADMIN)");
        try {
            page.verifyTableColumnsDisplayed();
        } catch (Exception e) {
            printFail("Kolom tabel Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public AccessRights testAccessRightsModuleDropdown() {
        printTestHeader("Test 4: Verifikasi Dropdown Filter Module Tampil");
        try {
            page.verifyModuleDropdownDisplayed();
        } catch (Exception e) {
            printFail("Dropdown filter module", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public AccessRights testAccessRightsCheckboxes() {
        printTestHeader("Test 5: Verifikasi Checkbox Permission Tersedia");
        try {
            page.verifyCheckboxesDisplayed();
        } catch (Exception e) {
            printFail("Checkbox permission di tabel Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public AccessRights testAccessRightsSettingMenuInTable() {
        printTestHeader("Test 6: Verifikasi Baris 'Setting' Ada di Menu List");
        try {
            page.verifySettingMenuInTable();
        } catch (Exception e) {
            printFail("Baris Setting di tabel Access Rights", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public AccessRights testAccessRightsDefaultModuleFilter() {
        printTestHeader("Test 7: Verifikasi Filter Module Default adalah 'HCM'");
        try {
            page.verifyDefaultModuleFilter();
        } catch (Exception e) {
            printFail("Filter module default HCM", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
