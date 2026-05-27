package org.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;

/**
 * ServicePage - Page Object Class untuk HCM > Setting > Service.
 * Mendukung method chaining (fluent interface) untuk:
 *   - Verifikasi tampilan halaman Service
 *   - Verifikasi sub-judul: "Produk yang sedang dipakai / subscribe" dan "Produk yang tersedia"
 *   - Verifikasi tab Product dan Billing
 *   - Verifikasi input pencarian layanan
 */
public class ServicePage extends BasePage {

    // ==================== Locators - Page ====================

    /** Header halaman "Service" */
    private final By pageTitle = By.xpath("//h1[contains(@class,'page-title')]");

    /** Sub-judul: "Produk yang sedang dipakai / subscribe" */
    private final By sectionSubscribed = By.xpath("//h2[contains(.,'sedang dipakai')]");

    /** Sub-judul: "Produk yang tersedia" */
    private final By sectionAvailable  = By.xpath("//h2[contains(.,'tersedia')]");

    // ==================== Locators - Tabs ====================

    /** Tab "Product" (aktif secara default) */
    private final By tabProduct = By.xpath("//button[contains(@class,'v-tab') and contains(.,'Product')]");

    /** Tab "Billing" */
    private final By tabBilling = By.xpath("//button[contains(@class,'v-tab') and contains(.,'Billing')]");

    // ==================== Locators - Search ====================

    /** Input pencarian layanan dengan placeholder "Cari nama layanan..." */
    private final By searchInput = By.xpath("//input[@placeholder='Cari nama layanan...']");

    // ==================== Locators - Request Buttons ====================

    /** Tombol "Request" yang tersedia di kartu produk */
    private final By requestButton = By.xpath("//button[contains(.,'Request')]");

    // ==================== Constructor ====================

    public ServicePage(WebDriver driver) {
        super(driver);
    }

    // ==================== Verifikasi Tampilan ====================

    /**
     * Verifikasi bahwa halaman Service telah termuat dengan benar.
     * Cek judul h1 "Service" tampil.
     */
    public ServicePage verifyPageLoaded() {
        // Gunakan isPresent (DOM presence) karena Service page butuh waktu lebih untuk render
        assertCondition("Halaman 'Service' tampil", isPresent(pageTitle, 15));
        return this;
    }

    /**
     * Verifikasi judul halaman adalah "Service"
     */
    public ServicePage verifyPageTitle() {
        String title = getText(pageTitle).trim();
        assertCondition("Judul halaman mengandung 'Service'", title.contains("Service"));
        return this;
    }

    /**
     * Verifikasi section "Produk yang sedang dipakai / subscribe" tampil
     */
    public ServicePage verifySubscribedSectionDisplayed() {
        assertCondition("Section 'Produk yang sedang dipakai' tampil",
            isDisplayed(sectionSubscribed, 10));
        return this;
    }

    /**
     * Verifikasi section "Produk yang tersedia" tampil
     */
    public ServicePage verifyAvailableSectionDisplayed() {
        assertCondition("Section 'Produk yang tersedia' tampil",
            isDisplayed(sectionAvailable, 10));
        return this;
    }

    /**
     * Verifikasi tab "Product" tampil dan aktif
     */
    public ServicePage verifyProductTabDisplayed() {
        assertCondition("Tab 'Product' tampil di halaman Service",
            isDisplayed(tabProduct, 5));
        return this;
    }

    /**
     * Verifikasi tab "Billing" tampil
     */
    public ServicePage verifyBillingTabDisplayed() {
        assertCondition("Tab 'Billing' tampil di halaman Service",
            isDisplayed(tabBilling, 5));
        return this;
    }

    /**
     * Verifikasi input pencarian layanan tampil
     */
    public ServicePage verifySearchInputDisplayed() {
        assertCondition("Input pencarian 'Cari nama layanan...' tampil",
            isDisplayed(searchInput, 5));
        return this;
    }

    /**
     * Verifikasi bahwa ada tombol "Request" pada produk yang tersedia
     */
    public ServicePage verifyRequestButtonDisplayed() {
        assertCondition("Tombol 'Request' tersedia di halaman Service",
            isPresent(requestButton, 10));
        return this;
    }

    // ==================== Actions ====================

    /**
     * Klik tab "Billing"
     */
    public ServicePage clickBillingTab() {
        click(tabBilling);
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    /**
     * Ketik teks pencarian di field search layanan
     *
     * @param keyword kata kunci pencarian
     */
    public ServicePage searchService(String keyword) {
        type(searchInput, keyword);
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }
}
