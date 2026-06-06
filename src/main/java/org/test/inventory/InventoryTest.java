package org.test.inventory;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * InventoryTest - Main Test Class untuk Inventory DDT Framework.
 *
 * Framework: TestNG + Selenium WebDriver + Apache POI
 * Arsitektur: Page Object Model (POM)
 *
 * ============================================================
 * TEST CASE 1: testDummyCRUD (priority=1)
 * - CRUD penuh menggunakan data dummy/random
 * - Menguji alur Unit, Category, Option, Item secara independen
 *
 * TEST CASE 2: testExcelCRU (priority=2, dependsOnMethods="testDummyCRUD")
 * - CRU (Create, Read, Update) tanpa Delete
 * - Data bersumber dari file Excel (Katalog_Produk_YAVA_Lengkap.xlsx)
 * - Data unik via LinkedHashSet, skip baris dengan cell kosong
 * ============================================================
 *
 * Cara menjalankan (dengan rtk prefix):
 *   rtk mvn test -Dtest=InventoryTest -pl .
 *
 * Atau dengan testng.xml:
 *   rtk mvn test
 */
public class InventoryTest extends BaseTest {

    // ============================================================
    // Path file Excel - sesuaikan jika perlu
    // ============================================================
    private static final String EXCEL_FILE_PATH =
            "C:\\Users\\LENOVO\\vedata-test\\src\\main\\resources\\Katalog_Produk_YAVA_Lengkap.xlsx";

    // ============================================================
    // Nama data dummy yang digunakan TC1 (berisi timestamp agar unik)
    // ============================================================
    private static String dummyUnitName;
    private static String dummyUnitNameUpdated;
    private static String dummyCategoryName;
    private static String dummyCategoryNameUpdated;
    private static String dummyOptionName;
    private static String dummyOptionNameUpdated;
    private static String dummyItemName;
    private static String dummyItemNameUpdated;
    private static String dummyItemCode;

    // ============================================================
    // PAGE OBJECTS
    // ============================================================
    private static UnitPage     unitPage;
    private static CategoryPage categoryPage;
    private static OptionPage   optionPage;
    private static ItemPage     itemPage;

    // ============================================================
    // TEST CASE 1: Dummy CRUD Penuh
    // ============================================================

    /**
     * TC01 - Dummy CRUD Test
     *
     * Melakukan Create, Read, Update, Delete pada masing-masing Tab
     * (Unit, Category, Option, Item) menggunakan data dummy acak/timestamp.
     *
     * Tujuan: Verifikasi bahwa semua operasi CRUD berfungsi sebelum
     * menjalankan test data dari Excel.
     */
    @Test(
        priority = 1,
        description = "TC01 - Dummy CRUD Test: Create, Read, Update, Delete dengan data dummy",
        groups = {"crud", "smoke"}
    )
    public void testDummyCRUD() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  TC01: DUMMY CRUD TEST DIMULAI");
        System.out.println("=".repeat(60));

        // Inisialisasi Page Objects
        unitPage     = new UnitPage(driver);
        categoryPage = new CategoryPage(driver);
        optionPage   = new OptionPage(driver);
        itemPage     = new ItemPage(driver);

        // Generate data dummy dengan timestamp pendek (6 digit terakhir) agar tidak melebihi batas 20 karakter database
        String ts = generateTimestamp();
        String shortTs = ts.substring(ts.length() - 6);
        dummyUnitName         = "U-" + shortTs;
        dummyUnitNameUpdated  = "U-Up-" + shortTs;
        dummyCategoryName     = "C-" + shortTs;
        dummyCategoryNameUpdated = "C-Up-" + shortTs;
        dummyOptionName       = "O-" + shortTs;
        dummyOptionNameUpdated= "O-Up-" + shortTs;
        dummyItemName         = "I-" + shortTs;
        dummyItemNameUpdated  = "I-Up-" + shortTs;
        dummyItemCode         = "COD-" + shortTs;

        System.out.println("  [TC01] Data Dummy:");
        System.out.println("         Unit     : " + dummyUnitName);
        System.out.println("         Category : " + dummyCategoryName);
        System.out.println("         Option   : " + dummyOptionName);
        System.out.println("         Item     : " + dummyItemName);

        // ── Navigasi ke Inventory Setting ────────────────────────────
        navigateToInventorySetting();

