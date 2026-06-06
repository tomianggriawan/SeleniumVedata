package org.pages.inventorypage;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemPage - Page Object untuk Tab ITEM (Form) di Category Setting.
 *
 * Mewarisi BasePage untuk helper Selenium (xpathString, clickEditButton,
 * clickDeleteButton, confirmDeleteDialog, checkBackendOrValidationError).
 *
 * Flow Lengkap Item:
 * CREATE:
 *   1. Code & SKU = auto-generate timestamp
 *   2. Name = dari Excel
 *   3. Dropdown Category, Unit, Option = dari data yang sudah dibuat
 *   4. Order Method = FEFO
 *   5. Brand = dari Excel
 *   6. Information = "Test Otomatis"
 *   7. Toggle "This Item Has Variants" ON
 *   8. Klik "Add Variant" → "Generate SKU" → "Generate Barcode"
 *   9. Dimensi otomatis berdasarkan nama option (misal: 'pouch' → 15x10x5)
 *  10. Simpan & Assert di tabel
 *
 * UPDATE:
 *   1. Buka form edit item
 *   2. Klik tombol "Auto-generate SKU"
 *   3. Update field sesuai kebutuhan
 *   4. Simpan
 */
public class ItemPage extends BasePage {

    // ==================== Dimension Map ====================

    /**
     * Pemetaan nama option/kemasan ke dimensi default (P x L x T dalam cm).
     */
    private static final Map<String, String[]> DIMENSION_MAP = new HashMap<>();
    static {
        DIMENSION_MAP.put("pouch",   new String[]{"15", "10", "5"});
        DIMENSION_MAP.put("sachet",  new String[]{"10", "5",  "2"});
        DIMENSION_MAP.put("botol",   new String[]{"8",  "8",  "25"});
        DIMENSION_MAP.put("bottle",  new String[]{"8",  "8",  "25"});
        DIMENSION_MAP.put("kaleng",  new String[]{"8",  "8",  "12"});
        DIMENSION_MAP.put("can",     new String[]{"8",  "8",  "12"});
        DIMENSION_MAP.put("box",     new String[]{"30", "20", "15"});
        DIMENSION_MAP.put("kotak",   new String[]{"30", "20", "15"});
        DIMENSION_MAP.put("bag",     new String[]{"40", "30", "10"});
        DIMENSION_MAP.put("karung",  new String[]{"50", "40", "20"});
        DIMENSION_MAP.put("pack",    new String[]{"20", "15", "10"});
        DIMENSION_MAP.put("tablet",  new String[]{"12", "8",  "4"});
        DIMENSION_MAP.put("kapsul",  new String[]{"10", "6",  "3"});
        DIMENSION_MAP.put("jar",     new String[]{"10", "10", "12"});
        DIMENSION_MAP.put("tube",    new String[]{"5",  "5",  "18"});
        DIMENSION_MAP.put("default", new String[]{"20", "15", "10"});
    }

    // ==================== Locators ====================

    private static final String ITEM_TAB_URL = "https://web.vedata.id/inventory/setting?tab=item";

    private final By tabItem = By.xpath(
            "//button[contains(@class,'v-tab') and " +
            "(contains(text(),'Item') or .//span[contains(text(),'Item')])]");

    private final By btnAddItem = By.xpath(
            "//button[.//span[contains(text(),'Add') or contains(text(),'Tambah')] or " +
            "contains(text(),'Add') or contains(text(),'Tambah')]");

    private final By inputCode = By.xpath(
            "//label[contains(text(),'Code') or contains(text(),'Kode')]/following::input[1] | " +
            "//input[@placeholder and (contains(@placeholder,'Code') or contains(@placeholder,'Kode'))]");

    private final By inputName = By.xpath(
            "//label[contains(text(),'Name') or contains(text(),'Nama')]/following::input[1] | " +
            "//input[@placeholder and (contains(@placeholder,'Name') or contains(@placeholder,'Nama'))]");

