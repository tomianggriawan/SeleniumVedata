package org.test.setting;

import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * Login - Main Test Runner using the Page Object Model (POM) pattern.
 */
public class Company {

    private static LoginPage loginPage;

    public static void main(String[] args) {
        try {
            // Initialize page object
            loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST LOGIN (POM PATTERN) - VEDATA");
            System.out.println("========================================\n");

            // Test 1: Verifikasi halaman login tampil
            testLoginPageDisplayed();

            // Test 2: Verifikasi validasi form kosong
            testEmptyFormValidation();

            // Test 3: Verifikasi login dengan kredensial salah
            testLoginWithInvalidCredentials();

            // Test 4: Verifikasi login dengan kredensial benar
            testLoginWithValidCredentials();

            // Test 5: Verifikasi visibilitas sidebar (chaining test)
            testSidebarVisibility();

            // Test 6: Verifikasi CRUD Company (Chaining Test)
            testVerifikasiCRUDCompany();

            // Test 7: Verifikasi Upload Logo Company
            testUploadCompanyLogo();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST SELESAI");
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
     * Test ini menggunakan sesi dashboard aktif dari Test 4 dan memverifikasi menu sidebar secara berantai.
     */
    public static void testSidebarVisibility() {
        BasePage.printTestHeader("Test 5: Verifikasi Visibilitas Sidebar (Chaining Test)");

        try {
            // Cek apakah sudah berada di dashboard (sesi dari Test 4)
            // Jika tidak, navigate ulang ke baseUrl dan login
            String currentUrl = chrome.getCurrentUrl();
            if (!currentUrl.contains("dashboard") && !currentUrl.contains("web.vedata.id") || currentUrl.contains("auth/realms")) {
                loginPage.navigateToLoginPage(baseUrl);
                loginPage
                    .loginExpectingSuccess("tomi@tester.com", "1234")
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
            } else {
                // Sudah di dashboard — gunakan sesi aktif, langsung verifikasi sidebar via chaining
                new org.test.pages.DashboardPage(chrome)
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
            }

        } catch (Exception e) {
            BasePage.printFail("Verifikasi visibilitas sidebar", e.getMessage());
        }

        System.out.println();
    }

    /**
     * Test 6: Verifikasi CRUD Company menggunakan method chaining (fluent interface).
     *
     * Skenario:
     *   1. [READ]   Navigasi ke Company Settings dan verifikasi card detail tampil.
     *   2. [UPDATE] Klik Edit, isi form dengan data baru, simpan, dan verifikasi perubahan.
     *   3. [RESTORE] Klik Edit lagi, kembalikan data ke nilai asli, simpan, dan verifikasi.
     *
     * Data asli (akan dipulihkan setelah test selesai):
     *   Name    : Arinda Mart
     *   Phone   : 081215414685
     *   Email   : tomianggriawan@gmail.com
     *   Address : Jl Cempaka No 15 Gondokusuman
     */
    public static void testVerifikasiCRUDCompany() {
        BasePage.printTestHeader("Test 6: Verifikasi CRUD Company (Chaining Test)");

        try {
            new DashboardPage(chrome)
                // --- READ: Navigasi & verifikasi tampilan ---
                .navigateToCompanyPage()
                .verifyCompanyDetailsDisplayed()

                // --- UPDATE: Edit dengan data baru ---
                .clickEditCompany()
                .verifyEditModalOpened()
                .fillCompanyDetails(
                    "Arinda Mart Updated",
                    "08987654321",
                    "arindamart-upd@gmail.com",
                    "Jl Cempaka No 20, Yogyakarta"
                )
                .clickSave()
                .verifyProfileDetails(
                    "Arinda Mart Updated",
                    "08987654321",
                    "arindamart-upd@gmail.com",
                    "Jl Cempaka No 20, Yogyakarta"
                )

                // --- RESTORE: Kembalikan data ke nilai asli ---
                .clickEditCompany()
                .verifyEditModalOpened()
                .fillCompanyDetails(
                    "Arinda Mart",
                    "081215414685",
                    "tomianggriawan@gmail.com",
                    "Jl Cempaka No 15 Gondokusuman"
                )
                .clickSave()
                .verifyProfileDetails(
                    "Arinda Mart",
                    "081215414685",
                    "tomianggriawan@gmail.com",
                    "Jl Cempaka No 15 Gondokusuman"
                );

        } catch (Exception e) {
            BasePage.printFail("Verifikasi CRUD Company", e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * Test 7: Verifikasi Upload Logo Perusahaan
     *
     * Skenario (TANPA membuka modal Edit Company):
     *   1. Navigasi ke halaman Company Settings.
     *   2. Klik area "Click to upload" pada logo-container di card utama.
     *   3. Pilih file logo (sendKeys ke hidden file input).
     *   4. Klik tombol "Upload" yang muncul setelah file dipilih.
     *   5. Verifikasi logo tampil di logo-container pada card Company Details.
     *   6. Jika logo tidak tampil → test FAIL.
     */
    public static void testUploadCompanyLogo() {
        BasePage.printTestHeader("Test 7: Verifikasi Upload Logo Company");

        try {
            // Path file logo dari folder resources
            String logoPath = new java.io.File("src/test/resources/arinda_mart_logo.png").getAbsolutePath();
            System.out.println("  [DEBUG] Logo path: " + logoPath);

            new DashboardPage(chrome)
                .navigateToCompanyPage()
                // Langkah 1: Klik area upload & pilih file (tanpa modal)
                .uploadLogoOnCard(logoPath)
                // Langkah 2: Klik tombol "Upload" yang muncul
                .clickUploadButton()
                // Langkah 3: Verifikasi logo tampil di card
                .verifyLogoDisplayedOnCard();

        } catch (Exception e) {
            BasePage.printFail("Verifikasi Upload Logo Company", e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }
}
