package org.pages.settingpage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;

import java.time.Duration;

/**
 * UserPage - Page Object Class representing the HCM > Setting > User page.
 * Mendukung method chaining (fluent interface) untuk:
 *   - Verifikasi tampilan halaman User List (Read)
 *   - Verifikasi kolom tabel: Action, Username, Full Name, Module Access
 *   - Klik tombol Add dan verifikasi modal
 */
public class UserPage extends BasePage {

    // ==================== Locators - Page ====================

    /** Header halaman "User List" */
    private final By pageTitle = By.xpath("//h1[contains(@class,'page-title')]");

    /** Tombol "Add" untuk membuka modal tambah User */
    private final By addButton = By.xpath("//button[contains(@class,'bg-primary') and contains(.,'Add')]");

    /** Kolom header tabel: Action */
    private final By tableHeaderAction      = By.xpath("//th[.//span[text()='Action']]");
    /** Kolom header tabel: Username */
    private final By tableHeaderUsername    = By.xpath("//th[.//span[text()='Username']]");
    /** Kolom header tabel: Full Name */
    private final By tableHeaderFullName    = By.xpath("//th[.//span[text()='Full Name']]");
    /** Kolom header tabel: Module Access */
    private final By tableHeaderModuleAccess = By.xpath("//th[.//span[text()='Module Access']]");

    // ==================== Locators - Modal ====================

    /** Modal overlay aktif */
    private final By modalOverlay = By.xpath(
        "//div[contains(@class,'v-overlay--active')] | //div[contains(@class,'v-dialog--active')]"
    );

    /** Tombol "Save" di dalam modal */
    private final By modalSaveButton = By.xpath(
        "//div[contains(@class,'v-overlay--active')]//button[.//span[contains(text(),'Save')]] | " +
        "//div[contains(@class,'v-dialog')]//button[.//span[contains(text(),'Save')]]"
    );

    // ==================== Constructor ====================

    public UserPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Read (Verifikasi Tampilan Halaman) ====================

    /**
     * Verifikasi bahwa halaman User telah termuat dengan benar.
     */
    public UserPage verifyPageLoaded() {
        assertCondition("Halaman 'User List' tampil", isDisplayed(pageTitle, 10));
        return this;
    }

    /**
     * Verifikasi judul halaman mengandung "User"
     */
    public UserPage verifyPageTitle() {
        String title = getText(pageTitle).trim();
        assertCondition("Judul halaman mengandung 'User'", title.contains("User"));
        return this;
    }

    /**
     * Verifikasi kolom-kolom tabel User tampil (Action, Username, Full Name, Module Access)
     */
    public UserPage verifyTableColumnsDisplayed() {
        assertCondition("Kolom 'Action' tampil di tabel User",
            isPresent(tableHeaderAction, 10));
        assertCondition("Kolom 'Username' tampil di tabel User",
            isPresent(tableHeaderUsername, 10));
        assertCondition("Kolom 'Full Name' tampil di tabel User",
            isPresent(tableHeaderFullName, 10));
        assertCondition("Kolom 'Module Access' tampil di tabel User",
            isPresent(tableHeaderModuleAccess, 10));
        return this;
    }

    /**
     * Verifikasi tombol "Add" tampil
     */
    public UserPage verifyAddButtonDisplayed() {
        assertCondition("Tombol 'Add' tampil di halaman User", isDisplayed(addButton, 5));
        return this;
    }

    // ==================== Create ====================

    /**
     * Klik tombol "Add" untuk membuka modal tambah User.
     */
    public UserPage clickAddButton() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(addButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Verifikasi modal Add User telah terbuka (ada elemen input di dalam overlay)
     */
    public UserPage verifyAddModalOpened() {
        By modalInput = By.xpath(
            "//div[contains(@class,'v-overlay--active')]//input | " +
            "//div[contains(@class,'v-dialog--active')]//input"
        );
        assertCondition("Modal Add User terbuka (input muncul)", isDisplayed(modalInput, 10));
        return this;
    }

    // ==================== Verifikasi Data Tabel ====================

    /**
     * Verifikasi bahwa tabel User memiliki setidaknya satu baris data.
     */
    public UserPage verifyTableHasData() {
        By firstCell = By.xpath("//tbody//tr[1]//td[2]");
        assertCondition("Tabel User memiliki data", isPresent(firstCell, 10));
        return this;
    }

    /**
     * Verifikasi bahwa username tertentu ada di tabel User.
     *
     * @param username Username yang dicari (sebagian teks cukup)
     */
    public UserPage verifyUsernameInTable(String username) {
        By usernameCell = By.xpath("//tbody//td[contains(.,'" + username + "')]");
        assertCondition("Username '" + username + "' ada di tabel User",
            isPresent(usernameCell, 10));
        return this;
    }
}