    private final By inputBrand = By.xpath(
            "//label[contains(text(),'Brand') or contains(text(),'Merek')]/following::input[1] | " +
            "//input[@placeholder and contains(@placeholder,'Brand')]");

    private final By inputInformation = By.xpath(
            "//label[contains(text(),'Information') or contains(text(),'Informasi') or contains(text(),'Description')]/following::input[1] | " +
            "//textarea[contains(@placeholder,'Information') or contains(@placeholder,'Informasi')]");

    private final By btnAddVariant = By.xpath(
            "//button[.//span[contains(text(),'Add Variant') or contains(text(),'Tambah Variant')] or " +
            "contains(text(),'Add Variant') or contains(text(),'Tambah Variant')]");

    private final By btnGenerateSKU = By.xpath(
            "//button[.//span[contains(text(),'Generate SKU')] or contains(text(),'Generate SKU')]");

    private final By btnGenerateBarcode = By.xpath(
            "//button[.//span[contains(text(),'Generate Barcode')] or contains(text(),'Generate Barcode')]");

    private final By btnAutoGenerateSKU = By.xpath(
            "//button[.//span[contains(text(),'Auto-generate SKU') or contains(text(),'Auto Generate SKU')] or " +
            "contains(text(),'Auto-generate SKU') or contains(text(),'Auto Generate SKU')]");

    private final By btnSave = By.xpath(
            "//button[contains(@class,'v-btn') and " +
            "(.//span[contains(text(),'Simpan') or contains(text(),'Save')] or " +
            "contains(text(),'Simpan') or contains(text(),'Save'))]");

    // ==================== Constructor ====================

    public ItemPage(WebDriver driver) {
        super(driver, 20);
    }

    // ==================== Navigation ====================

