package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.pages.DashboardPage;
import org.pages.JobTitlePage;
import org.pages.LoginPage;

import java.util.UUID;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * JobTitle - Vanilla Java CRU Test Runner for HCM > Setting > Job Title.
 * Menggunakan POM + fluent interface (method chaining).
 * Dilengkapi NetworkEventAnalyzer untuk intersepsi error HTTP 4xx/5xx & JS fatal errors.
 */
public class JobTitle extends BasePage {

    private static final TestReportManager reporter = new TestReportManager();

    private final JobTitlePage jobTitlePage;

    // Test data — auto-generated unique values per instance
    private final String generatedCode;
    private final String generatedName;
    private final String updatedName;

    /**
     * Constructor untuk inisialisasi driver dan mempersiapkan test data unik.
     */
    public JobTitle(WebDriver driver) {
        super(driver);
        this.jobTitlePage = new JobTitlePage(driver);

        String[] retailTitles = {
            "Store Manager", "Assistant Store Manager", "Cashier", "Sales Associate",
            "Merchandiser", "Stock Clerk", "Customer Service Specialist", "Inventory Associate",
            "Retail Sales Consultant", "Visual Merchandiser"
        };
        java.util.Random rand = new java.util.Random();
        String title = retailTitles[rand.nextInt(retailTitles.length)];

        this.generatedCode = "JT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.generatedName = title + " " + (rand.nextInt(9000) + 1000);
        this.updatedName   = "Senior " + title + " " + (rand.nextInt(9000) + 1000);
    }

    public String getGeneratedCode() {
        return this.generatedCode;
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

        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        String reportFileName = "JobTitle-test-report_" + timestamp + ".html";
        java.io.File reportFile = new java.io.File(reportDir, reportFileName);
        String reportPath = reportFile.getAbsolutePath();

        try {
            LoginPage loginPage = new LoginPage(chrome);
            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            reporter.startSuite();

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Drain log buffer dari proses login
            NetworkEventAnalyzer.drainLogs(chrome);

            new DashboardPage(chrome).navigateToJobTitlePage();

            JobTitle test = new JobTitle(chrome);
            System.out.println("  [DATA] Generated Code : " + test.generatedCode);
            System.out.println("  [DATA] Generated Name : " + test.generatedName);
            System.out.println("  [DATA] Updated Name   : " + test.updatedName);
            System.out.println();

            test.testCreateJobTitle()
                .testReadJobTitle()
                .testUpdateJobTitle();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST CRU SELESAI");
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
                System.err.println("Page source dumped.");
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            reporter.generateHtmlReport(reportPath);
            chrome.quit();
        }
    }

    // ==================== CREATE ====================

