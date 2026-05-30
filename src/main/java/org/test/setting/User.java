package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.pages.UserPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * User - Test Runner untuk HCM > Setting > User.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class User extends BasePage {

    private final UserPage page;

    /**
     * Constructor untuk inisialisasi driver dan objek halaman UserPage.
     * State driver dipertahankan agar tidak hilang saat method chaining.
     */
    public User(WebDriver driver) {
        super(driver);
        this.page = new UserPage(driver);
    }

    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST USER - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi sekali saja di awal untuk kestabilan sesi SPA
            new DashboardPage(chrome).navigateToUserPage();

            // Memulai method chaining / fluent test execution
            new User(chrome)
                .testUserPageLoaded()
                .testUserPageTitle()
                .testUserTableColumns()
                .testUserAddButtonDisplayed()
                .testUserTableHasData()
                .testUserExistsInTable("tomi@tester.com");

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

    public User testUserPageLoaded() {
        printTestHeader("Test 1: Verifikasi Halaman User List Tampil");
        try {
            page.verifyPageLoaded();
        } catch (Exception e) {
            printFail("Halaman User List tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserPageTitle() {
        printTestHeader("Test 2: Verifikasi Judul Halaman User");
        try {
            page.verifyPageTitle();
        } catch (Exception e) {
            printFail("Judul halaman User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserTableColumns() {
        printTestHeader("Test 3: Verifikasi Kolom Tabel (Action, Username, Full Name, Module Access)");
        try {
            page.verifyTableColumnsDisplayed();
        } catch (Exception e) {
            printFail("Kolom tabel User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserAddButtonDisplayed() {
        printTestHeader("Test 4: Verifikasi Tombol 'Add' Tampil");
        try {
            page.verifyAddButtonDisplayed();
        } catch (Exception e) {
            printFail("Tombol Add tampil di User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserTableHasData() {
        printTestHeader("Test 5: Verifikasi Data di Tabel User");
        try {
            page.verifyTableHasData();
        } catch (Exception e) {
            printFail("Data di tabel User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserExistsInTable(String username) {
        printTestHeader("Test 6: Verifikasi User '" + username + "' Ada di Tabel");
        try {
            page.verifyUsernameInTable(username);
        } catch (Exception e) {
            printFail("User " + username + " di tabel", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
