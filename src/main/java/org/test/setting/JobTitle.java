package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.DashboardPage;
import org.pages.settingpage.JobTitlePage;

import java.util.UUID;

import static org.common.WebDriverTools.chrome;

/**
 * JobTitle - Test Runner CRU untuk HCM > Setting > Job Title.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via JobTitlePage
 *  - Fluent interface (method chaining)
 */
public class JobTitle extends BasePage {

    private final JobTitlePage jobTitlePage;

    /** Kode Job Title unik yang di-generate per instance. */
    private final String generatedCode;
    /** Nama Job Title unik yang di-generate per instance. */
    private final String generatedName;
    /** Nama setelah diupdate. */
    private final String updatedName;

    // ==================== Constructor ====================

    public JobTitle(WebDriver driver) {
        super(driver);
        this.jobTitlePage = new JobTitlePage(driver);

        String[] retailTitles = {
            "Store Manager", "Assistant Store Manager", "Cashier", "Sales Associate",
            "Merchandiser", "Stock Clerk", "Customer Service Specialist", "Category Associate",
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

    // ==================== Entry Point ====================

    public static void main(String[] args) {
        runTest("HCM JobTitle", "JobTitle", () -> {
            new DashboardPage(chrome).navigateToJobTitlePage();

            JobTitle test = new JobTitle(chrome);
            System.out.println("  [DATA] Generated Code : " + test.generatedCode);
            System.out.println("  [DATA] Generated Name : " + test.generatedName);
            System.out.println("  [DATA] Updated Name   : " + test.updatedName);
            System.out.println();

            test.testCreate()
                .testRead()
                .testUpdate();
        });
    }

    // ==================== CREATE ====================

    /**
     * TC_JOB_TITLE_CREATE - Tambah Job Title baru.
     */
    public JobTitle testCreate() {
        reporter.startTest("TC_JOB_TITLE_CREATE", "TEST CREATE: Tambah Job Title Baru");
        drainLogs();
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

            inspectNetwork("Save Job Title");

            reporter.logStep("Verifikasi halaman list kembali ditampilkan setelah save...");
            jobTitlePage.verifyPageLoaded();

            boolean created = jobTitlePage.isJobTitleExistInTable(generatedCode);
            if (!created) {
                throw new AssertionError(
                    "[CREATE] Gagal: Job Title '" + generatedCode + "' tidak ditemukan di tabel setelah save");
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

    /**
     * TC_JOB_TITLE_READ - Verifikasi data Job Title di tabel.
     */
    public JobTitle testRead() {
        reporter.startTest("TC_JOB_TITLE_READ", "TEST READ: Verifikasi Data Job Title di Tabel");
        drainLogs();
        try {
            reporter.logStep("Verifikasi halaman Job Title List dimuat...");
            jobTitlePage.verifyPageLoaded();

            reporter.logStep("Memeriksa keberadaan Job Title dengan Code '" + generatedCode + "' di tabel...");
            boolean exists = jobTitlePage.isJobTitleExistInTable(generatedCode);
            if (!exists) {
                throw new AssertionError(
                    "[READ] Gagal: Job Title dengan Code '" + generatedCode + "' tidak ditemukan di tabel");
            }

            reporter.logStep("Mengambil data baris untuk Code '" + generatedCode + "'...");
            String[] rowData = jobTitlePage.getJobTitleRowData(generatedCode);
            if (rowData != null) {
                System.out.println("  [DATA] Row -> Action: '" + rowData[0]
                    + "' | Code: '" + rowData[1] + "' | Name: '" + rowData[2] + "'");

                if (!rowData[1].equals(generatedCode)) {
                    throw new AssertionError(
                        "[READ] Code mismatch: expected '" + generatedCode + "', got '" + rowData[1] + "'");
                }
                if (!rowData[2].equals(generatedName)) {
                    throw new AssertionError(
                        "[READ] Name mismatch: expected '" + generatedName + "', got '" + rowData[2] + "'");
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

    /**
     * TC_JOB_TITLE_UPDATE - Edit nama Job Title.
     */
    public JobTitle testUpdate() {
        reporter.startTest("TC_JOB_TITLE_UPDATE", "TEST UPDATE: Edit Nama Job Title");
        drainLogs();
        try {
            reporter.logStep("Klik tombol Edit untuk Code '" + generatedCode + "'...");
            jobTitlePage.clickEditJobTitle(generatedCode);

            reporter.logStep("Update Name menjadi '" + updatedName + "'...");
            jobTitlePage.fillJobTitleForm(null, updatedName);

            reporter.logStep("Klik Save untuk menyimpan perubahan...");
            jobTitlePage.clickSave();

            inspectNetwork("Save Update Job Title");

            reporter.logStep("Verifikasi halaman list setelah update...");
            jobTitlePage.verifyPageLoaded();

            String[] rowData = jobTitlePage.getJobTitleRowData(generatedCode);
            if (rowData != null) {
                System.out.println("  [DATA] After Update -> Code: '" + rowData[1]
                    + "' | Name: '" + rowData[2] + "'");

                if (!rowData[1].equals(generatedCode)) {
                    throw new AssertionError(
                        "[UPDATE] Code berubah: expected '" + generatedCode + "', got '" + rowData[1] + "'");
                }
                if (!rowData[2].equals(updatedName)) {
                    throw new AssertionError(
                        "[UPDATE] Name tidak berubah: expected '" + updatedName + "', got '" + rowData[2] + "'");
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
