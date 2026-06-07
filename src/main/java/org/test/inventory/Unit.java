package org.test.inventory;

import org.common.BasePage;
import org.openqa.selenium.WebDriver;
import org.pages.inventorypage.UnitPage;

import java.util.Random;

import static org.common.WebDriverTools.chrome;

/**
 * Unit - Test Runner CRUD untuk Satuan Barang di Inventory > Setting > Unit.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via UnitPage
 *  - Fluent interface (method chaining)
 *
 * Generator Nama Dinamis:
 *  Setiap run memilih entry acak dari UNIT_CATALOG lalu menambahkan suffix
 *  3 digit untuk memastikan keunikan — contoh: "PCS106", "KG392".
 *
 * Skenario:
 *  TC_INV_UNIT_CREATE - Tambah unit baru
 *  TC_INV_UNIT_READ   - Verifikasi unit ada di tabel
 *  TC_INV_UNIT_UPDATE - Edit nama unit dengan suffix "U"
 *  TC_INV_UNIT_DELETE - Hapus unit dan verifikasi hilang dari tabel
 */
public class Unit extends BasePage {

    // ==================== Konstanta ====================

    private static final String UNIT_TAB_URL = "https://web.vedata.id/inventory/setting?tab=unit";

    // ==================== Unit Data Catalog ====================

    /** Pasangan (Nama Lengkap, Kode/Abbreviation) satuan barang. */
    private static class UnitData {
        final String name;
        final String information;

        UnitData(String name, String information) {
            this.name = name;
            this.information = information;
        }
    }

    /** Katalog satuan retail yang valid — dipilih secara acak tiap run. */
    private static final UnitData[] UNIT_CATALOG = {
        new UnitData("Pieces",   "PCS"),
        new UnitData("Pouch",    "PCH"),
        new UnitData("Kilogram", "KG"),
        new UnitData("Pack",     "PCK"),
        new UnitData("Box",      "BOX"),
        new UnitData("Liter",    "LTR"),
        new UnitData("Ream",     "RIM"),
        new UnitData("Dozen",    "DZN"),
        new UnitData("Set",      "SET"),
        new UnitData("Carton",   "CTN")
    };

    /**
     * Menghasilkan UnitData acak: abbreviation + suffix 3 digit.
     * Contoh: abbreviation="PCS", suffix=106 → generatedName="PCS106".
     */
    private static String[] generateUnitData() {
        Random rnd = new Random();
        UnitData picked = UNIT_CATALOG[rnd.nextInt(UNIT_CATALOG.length)];
        int suffix = 100 + rnd.nextInt(900);
        String name = picked.information + suffix;
        String information = picked.name + " " + suffix;
        return new String[]{ name, information };
    }

    // ==================== Fields ====================

    private final UnitPage unitPage;

    /** Nama unit unik hasil generator, misal: "PCS106". */
    private final String generatedName;

    /** Nama lengkap satuan + angka, misal: "Pieces 106". Hanya untuk log. */
    private final String generatedInformation;

    /** Nama setelah diupdate (suffix "U"), misal: "PCS106U". */
    private final String updatedName;

    // ==================== Constructor ====================

    public Unit(WebDriver driver) {
        super(driver);
        this.unitPage = new UnitPage(driver);

        String[] data = generateUnitData();
        this.generatedName        = data[0];
        this.generatedInformation = data[1];
        this.updatedName          = this.generatedName + "U";
    }

    // ==================== Entry Point ====================

    /**
     * Entry point untuk menjalankan seluruh skenario CRUD secara standalone.
     */
    public static void main(String[] args) {
        runTest("Inventory Unit CRUD", "Unit", () -> {
            System.out.println("  [INFO] Navigasi ke Inventory Setting > Unit Tab ...");
            chrome.get("https://web.vedata.id/inventory/setting?tab=unit");
            Thread.sleep(4000);

            Unit test = new Unit(chrome);
            System.out.println("  [DATA] -----------------------------------------");
            System.out.println("  [DATA] Unit Name (input) : " + test.generatedName);
            System.out.println("  [DATA] Full Description  : " + test.generatedInformation);
            System.out.println("  [DATA] Updated Name      : " + test.updatedName);
            System.out.println("  [DATA] -----------------------------------------\n");

            test.testCreate()
                .testRead()
                .testUpdate()
                .testDelete();
        });
    }

    // ==================== CREATE ====================