        // ── TAB UNIT: CRUD ───────────────────────────────────────────
        System.out.println("\n  [TC01] ── Tab UNIT ──");
        unitPage.clickTabUnit();

        // CREATE Unit
        System.out.println("  [TC01] CREATE Unit: " + dummyUnitName);
        unitPage.createUnit(dummyUnitName);

        // READ Unit
        System.out.println("  [TC01] READ Unit: Verifikasi '" + dummyUnitName + "' di tabel");
        unitPage.assertUnitInTable(dummyUnitName);
        System.out.println("  [PASS] TC01-Unit-READ: Unit ditemukan di tabel.");

        // UPDATE Unit
        System.out.println("  [TC01] UPDATE Unit: '" + dummyUnitName + "' → '" + dummyUnitNameUpdated + "'");
        unitPage.updateUnit(dummyUnitName, dummyUnitNameUpdated);
        unitPage.assertUnitInTable(dummyUnitNameUpdated);
        System.out.println("  [PASS] TC01-Unit-UPDATE: Unit berhasil diperbarui.");

        // DELETE Unit
        System.out.println("  [TC01] DELETE Unit: '" + dummyUnitNameUpdated + "'");
        unitPage.deleteUnit(dummyUnitNameUpdated);
        boolean unitDeleted = !unitPage.isUnitInTable(dummyUnitNameUpdated);
        Assert.assertTrue(unitDeleted,
                "[FAIL] TC01-Unit-DELETE: Unit masih ada di tabel setelah dihapus.");
        System.out.println("  [PASS] TC01-Unit-DELETE: Unit berhasil dihapus.");

        // ── TAB CATEGORY: CRUD ───────────────────────────────────────
        System.out.println("\n  [TC01] ── Tab CATEGORY ──");
        categoryPage.clickTabCategory();

        // CREATE Category (tanpa parent)
        System.out.println("  [TC01] CREATE Category: " + dummyCategoryName);
        categoryPage.createWithoutParent(dummyCategoryName);

        // READ Category
        System.out.println("  [TC01] READ Category: Verifikasi '" + dummyCategoryName + "'");
        categoryPage.assertCategoryInTable(dummyCategoryName);
        System.out.println("  [PASS] TC01-Category-READ: Category ditemukan.");

        // UPDATE Category
        System.out.println("  [TC01] UPDATE Category: '" + dummyCategoryName + "' → '" + dummyCategoryNameUpdated + "'");
        categoryPage.updateCategory(dummyCategoryName, dummyCategoryNameUpdated);
        categoryPage.assertCategoryInTable(dummyCategoryNameUpdated);
        System.out.println("  [PASS] TC01-Category-UPDATE: Category berhasil diperbarui.");

        // DELETE Category
        System.out.println("  [TC01] DELETE Category: '" + dummyCategoryNameUpdated + "'");
        categoryPage.deleteCategory(dummyCategoryNameUpdated);
        boolean catDeleted = !categoryPage.isCategoryInTable(dummyCategoryNameUpdated);
        Assert.assertTrue(catDeleted,
                "[FAIL] TC01-Category-DELETE: Category masih ada setelah dihapus.");
        System.out.println("  [PASS] TC01-Category-DELETE: Category berhasil dihapus.");

        // ── TAB OPTION: CRUD ─────────────────────────────────────────
        System.out.println("\n  [TC01] ── Tab OPTION ──");
        optionPage.clickTabOption();

        // CREATE Option
        System.out.println("  [TC01] CREATE Option: " + dummyOptionName + " (variant: 'Pouch')");
        optionPage.createOption(dummyOptionName, "Pouch");

        // READ Option
        System.out.println("  [TC01] READ Option: Verifikasi '" + dummyOptionName + "'");
        optionPage.assertOptionInTable(dummyOptionName);
        System.out.println("  [PASS] TC01-Option-READ: Option ditemukan.");

        // UPDATE Option
        System.out.println("  [TC01] UPDATE Option: '" + dummyOptionName + "' → '" + dummyOptionNameUpdated + "'");
        optionPage.updateOption(dummyOptionName, dummyOptionNameUpdated);
        optionPage.assertOptionInTable(dummyOptionNameUpdated);
        System.out.println("  [PASS] TC01-Option-UPDATE: Option berhasil diperbarui.");

