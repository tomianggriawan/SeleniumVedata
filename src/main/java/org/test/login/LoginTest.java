package org.test.login;

import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;
import org.test.setting.*;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * LoginTest - Main Test Suite representing the entire regression tests in VEDATA.
 * Demonstrates the power of Page Object Model (POM) combined with Fluent Interface / Method Chaining.
 */
public class LoginTest {
    private static LoginPage loginPage;

    public static void main(String[] args) {
        try {
            // Initialize page object
            loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST SUITE (POM & METHOD CHAINING) - VEDATA");
            System.out.println("========================================\n");

            // --- 1. VERIFIKASI HALAMAN LOGIN & VALIDASI ---
            testLoginPageDisplayed();
            testEmptyFormValidation();
            testLoginWithInvalidCredentials();

            // --- 2. LOGIN DENGAN KREDENSIAL BENAR ---
            testLoginWithValidCredentials();
            testSidebarVisibility();

            // --- 3. RUN SETTINGS MODULES TESTS (CHAINED FLOW) ---
            System.out.println("\n--- MEMULAI CHAINED SETTINGS TESTS ---");

            // Chaining test untuk Company
            new DashboardPage(chrome).navigateToCompanyPage();
            new Company(chrome)
                .testVerifikasiCRUDCompany()
                .testUploadCompanyLogo();

            // Chaining test untuk Job Title
            new DashboardPage(chrome).navigateToJobTitlePage();
            new JobTitle(chrome)
                .testCreateJobTitle()
                .testReadJobTitle()
                .testUpdateJobTitle();

            // Chaining test untuk User
            new DashboardPage(chrome).navigateToUserPage();
            new User(chrome)
                .testUserPageLoaded()
                .testUserPageTitle()
                .testUserTableColumns()
                .testUserAddButtonDisplayed()
                .testUserTableHasData()
                .testUserExistsInTable("tomi@tester.com");

            // Chaining test untuk Access Rights
            new DashboardPage(chrome).navigateToAccessRightsPage();
            new AccessRights(chrome)
                .testAccessRightsPageLoaded()
                .testAccessRightsPageTitle()
                .testAccessRightsTableColumns()
                .testAccessRightsModuleDropdown()
                .testAccessRightsCheckboxes()
                .testAccessRightsSettingMenuInTable()
                .testAccessRightsDefaultModuleFilter();

            // Chaining test untuk Service
            new DashboardPage(chrome).navigateToServicePage();
            new Service(chrome)
                .testServicePageLoaded()
                .testServicePageTitle()
                .testServiceSubscribedSection()
                .testServiceAvailableSection()
                .testServiceProductTab()
                .testServiceBillingTab()
                .testServiceSearchInput()
                .testServiceRequestButton();

            // Chaining test untuk Employee Profile (Create, Read, Update)
            new DashboardPage(chrome).navigateToEmployeeProfilePage();
            new EmployeeProfile(chrome)
                .testCreateEmployee()
                .testReadEmployee()
                .testUpdateEmployee();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST SUITE SELESAI DENGAN SUKSES");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ENCOUNTERED AN ERROR OR FAILURE !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                String currentUrl = chrome.getCurrentUrl();
                System.err.println("Current URL at failure: " + currentUrl);
                java.nio.file.Files.writeString(
                        java.nio.file.Path.of("c:/Users/LENOVO/SDET/SeleniumVedata/page_source_error.html"),
                        chrome.getPageSource()
                );
                System.err.println("Page source at failure dumped to page_source_error.html");
            } catch (Exception ex) {
                System.err.println("Failed to dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }

    /**
     * Test 1: Verifikasi elemen-elemen halaman login tampil dengan benar
     */
    public static void testLoginPageDisplayed() {
        BasePage.printTestHeader("Test 1: Verifikasi Halaman Login");

        try {
            BasePage.assertCondition("Title halaman adalah 'VEDATA'", loginPage.isTitleValid());
        } catch (Exception e) {
            BasePage.printFail("Title halaman adalah 'VEDATA'", e.getMessage());
        }

        try {
            BasePage.assertCondition("Logo tampil", loginPage.isLogoDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Logo tampil", e.getMessage());
        }

        try {
            BasePage.assertCondition("Label 'Username' tampil", loginPage.isUsernameLabelDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Label 'Username' tampil", e.getMessage());
        }

        try {
            BasePage.assertCondition("Placeholder 'Masukan username' tampil", loginPage.isUsernamePlaceholderDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Placeholder 'Masukan username' tampil", e.getMessage());
        }

        try {
            BasePage.assertCondition("Label 'Password' tampil", loginPage.isPasswordLabelDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Label 'Password' tampil", e.getMessage());
        }

        try {
            BasePage.assertCondition("Placeholder 'Masukan password' tampil", loginPage.isPasswordPlaceholderDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Placeholder 'Masukan password' tampil", e.getMessage());
        }

        try {
            BasePage.assertCondition("Tombol Login tampil", loginPage.isLoginButtonDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Tombol Login tampil", e.getMessage());
        }

        System.out.println();
    }

    /**
     * Test 2: Verifikasi validasi ketika form dikosongkan
     */
    public static void testEmptyFormValidation() {
        BasePage.printTestHeader("Test 2: Verifikasi Validasi Form Kosong");

        try {
            // Click login to trigger Keycloak validation
            loginPage.clickLogin();

            // Verify email required validation message
            BasePage.assertCondition("Validasi 'E-mail wajib diisi' tampil", loginPage.isEmailRequiredMessageDisplayed());

            // Verify password required validation message
            BasePage.assertCondition("Validasi 'Password wajib diisi' tampil", loginPage.isPasswordRequiredMessageDisplayed());
        } catch (Exception e) {
            BasePage.printFail("Validasi form kosong", e.getMessage());
        }

        System.out.println();
    }

    /**
     * Test 3: Verifikasi login dengan kredensial yang salah
     */
    public static void testLoginWithInvalidCredentials() {
        BasePage.printTestHeader("Test 3: Login Dengan Kredensial Salah");

        try {
            loginPage.refresh();

            // Login with invalid username/password
            loginPage.login("invalid@user.com", "wrongpassword");

            // Verify it remains on login page
            BasePage.assertCondition("Tetap di halaman login setelah kredensial salah", loginPage.isStillOnLoginPage());
        } catch (Exception e) {
            BasePage.printFail("Login dengan kredensial salah", e.getMessage());
        }

        System.out.println();
    }

    /**
     * Test 4: Verifikasi login dengan kredensial yang benar
     */
    public static void testLoginWithValidCredentials() {
        BasePage.printTestHeader("Test 4: Login Dengan Kredensial Benar");

        try {
            loginPage.refresh();

            // Login with correct credentials
            loginPage.login("tomi@tester.com", "1234");

            // Verify dashboard is displayed
            BasePage.assertCondition("Dashboard tampil setelah login berhasil", loginPage.isDashboardDisplayed());

            // Verify URL updated to dashboard
            BasePage.assertCondition("URL mengarah ke dashboard", loginPage.isUrlContainingDashboard());
        } catch (Exception e) {
            BasePage.printFail("Login dengan kredensial benar", e.getMessage());
        }

        System.out.println();
    }

    /**
     * Test 5: Verifikasi visibilitas sidebar menggunakan method chaining (fluent interface).
     */
    public static void testSidebarVisibility() {
        BasePage.printTestHeader("Test 5: Verifikasi Visibilitas Sidebar (Chaining Test)");

        try {
            // Gunakan sesi aktif, langsung verifikasi sidebar via chaining
            new DashboardPage(chrome)
                    .verifySidebarDisplayed()
                    .verifyMenuCompanyDisplayed()
                    .verifyMenuJobTitleDisplayed()
                    .verifyMenuUserDisplayed()
                    .verifyMenuAccessRightsDisplayed()
                    .verifyMenuEmployeeDashboardDisplayed()
                    .verifyMenuEmployeeProfileDisplayed()
                    .verifyMenuInventoryDashboardDisplayed()
                    .verifyMenuInventoryWarehouseDisplayed()
                    .verifyMenuInventorySupplierDisplayed()
                    .verifyMenuFinanceDashboardDisplayed()
                    .verifyMenuFinanceBankDisplayed();

        } catch (Exception e) {
            BasePage.printFail("Verifikasi visibilitas sidebar", e.getMessage());
        }

        System.out.println();
    }
}
