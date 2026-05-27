package org.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;

/**
 * AccessRightsPage - Page Object Class untuk HCM > Setting > Access Rights (Role Menu).
 * Mendukung method chaining (fluent interface) untuk:
 *   - Verifikasi tampilan halaman Access Rights
 *   - Verifikasi tabel role matrix (Roles/Menu List, SUPER ADMIN, STAFF, ADMIN)
 *   - Verifikasi checkbox tersedia di tabel
 */
public class AccessRightsPage extends BasePage {

    // ==================== Locators - Page ====================

    /** Header halaman "Access Rights (Role Menu)" */
    private final By pageTitle = By.xpath("//h1[contains(@class,'page-title')]");

    // ==================== Locators - Table Headers ====================

    /** Kolom header: Roles / Menu List */
    private final By tableHeaderRoles      = By.xpath("//th[.//span[text()='Roles'] and .//span[text()='Menu List']]");
    /** Kolom header: SUPER ADMIN */
    private final By tableHeaderSuperAdmin = By.xpath("//th[contains(text(),'SUPER ADMIN')]");
    /** Kolom header: STAFF */
    private final By tableHeaderStaff      = By.xpath("//th[contains(text(),'STAFF')]");
    /** Kolom header: ADMIN */
    private final By tableHeaderAdmin      = By.xpath("//th[contains(text(),'ADMIN')]");

    // ==================== Locators - Filter Dropdown ====================

    /** Dropdown filter module (default: HCM) */
    private final By moduleDropdown = By.xpath("//div[@role='combobox']");

    // ==================== Locators - Checkbox ====================

    /** Checkbox pertama di dalam tabel */
    private final By firstCheckbox = By.xpath("//input[@type='checkbox']");

    // ==================== Constructor ====================

    public AccessRightsPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Verifikasi Tampilan ====================

    /**
     * Verifikasi bahwa halaman Access Rights telah termuat dengan benar.
     */
    public AccessRightsPage verifyPageLoaded() {
        assertCondition("Halaman 'Access Rights' tampil", isDisplayed(pageTitle, 10));
        return this;
    }

    /**
     * Verifikasi judul halaman mengandung "Access Rights"
     */
    public AccessRightsPage verifyPageTitle() {
        String title = getText(pageTitle).trim();
        assertCondition("Judul halaman mengandung 'Access Rights'", title.contains("Access Rights"));
        return this;
    }

    /**
     * Verifikasi kolom-kolom tabel role matrix tampil:
     *   Roles/Menu List | SUPER ADMIN | STAFF | ADMIN
     */
    public AccessRightsPage verifyTableColumnsDisplayed() {
        assertCondition("Kolom 'Roles/Menu List' tampil",  isPresent(tableHeaderRoles,      10));
        assertCondition("Kolom 'SUPER ADMIN' tampil",      isPresent(tableHeaderSuperAdmin, 10));
        assertCondition("Kolom 'STAFF' tampil",            isPresent(tableHeaderStaff,      10));
        assertCondition("Kolom 'ADMIN' tampil",            isPresent(tableHeaderAdmin,      10));
        return this;
    }

    /**
     * Verifikasi bahwa dropdown filter module tampil
     */
    public AccessRightsPage verifyModuleDropdownDisplayed() {
        assertCondition("Dropdown filter module tampil di Access Rights",
            isDisplayed(moduleDropdown, 5));
        return this;
    }

    /**
     * Verifikasi bahwa ada checkbox di tabel (untuk set role permission)
     */
    public AccessRightsPage verifyCheckboxesDisplayed() {
        assertCondition("Checkbox permission tampil di tabel Access Rights",
            isPresent(firstCheckbox, 10));
        return this;
    }

    /**
     * Verifikasi bahwa baris "Setting" ada di dalam tabel menu
     */
    public AccessRightsPage verifySettingMenuInTable() {
        By settingRow = By.xpath("//td[contains(.,'Setting')]");
        assertCondition("Baris 'Setting' ada di tabel Access Rights",
            isPresent(settingRow, 10));
        return this;
    }

    /**
     * Verifikasi bahwa nilai filter module saat ini adalah "HCM" (default)
     */
    public AccessRightsPage verifyDefaultModuleFilter() {
        String val = (String) ((JavascriptExecutor) driver).executeScript(
            "var el = document.querySelector('input[role=combobox]');" +
            "return el ? el.value : '';"
        );
        assertCondition("Filter module default adalah 'HCM'", "HCM".equalsIgnoreCase(val.trim()));
        return this;
    }
}
