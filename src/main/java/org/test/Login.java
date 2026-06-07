package org.test;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.pages.LoginPage;
import org.pages.DashboardPage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * Login - Test Runner untuk memverifikasi proses Login pada VEDATA.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer)
 *  - POM via LoginPage
 *  - Fluent interface (method chaining)
 *
 * Skenario Uji Lengkap Login:
 *  TC_LOGIN_UI_ELEMENTS         - Verifikasi Elemen UI Halaman Login
 *  TC_LOGIN_EMPTY_FIELDS        - Uji Validasi Halaman Login saat Field Kosong
 *  TC_LOGIN_INVALID_CREDENTIALS - Uji Login dengan Username/Password yang Salah
 *  TC_LOGIN_SUCCESS             - Uji Login Sukses dengan Kredensial Valid
 */
public class Login extends BasePage {

    private final LoginPage loginPage;

    // Kredensial login valid
    private static final String VALID_USERNAME = "tomi@tester.com";
    private static final String VALID_PASSWORD = "1234";

    public Login(WebDriver driver) {
        super(driver);
        this.loginPage = new LoginPage(driver);
    }

    /**
     * Custom runTest untuk Login agar tidak otomatis melakukan login di awal suite.
     */
    public static void runLoginSuite(String suiteName, String reportPrefix, TestRunner testRunner) {
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String reportFileName = reportPrefix + "-test-report_" + timestamp + ".html";
        File reportFile = new File(reportDir, reportFileName);
        String reportPath = reportFile.getAbsolutePath();

        try {
            chrome.manage().window().maximize();
            chrome.get(baseUrl);
            Thread.sleep(3000);

            reporter.startSuite();
            System.out.println("=================================================");
            System.out.println("[SUITE START] " + suiteName + " Test Suite");
            System.out.println("=================================================\n");

            // Drain logs di awal
            NetworkEventAnalyzer.drainLogs(chrome);

            // Jalankan skenario uji login
            testRunner.run();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST " + suiteName.toUpperCase() + " SELESAI");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ERROR !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                System.err.println("URL saat error: " + chrome.getCurrentUrl());
                Files.writeString(
                    Path.of("c:/Users/LENOVO/vedata-test/" + reportPrefix.toLowerCase() + "_page_source_error.html"),
                    chrome.getPageSource()
                );
                System.err.println("Page source dumped ke " + reportPrefix.toLowerCase() + "_page_source_error.html");
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            reporter.generateHtmlReport(reportPath);
            System.out.println("\n  [REPORT] Laporan HTML disimpan di: " + reportPath);
            chrome.quit();
        }
    }

    public static void main(String[] args) {
        runLoginSuite("VEDATA Login Verification", "Login", () -> {
            new Login(chrome)
                .testLoginUIElements()
                .testLoginEmptyFields()
                .testLoginInvalidCredentials()
                .testLoginSuccess();
        });
    }

    // ==================== TEST METHODS ====================