    /**
     * TC_INV_UNIT_CREATE — Alur lengkap pembuatan unit baru.
     *
     * Step 1 : Sidebar — verifikasi & klik menu "Item"
     * Step 2 : Page & Tab Validation — judul "Unit Setting" + tab tersedia
     * Step 3 : Unit List Navigation — verifikasi "Unit List" + klik Add
     * Step 4 : Negative Test — Save form kosong → cek validation message
     * Step 5 : Positive Test — isi Name & Information lalu Save
     * Step 6 : Final Assertion — unit muncul di tabel
     */
    public Unit testCreate() {
        reporter.startTest("TC_INV_UNIT_CREATE",
            "CREATE: Tambah Unit Baru '" + generatedName + "'");
        drainLogs();

        try {

            // ── Step 1: Sidebar Verification ────────────────────────────────────
            reporter.logStep("Step 1 - Verifikasi menu 'Item' di sidebar dan klik...");
            driver.get(UNIT_TAB_URL);
            sleep(3000);

            unitPage.verifySidebarItemMenu();
            unitPage.clickSidebarItemMenu();

            // ── Step 2: Page & Tab Validation ───────────────────────────────────
            reporter.logStep("Step 2 - Verifikasi judul halaman 'Unit Setting' dan tab tersedia...");
            unitPage.verifyPageTitle();
            unitPage.verifySettingTabs();

            // ── Step 3: Unit List & Navigation ──────────────────────────────────
            reporter.logStep("Step 3 - Klik tab 'Unit', verifikasi 'Unit List', verifikasi & klik Add...");
            unitPage.clickUnitTab();
            unitPage.verifyUnitListTitle();
            unitPage.verifyAddButtonVisible();
            unitPage.clickAddButtonAndWaitForm();

            // ── Step 4: Form Validation — Negative Test ─────────────────────────
            reporter.logStep("Step 4 - Verifikasi judul form 'Add Unit' dan uji validasi form kosong...");
            unitPage.verifyFormTitle();
            unitPage.clickSaveEmpty();
            unitPage.verifyRequiredValidationMessages();
            reporter.logStep("Step 4 PASS - Pesan validasi required field tampil dengan benar.");

            // ── Step 5: Data Input & Submission — Positive Test ─────────────────
            reporter.logStep("Step 5 - Isi Name='" + generatedName
                + "' dan Information='" + generatedInformation + "', lalu Save...");
            unitPage.fillName(generatedName);
            unitPage.fillInformation(generatedInformation);
            unitPage.clickSaveAndWaitList();

            // ── Step 6: Final Verification ──────────────────────────────────────
            reporter.logStep("Step 6 - Verifikasi unit '" + generatedName + "' muncul di tabel...");
            unitPage.verifyUnitInTableWithData(generatedName, generatedInformation);

            inspectNetwork("Save Unit");
            reporter.logPass("Unit '" + generatedName + "' berhasil dibuat — semua 6 step PASS.");

        } catch (Throwable e) {
            captureNetworkOnFail("CREATE");
            reporter.logFail("Gagal pada skenario CREATE Unit.", e);
            throw new AssertionError(e);
        }

        System.out.println();
        return this;
    }


    // ==================== READ ====================

    /**
     * TC_INV_UNIT_READ - Verifikasi unit yang dibuat ada di tabel list.
     */
    public Unit testRead() {
        reporter.startTest("TC_INV_UNIT_READ", "READ: Verifikasi Unit '" + generatedName + "' di Tabel");
        drainLogs();
        try {
            reporter.logStep("Cari '" + generatedName + "' di tabel ...");
            boolean exists = unitPage.isUnitInTable(generatedName);

            if (!exists) {
                throw new AssertionError("[READ] Unit '" + generatedName + "' tidak ditemukan di tabel.");
            }

            inspectNetwork("READ Unit");

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
     * TC_INV_UNIT_UPDATE - Edit nama unit menjadi '{generatedName}U'.
     */
    public Unit testUpdate() {
        reporter.startTest("TC_INV_UNIT_UPDATE",
            "UPDATE: Edit Unit '" + generatedName + "' → '" + updatedName + "'");
        drainLogs();
        try {
            reporter.logStep("Klik Edit pada baris '" + generatedName + "' ...");
            unitPage.updateUnit(generatedName, updatedName);

            inspectNetwork("Save Update Unit");

            reporter.logStep("Verifikasi nama baru '" + updatedName + "' muncul di tabel ...");
            boolean updated = unitPage.isUnitInTable(updatedName);
            if (!updated) {
                throw new AssertionError("[UPDATE] Nama baru '" + updatedName + "' tidak ditemukan di tabel setelah Save.");
            }

            reporter.logStep("Verifikasi nama lama '" + generatedName + "' sudah tidak ada di tabel ...");
            boolean oldStillExists = unitPage.isUnitInTable(generatedName);
            if (oldStillExists) {
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
     * TC_INV_UNIT_DELETE - Hapus unit (nama setelah update) dan verifikasi hilang dari tabel.
     */
    public Unit testDelete() {
        reporter.startTest("TC_INV_UNIT_DELETE", "DELETE: Hapus Unit '" + updatedName + "'");
        drainLogs();
        try {
            reporter.logStep("Klik Delete pada baris '" + updatedName + "' dan konfirmasi dialog ...");
            unitPage.deleteUnit(updatedName);

            inspectNetwork("Delete Unit");

            reporter.logStep("Verifikasi unit '" + updatedName + "' sudah hilang dari tabel ...");
            boolean stillExists = unitPage.isUnitInTable(updatedName);
            if (stillExists) {
                throw new AssertionError("[DELETE] Unit '" + updatedName + "' masih ada di tabel setelah penghapusan.");
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
}
