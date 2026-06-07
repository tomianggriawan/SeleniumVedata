package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.DashboardPage;
import org.pages.settingpage.CompanyPage;

import java.io.File;

import static org.common.WebDriverTools.chrome;

/**
 * Company - Test Runner untuk HCM > Setting > Company.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via CompanyPage
 *  - Fluent interface (method chaining)
 */
public class Company extends BasePage {

    public Company(WebDriver driver) {
        super(driver);
    }

    // ==================== Entry Point ====================

    public static void main(String[] args) {
        runTest("HCM Company", "Company", () -> {
            new Company(chrome)
                .testVerifikasiCRUD()
                .testUploadLogo();
        });
    }

    // ==================== TEST METHODS ====================

    /**
     * TC_HCM_COMPANY_CRUD - Verifikasi CRUD Perusahaan (Read, Update, Restore).
     */
    public Company testVerifikasiCRUD() {
        reporter.startTest("TC_HCM_COMPANY_CRUD", "Verifikasi CRUD Perusahaan (Read, Update, Restore)");
        drainLogs();
        try {
            reporter.logStep("Membuka halaman Dashboard & Navigasi ke Halaman Company Settings...");
            new DashboardPage(driver).navigateToCompanyPage();

            reporter.logStep("Verifikasi detail profil perusahaan awal ditampilkan...");
            CompanyPage companyPage = new CompanyPage(driver);
            companyPage.verifyCompanyDetailsDisplayed();

            reporter.logStep("Klik tombol Edit Company untuk membuka form modal...");
            companyPage.clickEditCompany();
            companyPage.verifyEditModalOpened();

            reporter.logStep("Mengisi detail perusahaan baru (Update)...");
            companyPage.fillCompanyDetails(
                "Arinda Mart Updated", "08987654321",
                "arindamart-upd@gmail.com", "Jl Cempaka No 20, Yogyakarta");

            reporter.logStep("Menyimpan detail perusahaan baru (Save)...");
            companyPage.clickSave();

            inspectNetwork("Save Update Company");

            reporter.logStep("Verifikasi detail profil perusahaan setelah diperbarui...");
            companyPage.verifyProfileDetails(
                "Arinda Mart Updated", "08987654321",
                "arindamart-upd@gmail.com", "Jl Cempaka No 20, Yogyakarta");

            // Drain sebelum Restore Save
            drainLogs();

            reporter.logStep("Klik tombol Edit Company lagi untuk mengembalikan data (Restore)...");
            companyPage.clickEditCompany();
            companyPage.verifyEditModalOpened();

            reporter.logStep("Mengisi detail perusahaan asli...");
            companyPage.fillCompanyDetails(
                "Arinda Mart", "081215414685",
                "tomianggriawan@gmail.com", "Jl Cempaka No 15 Gondokusuman");

            reporter.logStep("Menyimpan detail perusahaan asli (Restore Save)...");
            companyPage.clickSave();

            inspectNetwork("Save Restore Company");

            reporter.logStep("Verifikasi detail profil perusahaan setelah dikembalikan...");
            companyPage.verifyProfileDetails(
                "Arinda Mart", "081215414685",
                "tomianggriawan@gmail.com", "Jl Cempaka No 15 Gondokusuman");

            reporter.logPass("Skenario CRUD Company berhasil diselesaikan.");

        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario CRUD Company.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }

    /**
     * TC_HCM_COMPANY_LOGO - Verifikasi upload logo perusahaan.
     */
    public Company testUploadLogo() {
        reporter.startTest("TC_HCM_COMPANY_LOGO", "Verifikasi Upload Logo Perusahaan");
        try {
            // Pastikan direktori dan file logo tersedia sebelum pengujian
            File logoFile = new File("src/test/resources/arinda_mart_logo.png");
            if (!logoFile.exists()) {
                logoFile.getParentFile().mkdirs();
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                    1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                javax.imageio.ImageIO.write(img, "png", logoFile);
                System.out.println("  [DEBUG] Gambar placeholder berhasil dibuat di: " + logoFile.getAbsolutePath());
            }

            String logoPath = logoFile.getAbsolutePath();
            System.out.println("  [DEBUG] Logo path: " + logoPath);

            reporter.logStep("Navigasi ke halaman Company Settings...");
            new DashboardPage(driver).navigateToCompanyPage();

            reporter.logStep("Klik area upload logo dan pilih file gambar...");
            CompanyPage companyPage = new CompanyPage(driver);
            companyPage.uploadLogoOnCard(logoPath);

            reporter.logStep("Klik tombol Upload yang muncul...");
            companyPage.clickUploadButton();

            reporter.logStep("Verifikasi logo ditampilkan pada card utama...");
            companyPage.verifyLogoDisplayedOnCard();

            reporter.logPass("Skenario Upload Logo Company berhasil diselesaikan.");

        } catch (Throwable e) {
            reporter.logFail("Gagal pada skenario Upload Logo Company.", e);
            throw new AssertionError(e);
        }
        System.out.println();
        return this;
    }
}