        // DELETE Option
        System.out.println("  [TC01] DELETE Option: '" + dummyOptionNameUpdated + "'");
        optionPage.deleteOption(dummyOptionNameUpdated);
        boolean optDeleted = !optionPage.isOptionInTable(dummyOptionNameUpdated);
        Assert.assertTrue(optDeleted,
                "[FAIL] TC01-Option-DELETE: Option masih ada setelah dihapus.");
        System.out.println("  [PASS] TC01-Option-DELETE: Option berhasil dihapus.");

        // ── TAB ITEM: CRUD ───────────────────────────────────────────
        System.out.println("\n  [TC01] ── Tab ITEM ──");
        itemPage.clickTabItem();

        // CREATE Unit & Option dummy kembali untuk keperluan Item
        String itemUnitForTest = "DU-" + shortTs;
        String itemOptForTest  = "DO-" + shortTs;
        String itemCatForTest  = "DC-" + shortTs;

        navigateToInventorySetting();
        unitPage.clickTabUnit();
        unitPage.createUnit(itemUnitForTest);
        categoryPage.clickTabCategory();
        categoryPage.createWithoutParent(itemCatForTest);
        optionPage.clickTabOption();
        optionPage.createOption(itemOptForTest, "Botol");

        // Kembali ke Tab Item
        itemPage.clickTabItem();

        // CREATE Item
        System.out.println("  [TC01] CREATE Item: " + dummyItemName);
        itemPage.clickAddItem();
        sleep(2000);
        itemPage.fillAndCreateItem(
            dummyItemCode,         // Code (timestamp)
            dummyItemName,         // Name
            itemCatForTest,        // Category (yang baru dibuat)
            itemUnitForTest,       // Unit (yang baru dibuat)
            itemOptForTest,        // Option (yang baru dibuat)
            "DummyBrand",          // Brand
            "Test Otomatis"        // Information
        );
        itemPage.saveAndVerify(dummyItemName);
        System.out.println("  [PASS] TC01-Item-CREATE: Item berhasil dibuat.");

        // READ Item
        System.out.println("  [TC01] READ Item: Verifikasi '" + dummyItemName + "'");
        itemPage.assertItemInTable(dummyItemName);
        System.out.println("  [PASS] TC01-Item-READ: Item ditemukan di tabel.");

        // UPDATE Item
        System.out.println("  [TC01] UPDATE Item: '" + dummyItemName + "' → '" + dummyItemNameUpdated + "'");
        itemPage.updateItem(dummyItemName, dummyItemNameUpdated, "DummyBrand-Updated");
        System.out.println("  [PASS] TC01-Item-UPDATE: Item berhasil diperbarui.");

        // DELETE Item
        System.out.println("  [TC01] DELETE Item: '" + dummyItemNameUpdated + "'");
        itemPage.deleteItem(dummyItemNameUpdated);
        boolean itemDeleted = !itemPage.isItemInTable(dummyItemNameUpdated);
        Assert.assertTrue(itemDeleted,
                "[FAIL] TC01-Item-DELETE: Item masih ada setelah dihapus.");
        System.out.println("  [PASS] TC01-Item-DELETE: Item berhasil dihapus.");

        // Cleanup: hapus unit/cat/opt dummy yang dibuat untuk item test
        navigateToInventorySetting();
        unitPage.clickTabUnit();
        unitPage.deleteUnit(itemUnitForTest);
        categoryPage.clickTabCategory();
        categoryPage.deleteCategory(itemCatForTest);
        optionPage.clickTabOption();
        optionPage.deleteOption(itemOptForTest);

