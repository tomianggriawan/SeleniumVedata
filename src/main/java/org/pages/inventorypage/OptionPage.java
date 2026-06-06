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
import java.util.List;

/**
 * OptionPage - Page Object untuk Tab OPTION di Category Setting.
 *
 * Mewarisi BasePage untuk helper Selenium (xpathString, clearAndSetInputValueJS,
 * clickEditButton, clickDeleteButton, confirmDeleteDialog, checkBackendOrValidationError,
 * waitForDialogToClose).
 *
 * Logic khusus Option:
 * - Navigasi via URL (?tab=option)
 * - Input via JS setValue + Vue event dispatch (Vuetify 3)
 * - Menambahkan Variant: input ke input terakhir yang visible setelah klik Add Variant
 */
public class OptionPage extends BasePage {

    private static final String OPTION_TAB_URL = "https://web.vedata.id/inventory/setting?tab=option";

    public OptionPage(WebDriver driver) {
        super(driver, 20);
    }

    // ==================== Navigation ====================

    public OptionPage navigateToOptionTab() throws InterruptedException {
        System.out.println("  [OptionPage] Navigasi ke tab Option...");
        driver.get(OPTION_TAB_URL);
        Thread.sleep(3000);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("leftSidebar")));
        } catch (Exception ignored) {}
        Thread.sleep(1000);
        return this;
    }

    public OptionPage clickTabOption() throws InterruptedException {
        return navigateToOptionTab();
    }

    // ==================== Create ====================

    /**
     * Buat Option baru beserta satu variant.
     *
     * @param optionName   Nama option/varian (misal: "Kemasan")
     * @param variantValue Nilai varian pertama (misal: "Pouch")
     */
    public OptionPage createOption(String optionName, String variantValue) throws InterruptedException {
        System.out.println("  [OptionPage] Membuat Option: '" + optionName + "' variant: '" + variantValue + "'");

        clickAddButton();
        Thread.sleep(2000);

        // Isi Option Name (input ke-0)
        clearAndSetInputValueJS(optionName, 0);
        Thread.sleep(500);

        // Klik Add Variant
        clickAddVariantButton();
        Thread.sleep(1000);

        // Isi Variant Value (input terakhir yang visible setelah Add Variant)
        setLastInputValueJS(variantValue);
        Thread.sleep(500);

        clickSaveButton();
        checkBackendOrValidationError();
        waitForDialogToClose();
        Thread.sleep(2000);

        System.out.println("  [OptionPage] Option '" + optionName + "' berhasil dibuat.");
        return this;
    }

    // ==================== Read / Verify ====================

    /**
     * Verifikasi apakah option tersedia di tabel list.
     */
    public boolean isOptionInTable(String optionName) {
        System.out.println("  [OptionPage] Verifikasi Option '" + optionName + "'...");
        try {
            driver.get(OPTION_TAB_URL);
            Thread.sleep(3000);

            Boolean found = (Boolean) js.executeScript(
                "var rows = document.querySelectorAll('tr, td');" +
                "for(var i=0; i<rows.length; i++){" +
                "  if(rows[i].textContent.trim().includes(arguments[0]) && rows[i].offsetParent !== null){" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;",
                optionName
            );
            if (Boolean.TRUE.equals(found)) {
                System.out.println("  [OptionPage] Option '" + optionName + "' DITEMUKAN di tabel.");
                return true;
            }

            // Fallback: XPath wait
            try {
                By rowLocator = By.xpath(
                    "//tr[contains(normalize-space(.)," + xpathString(optionName) + ")] | " +
                    "//td[contains(normalize-space(text())," + xpathString(optionName) + ")]");
                new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
                System.out.println("  [OptionPage] Option '" + optionName + "' DITEMUKAN (via XPath).");
                return true;
            } catch (Exception ignored) {}

            System.out.println("  [OptionPage] Option '" + optionName + "' TIDAK DITEMUKAN.");
            return false;
        } catch (Exception e) {
            System.out.println("  [OptionPage] isOptionInTable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assert option ada di tabel (TestNG assertion).
     */
    public void assertOptionInTable(String optionName) {
        Assert.assertTrue(isOptionInTable(optionName),
            "[FAIL] Option '" + optionName + "' tidak ditemukan di tabel.");
        System.out.println("  [PASS] Option '" + optionName + "' terverifikasi.");
    }

    // ==================== Update ====================

    /**
     * Update nama option. Selalu reload tab sebelum mencari baris.
     */
    public OptionPage updateOption(String oldName, String newName) throws InterruptedException {
        System.out.println("  [OptionPage] Update Option '" + oldName + "' → '" + newName + "'");
        driver.get(OPTION_TAB_URL);
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
        System.out.println("  [OptionPage] Option diupdate ke '" + newName + "'");
        return this;
    }

    // ==================== Delete ====================

    /**
     * Hapus option. Navigasi ke tab sebelum mencari baris jika perlu.
     */
    public OptionPage deleteOption(String optionName) throws InterruptedException {
        System.out.println("  [OptionPage] Hapus Option: '" + optionName + "'");

        if (!driver.getCurrentUrl().contains("tab=option") ||
            driver.findElements(By.xpath("//*[contains(text(),'Tambah') or contains(text(),'Add')]")).isEmpty()) {
            driver.get(OPTION_TAB_URL);
            Thread.sleep(3000);
        }

        try {
            By rowLocator = By.xpath(
                "//tr[contains(.," + xpathString(optionName) + ")] | " +
                "//td[contains(text()," + xpathString(optionName) + ")]");
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
        } catch (Exception e) {
            System.out.println("  [WARN] Row '" + optionName + "' tidak ditemukan sebelum delete.");
        }

        clickDeleteButton(optionName);
        Thread.sleep(1000);
        confirmDeleteDialog();
        checkBackendOrValidationError();
        Thread.sleep(2000);
        System.out.println("  [OptionPage] Option '" + optionName + "' dihapus.");
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
     * Klik tombol Add Variant di dalam form Option.
     * Fallback: tombol icon (+) terakhir di dalam form.
     */
    private void clickAddVariantButton() throws InterruptedException {
        System.out.println("  [OptionPage] Klik Add Variant...");
        Boolean clicked = (Boolean) js.executeScript(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0;i<btns.length;i++){" +
            "  var t = btns[i].textContent.trim();" +
            "  if(t.toLowerCase().includes('add variant') || t.toLowerCase().includes('tambah variant')){" +
            "    btns[i].click(); return true;" +
            "  }}" +
            "var plusBtns = document.querySelectorAll('.v-btn--icon, button[class*=\"icon\"]');" +
            "if(plusBtns.length > 0){ plusBtns[plusBtns.length-1].click(); return true; }" +
            "return false;"
        );
        if (Boolean.FALSE.equals(clicked)) {
            System.out.println("  [WARN] Add Variant button tidak ditemukan.");
        }
        Thread.sleep(800);
    }

    /**
     * Set nilai ke input terakhir yang visible (untuk variant yang baru ditambahkan).
     */
    private void setLastInputValueJS(String value) throws InterruptedException {
        System.out.println("  [OptionPage] Set last input = '" + value + "'");

        try {
            By dialogInputLocator = By.cssSelector(
                ".v-overlay--active input:not([type]), .v-overlay--active input[type='text'], " +
                ".v-dialog input:not([type]), .v-dialog input[type='text'], " +
                ".v-card input:not([type]), .v-card input[type='text']"
            );
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(dialogInputLocator));
        } catch (Exception e) {
            System.out.println("  [WARN] Input field inside dialog/overlay not visible, continuing...");
        }

        Boolean success = (Boolean) js.executeScript(
            "var selectors = [" +
            "  '.v-overlay--active input:not([type]), .v-overlay--active input[type=text]'," +
            "  '.v-dialog input:not([type]), .v-dialog input[type=text]'," +
            "  '.v-card input:not([type]), .v-card input[type=text]'" +
            "];" +
            "for(var s=0;s<selectors.length;s++){" +
            "  var inputs = document.querySelectorAll(selectors[s]);" +
            "  var vis = [];" +
            "  for(var i=0;i<inputs.length;i++){ if(inputs[i].offsetParent!==null) vis.push(inputs[i]); }" +
            "  if(vis.length > 0){" +
            "    var inp = vis[vis.length-1];" +
            "    var nv = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "    nv.call(inp, arguments[0]);" +
            "    inp.dispatchEvent(new Event('input',{bubbles:true}));" +
            "    inp.dispatchEvent(new Event('change',{bubbles:true}));" +
            "    inp.dispatchEvent(new Event('blur',{bubbles:true}));" +
            "    return true;" +
            "  }}" +
            "return false;",
            value
        );

        if (Boolean.FALSE.equals(success)) {
            List<WebElement> inputs = driver.findElements(
                By.xpath("//input[@type='text' and not(@readonly) and not(@disabled)]"));
            WebElement last = null;
            for (WebElement inp : inputs) {
                if (inp.isDisplayed()) last = inp;
            }
            if (last != null) {
                last.click();
                last.sendKeys(Keys.CONTROL + "a");
                last.clear();
                last.sendKeys(value);
                js.executeScript(
                    "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                    "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", last);
            }
        }
        Thread.sleep(400);
    }
}
