package org.test.inventory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

/**
 * CategoryPage - Page Object untuk Tab CATEGORY di Inventory Setting.
 *
 * Mewarisi BasePage untuk helper Selenium (xpathString, clearAndSetInputValueJS,
 * clickEditButton, clickDeleteButton, confirmDeleteDialog, checkBackendOrValidationError,
 * waitForDialogToClose).
 *
 * Logic khusus Category:
 * - Navigasi via URL (?tab=category)
 * - Input via JS setValue + Vue event dispatch (Vuetify 3)
 * - Separator "/" untuk menentukan Parent/Child
 * - Dropdown Parent dipilih via Vuetify combobox
 */
public class CategoryPage extends BasePage {

    private static final String CATEGORY_TAB_URL = "https://web.vedata.id/inventory/setting?tab=category";

    public CategoryPage(WebDriver driver) {
        super(driver, 20);
    }

    // ==================== Navigation ====================

    /**
     * Navigasi langsung ke tab Category via URL.
     */
    public CategoryPage navigateToCategoryTab() throws InterruptedException {
        System.out.println("  [CategoryPage] Navigasi ke tab Category...");
        driver.get(CATEGORY_TAB_URL);
        Thread.sleep(3000);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("leftSidebar")));
        } catch (Exception ignored) {}
        Thread.sleep(1000);
        return this;
    }

    public CategoryPage clickTabCategory() throws InterruptedException {
        return navigateToCategoryTab();
    }

    // ==================== Create ====================

    /**
     * Membuat Category baru. Jika rawCategory mengandung "/", format "Parent/Child"
     * akan digunakan untuk memilih parent dari dropdown.
     */
    public CategoryPage createCategory(String rawCategory) throws InterruptedException {
        System.out.println("  [CategoryPage] Membuat Category: '" + rawCategory + "'");
        if (rawCategory.contains("/")) {
            String[] parts = rawCategory.split("/", 2);
            String parent  = parts[0].trim();
            String child   = parts[1].trim();
            createWithParent(parent, child);
        } else {
            createWithoutParent(rawCategory.trim());
        }
        System.out.println("  [CategoryPage] Category '" + rawCategory + "' berhasil dibuat.");
        return this;
    }

    /**
     * Buat category dengan parent (format: "Parent / Child").
     */
    public CategoryPage createWithParent(String parent, String child) throws InterruptedException {
        clickAddButton();
        Thread.sleep(2000);
        selectParentDropdown(parent);
        Thread.sleep(1000);
        clearAndSetInputValueJS(child, 1); // field kedua = Name child
        clickSaveButton();
        checkBackendOrValidationError();
        waitForDialogToClose();
        Thread.sleep(2000);
        return this;
    }

    /**
     * Buat category tanpa parent.
     */
    public CategoryPage createWithoutParent(String name) throws InterruptedException {
        clickAddButton();
        Thread.sleep(2000);
        clearAndSetInputValueJS(name, 0); // field pertama = Name
        clickSaveButton();
        checkBackendOrValidationError();
        waitForDialogToClose();
        Thread.sleep(2000);
        return this;
    }

    // ==================== Read / Verify ====================

    /**
     * Verifikasi apakah category tersedia di tabel list.
     * Selalu reload tab sebelum memeriksa agar data ter-persist di backend.
     */
    public boolean isCategoryInTable(String rawCategory) {
        String searchName = rawCategory.contains("/")
            ? rawCategory.split("/", 2)[1].trim()
            : rawCategory.trim();
        System.out.println("  [CategoryPage] Verifikasi category '" + searchName + "'...");
        try {
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3000);

            Boolean found = (Boolean) js.executeScript(
                "var rows = document.querySelectorAll('tr, td');" +
                "for(var i=0; i<rows.length; i++){" +
                "  if(rows[i].textContent.trim().includes(arguments[0]) && rows[i].offsetParent !== null){" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;",
                searchName
            );
            if (Boolean.TRUE.equals(found)) {
                System.out.println("  [CategoryPage] Category '" + searchName + "' DITEMUKAN di tabel.");
                return true;
            }

            // Fallback: XPath wait
            try {
                By rowLocator = By.xpath(
                    "//tr[contains(normalize-space(.)," + xpathString(searchName) + ")] | " +
                    "//td[contains(normalize-space(text())," + xpathString(searchName) + ")]");
                new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
                System.out.println("  [CategoryPage] Category '" + searchName + "' DITEMUKAN (via XPath).");
                return true;
            } catch (Exception ignored) {}

            System.out.println("  [CategoryPage] Category '" + searchName + "' TIDAK DITEMUKAN.");
            return false;
        } catch (Exception e) {
            System.out.println("  [CategoryPage] isCategoryInTable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assert category ada di tabel (TestNG assertion).
     */
    public void assertCategoryInTable(String rawCategory) {
        String displayName = rawCategory.contains("/")
            ? rawCategory.split("/", 2)[1].trim()
            : rawCategory.trim();
        Assert.assertTrue(isCategoryInTable(rawCategory),
            "[FAIL] Category '" + displayName + "' tidak ditemukan di tabel.");
        System.out.println("  [PASS] Category '" + displayName + "' terverifikasi.");
    }

    // ==================== Update ====================

    /**
     * Update nama category. Selalu reload tab sebelum mencari baris.
     */
    public CategoryPage updateCategory(String oldName, String newName) throws InterruptedException {
        System.out.println("  [CategoryPage] Update Category '" + oldName + "' → '" + newName + "'");
        driver.get(CATEGORY_TAB_URL);
        Thread.sleep(3000);

        try {
            By rowLocator = By.xpath(
                "//tr[contains(normalize-space(.)," + xpathString(oldName) + ")] | " +
                "//td[contains(normalize-space(text())," + xpathString(oldName) + ")]");
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
        } catch (Exception e) {
            System.out.println("  [WARN] Row '" + oldName + "' tidak ditemukan sebelum edit.");
        }

        clickEditButton(oldName);
        Thread.sleep(2500);
        clearAndSetInputValueJS(newName, 0);
        Thread.sleep(500);
        clickSaveButton();
        checkBackendOrValidationError();
        waitForDialogToClose();
        Thread.sleep(2500);
        System.out.println("  [CategoryPage] Category diupdate ke '" + newName + "'");
        return this;
    }

    // ==================== Delete ====================

    /**
     * Hapus category. Navigasi ke tab sebelum mencari baris jika perlu.
     */
    public CategoryPage deleteCategory(String categoryName) throws InterruptedException {
        System.out.println("  [CategoryPage] Hapus Category: '" + categoryName + "'");

        if (!driver.getCurrentUrl().contains("tab=category") ||
            driver.findElements(By.xpath("//*[contains(text(),'Tambah') or contains(text(),'Add')]")).isEmpty()) {
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3000);
        }

        try {
            By rowLocator = By.xpath(
                "//tr[contains(.," + xpathString(categoryName) + ")] | " +
                "//td[contains(text()," + xpathString(categoryName) + ")]");
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
        } catch (Exception e) {
            System.out.println("  [WARN] Row '" + categoryName + "' tidak ditemukan sebelum delete.");
        }

        clickDeleteButton(categoryName);
        Thread.sleep(1000);
        confirmDeleteDialog();
        checkBackendOrValidationError();
        Thread.sleep(2000);
        return this;
    }

    // ==================== Private Helpers ====================

    private void clickAddButton() throws InterruptedException {
        js.executeScript(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0;i<btns.length;i++){" +
            "  var t = btns[i].textContent.trim();" +
            "  if(t.includes('Add') || t.includes('Tambah')){ btns[i].click(); return; }}"
        );
        Thread.sleep(1500);
    }

    private void clickSaveButton() throws InterruptedException {
        js.executeScript(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0;i<btns.length;i++){" +
            "  var t = btns[i].textContent.trim();" +
            "  if((t.includes('Simpan')||t.includes('Save')) && !btns[i].disabled){ btns[i].click(); return; }}"
        );
        Thread.sleep(1000);
    }

    /**
     * Pilih Parent dropdown via Vuetify combobox.
     * Klik label "parent/induk" → ketik nama → klik item di overlay.
     */
    private void selectParentDropdown(String parentName) throws InterruptedException {
        System.out.println("  [CategoryPage] Pilih Parent: '" + parentName + "'");
        try {
            js.executeScript(
                "var labels = document.querySelectorAll('label');" +
                "for(var i=0;i<labels.length;i++){" +
                "  var t = labels[i].textContent.trim().toLowerCase();" +
                "  if(t.includes('parent') || t.includes('induk')){" +
                "    var field = labels[i].closest('.v-field') || labels[i].parentElement;" +
                "    if(field){ field.click(); return; }" +
                "  }}"
            );
            Thread.sleep(800);

            List<WebElement> inputs = driver.findElements(
                By.xpath("//input[@type='text' and not(@readonly)]"));
            for (WebElement inp : inputs) {
                if (inp.isDisplayed()) {
                    inp.sendKeys(parentName);
                    break;
                }
            }
            Thread.sleep(1000);

            Boolean picked = (Boolean) js.executeScript(
                "var items = document.querySelectorAll('.v-list-item, .v-overlay--active .v-list-item');" +
                "for(var i=0;i<items.length;i++){" +
                "  if(items[i].textContent.trim().includes(arguments[0])){" +
                "    items[i].click(); return true;" +
                "  }}" +
                "return false;", parentName
            );
            if (Boolean.FALSE.equals(picked)) {
                System.out.println("  [WARN] Parent '" + parentName + "' tidak ditemukan di dropdown.");
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal pilih Parent: " + e.getMessage());
        }
        Thread.sleep(500);
    }
}