        System.out.println("\n  [PASS] TC01: Semua operasi CRUD Dummy berhasil!");
        System.out.println("=".repeat(60));
    }

    // ============================================================
    // TEST CASE 2: Excel Data CRU (Create, Read, Update)
    // ============================================================

    /**
     * TC02 - Excel Data-Driven CRU Test
     *
     * Membaca data dari file Excel (Katalog_Produk_YAVA_Lengkap.xlsx),
     * lalu melakukan operasi Create, Read, Update (tanpa Delete) untuk
     * setiap baris data unik.
     *
     * Urutan flow per data item:
     *   1. Tab Unit: CRU
     *   2. Tab Category: CRU (dengan logic "/" separator)
     *   3. Tab Option: CRU
     *   4. Tab Item: CRU (Code=timestamp, SKU auto-generate pada update)
     *
     * Dependensi: Hanya berjalan jika testDummyCRUD PASS.
     */
    @Test(
        priority = 2,
        dependsOnMethods = {"testDummyCRUD"},
        description = "TC02 - Excel CRU Test: Create, Read, Update dari data Excel",
        groups = {"excel", "ddt"}
    )
    public void testExcelCRU() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  TC02: EXCEL DATA-DRIVEN CRU TEST DIMULAI");
        System.out.println("=".repeat(60));

        // Inisialisasi Page Objects (pastikan driver masih valid)
        unitPage     = new UnitPage(driver);
        categoryPage = new CategoryPage(driver);
        optionPage   = new OptionPage(driver);
        itemPage     = new ItemPage(driver);

        // ── Validasi file Excel ──────────────────────────────────────
        File excelFile = new File(EXCEL_FILE_PATH);
        if (!excelFile.exists()) {
            System.out.println("  [WARN] File Excel tidak ditemukan di: " + EXCEL_FILE_PATH);
            System.out.println("  [INFO] Mencoba path alternatif...");

            // Coba path alternatif
            String[] altPaths = {
                "Katalog_Produk_YAVA_Lengkap.xlsx",
                "src\\main\\resources\\Katalog_Produk_YAVA_Lengkap.xlsx",
                System.getProperty("user.home") + "\\vedata-test\\src\\main\\resources\\Katalog_Produk_YAVA_Lengkap.xlsx"
            };
            File altFile = null;
            for (String path : altPaths) {
                File f = new File(path);
                if (f.exists()) {
                    altFile = f;
                    System.out.println("  [INFO] File Excel ditemukan di: " + f.getAbsolutePath());
                    break;
                }
            }
            if (altFile == null) {
                Assert.fail("[FAIL] TC02: File Excel tidak ditemukan. Path yang dicoba: " + EXCEL_FILE_PATH +
                        "\nLetakkan file 'Katalog_Produk_YAVA_Lengkap.xlsx' di folder: src/main/resources/");
                return;
            }
            excelFile = altFile;
        }

        // ── Baca data dari Excel ─────────────────────────────────────
        List<ExcelReader.InventoryRow> rows;
        try {
            rows = ExcelReader.readUniqueRows(excelFile.getAbsolutePath());
        } catch (Exception e) {
            Assert.fail("[FAIL] TC02: Gagal membaca file Excel: " + e.getMessage());
            return;
        }

        if (rows.isEmpty()) {
            Assert.fail("[FAIL] TC02: File Excel tidak memiliki data valid (semua baris kosong atau duplikat).");
            return;
        }

        System.out.println("  [TC02] Total data unik dari Excel: " + rows.size() + " baris");

        // ── Ekstrak data unik per Tab ────────────────────────────────
        List<String>   uniqueUnits      = ExcelReader.getUniqueUnits(rows);
        List<String>   uniqueCategories = ExcelReader.getUniqueCategories(rows);
        List<String[]> uniqueOptions    = ExcelReader.getUniqueOptions(rows);

        System.out.println("  [TC02] Unik Unit     : " + uniqueUnits.size());
        System.out.println("  [TC02] Unik Category : " + uniqueCategories.size());
        System.out.println("  [TC02] Unik Option   : " + uniqueOptions.size());
        System.out.println("  [TC02] Unik Item     : " + rows.size());

        // Navigasi ke Inventory Setting
        navigateToInventorySetting();

        // ── CRU: TAB UNIT ────────────────────────────────────────────
        System.out.println("\n  [TC02] ══ TAB UNIT: CRU ══");
        unitPage.clickTabUnit();

        for (String unitName : uniqueUnits) {
            System.out.println("\n  [TC02][Unit] Memproses: '" + unitName + "'");

            // CREATE
            unitPage.createUnit(unitName);

            // READ (Verify)
            unitPage.assertUnitInTable(unitName);
            System.out.println("  [PASS] TC02-Unit-CREATE+READ: '" + unitName + "'");

            // UPDATE (tambahkan suffix "-Updated")
            String updatedUnitName = unitName + "-Upd";
            unitPage.updateUnit(unitName, updatedUnitName);
            unitPage.assertUnitInTable(updatedUnitName);
            System.out.println("  [PASS] TC02-Unit-UPDATE: '" + unitName + "' → '" + updatedUnitName + "'");
        }

        // ── CRU: TAB CATEGORY ────────────────────────────────────────
        System.out.println("\n  [TC02] ══ TAB CATEGORY: CRU ══");
        categoryPage.clickTabCategory();

        for (String rawCategory : uniqueCategories) {
            System.out.println("\n  [TC02][Category] Memproses: '" + rawCategory + "'");

            // CREATE (dengan logic "/" separator)
            categoryPage.createCategory(rawCategory);

            // READ (Verify)
            categoryPage.assertCategoryInTable(rawCategory);
            System.out.println("  [PASS] TC02-Category-CREATE+READ: '" + rawCategory + "'");

            // Tentukan nama yang terlihat di tabel untuk UPDATE
            String displayName = rawCategory.contains("/")
                    ? rawCategory.split("/", 2)[1].trim()
                    : rawCategory.trim();
            String updatedCatName = displayName + "-Upd";

            // UPDATE
            categoryPage.updateCategory(displayName, updatedCatName);
            categoryPage.assertCategoryInTable(updatedCatName);
            System.out.println("  [PASS] TC02-Category-UPDATE: '" + displayName + "' → '" + updatedCatName + "'");
        }

        // ── CRU: TAB OPTION ──────────────────────────────────────────
        System.out.println("\n  [TC02] ══ TAB OPTION: CRU ══");
        optionPage.clickTabOption();

        for (String[] optPair : uniqueOptions) {
            String optionName  = optPair[0];
            String variantVal  = optPair[1];
            System.out.println("\n  [TC02][Option] Memproses: '" + optionName + "' (Variant: '" + variantVal + "')");

            // CREATE
            optionPage.createOption(optionName, variantVal);

            // READ
            optionPage.assertOptionInTable(optionName);
            System.out.println("  [PASS] TC02-Option-CREATE+READ: '" + optionName + "'");

            // UPDATE
            String updatedOptName = optionName + "-Upd";
            optionPage.updateOption(optionName, updatedOptName);
            optionPage.assertOptionInTable(updatedOptName);
            System.out.println("  [PASS] TC02-Option-UPDATE: '" + optionName + "' → '" + updatedOptName + "'");
        }

        // ── CRU: TAB ITEM ─────────────────────────────────────────────
        System.out.println("\n  [TC02] ══ TAB ITEM: CRU ══");

        int itemIndex = 0;
        for (ExcelReader.InventoryRow row : rows) {
            itemIndex++;
            System.out.println("\n  [TC02][Item " + itemIndex + "/" + rows.size() + "] Memproses: '" + row.itemName + "'");

            // Navigate ke item tab
            itemPage.clickTabItem();

            // Tentukan nama unit/category/option yang sudah di-CRU (dengan suffix "-Upd")
            String usedUnitName     = row.unit + "-Upd";
            String usedCategoryName = (row.category.contains("/")
                    ? row.category.split("/", 2)[1].trim()
                    : row.category.trim()) + "-Upd";
            String usedOptionName   = row.optionName + "-Upd";

            // Code & SKU = timestamp unik per item (pendek agar aman dari DB constraint)
            String fullTs = generateTimestamp();
            String itemCode = "S-" + fullTs.substring(fullTs.length() - 8);
            sleep(10); // Pastikan timestamp berbeda antar item

            // CREATE Item
            System.out.println("  [TC02][Item] CREATE: '" + row.itemName + "'");
            itemPage.clickAddItem();
            sleep(2000);

            itemPage.fillAndCreateItem(
                itemCode,           // Code (timestamp)
                row.itemName,       // Name dari Excel
                usedCategoryName,   // Category yang sudah diupdate
                usedUnitName,       // Unit yang sudah diupdate
                usedOptionName,     // Option yang sudah diupdate
                row.brand,          // Brand dari Excel
                "Test Otomatis"     // Information default
            );
            itemPage.saveAndVerify(row.itemName);
            System.out.println("  [PASS] TC02-Item-CREATE+READ: '" + row.itemName + "'");

            // UPDATE Item (Auto-generate SKU)
            System.out.println("  [TC02][Item] UPDATE: '" + row.itemName + "'");
            String updatedItemName = row.itemName + " [Updated]";
            itemPage.updateItem(row.itemName, updatedItemName, row.brand + " Updated");
            System.out.println("  [PASS] TC02-Item-UPDATE: '" + row.itemName + "' → '" + updatedItemName + "'");
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  [PASS] TC02: Semua operasi Excel CRU berhasil!");
        System.out.println("         Total item diproses: " + rows.size());
        System.out.println("=".repeat(60));
    }
}