    public JobTitle testCreateJobTitle() {
        reporter.startTest("TC_JOB_TITLE_CREATE", "TEST CREATE: Tambah Job Title Baru");
        // Drain sebelum aksi write
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi halaman Job Title List dimuat...");
            jobTitlePage.verifyPageLoaded();

            reporter.logStep("Verifikasi tombol Add tersedia dan klik...");
            jobTitlePage.verifyAddButtonDisplayed();
            jobTitlePage.clickAddButton();

            reporter.logStep("Isi form Code '" + generatedCode + "' dan Name '" + generatedName + "'...");
            jobTitlePage.fillJobTitleForm(generatedCode, generatedName);

            reporter.logStep("Klik tombol Save untuk menyimpan Job Title baru...");
            jobTitlePage.clickSave();

            // Intersepsi error sebelum assertion
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Save...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Kegagalan sistem terdeteksi setelah Save Job Title (" +
                    analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }

            reporter.logStep("Verifikasi halaman list kembali ditampilkan setelah save...");
            jobTitlePage.verifyPageLoaded();

            boolean created = jobTitlePage.isJobTitleExistInTable(generatedCode);
            if (!created) {
                throw new AssertionError(
                    "[CREATE] Gagal: Job Title '" + generatedCode + "' tidak ditemukan di tabel setelah save"
                );
            }
            reporter.logPass("Job Title '" + generatedCode + "' berhasil dibuat dan muncul di tabel.");
        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario CREATE Job Title.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== READ ====================

    public JobTitle testReadJobTitle() {
        reporter.startTest("TC_JOB_TITLE_READ", "TEST READ: Verifikasi Data Job Title di Tabel");
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Verifikasi halaman Job Title List dimuat...");
            jobTitlePage.verifyPageLoaded();

            reporter.logStep("Memeriksa keberadaan Job Title dengan Code '" + generatedCode + "' di tabel...");
            boolean exists = jobTitlePage.isJobTitleExistInTable(generatedCode);
            if (!exists) {
                throw new AssertionError(
                    "[READ] Gagal: Job Title dengan Code '" + generatedCode + "' tidak ditemukan di tabel"
                );
            }

            reporter.logStep("Mengambil data baris untuk Code '" + generatedCode + "'...");
            String[] rowData = jobTitlePage.getJobTitleRowData(generatedCode);
            if (rowData != null) {
                System.out.println("  [DATA] Row -> Action: '" + rowData[0]
                    + "' | Code: '" + rowData[1] + "' | Name: '" + rowData[2] + "'");

                if (!rowData[1].equals(generatedCode)) {
                    throw new AssertionError(
                        "[READ] Code mismatch: expected '" + generatedCode + "', got '" + rowData[1] + "'"
                    );
                }
                if (!rowData[2].equals(generatedName)) {
                    throw new AssertionError(
                        "[READ] Name mismatch: expected '" + generatedName + "', got '" + rowData[2] + "'"
                    );
                }
                reporter.logPass("Data Job Title Code='" + rowData[1] + "', Name='" + rowData[2] + "' terkonfirmasi sesuai.");
            } else {
                throw new AssertionError("[READ] Gagal mengambil data baris untuk code '" + generatedCode + "'");
            }
        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario READ Job Title.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== UPDATE ====================

    public JobTitle testUpdateJobTitle() {
        reporter.startTest("TC_JOB_TITLE_UPDATE", "TEST UPDATE: Edit Nama Job Title");
        // Drain sebelum aksi write
        NetworkEventAnalyzer.drainLogs(driver);
        try {
            reporter.logStep("Klik tombol Edit untuk Code '" + generatedCode + "'...");
            jobTitlePage.clickEditJobTitle(generatedCode);

            reporter.logStep("Update Name menjadi '" + updatedName + "'...");
            jobTitlePage.fillJobTitleForm(null, updatedName);

            reporter.logStep("Klik Save untuk menyimpan perubahan...");
            jobTitlePage.clickSave();

            // Intersepsi error sebelum assertion
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Update Save...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Kegagalan sistem terdeteksi setelah Update Job Title (" +
                    analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK ANALYSIS FAIL] " + analysis.buildSummary());
            }

            reporter.logStep("Verifikasi halaman list setelah update...");
            jobTitlePage.verifyPageLoaded();

            String[] rowData = jobTitlePage.getJobTitleRowData(generatedCode);
            if (rowData != null) {
                System.out.println("  [DATA] After Update -> Code: '" + rowData[1]
                    + "' | Name: '" + rowData[2] + "'");

                if (!rowData[1].equals(generatedCode)) {
                    throw new AssertionError(
                        "[UPDATE] Code berubah: expected '" + generatedCode + "', got '" + rowData[1] + "'"
                    );
                }
                if (!rowData[2].equals(updatedName)) {
                    throw new AssertionError(
                        "[UPDATE] Name tidak berubah: expected '" + updatedName + "', got '" + rowData[2] + "'"
                    );
                }
                reporter.logPass("Job Title berhasil diupdate: Code='" + generatedCode + "', Name='" + updatedName + "'.");
            } else {
                throw new AssertionError("[UPDATE] Gagal mengambil data baris setelah update");
            }
        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario UPDATE Job Title.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }
}
