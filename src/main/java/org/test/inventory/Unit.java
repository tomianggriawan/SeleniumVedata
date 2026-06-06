package org.test.inventory;

import org.common.BasePage;
import org.common.NetworkEventAnalyzer;
import org.common.TestReportManager;
import org.openqa.selenium.WebDriver;
import org.pages.LoginPage;
import org.pages.inventorypage.UnitPage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * Unit - Test Runner CRUD untuk Category > Setting > Unit (Satuan Barang).
 *
 * Arsitektur mengikuti standar proyek:
 *  - Mewarisi BasePage untuk akses WebDriver helper
 *  - POM via UnitPage (org.pages.inventorypage.UnitPage)
 *  - Fluent interface / method chaining antar skenario
 *  - TestReportManager untuk laporan HTML per test case
 *  - NetworkEventAnalyzer (CDP) untuk intersepsi error HTTP 4xx/5xx
 *    dan JS fatal — kegagalan diklasifikasikan [BACKEND ERROR] atau [FRONTEND ERROR]
 *
 * Generator Nama Dinamis:
 *  Setiap run memilih entry acak dari UNIT_CATALOG lalu menambahkan angka acak
 *  3-4 digit untuk memastikan keunikan — contoh hasil: "PCS1726", "PCH881", "KG392".
 *
 * Skenario:
 *  TC_INV_UNIT_CREATE  — Tambah unit baru dengan data hasil generator
 *  TC_INV_UNIT_READ    — Verifikasi nama unit ada di tabel
 *  TC_INV_UNIT_UPDATE  — Edit nama unit dengan suffix " Updated"
 *  TC_INV_UNIT_DELETE  — Hapus unit dan verifikasi hilang dari tabel
 */
public class Unit extends BasePage {

    // ==================== Konstanta ====================

    private static final String UNIT_TAB_URL = "https://web.vedata.id/inventory/setting?tab=unit";
    private static final String REPORT_DIR   = "c:/Users/LENOVO/vedata-test/src/main/java/org/test/report";

    // ==================== Unit Data Catalog ====================

    /**
     * Pasangan (Nama Lengkap, Kode/Abbreviation) satuan barang yang realistis.
     * Nama akhir = abbreviation + angka acak 3-4 digit, misal: "PCS1726", "KG392".
     */
    private static class UnitData {
        final String name;    // Nama lengkap satuan, misal: "Pieces"
        final String information; // Kode/abbreviation, misal: "PCS"

        UnitData(String name, String information) {
            this.name = name;
            this.information = information;
        }
    }

    /** Katalog satuan retail yang valid — dipilih secara acak tiap run. */
    private static final UnitData[] UNIT_CATALOG = {
        new UnitData("Pieces",    "PCS"),
        new UnitData("Pouch",     "PCH"),
        new UnitData("Kilogram",  "KG"),
        new UnitData("Pack",      "PCK"),
        new UnitData("Box",       "BOX"),
        new UnitData("Liter",     "LTR"),
        new UnitData("Ream",      "RIM"),
        new UnitData("Dozen",     "DZN"),
        new UnitData("Set",       "SET"),
        new UnitData("Carton",    "CTN")
    };

    /**
     * Menghasilkan UnitData acak dari katalog.
     * Nama akhir = abbreviation + angka acak 3-4 digit untuk keunikan.
     * Contoh: abbreviation="PCS", suffix=1726 → generatedName="PCS1726".
     */
    private static String[] generateUnitData() {
        Random rnd = new Random();
        UnitData picked = UNIT_CATALOG[rnd.nextInt(UNIT_CATALOG.length)];
        // Angka 3 digit: 100 – 999
        int suffix = 100 + rnd.nextInt(900);
        String name = picked.information + suffix;                  // e.g. "PCS106" (6 chars)
        String information = picked.name + " " + suffix;        // e.g. "Pieces 106"
        return new String[]{ name, information };
    }

    // ==================== Fields ====================

    private static final TestReportManager reporter = new TestReportManager();

    private final UnitPage unitPage;

    /** Nama unit unik hasil generator, misal: "PC106". Diinput ke form. */
    private final String generatedName;

    /** Nama lengkap satuan + angka, misal: "Pieces 106". Hanya untuk log. */
    private final String generatedInformation;

    /** Nama setelah diupdate (suffix "U"), misal: "PC106U". */
    private final String updatedName;

    // ==================== Constructor ====================

