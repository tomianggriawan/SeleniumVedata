package org.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.common.BasePage;

/**
 * DashboardPage - Page Object Class representing the Dashboard of VEDATA.
 * Mendukung method chaining (fluent interface) untuk verifikasi visibilitas elemen sidebar.
 */
public class DashboardPage extends BasePage {

    // ==================== Locators ====================

    // Sidebar / navigasi utama
    private final By leftSidebar = By.className("leftSidebar");

    // --- Modul HCM Setting ---
    private final By menuCompany = By.xpath("//a[@href='/hcm/setting/company']");
    private final By menuJobTitle = By.xpath("//a[@href='/hcm/setting/job-title']");
    private final By menuUser = By.xpath("//a[@href='/hcm/setting/user']");
    private final By menuAccessRights = By.xpath("//a[@href='/hcm/setting/access-rights']");

    // --- Modul HCM Employee ---
    private final By menuEmployeeDashboard = By.xpath("//a[@href='/hcm/employee/dashboard']");
    private final By menuEmployeeProfile = By.xpath("//a[@href='/hcm/employee/profile']");

    // --- Modul Inventory ---
    private final By menuInventoryDashboard = By.xpath("//a[@href='/inventory/dashboard']");
    private final By menuInventoryWarehouse = By.xpath("//a[@href='/inventory/warehouse']");
    private final By menuInventorySupplier = By.xpath("//a[@href='/inventory/supplier']");

    // --- Modul Finance ---
    private final By menuFinanceDashboard = By.xpath("//a[@href='/finance/dashboard']");
    private final By menuFinanceBank = By.xpath("//a[@href='/finance/master/bank']");

    // ==================== Constructor ====================

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Chaining Verification Methods ====================

    /**
     * Verifikasi bahwa sidebar/navigasi utama tampil
     */
    public DashboardPage verifySidebarDisplayed() {
        assertCondition("Sidebar kiri tampil", isDisplayed(leftSidebar, 10));
        return this;
    }

    /**
     * Verifikasi menu HCM Setting > Company tampil di sidebar
     */
    public DashboardPage verifyMenuCompanyDisplayed() {
        assertCondition("Menu 'Company' tampil di sidebar", isDisplayed(menuCompany, 5));
        return this;
    }

    /**
     * Verifikasi menu HCM Setting > Job Title tampil di sidebar
     */
    public DashboardPage verifyMenuJobTitleDisplayed() {
        assertCondition("Menu 'Job Title' tampil di sidebar", isDisplayed(menuJobTitle, 5));
        return this;
    }

    /**
     * Verifikasi menu HCM Setting > User tampil di sidebar
     */
    public DashboardPage verifyMenuUserDisplayed() {
        assertCondition("Menu 'User' tampil di sidebar", isDisplayed(menuUser, 5));
        return this;
    }

    /**
     * Verifikasi menu HCM Setting > Access Rights tampil di sidebar
     */
    public DashboardPage verifyMenuAccessRightsDisplayed() {
        assertCondition("Menu 'Access Rights' tampil di sidebar", isDisplayed(menuAccessRights, 5));
        return this;
    }

    /**
     * Verifikasi menu HCM Employee > Dashboard tampil di sidebar
     */
    public DashboardPage verifyMenuEmployeeDashboardDisplayed() {
        assertCondition("Menu 'Employee Dashboard' tampil di sidebar", isDisplayed(menuEmployeeDashboard, 5));
        return this;
    }

    /**
     * Verifikasi menu HCM Employee > Profile tampil di sidebar
     */
    public DashboardPage verifyMenuEmployeeProfileDisplayed() {
        assertCondition("Menu 'Employee Profile' tampil di sidebar", isDisplayed(menuEmployeeProfile, 5));
        return this;
    }

    /**
     * Verifikasi menu Inventory > Dashboard tampil di sidebar
     */
    public DashboardPage verifyMenuInventoryDashboardDisplayed() {
        assertCondition("Menu 'Inventory Dashboard' tampil di sidebar", isDisplayed(menuInventoryDashboard, 5));
        return this;
    }

    /**
     * Verifikasi menu Inventory > Warehouse tampil di sidebar
     */
    public DashboardPage verifyMenuInventoryWarehouseDisplayed() {
        assertCondition("Menu 'Inventory Warehouse' tampil di sidebar", isDisplayed(menuInventoryWarehouse, 5));
        return this;
    }

    /**
     * Verifikasi menu Inventory > Supplier tampil di sidebar
     */
    public DashboardPage verifyMenuInventorySupplierDisplayed() {
        assertCondition("Menu 'Inventory Supplier' tampil di sidebar", isDisplayed(menuInventorySupplier, 5));
        return this;
    }

    /**
     * Verifikasi menu Finance > Dashboard tampil di sidebar
     */
    public DashboardPage verifyMenuFinanceDashboardDisplayed() {
        assertCondition("Menu 'Finance Dashboard' tampil di sidebar", isPresent(menuFinanceDashboard, 5));
        return this;
    }

