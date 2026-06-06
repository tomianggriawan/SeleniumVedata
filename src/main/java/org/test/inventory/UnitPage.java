package org.test.inventory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;
import org.testng.Assert;

import java.time.Duration;

/**
 * UnitPage - Page Object untuk Tab UNIT di Inventory Setting.
 *
 * Mewarisi BasePage untuk helper Selenium (xpathString, clearAndSetInputValueJS,
 * clickEditButton, clickDeleteButton, confirmDeleteDialog, checkBackendOrValidationError,
 * waitForDialogToClose).
 *
 * Logic khusus Unit:
 * - Navigasi via URL (?tab=unit) lebih reliable daripada klik tab
 * - Input Vuetify 3 via JS setValue + dispatch event
 * - Tunggu dialog benar-benar tertutup setelah save
 * - Reload halaman sebelum verifikasi tabel
 */
public class UnitPage extends BasePage {

    private static final String UNIT_TAB_URL = "https://web.vedata.id/inventory/setting?tab=unit";

    public UnitPage(WebDriver driver) {
        super(driver, 20);
    }

    // ==================== Navigation ====================

    /**
     * Navigasi langsung ke Unit tab via URL.
     */
    public UnitPage navigateToUnitTab() throws InterruptedException {
        System.out.println("  [UnitPage] Navigasi ke tab Unit via URL...");
        driver.get(UNIT_TAB_URL);
        Thread.sleep(3000);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'leftSidebar')]")));
        } catch (Exception ignored) {}
        Thread.sleep(1000);
        return this;
    }

    public UnitPage clickTabUnit() throws InterruptedException {
        return navigateToUnitTab();
    }

    // ==================== Create ====================

    /**
     * Buat Unit baru dengan nama yang diberikan.
     */
    public UnitPage createUnit(String unitName) throws InterruptedException {
        System.out.println("  [UnitPage] Membuat Unit: '" + unitName + "'");

        clickAddButton();
        Thread.sleep(2000);
        clearAndSetInputValueJS(unitName, 0);
        clickSaveButton();
        checkBackendOrValidationError();
        waitForDialogToClose();
        Thread.sleep(2000);

        System.out.println("  [UnitPage] Unit '" + unitName + "' berhasil dibuat.");
        return this;
    }

    // ==================== Read / Verify ====================

    /**
     * Verifikasi apakah unit tersedia di tabel list.
     * Selalu reload tab sebelum memeriksa agar data ter-persist di backend.
     */
    public boolean isUnitInTable(String unitName) {
        System.out.println("  [UnitPage] Verifikasi unit '" + unitName + "' di tabel...");
        try {
            driver.get(UNIT_TAB_URL);
            Thread.sleep(3000);

            Boolean found = (Boolean) js.executeScript(
                "var rows = document.querySelectorAll('tr, td');" +
                "for(var i=0; i<rows.length; i++){" +
                "  if(rows[i].textContent.trim().includes(arguments[0]) && rows[i].offsetParent !== null){" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;",
                unitName
            );
            if (Boolean.TRUE.equals(found)) {
                System.out.println("  [UnitPage] Unit '" + unitName + "' DITEMUKAN di tabel.");
                return true;
            }

            // Fallback: XPath wait
            try {
                By rowLocator = By.xpath(
                    "//tr[contains(normalize-space(.)," + xpathString(unitName) + ")] | " +
                    "//td[contains(normalize-space(text())," + xpathString(unitName) + ")]");
                new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
                System.out.println("  [UnitPage] Unit '" + unitName + "' DITEMUKAN (via XPath).");
                return true;
            } catch (Exception ignored) {}

            System.out.println("  [UnitPage] Unit '" + unitName + "' TIDAK DITEMUKAN.");
            return false;
        } catch (Exception e) {
            System.out.println("  [UnitPage] isUnitInTable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assert unit ada di tabel (TestNG assertion).
     */
    public void assertUnitInTable(String unitName) {
        Assert.assertTrue(isUnitInTable(unitName),
            "[FAIL] Unit '" + unitName + "' tidak ditemukan di tabel setelah disimpan.");
        System.out.println("  [PASS] Unit '" + unitName + "' terverifikasi di tabel.");
    }

    // ==================== Update ====================

    /**
     * Update nama unit. Selalu reload tab sebelum mencari baris.
     */
    public UnitPage updateUnit(String oldName, String newName) throws InterruptedException {
        System.out.println("  [UnitPage] Mengupdate Unit '" + oldName + "' → '" + newName + "'");
        driver.get(UNIT_TAB_URL);
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
        System.out.println("  [UnitPage] Unit berhasil diupdate ke '" + newName + "'");
        return this;
    }

    // ==================== Delete ====================

    /**
     * Hapus unit. Selalu reload tab sebelum mencari baris.
     */
    public UnitPage deleteUnit(String unitName) throws InterruptedException {
        System.out.println("  [UnitPage] Menghapus Unit: '" + unitName + "'");
        driver.get(UNIT_TAB_URL);
        Thread.sleep(3000);

        try {
            By rowLocator = By.xpath(
                "//tr[contains(normalize-space(.)," + xpathString(unitName) + ")] | " +
                "//td[contains(normalize-space(text())," + xpathString(unitName) + ")]");
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
        } catch (Exception e) {
            System.out.println("  [WARN] Row '" + unitName + "' tidak ditemukan sebelum delete.");
        }

        clickDeleteButton(unitName);
        Thread.sleep(1000);
        confirmDeleteDialog();
        checkBackendOrValidationError();
        Thread.sleep(2000);
        System.out.println("  [UnitPage] Unit '" + unitName + "' dihapus.");
        return this;
    }

    // ==================== Private Helpers ====================

    private void clickAddButton() throws InterruptedException {
        System.out.println("  [UnitPage] Klik tombol Add...");
        Boolean clicked = (Boolean) js.executeScript(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0;i<btns.length;i++){" +
            "  var t = btns[i].textContent.trim();" +
            "  if(t.includes('Add') || t.includes('Tambah')){" +
            "    btns[i].click(); return true;" +
            "  }}" +
            "return false;"
        );
        if (Boolean.FALSE.equals(clicked)) {
            System.out.println("  [WARN] Tombol Add tidak ditemukan.");
        }
        Thread.sleep(1500);
    }

    private void clickSaveButton() throws InterruptedException {
        System.out.println("  [UnitPage] Klik tombol Simpan...");
        Boolean clicked = (Boolean) js.executeScript(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0;i<btns.length;i++){" +
            "  var t = btns[i].textContent.trim();" +
            "  if((t.includes('Simpan') || t.includes('Save')) && !btns[i].disabled){" +
            "    btns[i].click(); return true;" +
            "  }}" +
            "return false;"
        );
        if (Boolean.FALSE.equals(clicked)) {
            System.out.println("  [WARN] Tombol Simpan tidak ditemukan.");
        }
        Thread.sleep(1000);
    }
}
