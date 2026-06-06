package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.pages.DashboardPage;
import org.pages.LoginPage;
import org.pages.settingpage.ServicePage;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * Service - Test Runner untuk HCM > Setting > Service.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class Service extends BasePage {

    private static final TestReportManager reporter = new TestReportManager();
    private final ServicePage page;

    /**
     * Constructor untuk inisialisasi driver dan objek halaman ServicePage.
     * State driver dipertahaman agar tidak hilang saat method chaining.
     */
    public Service(WebDriver driver) {
        super(driver);
        this.page = new ServicePage(driver);
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

        // Aturan Penamaan File Dinamis: Service-test-report_yyyyMMdd-HHmmss.html
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        String reportFileName = "Service-test-report_" + timestamp + ".html";
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
            new DashboardPage(chrome).navigateToServicePage();

            // Memulai method chaining / fluent test execution
            new Service(chrome)
                .testServicePageLoaded()
                .testServicePageTitle()
                .testServiceSubscribedSection()
                .testServiceAvailableSection()
                .testServiceProductTab()
                .testServiceBillingTab()
                .testServiceSearchInput()
                .testServiceRequestButton();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST SERVICE SELESAI");
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

    public Service testServicePageLoaded() {
        reporter.startTest("TC_SERVICE_PAGE_LOADED", "Verifikasi Halaman Service Tampil");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi halaman Service berhasil dimuat...");
            page.verifyPageLoaded();
            // Inspeksi log setelah navigasi halaman pertama
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail("[NETWORK ANALYSIS] Error saat memuat halaman Service", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }
            reporter.logPass("Halaman Service berhasil ditampilkan.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Halaman Service gagal dimuat: " + e.getMessage(), e);
            printFail("Halaman Service tampil", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServicePageTitle() {
        reporter.startTest("TC_SERVICE_PAGE_TITLE", "Verifikasi Judul Halaman Service");
        try {
            reporter.logStep("Verifikasi judul halaman Service sesuai...");
            page.verifyPageTitle();
            reporter.logPass("Judul halaman Service sesuai.");
        } catch (Exception e) {
            reporter.logFail("Judul halaman Service tidak sesuai: " + e.getMessage(), e);
            printFail("Judul halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceSubscribedSection() {
        reporter.startTest("TC_SERVICE_SUBSCRIBED_SECTION", "Verifikasi Section 'Produk yang sedang dipakai / subscribe'");
        try {
            reporter.logStep("Verifikasi section produk yang sedang dilanggani tersedia...");
            page.verifySubscribedSectionDisplayed();
            reporter.logPass("Section 'Produk yang sedang dipakai' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Section 'Produk yang sedang dipakai' tidak tampil: " + e.getMessage(), e);
            printFail("Section 'Produk yang sedang dipakai'", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceAvailableSection() {
        reporter.startTest("TC_SERVICE_AVAILABLE_SECTION", "Verifikasi Section 'Produk yang tersedia'");
        try {
            reporter.logStep("Verifikasi section produk yang tersedia ditampilkan...");
            page.verifyAvailableSectionDisplayed();
            reporter.logPass("Section 'Produk yang tersedia' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Section 'Produk yang tersedia' tidak tampil: " + e.getMessage(), e);
            printFail("Section 'Produk yang tersedia'", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceProductTab() {
        reporter.startTest("TC_SERVICE_PRODUCT_TAB", "Verifikasi Tab 'Product' Tampil");
        try {
            reporter.logStep("Verifikasi tab 'Product' tersedia di halaman Service...");
            page.verifyProductTabDisplayed();
            reporter.logPass("Tab 'Product' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Tab Product tidak tampil: " + e.getMessage(), e);
            printFail("Tab Product di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceBillingTab() {
        reporter.startTest("TC_SERVICE_BILLING_TAB", "Verifikasi Tab 'Billing' Tampil");
        try {
            reporter.logStep("Verifikasi tab 'Billing' tersedia di halaman Service...");
            page.verifyBillingTabDisplayed();
            reporter.logPass("Tab 'Billing' berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Tab Billing tidak tampil: " + e.getMessage(), e);
            printFail("Tab Billing di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceSearchInput() {
        reporter.startTest("TC_SERVICE_SEARCH_INPUT", "Verifikasi Input Pencarian Layanan Tampil");
        try {
            reporter.logStep("Verifikasi field input pencarian layanan tersedia...");
            page.verifySearchInputDisplayed();
            reporter.logPass("Input pencarian layanan berhasil ditampilkan.");
        } catch (Exception e) {
            reporter.logFail("Input pencarian layanan tidak tampil: " + e.getMessage(), e);
            printFail("Input pencarian layanan", e.getMessage());
        }
        System.out.println();
        return this;
    }

    public Service testServiceRequestButton() {
        reporter.startTest("TC_SERVICE_REQUEST_BUTTON", "Verifikasi Tombol 'Request' Tersedia");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi tombol 'Request' tersedia di halaman Service...");
            page.verifyRequestButtonDisplayed();
            // Inspeksi log akhir setelah semua interaksi halaman Service
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail("[NETWORK ANALYSIS] Error terdeteksi pada halaman Service", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }
            reporter.logPass("Tombol 'Request' berhasil ditemukan di halaman Service.");
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            reporter.logFail("Tombol Request tidak tersedia: " + e.getMessage(), e);
            printFail("Tombol Request di halaman Service", e.getMessage());
        }
        System.out.println();
        return this;
    }
}