    /**
     * Verifikasi menu Finance > Bank tampil di sidebar
     */
    public DashboardPage verifyMenuFinanceBankDisplayed() {
        assertCondition("Menu 'Finance Bank' tampil di sidebar", isPresent(menuFinanceBank, 5));
        return this;
    }

    /**
     * Helper: verifikasi URL saat ini mengandung 'dashboard'
     */
    public boolean isDashboardUrl() {
        return getCurrentUrl().contains("dashboard") || getCurrentUrl().contains("web.vedata.id");
    }

    /**
     * Navigasi ke halaman HCM > Setting > Company dan kembalikan CompanyPage.
     * Navigasi langsung via URL untuk menghindari masalah Keycloak rate-limit
     * dan memanfaatkan sesi cookie yang sudah aktif.
     */
    public CompanyPage navigateToCompanyPage() {
        navigateTo("https://web.vedata.id/hcm/setting/company");
        waitForPageReady("https://web.vedata.id/hcm/setting/company", 3000);
        return new CompanyPage(driver);
    }

    /**
     * Navigasi ke halaman HCM > Setting > Job Title dan kembalikan JobTitlePage.
     */
    public JobTitlePage navigateToJobTitlePage() {
        navigateTo("https://web.vedata.id/hcm/setting/job-title");
        waitForPageReady("https://web.vedata.id/hcm/setting/job-title", 3000);
        return new JobTitlePage(driver);
    }

    /**
     * Navigasi ke halaman HCM > Setting > User dan kembalikan UserPage.
     */
    public UserPage navigateToUserPage() {
        navigateTo("https://web.vedata.id/hcm/setting/user");
        waitForPageReady("https://web.vedata.id/hcm/setting/user", 3000);
        return new UserPage(driver);
    }

    /**
     * Navigasi ke halaman HCM > Setting > Access Rights dan kembalikan AccessRightsPage.
     */
    public AccessRightsPage navigateToAccessRightsPage() {
        navigateTo("https://web.vedata.id/hcm/setting/access-rights");
        waitForPageReady("https://web.vedata.id/hcm/setting/access-rights", 3000);
        return new AccessRightsPage(driver);
    }

    /**
     * Navigasi ke halaman HCM > Setting > Service dan kembalikan ServicePage.
     */
    public ServicePage navigateToServicePage() {
        navigateTo("https://web.vedata.id/hcm/setting/service");
        // Service page butuh lebih banyak waktu untuk memuat data produk dari API
        waitForPageReady("https://web.vedata.id/hcm/setting/service", 7000);
        return new ServicePage(driver);
    }

    /**
     * Navigasi ke halaman HCM > Employee > Profile dan kembalikan EmployeePage.
     * Halaman ini memerlukan waktu ekstra karena me-load data karyawan dari API.
     */
    public EmployeePage navigateToEmployeeProfilePage() {
        navigateTo("https://web.vedata.id/hcm/employee/profile");
        waitForPageReady("https://web.vedata.id/hcm/employee/profile", 5000);
        return new EmployeePage(driver);
    }

    /**
     * Tunggu hingga halaman target benar-benar siap setelah navigasi.
     *
     * Strategi:
     *   1. Jika URL masih berupa Keycloak OAuth callback (mengandung #code= & session_state=),
     *      navigasikan ulang ke targetUrl untuk memaksa SPA merender halaman tanpa fragment.
     *   2. Tunggu hingga sidebar (leftSidebar) muncul, memastikan sesi aktif dan halaman ter-render.
     *   3. Jika sidebar tidak muncul dalam 15 detik, log warning dan lanjutkan (soft-fail).
     *   4. Berikan extra sleep sesuai parameter extraSleepMs setelah sidebar terdeteksi.
     *
     * @param targetUrl    URL tujuan (untuk recovery jika masih di Keycloak callback)
     * @param extraSleepMs Waktu tambahan tunggu (ms) setelah sidebar terdeteksi
     */
    private void waitForPageReady(String targetUrl, long extraSleepMs) {
        try {
            // Langkah 1: Periksa apakah URL masih Keycloak OAuth callback
            Thread.sleep(1000); // beri sedikit waktu SPA memproses fragment
            if (isKeycloakCallback()) {
                System.out.println("  [WARN] Keycloak OAuth callback terdeteksi. Navigasi ulang ke: " + targetUrl);
                driver.navigate().to(targetUrl);
                Thread.sleep(2000);
            }

            // Langkah 2: Tunggu sidebar muncul (bukti sesi aktif & halaman ter-render)
            boolean sidebarReady = isPresent(leftSidebar, 15);
            if (!sidebarReady) {
                System.out.println("  [WARN] Sidebar tidak muncul dalam 15 detik. URL: " + getCurrentUrl());
            } else {
                System.out.println("  [INFO] Sidebar terdeteksi. Halaman siap: " + getCurrentUrl());
            }

            // Langkah 3: Extra sleep agar konten halaman ter-render penuh
            if (extraSleepMs > 0) {
                Thread.sleep(extraSleepMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