    /**
     * Klik tab "Item" di navigation tabs.
     */
    public ItemPage clickTabItem() throws InterruptedException {
        System.out.println("  [ItemPage] Klik tab Item...");
        try {
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabItem));
            js.executeScript("arguments[0].click();", tab);
        } catch (Exception e) {
            js.executeScript(
                "var tabs=document.querySelectorAll('.v-tab');" +
                "for(var i=0;i<tabs.length;i++){" +
                "  if(tabs[i].textContent.trim().toLowerCase()==='item'){" +
                "    tabs[i].click(); break;" +
                "  }}"
            );
        }
        Thread.sleep(2000);
        return this;
    }

    /**
     * Klik tombol Add untuk membuka form item baru.
     */
    public ItemPage clickAddItem() throws InterruptedException {
        System.out.println("  [ItemPage] Klik Add Item...");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnAddItem));
            js.executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            js.executeScript(
                "var btns=document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  var t=btns[i].textContent.trim();" +
                "  if(t.includes('Add')||t.includes('Tambah')){" +
                "    btns[i].click(); break;" +
                "  }}"
            );
        }
        Thread.sleep(3000);

        try {
            wait.until(driver ->
                driver.getCurrentUrl().contains("/form") ||
                !driver.findElements(By.xpath("//input[@type='text']")).isEmpty());
        } catch (Exception e) {
            System.out.println("  [WARN] Form item mungkin belum terbuka sempurna.");
        }
        return this;
    }

    // ==================== Create Item ====================

    /**
     * Mengisi dan menyimpan item baru secara penuh.
     *
     * @param code         Kode item (auto-timestamp)
     * @param name         Nama item
     * @param categoryName Nama category yang sudah dibuat
     * @param unitName     Nama unit (null/empty → pilih index pertama)
     * @param optionName   Nama option yang sudah dibuat
     * @param brand        Brand
     * @param information  Teks informasi (misal: "Test Otomatis")
     */
    public ItemPage fillAndCreateItem(
            String code, String name, String categoryName,
            String unitName, String optionName,
            String brand, String information) throws InterruptedException {

        System.out.println("  [ItemPage] Mengisi form Create Item: '" + name + "'");

        fillField(inputCode, code, "Code");
        Thread.sleep(300);

        fillField(inputName, name, "Name");
        Thread.sleep(300);

        if (categoryName != null && !categoryName.isEmpty()) {
            selectDropdown("Category", categoryName);
            Thread.sleep(800);
        }

        if (unitName != null && !unitName.isEmpty()) {
            selectDropdown("Unit", unitName);
        } else {
            selectDropdownFirstOption("Unit");
        }
        Thread.sleep(800);

        if (optionName != null && !optionName.isEmpty()) {
            selectDropdown("Option", optionName);
            Thread.sleep(800);
        }

        selectOrderMethodFEFO();
        Thread.sleep(500);

        fillField(inputBrand, brand, "Brand");
        Thread.sleep(300);

        fillField(inputInformation, information, "Information");
        Thread.sleep(300);

        enableHasVariantsToggle();
        Thread.sleep(1000);

        addVariantWithGenerations();
        Thread.sleep(1000);

        fillDimensionByOptionName(optionName);
        Thread.sleep(500);

        return this;
    }

    /**
     * Klik Simpan dan verifikasi item di tabel.
     */
    public ItemPage saveAndVerify(String itemName) throws InterruptedException {
        System.out.println("  [ItemPage] Klik Simpan...");
        clickSaveButton();
        checkBackendOrValidationError();
        Thread.sleep(4000);

        try {
            wait.until(driver ->
                !driver.getCurrentUrl().contains("/form") ||
                !driver.findElements(By.xpath("//*[contains(@class,'success')]")).isEmpty()
            );
        } catch (Exception e) {
            System.out.println("  [WARN] Menunggu konfirmasi save...");
            Thread.sleep(2000);
        }

        if (driver.getCurrentUrl().contains("/form")) {
            System.out.println("  [INFO] Masih di form, kembali ke list...");
            driver.navigate().back();
            Thread.sleep(2000);
        }

        assertItemInTable(itemName);
        return this;
    }

    // ==================== Read / Verify ====================

    /**
     * Verifikasi bahwa item ada di tabel list.
     */
    public boolean isItemInTable(String itemName) {
        System.out.println("  [ItemPage] Verifikasi item '" + itemName + "' di tabel...");
        try {
            driver.get(ITEM_TAB_URL);
            Thread.sleep(3000);

            Boolean found = (Boolean) js.executeScript(
                "var rows = document.querySelectorAll('tr, td');" +
                "for(var i=0; i<rows.length; i++){" +
                "  if(rows[i].textContent.trim().includes(arguments[0]) && rows[i].offsetParent !== null){" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;",
                itemName
            );
            if (Boolean.TRUE.equals(found)) {
                System.out.println("  [ItemPage] Item '" + itemName + "' DITEMUKAN di tabel.");
                return true;
            }

            try {
                By rowLocator = By.xpath(
                    "//td[contains(normalize-space(text())," + xpathString(itemName) + ")] | " +
                    "//tr[contains(normalize-space(.)," + xpathString(itemName) + ")]");
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
                System.out.println("  [ItemPage] Item '" + itemName + "' DITEMUKAN (via XPath).");
                return true;
            } catch (Exception ignored) {}

            System.out.println("  [ItemPage] Item '" + itemName + "' TIDAK DITEMUKAN di tabel.");
            return false;
        } catch (Exception e) {
            System.out.println("  [ItemPage] isItemInTable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assert item ada di tabel (TestNG assertion).
     */
    public void assertItemInTable(String itemName) {
        Assert.assertTrue(isItemInTable(itemName),
                "[FAIL] Item '" + itemName + "' tidak ditemukan di tabel setelah disimpan.");
        System.out.println("  [PASS] Item '" + itemName + "' terverifikasi di tabel list.");
    }

    // ==================== Update Item ====================

    /**
     * Buka form edit item dan lakukan update.
     *
     * @param itemName Nama item yang akan diedit
     * @param newName  Nama baru (null jika tidak diubah)
     * @param newBrand Brand baru (null jika tidak diubah)
     */
    public ItemPage updateItem(String itemName, String newName, String newBrand) throws InterruptedException {
        System.out.println("  [ItemPage] Mengupdate Item: '" + itemName + "'");

        clickEditButton(itemName);
        Thread.sleep(2000);

        clickAutoGenerateSKUButton();
        Thread.sleep(1000);

        if (newName != null && !newName.isEmpty()) {
            fillField(inputName, newName, "Name (update)");
            Thread.sleep(300);
        }

        if (newBrand != null && !newBrand.isEmpty()) {
            fillField(inputBrand, newBrand, "Brand (update)");
            Thread.sleep(300);
        }

        clickSaveButton();
        checkBackendOrValidationError();
        Thread.sleep(4000);

        try {
            wait.until(driver -> !driver.getCurrentUrl().contains("/form"));
        } catch (Exception e) {
            Thread.sleep(2000);
        }

        String verifyName = (newName != null && !newName.isEmpty()) ? newName : itemName;
        assertItemInTable(verifyName);
        System.out.println("  [ItemPage] Item berhasil diupdate.");
        return this;
    }

    // ==================== Delete Item ====================

    /**
     * Hapus item dari tabel.
     */
    public ItemPage deleteItem(String itemName) throws InterruptedException {
        System.out.println("  [ItemPage] Menghapus Item: '" + itemName + "'");

        clickDeleteButton(itemName);
        Thread.sleep(1000);
        confirmDeleteDialog();
        checkBackendOrValidationError();
        Thread.sleep(3000);

        System.out.println("  [ItemPage] Item '" + itemName + "' berhasil dihapus.");
        return this;
    }

    // ==================== Private: Form Helpers ====================

    /**
     * Isi input field dengan value tertentu (Ctrl+A → clear → sendKeys).
     */
    private void fillField(By locator, String value, String fieldName) {
        if (value == null || value.isEmpty()) return;
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            input.click();
            input.sendKeys(Keys.CONTROL + "a");
            input.clear();
            input.sendKeys(value);
            System.out.println("  [ItemPage] Isi field '" + fieldName + "': " + value);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal mengisi field '" + fieldName + "': " + e.getMessage());
        }
    }

    /**
     * Pilih item dari dropdown Vuetify berdasarkan label dan teks pilihan.
     */
    private void selectDropdown(String labelText, String optionText) throws InterruptedException {
        System.out.println("  [ItemPage] Pilih dropdown '" + labelText + "' = '" + optionText + "'");
        try {
            By ddInput = By.xpath(
                "//label[contains(text(),'" + labelText + "')]/following::input[1] | " +
                "//div[contains(@class,'v-field')][.//label[contains(text(),'" + labelText + "')]]//input"
            );
            WebElement dd = wait.until(ExpectedConditions.elementToBeClickable(ddInput));
            js.executeScript("arguments[0].click();", dd);
            Thread.sleep(500);
            dd.sendKeys(optionText);
            Thread.sleep(800);

            By listItem = By.xpath(
                "//div[contains(@class,'v-list-item')][.//*[contains(text(),'" + optionText + "')] or " +
                "contains(text(),'" + optionText + "')]");
            WebElement item = new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(ExpectedConditions.elementToBeClickable(listItem));
            js.executeScript("arguments[0].click();", item);
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal pilih dropdown '" + labelText + "'='" + optionText + "': " + e.getMessage());
            try {
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                Thread.sleep(300);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Pilih opsi pertama dari dropdown (fallback jika unitName kosong).
     */
    private void selectDropdownFirstOption(String labelText) throws InterruptedException {
        System.out.println("  [ItemPage] Pilih item pertama dari dropdown '" + labelText + "'");
        try {
            By ddInput = By.xpath("//label[contains(text(),'" + labelText + "')]/following::input[1]");
            WebElement dd = wait.until(ExpectedConditions.elementToBeClickable(ddInput));
            js.executeScript("arguments[0].click();", dd);
            Thread.sleep(800);

            By firstItem = By.xpath("//div[contains(@class,'v-list-item')][1]");
            WebElement item = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(firstItem));
            js.executeScript("arguments[0].click();", item);
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal memilih opsi pertama dropdown '" + labelText + "'");
        }
    }

    /**
     * Pilih Order Method = FEFO.
     */
    private void selectOrderMethodFEFO() throws InterruptedException {
        System.out.println("  [ItemPage] Pilih Order Method: FEFO");
        try {
            By fefoLocator = By.xpath(
                "//span[contains(text(),'FEFO')]/ancestor::*[self::button or self::div[@role='radio'] or " +
                "self::label or self::div[contains(@class,'v-chip') or contains(@class,'v-btn')]][1] | " +
                "//input[@type='radio' and contains(@value,'FEFO')]/following-sibling::label | " +
                "//div[contains(@class,'v-chip')][contains(text(),'FEFO')]"
            );
            WebElement fefo = wait.until(ExpectedConditions.elementToBeClickable(fefoLocator));
            js.executeScript("arguments[0].click();", fefo);
        } catch (Exception e) {
            Boolean found = (Boolean) js.executeScript(
                "var els = document.querySelectorAll('button, .v-chip, .v-btn, label, input');" +
                "for(var i=0;i<els.length;i++){" +
                "  if(els[i].textContent.trim()==='FEFO'){" +
                "    els[i].click(); return true;" +
                "  }}" +
                "return false;"
            );
            if (Boolean.FALSE.equals(found)) {
                System.out.println("  [WARN] FEFO option tidak ditemukan.");
            }
        }
        Thread.sleep(300);
    }

    /**
     * Aktifkan toggle "This Item Has Variants".
     */
    private void enableHasVariantsToggle() throws InterruptedException {
        System.out.println("  [ItemPage] Aktifkan toggle Has Variants...");
        try {
            By toggleLocator = By.xpath(
                "//div[contains(@class,'v-switch')][.//*[contains(text(),'Variant') or contains(text(),'Varian')]]//input | " +
                "//label[contains(text(),'Variant') or contains(text(),'Varian')]/preceding-sibling::input"
            );
            WebElement toggle = wait.until(ExpectedConditions.presenceOfElementLocated(toggleLocator));
            if (!toggle.isSelected()) {
                WebElement toggleContainer = toggle.findElement(By.xpath(
                    "./ancestor::div[contains(@class,'v-switch')][1] | " +
                    "./ancestor::div[contains(@class,'v-input')][1]"
                ));
                js.executeScript("arguments[0].click();", toggleContainer);
            }
        } catch (Exception e) {
            js.executeScript(
                "var els=document.querySelectorAll('.v-switch, .v-input--switch');" +
                "for(var i=0;i<els.length;i++){" +
                "  if(els[i].textContent.includes('Variant')||els[i].textContent.includes('Varian')){" +
                "    var inp=els[i].querySelector('input[type=checkbox]');" +
                "    if(inp && !inp.checked){els[i].click();}" +
                "    return;" +
                "  }}"
            );
        }
        Thread.sleep(500);
    }

    /**
     * Klik "Add Variant", kemudian "Generate SKU" dan "Generate Barcode".
     */
    private void addVariantWithGenerations() throws InterruptedException {
        System.out.println("  [ItemPage] Klik Add Variant...");
        try {
            WebElement addVariantBtn = wait.until(ExpectedConditions.elementToBeClickable(btnAddVariant));
            js.executeScript("arguments[0].click();", addVariantBtn);
        } catch (Exception e) {
            js.executeScript(
                "var btns=document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  if(btns[i].textContent.includes('Add Variant')||btns[i].textContent.includes('Tambah Variant')){" +
                "    btns[i].click(); break;" +
                "  }}"
            );
        }
        Thread.sleep(1500);

        System.out.println("  [ItemPage] Klik Generate SKU...");
        try {
            List<WebElement> skuBtns = driver.findElements(btnGenerateSKU);
            if (!skuBtns.isEmpty()) {
                js.executeScript("arguments[0].click();", skuBtns.get(0));
            } else {
                js.executeScript(
                    "var btns=document.querySelectorAll('button');" +
                    "for(var i=0;i<btns.length;i++){" +
                    "  if(btns[i].textContent.includes('Generate SKU')){ btns[i].click(); break; }}"
                );
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Generate SKU gagal: " + e.getMessage());
        }
        Thread.sleep(1000);

        System.out.println("  [ItemPage] Klik Generate Barcode...");
        try {
            List<WebElement> barcodeBtns = driver.findElements(btnGenerateBarcode);
            if (!barcodeBtns.isEmpty()) {
                js.executeScript("arguments[0].click();", barcodeBtns.get(0));
            } else {
                js.executeScript(
                    "var btns=document.querySelectorAll('button');" +
                    "for(var i=0;i<btns.length;i++){" +
                    "  if(btns[i].textContent.includes('Generate Barcode')){ btns[i].click(); break; }}"
                );
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Generate Barcode gagal: " + e.getMessage());
        }
        Thread.sleep(1000);
    }

    /**
     * Isi dimensi item secara otomatis berdasarkan nama option.
     * Lookup DIMENSION_MAP → jika tidak ada, gunakan default.
     */
    private void fillDimensionByOptionName(String optionName) {
        if (optionName == null || optionName.isEmpty()) return;

        String[] dims = null;
        for (Map.Entry<String, String[]> entry : DIMENSION_MAP.entrySet()) {
            if (optionName.toLowerCase().contains(entry.getKey())) {
                dims = entry.getValue();
                break;
            }
        }
        if (dims == null) {
            dims = DIMENSION_MAP.get("default");
        }

        System.out.println("  [ItemPage] Mengisi Dimensi untuk Option '" + optionName +
                "': " + dims[0] + " x " + dims[1] + " x " + dims[2]);

        String[] finalDims = dims;
        js.executeScript(
            "var labels=document.querySelectorAll('label');" +
            "var filled=0;" +
            "for(var i=0;i<labels.length;i++){" +
            "  var t=labels[i].textContent.trim().toLowerCase();" +
            "  var input=labels[i].nextElementSibling || " +
            "    (labels[i].parentElement && labels[i].parentElement.querySelector('input'));" +
            "  if(!input||input.tagName!=='INPUT') continue;" +
            "  if((t.includes('panjang')||t.includes('length')||t.includes(' p ')) && filled===0){" +
            "    input.value=arguments[0]; input.dispatchEvent(new Event('input',{bubbles:true})); filled++;" +
            "  } else if((t.includes('lebar')||t.includes('width')||t.includes(' l ')) && filled===1){" +
            "    input.value=arguments[1]; input.dispatchEvent(new Event('input',{bubbles:true})); filled++;" +
            "  } else if((t.includes('tinggi')||t.includes('height')||t.includes(' t ')) && filled===2){" +
            "    input.value=arguments[2]; input.dispatchEvent(new Event('input',{bubbles:true})); filled++;" +
            "  }" +
            "}", finalDims[0], finalDims[1], finalDims[2]
        );
    }

    /**
     * Klik tombol "Auto-generate SKU" pada halaman update item.
     */
    private void clickAutoGenerateSKUButton() throws InterruptedException {
        System.out.println("  [ItemPage] Klik Auto-generate SKU...");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnAutoGenerateSKU));
            js.executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            js.executeScript(
                "var btns=document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  if(btns[i].textContent.includes('Auto-generate SKU')||btns[i].textContent.includes('Auto Generate SKU')){" +
                "    btns[i].click(); break;" +
                "  }}"
            );
        }
        Thread.sleep(800);
    }

    private void clickSaveButton() throws InterruptedException {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnSave));
            js.executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            js.executeScript(
                "var btns=document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  var t=btns[i].textContent.trim();" +
                "  if(t.includes('Simpan')||t.includes('Save')){ btns[i].click(); break; }}"
            );
        }
    }

    // confirmDeleteDialog() diwarisi dari BasePage (protected)
}
