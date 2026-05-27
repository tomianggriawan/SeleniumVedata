package org.test.setting;

import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.JobTitlePage;
import org.test.pages.LoginPage;

import java.util.UUID;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * JobTitle - Vanilla Java CRU Test Runner for HCM > Setting > Job Title.
 *
 * Skenario Test (sequential flow via main method):
 *   1. [CREATE] - Add new Job Title with auto-generated Code & Name
 *   2. [READ]   - Verify the created record appears in the table
 *   3. [UPDATE] - Edit the record's Name, save, and verify the change
 *
 * Data Generation: UUID + System.currentTimeMillis() for unique values.
 * No TestNG/JUnit dependencies.
 */
public class JobTitle {

    private static LoginPage loginPage;
    private static DashboardPage dashboardPage;
    private static JobTitlePage jobTitlePage;

    // Test data — auto-generated unique values
    private static String generatedCode;
    private static String generatedName;
    private static String updatedName;

    public static void main(String[] args) {
        try {
            // ======================== SETUP ========================

            System.out.println("========================================");
            System.out.println("  JOB TITLE CRU - VEDATA HCM");
            System.out.println("========================================\n");

            loginPage = new LoginPage(chrome);
            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            loginPage.login("tomi@tester.com", "1234");
            sleep(4000);

            dashboardPage = new DashboardPage(chrome);
            jobTitlePage = dashboardPage.navigateToJobTitlePage();

            generatedCode = "JT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            generatedName = "Auto Test " + System.currentTimeMillis();
            updatedName   = "Updated " + System.currentTimeMillis();

            System.out.println("  [DATA] Generated Code : " + generatedCode);
            System.out.println("  [DATA] Generated Name : " + generatedName);
            System.out.println("  [DATA] Updated Name   : " + updatedName);
            System.out.println();

            // ======================== CREATE ========================

            testCreateJobTitle();

            // ======================== READ ==========================

            testReadJobTitle();

            // ======================== UPDATE ========================

            testUpdateJobTitle();

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
            // ======================== TEARDOWN ========================
            chrome.quit();
        }
    }

    // ==================== CREATE ====================

    private static void testCreateJobTitle() {
        BasePage.printTestHeader("TEST CREATE: Tambah Job Title Baru");

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
    }

    // ==================== READ ====================

    private static void testReadJobTitle() {
        BasePage.printTestHeader("TEST READ: Verifikasi Data Job Title di Tabel");

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
    }

    // ==================== UPDATE ====================

    private static void testUpdateJobTitle() {
        BasePage.printTestHeader("TEST UPDATE: Edit Nama Job Title");

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
    }

    // ==================== Helper ====================

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
