package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.pages.DashboardPage;
import org.pages.LoginPage;
import org.pages.UserPage;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * User - Test Runner untuk HCM > Setting > User.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class User extends BasePage {

    private static final TestReportManager reporter = new TestReportManager();
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
        String reportDirPath = "c:/Users/LENOVO/vedata-test/src/main/java/org/test/report";
        java.io.File reportDir = new java.io.File(reportDirPath);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Pembersihan (Replace File):
        // Hapus file laporan lama 'employee-test-report.html' jika ada di folder report
        java.io.File oldReportInDir = new java.io.File(reportDir, "employee-test-report.html");
        if (oldReportInDir.exists()) {
            oldReportInDir.delete();
            System.out.println("  [INFO] Deleted old employee-test-report.html from report directory.");
        }
        // Hapus dari root folder juga jika ada
        java.io.File oldReportInRoot = new java.io.File("c:/Users/LENOVO/vedata-test/employee-test-report.html");
        if (oldReportInRoot.exists()) {
            oldReportInRoot.delete();
            System.out.println("  [INFO] Deleted old employee-test-report.html from root.");
        }

        // Aturan Penamaan File Dinamis: User-test-report_yyyyMMdd-HHmmss.html
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        String reportFileName = "User-test-report_" + timestamp + ".html";
        java.io.File reportFile = new java.io.File(reportDir, reportFileName);
        String reportPath = reportFile.getAbsolutePath();

        try {
            LoginPage loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            // Init reporter suite
            reporter.startSuite();

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Drain log buffer dari proses login
            NetworkEventAnalyzer.drainLogs(chrome);

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
                    java.nio.file.Path.of("c:/Users/LENOVO/vedata-test/page_source_error.html"),
                    chrome.getPageSource()
                );
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            // Generate report
            reporter.generateHtmlReport(reportPath);
            chrome.quit();
        }
    }

    public User testUserPageLoaded() {
        reporter.startTest("TC_USER_PAGE_LOADED", "Verifikasi Halaman User List Tampil");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi halaman User List berhasil dimuat...");
            page.verifyPageLoaded();
            // Inspeksi log setelah navigasi halaman
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail("[NETWORK ANALYSIS] Error saat memuat halaman User List", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }
            reporter.logPass("Halaman User List berhasil ditampilkan.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Halaman User List gagal dimuat: " + e.getMessage(), e);
            printFail("Halaman User List tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserPageTitle() {
        reporter.startTest("TC_USER_PAGE_TITLE", "Verifikasi Judul Halaman User");
        try {
            reporter.logStep("Verifikasi judul halaman User sesuai...");
            page.verifyPageTitle();
            reporter.logPass("Judul halaman User sesuai.");
        } catch (Exception e) {
            reporter.logFail("Judul halaman User tidak sesuai: " + e.getMessage(), e);
            printFail("Judul halaman User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserTableColumns() {
        reporter.startTest("TC_USER_TABLE_COLUMNS", "Verifikasi Kolom Tabel (Action, Username, Full Name, Module Access)");
        try {
            reporter.logStep("Verifikasi kolom-kolom tabel User tersedia...");
            page.verifyTableColumnsDisplayed();
            reporter.logPass("Semua kolom tabel User (Action, Username, Full Name, Module Access) tampil.");
        } catch (Exception e) {
            reporter.logFail("Kolom tabel User tidak lengkap: " + e.getMessage(), e);
            printFail("Kolom tabel User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserAddButtonDisplayed() {
        reporter.startTest("TC_USER_ADD_BUTTON", "Verifikasi Tombol 'Add' Tampil");
        try {
            reporter.logStep("Verifikasi tombol 'Add' tersedia di halaman User...");
            page.verifyAddButtonDisplayed();
            reporter.logPass("Tombol 'Add' berhasil ditemukan di halaman User.");
        } catch (Exception e) {
            reporter.logFail("Tombol Add tidak tampil di halaman User: " + e.getMessage(), e);
            printFail("Tombol Add tampil di User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserTableHasData() {
        reporter.startTest("TC_USER_TABLE_HAS_DATA", "Verifikasi Data di Tabel User");
        try {
            reporter.logStep("Verifikasi tabel User memiliki setidaknya satu baris data...");
            page.verifyTableHasData();
            reporter.logPass("Tabel User memiliki data.");
        } catch (Exception e) {
            reporter.logFail("Tabel User tidak memiliki data: " + e.getMessage(), e);
            printFail("Data di tabel User", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public User testUserExistsInTable(String username) {
        reporter.startTest("TC_USER_EXISTS_IN_TABLE", "Verifikasi User '" + username + "' Ada di Tabel");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Memeriksa keberadaan user '" + username + "' di tabel...");
            page.verifyUsernameInTable(username);
            // Inspeksi log akhir setelah semua verifikasi halaman User
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail("[NETWORK ANALYSIS] Error terdeteksi pada halaman User", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }
            reporter.logPass("User '" + username + "' berhasil ditemukan di tabel.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("User '" + username + "' tidak ditemukan di tabel: " + e.getMessage(), e);
            printFail("User " + username + " di tabel", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
