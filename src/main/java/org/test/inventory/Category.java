package org.test.inventory;

import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.openqa.selenium.WebDriver;
import org.pages.LoginPage;
import org.pages.inventorypage.CategoryPage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * Category - Test Runner CRUD untuk Kategori di Inventory > Setting > Category.
 *
 * Arsitektur mengikuti standar proyek:
 *  - Mewarisi BasePage untuk akses WebDriver helper
 *  - POM via CategoryPage (org.pages.inventorypage.CategoryPage)
 *  - Fluent interface / method chaining
 *  - TestReportManager untuk laporan HTML per test case
 *  - NetworkEventAnalyzer (CDP) untuk intersepsi error HTTP 4xx/5xx dan JS fatal
 *
 * Generator Nama Dinamis:
 *  Setiap run memilih entry acak dari RETAIL_CATALOG lalu menambahkan angka acak
 *  4 digit untuk memastikan keunikan.
 *
 * Skenario:
 *  TC_INV_CATEGORY_CREATE  - Tambah kategori baru dengan data hasil generator (mengabaikan field Parent)
 *  TC_INV_CATEGORY_READ    - Verifikasi nama kategori ada di tabel
 *  TC_INV_CATEGORY_UPDATE  - Edit nama kategori dengan suffix "U"
 *  TC_INV_CATEGORY_DELETE  - Hapus kategori dan verifikasi hilang dari tabel
 */
public class Category extends BasePage {

    // ==================== Konstanta ====================

    private static final String CATEGORY_TAB_URL = "https://web.vedata.id/inventory/setting?tab=category";
    private static final String REPORT_DIR   = "c:/Users/LENOVO/vedata-test/src/main/java/org/test/report";
    private static final TestReportManager reporter = new TestReportManager();

    private final CategoryPage categoryPage;

    /** Nama kategori unik hasil generator, misal: "Electronics_4817". Diinput ke form. */
    private final String generatedName;

    /** Nama setelah diupdate (suffix "U"), misal: "Electronics_4817U". */
    private final String updatedName;

    // ==================== Retail Category Catalog ====================

    /**
     * Kategori produk retail yang realistis (tanpa sub-kategori/child).
     * Dipilih secara acak tiap run untuk variasi data pengujian.
     */
    private static final String[] RETAIL_CATALOG = {
        "Electronics",
        "Clothing",
        "Food",
        "Beauty",
        "Sports",
        "Home",
        "Stationery",
        "Toys",
        "Automotive",
        "Healthcare",
        "Groceries",
        "Beverages",
        "Furniture",
        "Snacks"
    };

    // ==================== Constructor ====================

    public Category(WebDriver driver) {
        super(driver);
        this.categoryPage = new CategoryPage(driver);

        // Pilih kategori retail acak
        int idx = new Random().nextInt(RETAIL_CATALOG.length);
        String category = RETAIL_CATALOG[idx];

        // Suffix = 4 digit acak untuk keunikan tiap run
        String suffix = String.valueOf(1000 + new Random().nextInt(9000));

        // Format: "Electronics_4817"
        this.generatedName = category + "_" + suffix;
        this.updatedName   = this.generatedName + "U";
    }

    // ==================== Entry Point ====================

