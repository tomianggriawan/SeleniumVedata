package org.test.setting;

import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.pages.UserPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * User - Test Runner untuk HCM > Setting > User.
 * Menggunakan POM + fluent interface (method chaining).
 *
 * Skenario Test:
 *   1. Verifikasi halaman User List tampil
 *   2. Verifikasi judul halaman
 *   3. Verifikasi kolom tabel (Action, Username, Full Name, Module Access)
 *   4. Verifikasi tombol Add tampil
 *   5. Verifikasi data tersedia di tabel
 *   6. Verifikasi user "tomi@tester.com" ada di tabel
 */
public class User {

    private static LoginPage loginPage;

    public static void main(String[] args) {
        try {
            loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST USER - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi sekali saja di awal untuk kestabilan sesi SPA
            UserPage userPage = new DashboardPage(chrome).navigateToUserPage();

            testUserPageLoaded(userPage);
            testUserPageTitle(userPage);
            testUserTableColumns(userPage);
            testUserAddButtonDisplayed(userPage);
            testUserTableHasData(userPage);
            testUserExistsInTable(userPage);

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST USER SELESAI");
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

    public static void testUserPageLoaded(UserPage page) {
        BasePage.printTestHeader("Test 1: Verifikasi Halaman User List Tampil");
        try {
            page.verifyPageLoaded();
        } catch (Exception e) {
            BasePage.printFail("Halaman User List tampil", e.getMessage());
        }
        System.out.println();
    }

    public static void testUserPageTitle(UserPage page) {
        BasePage.printTestHeader("Test 2: Verifikasi Judul Halaman User");
        try {
            page.verifyPageTitle();
        } catch (Exception e) {
            BasePage.printFail("Judul halaman User", e.getMessage());
        }
        System.out.println();
    }

    public static void testUserTableColumns(UserPage page) {
        BasePage.printTestHeader("Test 3: Verifikasi Kolom Tabel (Action, Username, Full Name, Module Access)");
        try {
            page.verifyTableColumnsDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Kolom tabel User", e.getMessage());
        }
        System.out.println();
    }

    public static void testUserAddButtonDisplayed(UserPage page) {
        BasePage.printTestHeader("Test 4: Verifikasi Tombol 'Add' Tampil");
        try {
            page.verifyAddButtonDisplayed();
        } catch (Exception e) {
            BasePage.printFail("Tombol Add tampil di User", e.getMessage());
        }
        System.out.println();
    }

    public static void testUserTableHasData(UserPage page) {
        BasePage.printTestHeader("Test 5: Verifikasi Data di Tabel User");
        try {
            page.verifyTableHasData();
        } catch (Exception e) {
            BasePage.printFail("Data di tabel User", e.getMessage());
        }
        System.out.println();
    }

    public static void testUserExistsInTable(UserPage page) {
        BasePage.printTestHeader("Test 6: Verifikasi User 'tomi@tester.com' Ada di Tabel");
        try {
            page.verifyUsernameInTable("tomi@tester.com");
        } catch (Exception e) {
            BasePage.printFail("User tomi@tester.com di tabel", e.getMessage());
        }
        System.out.println();
    }
}