    /**
     * Inisialisasi driver, UnitPage, dan data uji dinamis via generateUnitData().
     */
    public Unit(WebDriver driver) {
        super(driver);
        this.unitPage = new UnitPage(driver);

        String[] data = generateUnitData();
        this.generatedName     = data[0];                      // e.g. "PC106"
        this.generatedInformation = data[1];                      // e.g. "Pieces 106"
        this.updatedName       = this.generatedName + "U";     // e.g. "PC106U" (6 chars)
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
        String reportFileName = "Unit-test-report_" + timestamp + ".html";
        File   reportFile     = new File(reportDir, reportFileName);
        String reportPath     = reportFile.getAbsolutePath();

        try {
            // Setup browser & login
            chrome.manage().window().maximize();
            chrome.get(baseUrl);
            Thread.sleep(3000);

            reporter.startSuite();
            System.out.println("=================================================");
            System.out.println("[SUITE START] Category Unit CRUD Test Suite");
            System.out.println("=================================================\n");

            // Login
            System.out.println("  [INFO] Login sebagai tomi@tester.com ...");
            new LoginPage(chrome).login("tomi@tester.com", "1234");
            Thread.sleep(5000);

            // Bersihkan buffer log sesi login sebelum pengujian dimulai
            NetworkEventAnalyzer.drainLogs(chrome);

            // Navigasi ke tab Unit
            System.out.println("  [INFO] Navigasi ke Category Setting > Unit Tab ...");
            chrome.get(UNIT_TAB_URL);
            Thread.sleep(4000);

            // Inisialisasi test instance
            Unit test = new Unit(chrome);
            System.out.println("  [DATA] ─────────────────────────────────────");
            System.out.println("  [DATA] Unit Name (input) : " + test.generatedName);
            System.out.println("  [DATA] Full Description  : " + test.generatedInformation);
            System.out.println("  [DATA] Updated Name      : " + test.updatedName);
            System.out.println("  [DATA] ─────────────────────────────────────");
            System.out.println();

            // Jalankan skenario CRUD berantai
            test.testCreateUnit()
                .testReadUnit()
                .testUpdateUnit()
                .testDeleteUnit();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST CRUD UNIT SELESAI");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ERROR !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                System.err.println("URL saat error: " + chrome.getCurrentUrl());
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("c:/Users/LENOVO/vedata-test/unit_page_source_error.html"),
                    chrome.getPageSource()
                );
                System.err.println("Page source dumped ke unit_page_source_error.html");
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
     * TC_INV_UNIT_CREATE — Tambah unit baru dan verifikasi muncul di tabel.
     */
    public Unit testCreateUnit() {
        reporter.startTest("TC_INV_UNIT_CREATE", "CREATE: Tambah Unit Baru '" + generatedName + "'");

        // Bersihkan buffer log CDP sebelum aksi write
        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Navigasi ke tab Unit ...");
            driver.get(UNIT_TAB_URL);
            Thread.sleep(3000);

            reporter.logStep("Membuat unit baru: name='" + generatedName
                    + "' information='" + generatedInformation + "' ...");
            unitPage.createUnit(generatedName, generatedInformation);

            // ---- Verifikasi DULU sebelum analisis jaringan ----
            // Penting: window waktu URL bersih sangat pendek (~4-6 detik).
            // NetworkEventAnalyzer.analyze() memakan waktu tsb jika dipanggil lebih dulu.
            reporter.logStep("Verifikasi unit '" + generatedName + "' muncul di tabel ...");
            boolean created = unitPage.isUnitInTable(generatedName);

            // ---- Intersepsi error jaringan setelah assertion UI ----
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Save ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah Create Unit ("
                    + analysis.getErrors().size() + " error terdeteksi)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            if (!created) {
                throw new AssertionError(
                    "[CREATE] Unit '" + generatedName + "' tidak ditemukan di tabel setelah Save.");
            }

            reporter.logPass("Unit '" + generatedName + "' berhasil dibuat dan terverifikasi di tabel.");

        } catch (Throwable e) {
            // Intersepsi tambahan saat gagal untuk memperkaya laporan kegagalan
            captureNetworkOnFail("CREATE");
            reporter.logFail("Gagal pada skenario CREATE Unit.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== READ ====================

    /**
     * TC_INV_UNIT_READ — Verifikasi unit yang dibuat ada di tabel list.
     */
    public Unit testReadUnit() {
        reporter.startTest("TC_INV_UNIT_READ", "READ: Verifikasi Unit '" + generatedName + "' di Tabel");

        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Reload tab Unit dan cari '" + generatedName + "' di tabel ...");
            boolean exists = unitPage.isUnitInTable(generatedName);

            if (!exists) {
                throw new AssertionError(
                    "[READ] Unit '" + generatedName + "' tidak ditemukan di tabel.");
            }

            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Error jaringan terdeteksi saat READ Unit ("
                    + analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            reporter.logPass("Unit '" + generatedName + "' terkonfirmasi ada di tabel (READ OK).");

        } catch (Throwable e) {
            captureNetworkOnFail("READ");
            reporter.logFail("Gagal pada skenario READ Unit.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== UPDATE ====================

    /**
     * TC_INV_UNIT_UPDATE — Edit nama unit menjadi '{generatedName} Updated' dan verifikasi perubahan.
     */
    public Unit testUpdateUnit() {
        reporter.startTest("TC_INV_UNIT_UPDATE",
            "UPDATE: Edit Unit '" + generatedName + "' → '" + updatedName + "'");

        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Klik Edit pada baris '" + generatedName + "' ...");
            unitPage.updateUnit(generatedName, updatedName);

            // ---- Intersepsi error jaringan sebelum assertion UI ----
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Update Save ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah Update Unit ("
                    + analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            reporter.logStep("Verifikasi nama baru '" + updatedName + "' muncul di tabel ...");
            boolean updated = unitPage.isUnitInTable(updatedName);
            if (!updated) {
                throw new AssertionError(
                    "[UPDATE] Nama baru '" + updatedName + "' tidak ditemukan di tabel setelah Save.");
            }

            reporter.logStep("Verifikasi nama lama '" + generatedName + "' sudah tidak ada di tabel ...");
            boolean oldStillExists = unitPage.isUnitInTable(generatedName);
            if (oldStillExists) {
                // Peringatan saja — bisa jadi nama lama adalah substring dari nama baru
                System.out.println("  [WARN] Nama lama '" + generatedName
                    + "' masih terdeteksi (kemungkinan substring match).");
            }

            reporter.logPass("Unit berhasil diupdate: '" + generatedName + "' → '" + updatedName + "'.");

        } catch (Throwable e) {
            captureNetworkOnFail("UPDATE");
            reporter.logFail("Gagal pada skenario UPDATE Unit.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== DELETE ====================

    /**
     * TC_INV_UNIT_DELETE — Hapus unit (nama setelah update) dan verifikasi hilang dari tabel.
     */
    public Unit testDeleteUnit() {
        reporter.startTest("TC_INV_UNIT_DELETE", "DELETE: Hapus Unit '" + updatedName + "'");

        NetworkEventAnalyzer.drainLogs(driver);

        try {
            reporter.logStep("Klik Delete pada baris '" + updatedName + "' dan konfirmasi dialog ...");
            unitPage.deleteUnit(updatedName);

            // ---- Intersepsi error jaringan sebelum assertion UI ----
            reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah Delete ...");
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah Delete Unit ("
                    + analysis.getErrors().size() + " error)", analysis);
                throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
            }

            reporter.logStep("Verifikasi unit '" + updatedName + "' sudah hilang dari tabel ...");
            boolean stillExists = unitPage.isUnitInTable(updatedName);
            if (stillExists) {
                throw new AssertionError(
                    "[DELETE] Unit '" + updatedName + "' masih ada di tabel setelah penghapusan.");
            }

            reporter.logPass("Unit '" + updatedName + "' berhasil dihapus dan tidak ada di tabel.");

        } catch (Throwable e) {
            captureNetworkOnFail("DELETE");
            reporter.logFail("Gagal pada skenario DELETE Unit.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    // ==================== Private Helpers ====================

    /**
     * Menjalankan NetworkEventAnalyzer.analyze saat terjadi kegagalan test
     * untuk memperkaya laporan dengan detail error backend/frontend.
     *
     * @param scenarioName Nama skenario (CREATE/READ/UPDATE/DELETE) untuk log
     */
    private void captureNetworkOnFail(String scenarioName) {
        try {
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS ON FAIL - " + scenarioName + "] Detail kegagalan sistem:",
                    analysis);
            }
        } catch (Exception ignored) {
            // Jangan biarkan analisis sekunder menimpa exception utama
        }
    }
}
