package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.pages.settingpage.AccessRightsPage;
import org.pages.DashboardPage;
import org.pages.LoginPage;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * AccessRights - Test Runner untuk HCM > Setting > Access Rights (Role Menu).
 * Menggunakan POM + fluent interface (method chaining).
 * Dilengkapi NetworkEventAnalyzer untuk intersepsi error HTTP 4xx/5xx & JS fatal errors.
 */
public class AccessRights extends BasePage {

    private static final TestReportManager reporter = new TestReportManager();
    private final AccessRightsPage page;

    /**
     * Constructor untuk inisialisasi driver dan objek halaman AccessRightsPage.
     */
    public AccessRights(WebDriver driver) {
        super(driver);
        this.page = new AccessRightsPage(driver);
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

        // Aturan Penamaan File Dinamis: AccessRights-test-report_yyyyMMdd-HHmmss.html
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        String reportFileName = "AccessRights-test-report_" + timestamp + ".html";
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
                    java.nio.file.Path.of("c:/Users/LENOVO/vedata-test/page_source_error.html"),
                    chrome.getPageSource()
                );
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            reporter.generateHtmlReport(reportPath);
            chrome.quit();
        }
    }

    public AccessRights testAccessRightsPageLoaded() {
        reporter.startTest("TC_ACCESS_RIGHTS_PAGE_LOADED", "Verifikasi Halaman Access Rights Tampil");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi halaman Access Rights berhasil dimuat...");
            page.verifyPageLoaded();
            // Inspeksi log setelah navigasi halaman
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail("[NETWORK ANALYSIS] Error saat memuat halaman Access Rights", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }
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

    public AccessRights testAccessRightsPageTitle() {
        reporter.startTest("TC_ACCESS_RIGHTS_PAGE_TITLE", "Verifikasi Judul Halaman Access Rights");
        NetworkEventAnalyzer.drainLogs(driver);
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

    public AccessRights testAccessRightsTableColumns() {
        reporter.startTest("TC_ACCESS_RIGHTS_TABLE_COLUMNS", "Verifikasi Kolom Tabel (Roles/Menu List, SUPER ADMIN, STAFF, ADMIN)");
        NetworkEventAnalyzer.drainLogs(driver);
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

    public AccessRights testAccessRightsModuleDropdown() {
        reporter.startTest("TC_ACCESS_RIGHTS_MODULE_DROPDOWN", "Verifikasi Dropdown Filter Module Tampil");
        NetworkEventAnalyzer.drainLogs(driver);
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

    public AccessRights testAccessRightsCheckboxes() {
        reporter.startTest("TC_ACCESS_RIGHTS_CHECKBOXES", "Verifikasi Checkbox Permission Tersedia");
        NetworkEventAnalyzer.drainLogs(driver);
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

    public AccessRights testAccessRightsSettingMenuInTable() {
        reporter.startTest("TC_ACCESS_RIGHTS_SETTING_MENU", "Verifikasi Baris 'Setting' Ada di Menu List");
        NetworkEventAnalyzer.drainLogs(driver);
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

    public AccessRights testAccessRightsDefaultModuleFilter() {
        reporter.startTest("TC_ACCESS_RIGHTS_DEFAULT_FILTER", "Verifikasi Filter Module Default adalah 'HCM'");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi bahwa filter module default adalah 'HCM'...");
            page.verifyDefaultModuleFilter();
            // Inspeksi log akhir setelah semua interaksi halaman
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail("[NETWORK ANALYSIS] Error terdeteksi pada halaman Access Rights", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }
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
