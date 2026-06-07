package org.test.inventory;

import org.common.BasePage;
import org.openqa.selenium.WebDriver;
import org.pages.inventorypage.CategoryPage;

import java.util.Random;

import static org.common.WebDriverTools.chrome;

/**
 * Category - Test Runner CRUD untuk Kategori di Inventory > Setting > Category.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via CategoryPage
 *  - Fluent interface (method chaining)
 *
 * Skenario:
 *  TC_INV_CATEGORY_CREATE - Tambah kategori baru (tanpa field Parent)
 *  TC_INV_CATEGORY_READ   - Verifikasi kategori ada di tabel
 *  TC_INV_CATEGORY_UPDATE - Edit nama kategori dengan suffix "U"
 *  TC_INV_CATEGORY_DELETE - Hapus kategori dan verifikasi hilang dari tabel
 */
public class Category extends BasePage {

    // ==================== Konstanta ====================

    private static final String CATEGORY_TAB_URL = "https://web.vedata.id/inventory/setting?tab=category";

    private final CategoryPage categoryPage;

    /** Nama kategori unik hasil generator, misal: "Electronics_4817". */
    private final String generatedName;

    /** Nama setelah diupdate (suffix "U"), misal: "Electronics_4817U". */
    private final String updatedName;

    // ==================== Retail Category Catalog ====================

    /** Kategori produk retail yang realistis. Dipilih secara acak tiap run. */
    private static final String[] RETAIL_CATALOG = {
        "Electronics", "Clothing", "Food", "Beauty", "Sports",
        "Home", "Stationery", "Toys", "Automotive", "Healthcare",
        "Groceries", "Beverages", "Furniture", "Snacks"
    };

    // ==================== Constructor ====================

    public Category(WebDriver driver) {
        super(driver);
        this.categoryPage = new CategoryPage(driver);

        int idx = new Random().nextInt(RETAIL_CATALOG.length);
        String suffix = String.valueOf(1000 + new Random().nextInt(9000));
        this.generatedName = RETAIL_CATALOG[idx] + "_" + suffix;
        this.updatedName   = this.generatedName + "U";
    }

    // ==================== Entry Point ====================

    /**
     * Entry point untuk menjalankan seluruh skenario CRUD secara standalone.
     */
    public static void main(String[] args) {
        runTest("Inventory Category CRUD", "Category", () -> {
            System.out.println("  [INFO] Navigasi ke Inventory Setting > Category Tab ...");
            chrome.get("https://web.vedata.id/inventory/setting?tab=category");
            Thread.sleep(4000);

            Category test = new Category(chrome);
            System.out.println("  [DATA] -----------------------------------------");
            System.out.println("  [DATA] Category Name (input) : " + test.generatedName);
            System.out.println("  [DATA] Updated Name          : " + test.updatedName);
            System.out.println("  [DATA] -----------------------------------------\n");

            test.testCreate()
                .testRead()
                .testUpdate()
                .testDelete();
        });
    }

    // ==================== CREATE ====================

    /**
     * TC_INV_CATEGORY_CREATE - Tambah kategori baru (mengabaikan field Parent).
     */
    public Category testCreate() {
        reporter.startTest("TC_INV_CATEGORY_CREATE", "CREATE: Tambah Kategori Baru '" + generatedName + "'");
        drainLogs();
        try {
            reporter.logStep("Navigasi ke tab Category ...");
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3000);

            reporter.logStep("Mencari dan mengklik tombol Add ...");
            categoryPage.clickAddButtonJS();
            Thread.sleep(2000);

            reporter.logStep("Navigasi ke halaman Add Category form ...");
            driver.get("https://web.vedata.id/inventory/setting/category/form");
            Thread.sleep(2000);

            categoryPage.waitForFormInputReady();

            reporter.logStep("1. Abaikan field Parent.");
            try {
                org.openqa.selenium.WebElement parentInput = driver.findElement(
                    org.openqa.selenium.By.id("category-parent"));
                if (parentInput.isDisplayed()) {
                    System.out.println("  [Category] Field 'category-parent' terdeteksi. Diabaikan sesuai instruksi.");
                }
            } catch (Exception e) {
                System.out.println("  [Category] Field 'category-parent' tidak terdeteksi.");
            }

            reporter.logStep("2. Mengisi field Name: '" + generatedName + "' ...");
            categoryPage.fillNameOnForm(generatedName);
            Thread.sleep(1000);

            reporter.logStep("3. Klik Save ...");
            categoryPage.clickSaveOnForm();
            categoryPage.waitForReturnToList();
            Thread.sleep(2000);

            reporter.logStep("Verifikasi kategori '" + generatedName + "' muncul di tabel ...");
            boolean created = categoryPage.isCategoryInTable(generatedName);

            inspectNetwork("Save Category");

            if (!created) {
                throw new AssertionError("[CREATE] Kategori '" + generatedName + "' tidak ditemukan di tabel setelah Save.");
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
    public Category testRead() {
        reporter.startTest("TC_INV_CATEGORY_READ", "READ: Verifikasi Kategori '" + generatedName + "' di Tabel");
        drainLogs();
        try {
            reporter.logStep("Reload tab Category dan cari '" + generatedName + "' di tabel ...");
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3000);

            boolean exists = categoryPage.isCategoryInTable(generatedName);

            inspectNetwork("READ Category");

            if (!exists) {
                throw new AssertionError("[READ] Kategori '" + generatedName + "' tidak ditemukan di tabel.");
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
     * TC_INV_CATEGORY_UPDATE - Edit nama kategori menjadi '{generatedName}U'.
     */
    public Category testUpdate() {
        reporter.startTest("TC_INV_CATEGORY_UPDATE", "UPDATE: Edit Kategori '" + generatedName + "' -> '" + updatedName + "'");
        drainLogs();
        try {
            reporter.logStep("Klik Edit pada baris '" + generatedName + "' dan ubah nama menjadi '" + updatedName + "' ...");
            categoryPage.updateCategory(generatedName, updatedName);

            inspectNetwork("Save Update Category");

            reporter.logStep("Verifikasi nama baru '" + updatedName + "' muncul di tabel ...");
            boolean updated = categoryPage.isCategoryInTable(updatedName);
            if (!updated) {
                throw new AssertionError("[UPDATE] Nama baru '" + updatedName + "' tidak ditemukan di tabel setelah Save.");
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
    public Category testDelete() {
        reporter.startTest("TC_INV_CATEGORY_DELETE", "DELETE: Hapus Kategori '" + updatedName + "'");
        drainLogs();
        try {
            reporter.logStep("Klik Delete pada baris '" + updatedName + "' dan konfirmasi dialog ...");
            categoryPage.deleteCategory(updatedName);

            inspectNetwork("Delete Category");

            reporter.logStep("Verifikasi kategori '" + updatedName + "' sudah hilang dari tabel ...");
            boolean stillExists = categoryPage.isCategoryInTable(updatedName);
            if (stillExists) {
                throw new AssertionError("[DELETE] Kategori '" + updatedName + "' masih ada di tabel setelah penghapusan.");
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
}
