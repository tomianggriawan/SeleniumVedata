package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.pages.DashboardPage;
import org.pages.settingpage.CompanyPage;
import org.pages.LoginPage;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * Company - Test Runner untuk HCM > Setting > Company.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class Company extends BasePage {

    private static final TestReportManager reporter = new TestReportManager();

    /**
     * Constructor untuk inisialisasi driver.
     * State driver dipertahankan agar tidak hilang saat method chaining.
     */
    public Company(WebDriver driver) {
        super(driver);
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

        // Aturan Penamaan File Dinamis: Company-test-report_yyyyMMdd-HHmmss.html
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        String reportFileName = "Company-test-report_" + timestamp + ".html";
        java.io.File reportFile = new java.io.File(reportDir, reportFileName);
        String reportPath = reportFile.getAbsolutePath();

        try {
            LoginPage loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            // Init reporter suite
            reporter.startSuite();

            // Login dan dapatkan sesi aktif
            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Drain log buffer dari proses login
            NetworkEventAnalyzer.drainLogs(chrome);

            // Jalankan pengujian Company secara berantai
            new Company(chrome)
                .testVerifikasiCRUDCompany()
                .testUploadCompanyLogo();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST COMPANY SELESAI");
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

    /**
     * Verifikasi CRUD Company menggunakan method chaining (fluent interface).
     *
     * Skenario:
     *   1. [READ]   Navigasi ke Company Settings dan verifikasi card detail tampil.
     *   2. [UPDATE] Klik Edit, isi form dengan data baru, simpan, dan verifikasi perubahan.
     *   3. [RESTORE] Klik Edit lagi, kembalikan data ke nilai asli, simpan, dan verifikasi.
     */
    public Company testVerifikasiCRUDCompany() {
        reporter.startTest("TC_HCM_COMPANY_CRUD", "Verifikasi CRUD Perusahaan (Read, Update, Restore)");
        // Drain log buffer sebelum aksi write
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Membuka halaman Dashboard & Navigasi ke Halaman Company Settings...");
            DashboardPage dashboard = new DashboardPage(driver);
            dashboard.navigateToCompanyPage();

            reporter.logStep("Verifikasi detail profil perusahaan awal ditampilkan...");
            CompanyPage companyPage = new CompanyPage(driver);
            companyPage.verifyCompanyDetailsDisplayed();

            reporter.logStep("Klik tombol Edit Company untuk membuka form modal...");
            companyPage.clickEditCompany();
            companyPage.verifyEditModalOpened();

            reporter.logStep("Mengisi detail perusahaan baru (Update)...");
            companyPage.fillCompanyDetails(
                "Arinda Mart Updated",
                "08987654321",
                "arindamart-upd@gmail.com",
                "Jl Cempaka No 20, Yogyakarta"
            );

            reporter.logStep("Menyimpan detail perusahaan baru (Save)...");
            companyPage.clickSave();

            // Intersepsi error setelah Update Save
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Update Save...");
            NetworkEventAnalyzer.AnalysisResult updateAnalysis = NetworkEventAnalyzer.analyze(driver);
            if (updateAnalysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Kegagalan sistem setelah Update Company (" +
                    updateAnalysis.getErrors().size() + " error)", updateAnalysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + updateAnalysis.buildSummary());
            }

            reporter.logStep("Verifikasi detail profil perusahaan setelah diperbarui...");
            companyPage.verifyProfileDetails(
                "Arinda Mart Updated",
                "08987654321",
                "arindamart-upd@gmail.com",
                "Jl Cempaka No 20, Yogyakarta"
            );

            // Drain sebelum Restore Save
            NetworkEventAnalyzer.drainLogs(driver);

            reporter.logStep("Klik tombol Edit Company lagi untuk mengembalikan data (Restore)...");
            companyPage.clickEditCompany();
            companyPage.verifyEditModalOpened();

            reporter.logStep("Mengisi detail perusahaan asli...");
            companyPage.fillCompanyDetails(
                "Arinda Mart",
                "081215414685",
                "tomianggriawan@gmail.com",
                "Jl Cempaka No 15 Gondokusuman"
            );

            reporter.logStep("Menyimpan detail perusahaan asli (Restore Save)...");
            companyPage.clickSave();

            // Intersepsi error setelah Restore Save
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Restore Save...");
            NetworkEventAnalyzer.AnalysisResult restoreAnalysis = NetworkEventAnalyzer.analyze(driver);
            if (restoreAnalysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Kegagalan sistem setelah Restore Company (" +
                    restoreAnalysis.getErrors().size() + " error)", restoreAnalysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + restoreAnalysis.buildSummary());
            }

            reporter.logStep("Verifikasi detail profil perusahaan setelah dikembalikan...");
            companyPage.verifyProfileDetails(
                "Arinda Mart",
                "081215414685",
                "tomianggriawan@gmail.com",
                "Jl Cempaka No 15 Gondokusuman"
            );

            reporter.logPass("Skenario CRUD Company berhasil diselesaikan.");
        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario CRUD Company.", e);
            throw new AssertionError(e);
        }

        System.out.println();
        return this;
    }

    /**
     * Verifikasi Upload Logo Perusahaan
     *
     * Skenario (TANPA membuka modal Edit Company):
     *   1. Navigasi ke halaman Company Settings.
     *   2. Klik area "Click to upload" pada logo-container di card utama.
     *   3. Pilih file logo (sendKeys ke hidden file input).
     *   4. Klik tombol "Upload" yang muncul setelah file dipilih.
     *   5. Verifikasi logo tampil di logo-container pada card Company Details.
     */
    public Company testUploadCompanyLogo() {
        reporter.startTest("TC_HCM_COMPANY_LOGO", "Verifikasi Upload Logo Perusahaan");
        try {
            // Pastikan direktori dan file logo tersedia sebelum pengujian
            java.io.File logoFile = new java.io.File("src/test/resources/arinda_mart_logo.png");
            if (!logoFile.exists()) {
                logoFile.getParentFile().mkdirs();
                // Buat gambar 1x1 sederhana secara programatis
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                javax.imageio.ImageIO.write(img, "png", logoFile);
                System.out.println("  [DEBUG] Gambar placeholder berhasil dibuat di: " + logoFile.getAbsolutePath());
            }

            // Path file logo dari folder resources
            String logoPath = logoFile.getAbsolutePath();
            System.out.println("  [DEBUG] Logo path: " + logoPath);

            reporter.logStep("Navigasi ke halaman Company Settings...");
            DashboardPage dashboard = new DashboardPage(driver);
            dashboard.navigateToCompanyPage();

            reporter.logStep("Klik area upload logo dan pilih file gambar...");
            CompanyPage companyPage = new CompanyPage(driver);
            companyPage.uploadLogoOnCard(logoPath);

            reporter.logStep("Klik tombol Upload yang muncul...");
            companyPage.clickUploadButton();

            reporter.logStep("Verifikasi logo ditampilkan pada card utama...");
            companyPage.verifyLogoDisplayedOnCard();

            reporter.logPass("Skenario Upload Logo Company berhasil diselesaikan.");
        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario Upload Logo Company.", e);
            throw new AssertionError(e);
        }

        System.out.println();
        return this;
    }
}