    public static void main(String[] args) throws InterruptedException {

        // Pastikan folder report tersedia
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Nama laporan dengan timestamp agar tidak overwrite antar run
        String timestamp      = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String reportFileName = "Category-test-report_" + timestamp + ".html";
        File   reportFile     = new File(reportDir, reportFileName);
        String reportPath     = reportFile.getAbsolutePath();

        try {
            // Setup browser & login
            chrome.manage().window().maximize();
            chrome.get(baseUrl);
            Thread.sleep(3000);

            reporter.startSuite();
            System.out.println("=================================================");
            System.out.println("[SUITE START] Inventory Category CRUD Test Suite");
            System.out.println("=================================================\n");

            // Login
            System.out.println("  [INFO] Login sebagai tomi@tester.com ...");
            new LoginPage(chrome).login("tomi@tester.com", "1234");
            Thread.sleep(5000);

            // Bersihkan buffer log sesi login sebelum pengujian dimulai
            NetworkEventAnalyzer.drainLogs(chrome);

            // Navigasi ke tab Category
            System.out.println("  [INFO] Navigasi ke Inventory Setting > Category Tab ...");
            chrome.get(CATEGORY_TAB_URL);
            Thread.sleep(4000);

            // Inisialisasi test instance
            Category test = new Category(chrome);
            System.out.println("  [DATA] -----------------------------------------");
            System.out.println("  [DATA] Category Name (input) : " + test.generatedName);
            System.out.println("  [DATA] Updated Name          : " + test.updatedName);
            System.out.println("  [DATA] -----------------------------------------");
            System.out.println();

            // Jalankan skenario CRUD berantai
            test.testCreateCategory()
                .testReadCategory()
                .testUpdateCategory()
                .testDeleteCategory();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST CRUD CATEGORY SELESAI");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ERROR !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                System.err.println("URL saat error: " + chrome.getCurrentUrl());
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("c:/Users/LENOVO/vedata-test/category_page_source_error.html"),
                    chrome.getPageSource()
                );
                System.err.println("Page source dumped ke category_page_source_error.html");
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

    // ==================== CREATE ====================

    /**
     * TC_INV_CATEGORY_CREATE - Tambah kategori baru (mengabaikan field Parent).
     */
    public Category testCreateCategory() {
        reporter.startTest("TC_INV_CATEGORY_CREATE", "CREATE: Tambah Kategori Baru '" + generatedName + "'");

        // Bersihkan buffer log CDP sebelum aksi write
        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Navigasi ke tab Category ...");
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3000);

            reporter.logStep("Mencari dan mengklik tombol Add ...");
            categoryPage.clickAddButtonJS();
            Thread.sleep(2000);

            reporter.logStep("Navigasi ke halaman Add Category pada url https://web.vedata.id/inventory/setting/category/form ...");
            driver.get("https://web.vedata.id/inventory/setting/category/form");
            Thread.sleep(2000);

            categoryPage.waitForFormInputReady();

            reporter.logStep("1. Abaikan field Parent.");
            try {
                org.openqa.selenium.WebElement parentInput = driver.findElement(org.openqa.selenium.By.id("category-parent"));
                if (parentInput.isDisplayed()) {
                    System.out.println("  [Category] Element 'category-parent' terdeteksi. Sesuai instruksi, field Parent diabaikan.");
                }
            } catch (Exception e) {
                System.out.println("  [Category] Warning: Element 'category-parent' tidak terdeteksi: " + e.getMessage());
            }

            reporter.logStep("2. Mengisi field Name dengan nama Category Retail: '" + generatedName + "' ...");
            categoryPage.fillNameOnForm(generatedName);
            Thread.sleep(1000);

            reporter.logStep("3. Klik Save ...");
            categoryPage.clickSaveOnForm();

            categoryPage.waitForReturnToList();
            Thread.sleep(2000);

            // Verifikasi di UI
            reporter.logStep("Verifikasi kategori '" + generatedName + "' muncul di tabel ...");
            boolean created = categoryPage.isCategoryInTable(generatedName);

            // Intersepsi error jaringan setelah aksi
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Save Category ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah Create Category ("
                    + analysis.getErrors().size() + " error terdeteksi)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            if (!created) {
                throw new AssertionError(
                    "[CREATE] Kategori '" + generatedName + "' tidak ditemukan di tabel setelah Save.");
            }

            reporter.logPass("Kategori '" + generatedName + "' berhasil dibuat dan terverifikasi di tabel.");

        } catch (Throwable e) {
            captureNetworkOnFail("CREATE");
            reporter.logFail("Gagal pada skenario CREATE Kategori.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== READ ====================

    /**
     * TC_INV_CATEGORY_READ - Verifikasi kategori yang dibuat ada di tabel list.
     */
    public Category testReadCategory() {
        reporter.startTest("TC_INV_CATEGORY_READ", "READ: Verifikasi Kategori '" + generatedName + "' di Tabel");

        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Reload tab Category dan cari '" + generatedName + "' di tabel ...");
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3000);

            boolean exists = categoryPage.isCategoryInTable(generatedName);

            // Intersepsi error jaringan setelah aksi
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Error jaringan terdeteksi saat READ Category ("
                    + analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            if (!exists) {
                throw new AssertionError(
                    "[READ] Kategori '" + generatedName + "' tidak ditemukan di tabel.");
            }

            reporter.logPass("Kategori '" + generatedName + "' terkonfirmasi ada di tabel (READ OK).");

        } catch (Throwable e) {
            captureNetworkOnFail("READ");
            reporter.logFail("Gagal pada skenario READ Kategori.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== UPDATE ====================

    /**
     * TC_INV_CATEGORY_UPDATE - Edit nama kategori menjadi '{generatedName}U' dan verifikasi perubahan.
     */
    public Category testUpdateCategory() {
        reporter.startTest("TC_INV_CATEGORY_UPDATE", "UPDATE: Edit Kategori '" + generatedName + "' -> '" + updatedName + "'");

        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Klik Edit pada baris '" + generatedName + "' dan ubah nama menjadi '" + updatedName + "' ...");
            categoryPage.updateCategory(generatedName, updatedName);

            // Intersepsi error jaringan setelah aksi
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Save Update Category ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah Update Category ("
                    + analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            reporter.logStep("Verifikasi nama baru '" + updatedName + "' muncul di tabel ...");
            boolean updated = categoryPage.isCategoryInTable(updatedName);
            if (!updated) {
                throw new AssertionError(
                    "[UPDATE] Nama baru '" + updatedName + "' tidak ditemukan di tabel setelah Save.");
            }

            reporter.logPass("Kategori berhasil diupdate menjadi '" + updatedName + "'.");

        } catch (Throwable e) {
            captureNetworkOnFail("UPDATE");
            reporter.logFail("Gagal pada skenario UPDATE Kategori.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== DELETE ====================

    /**
     * TC_INV_CATEGORY_DELETE - Hapus kategori dan verifikasi hilang dari tabel.
     */
    public Category testDeleteCategory() {
        reporter.startTest("TC_INV_CATEGORY_DELETE", "DELETE: Hapus Kategori '" + updatedName + "'");

        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Klik Delete pada baris kategori '" + updatedName + "' dan konfirmasi dialog ...");
            categoryPage.deleteCategory(updatedName);

            // Intersepsi error jaringan setelah aksi
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Delete Category ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah Delete Category ("
                    + analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            reporter.logStep("Verifikasi kategori '" + updatedName + "' sudah hilang dari tabel ...");
            boolean stillExists = categoryPage.isCategoryInTable(updatedName);
            if (stillExists) {
                throw new AssertionError(
                    "[DELETE] Kategori '" + updatedName + "' masih ada di tabel setelah penghapusan.");
            }

            reporter.logPass("Kategori '" + updatedName + "' berhasil dihapus.");

        } catch (Throwable e) {
            captureNetworkOnFail("DELETE");
            reporter.logFail("Gagal pada skenario DELETE Kategori.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== Private Helpers ====================

    private void captureNetworkOnFail(String scenarioName) {
        try {
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS ON FAIL - " + scenarioName + "] Detail kegagalan sistem:",
                    analysis);
            }
        } catch (Exception ignored) {
        }
    }
}
