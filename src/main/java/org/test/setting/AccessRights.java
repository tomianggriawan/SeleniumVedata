package org.test.setting;

import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.pages.AccessRightsPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * AccessRights - Test Runner untuk HCM > Setting > Access Rights (Role Menu).
 * Menggunakan POM + fluent interface (method chaining).
 *
 * Skenario Test:
 *   1. Verifikasi halaman Access Rights tampil
 *   2. Verifikasi judul halaman
 *   3. Verifikasi kolom tabel role matrix (Roles/Menu List, SUPER ADMIN, STAFF, ADMIN)
 *   4. Verifikasi dropdown filter module tampil
 *   5. Verifikasi checkbox permission tersedia di tabel
 *   6. Verifikasi baris "Setting" ada di menu list
 *   7. Verifikasi filter module default adalah "HCM"
 */
public class AccessRights {

    private static LoginPage loginPage;

    public static void main(String[] args) {
        try {
            loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST ACCESS RIGHTS - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi sekali saja di awal untuk kestabilan sesi SPA
            AccessRightsPage accessRightsPage = new DashboardPage(chrome).navigateToAccessRightsPage();

            testAccessRightsPageLoaded(accessRightsPage);
            testAccessRightsPageTitle(accessRightsPage);
            testAccessRightsTableColumns(accessRightsPage);
            testAccessRightsModuleDropdown(accessRightsPage);
            testAccessRightsCheckboxes(accessRightsPage);
            testAccessRightsSettingMenuInTable(accessRightsPage);
            testAccessRightsDefaultModuleFilter(accessRightsPage);

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

    public static void testAccessRightsPageLoaded(AccessRightsPage page) {
        BasePage.printTestHeader("Test 1: Verifikasi Halaman Access Rights Tampil");
        try {
            page.verifyPageLoaded();
        } catch (Exception e) {
            BasePage.printFail("Halaman Access Rights tampil", e.getMessage());
        }
        System.out.println();
    }

    public static void testAccessRightsPageTitle(AccessRightsPage page) {
        BasePage.printTestHeader("Test 2: Verifikasi Judul Halaman Access Rights");
        try {
            page.verifyPageTitle();
        } catch (Exception e) {
            BasePage.printFail("Judul halaman Access Rights", e.getMessage());
        }
        System.out.println();
    }

    public static void testAccessRightsTableColumns(AccessRightsPage page) {
        BasePage.printTestHeader("Test 3: Verifikasi Kolom Tabel (Roles/Menu List, SUPER ADMIN, STAFF, ADMIN)");
        try {
            page.verifyTableColumnsDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Kolom tabel Access Rights", e.getMessage());
        }
        System.out.println();
    }

    public static void testAccessRightsModuleDropdown(AccessRightsPage page) {
        BasePage.printTestHeader("Test 4: Verifikasi Dropdown Filter Module Tampil");
        try {
            page.verifyModuleDropdownDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Dropdown filter module", e.getMessage());
        }
        System.out.println();
    }

    public static void testAccessRightsCheckboxes(AccessRightsPage page) {
        BasePage.printTestHeader("Test 5: Verifikasi Checkbox Permission Tersedia");
        try {
            page.verifyCheckboxesDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Checkbox permission di tabel Access Rights", e.getMessage());
        }
        System.out.println();
    }

    public static void testAccessRightsSettingMenuInTable(AccessRightsPage page) {
        BasePage.printTestHeader("Test 6: Verifikasi Baris 'Setting' Ada di Menu List");
        try {
            page.verifySettingMenuInTable();
        } catch (Exception e) {
            BasePage.printFail("Baris Setting di tabel Access Rights", e.getMessage());
        }
        System.out.println();
    }

    public static void testAccessRightsDefaultModuleFilter(AccessRightsPage page) {
        BasePage.printTestHeader("Test 7: Verifikasi Filter Module Default adalah 'HCM'");
        try {
            page.verifyDefaultModuleFilter();
        } catch (Exception e) {
            BasePage.printFail("Filter module default HCM", e.getMessage());
        }
        System.out.println();
    }
}