    /**
     * TC_LOGIN_UI_ELEMENTS - Verifikasi Elemen UI Halaman Login.
     */
    public Login testLoginUIElements() {
        reporter.startTest("TC_LOGIN_UI_ELEMENTS", "Verifikasi Elemen UI Halaman Login");
        drainLogs();
        try {
            reporter.logStep("Navigasi ke URL Login...");
            loginPage.navigateToLoginPage(baseUrl);
            sleep(3000);

            reporter.logStep("Verifikasi Page Title...");
            assertCondition("Title valid (mengandung 'vedata' atau 'sign in')", loginPage.isTitleValid());

            reporter.logStep("Verifikasi Logo Keycloak/Vedata...");
            assertCondition("Logo ditampilkan", loginPage.isLogoDisplayed());

            reporter.logStep("Verifikasi label field input...");
            assertCondition("Label Username ditampilkan", loginPage.isUsernameLabelDisplayed());
            assertCondition("Label Password ditampilkan", loginPage.isPasswordLabelDisplayed());

            reporter.logStep("Verifikasi placeholder/input field...");
            assertCondition("Input field Username ditampilkan", loginPage.isUsernamePlaceholderDisplayed());
            assertCondition("Input field Password ditampilkan", loginPage.isPasswordPlaceholderDisplayed());

            reporter.logStep("Verifikasi Tombol Sign In...");
            assertCondition("Tombol Sign In ditampilkan", loginPage.isLoginButtonDisplayed());

            inspectNetwork("verifikasi UI Login");
            reporter.logPass("Elemen UI Halaman Login terverifikasi dengan sukses.");
        } catch (Throwable e) {
            captureNetworkOnFail("UI_ELEMENTS");
            reporter.logFail("Gagal verifikasi elemen UI Halaman Login.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_LOGIN_EMPTY_FIELDS - Uji Validasi Halaman Login saat Field Kosong.
     */
    public Login testLoginEmptyFields() {
        reporter.startTest("TC_LOGIN_EMPTY_FIELDS", "Uji Validasi Halaman Login saat Field Kosong");
        drainLogs();
        try {
            reporter.logStep("Navigasi ke halaman login & pastikan input kosong...");
            loginPage.navigateToLoginPage(baseUrl);
            sleep(2000);
            loginPage.enterUsername("");
            loginPage.enterPassword("");

            reporter.logStep("Klik Tombol Sign In...");
            loginPage.clickLogin();
            sleep(2000);

            reporter.logStep("Verifikasi munculnya pesan kesalahan field wajib diisi...");
            boolean errorUsername = loginPage.isEmailRequiredMessageDisplayed();
            boolean errorPassword = loginPage.isPasswordRequiredMessageDisplayed();
            assertCondition("Pesan kesalahan required field muncul", errorUsername || errorPassword);

            inspectNetwork("verifikasi validasi form kosong");
            reporter.logPass("Pesan kesalahan required field berhasil ditampilkan.");
        } catch (Throwable e) {
            captureNetworkOnFail("EMPTY_FIELDS");
            reporter.logFail("Pesan kesalahan required field tidak muncul.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_LOGIN_INVALID_CREDENTIALS - Uji Login dengan Username/Password yang Salah.
     */
    public Login testLoginInvalidCredentials() {
        reporter.startTest("TC_LOGIN_INVALID_CREDENTIALS", "Uji Login dengan Username/Password yang Salah");
        drainLogs();
        try {
            reporter.logStep("Navigasi ke halaman login...");
            loginPage.navigateToLoginPage(baseUrl);
            sleep(2000);

            reporter.logStep("Masukkan username salah & password salah, lalu klik login...");
            loginPage.loginExpectingFailure("wrong_user@tester.com", "wrong_pass");
            sleep(3000);

            reporter.logStep("Verifikasi bahwa pengguna tetap berada di halaman login...");
            assertCondition("Masih berada di Halaman Login", loginPage.isStillOnLoginPage());

            reporter.logStep("Verifikasi pesan kesalahan/validation error ditampilkan...");
            boolean hasError = loginPage.isEmailRequiredMessageDisplayed() || loginPage.isPasswordRequiredMessageDisplayed() || 
                               driver.getPageSource().contains("Invalid username or password") || 
                               driver.getPageSource().contains("Kredensial salah") ||
                               driver.getPageSource().contains("invalid");
            assertCondition("Pesan error kredensial tidak valid terdeteksi", hasError);

            inspectNetwork("verifikasi login kredensial tidak valid");
            reporter.logPass("Sistem berhasil mencegah login dengan kredensial tidak valid.");
        } catch (Throwable e) {
            captureNetworkOnFail("INVALID_CREDENTIALS");
            reporter.logFail("Gagal verifikasi login kredensial tidak valid.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_LOGIN_SUCCESS - Uji Login Sukses dengan Kredensial Valid.
     */
    public Login testLoginSuccess() {
        reporter.startTest("TC_LOGIN_SUCCESS", "Uji Login Sukses dengan Kredensial Valid");
        drainLogs();
        try {
            reporter.logStep("Navigasi ke halaman login...");
            loginPage.navigateToLoginPage(baseUrl);
            sleep(2000);

            reporter.logStep("Masukkan username='" + VALID_USERNAME + "' dan password, lalu klik login...");
            DashboardPage dashboard = loginPage.loginExpectingSuccess(VALID_USERNAME, VALID_PASSWORD);
            sleep(5000);

            reporter.logStep("Verifikasi URL telah diarahkan ke Dashboard...");
            assertCondition("URL mengandung 'dashboard' or redirect sukses", loginPage.isUrlContainingDashboard() || dashboard.isDashboardUrl());

            reporter.logStep("Verifikasi Sidebar utama ditampilkan di Dashboard...");
            dashboard.verifySidebarDisplayed();

            inspectNetwork("verifikasi login sukses");
            reporter.logPass("Login berhasil dan Dashboard berhasil dimuat.");
        } catch (Throwable e) {
            captureNetworkOnFail("LOGIN_SUCCESS");
            reporter.logFail("Gagal masuk ke Dashboard dengan kredensial valid.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }
}
