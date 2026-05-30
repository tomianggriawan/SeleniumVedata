package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.JobTitlePage;
import org.test.pages.LoginPage;

import java.util.UUID;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * JobTitle - Vanilla Java CRU Test Runner for HCM > Setting > Job Title.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class JobTitle extends BasePage {

    private final JobTitlePage jobTitlePage;

    // Test data — auto-generated unique values per instance
    private final String generatedCode;
    private final String generatedName;
    private final String updatedName;

    /**
     * Constructor untuk inisialisasi driver dan mempersiapkan test data unik.
     * State driver dipertahankan agar tidak hilang saat method chaining.
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
        try {
            LoginPage loginPage = new LoginPage(chrome);
            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  JOB TITLE CRU - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Navigasi awal
            new DashboardPage(chrome).navigateToJobTitlePage();

            // Memulai method chaining / fluent test execution
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
            System.err.println("URL saat error: " + chrome.getCurrentUrl());
            try {
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("C:/Users/LENOVO/SDET/SeleniumVedata/page_source_error.html"),
                    chrome.getPageSource()
                );
                System.err.println("Page source dumped to page_source_error.html");
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }

    // ==================== CREATE ====================

    public JobTitle testCreateJobTitle() {
        printTestHeader("TEST CREATE: Tambah Job Title Baru");

        System.out.println("  [STEP] Verifikasi halaman Job Title List...");
        jobTitlePage.verifyPageLoaded();

        System.out.println("  [STEP] Klik tombol Add...");
        jobTitlePage.verifyAddButtonDisplayed();
        jobTitlePage.clickAddButton();

        System.out.println("  [STEP] Isi form Code & Name...");
        System.out.println("  [DATA] Code = '" + generatedCode + "', Name = '" + generatedName + "'");
        jobTitlePage.fillJobTitleForm(generatedCode, generatedName);

        System.out.println("  [STEP] Klik Save...");
        jobTitlePage.clickSave();

        System.out.println("  [STEP] Verifikasi halaman list kembali...");
        jobTitlePage.verifyPageLoaded();

        boolean created = jobTitlePage.isJobTitleExistInTable(generatedCode);
        if (!created) {
            throw new AssertionError(
                "[CREATE] Gagal: Job Title '" + generatedCode + "' tidak ditemukan di tabel setelah save"
            );
        }
        System.out.println("  [PASS] Job Title '" + generatedCode + "' berhasil dibuat dan muncul di tabel");
        System.out.println();
        return this;
    }

    // ==================== READ ====================

    public JobTitle testReadJobTitle() {
        printTestHeader("TEST READ: Verifikasi Data Job Title di Tabel");

        jobTitlePage.verifyPageLoaded();

        boolean exists = jobTitlePage.isJobTitleExistInTable(generatedCode);
        if (!exists) {
            throw new AssertionError(
                "[READ] Gagal: Job Title dengan Code '" + generatedCode + "' tidak ditemukan di tabel"
            );
        }
        System.out.println("  [PASS] Job Title dengan Code '" + generatedCode + "' ditemukan di tabel");

        String[] rowData = jobTitlePage.getJobTitleRowData(generatedCode);
        if (rowData != null) {
            System.out.println("  [DATA] Row -> Action: '" + rowData[0]
                + "' | Code: '" + rowData[1] + "' | Name: '" + rowData[2] + "'");

            if (!rowData[1].equals(generatedCode)) {
                throw new AssertionError(
                    "[READ] Code mismatch: expected '" + generatedCode + "', got '" + rowData[1] + "'"
                );
            }
            System.out.println("  [PASS] Code di tabel sesuai: '" + rowData[1] + "'");

            if (!rowData[2].equals(generatedName)) {
                throw new AssertionError(
                    "[READ] Name mismatch: expected '" + generatedName + "', got '" + rowData[2] + "'"
                );
            }
            System.out.println("  [PASS] Name di tabel sesuai: '" + rowData[2] + "'");
        } else {
            throw new AssertionError("[READ] Gagal mengambil data baris untuk code '" + generatedCode + "'");
        }

        System.out.println();
        return this;
    }

    // ==================== UPDATE ====================

    public JobTitle testUpdateJobTitle() {
        printTestHeader("TEST UPDATE: Edit Nama Job Title");

        System.out.println("  [STEP] Klik Edit untuk Code '" + generatedCode + "'...");
        jobTitlePage.clickEditJobTitle(generatedCode);

        System.out.println("  [STEP] Update Name menjadi '" + updatedName + "'...");
        jobTitlePage.fillJobTitleForm(null, updatedName);

        System.out.println("  [STEP] Klik Save...");
        jobTitlePage.clickSave();

        System.out.println("  [STEP] Verifikasi halaman list...");
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
            System.out.println("  [PASS] Code masih sesuai: '" + rowData[1] + "'");

            if (!rowData[2].equals(updatedName)) {
                throw new AssertionError(
                    "[UPDATE] Name tidak berubah: expected '" + updatedName + "', got '" + rowData[2] + "'"
                );
            }
            System.out.println("  [PASS] Name berhasil diubah menjadi: '" + updatedName + "'");
        } else {
            throw new AssertionError("[UPDATE] Gagal mengambil data baris setelah update");
        }

        System.out.println();
        return this;
    }
}
