package org.test.setting;

import org.openqa.selenium.WebDriver;
import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.LoginPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * Company - Test Runner untuk HCM > Setting > Company.
 * Menggunakan POM + fluent interface (method chaining).
 */
public class Company extends BasePage {

    /**
     * Constructor untuk inisialisasi driver.
     * State driver dipertahankan agar tidak hilang saat method chaining.
     */
    public Company(WebDriver driver) {
        super(driver);
    }

    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);

            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("========================================");
            System.out.println("  TEST COMPANY - VEDATA HCM");
            System.out.println("========================================\n");

            // Login dan dapatkan sesi aktif
            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            // Jalankan pengujian Company secara berantai
            new Company(chrome)
                .testVerifikasiCRUDCompany()
                .testUploadCompanyLogo();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST COMPANY SELESAI");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ERROR !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                System.err.println("URL saat error: " + chrome.getCurrentUrl());
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("c:/Users/LENOVO/SDET/SeleniumVedata/page_source_error.html"),
                    chrome.getPageSource()
                );
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }

    /**
     * Verifikasi CRUD Company menggunakan method chaining (fluent interface).
     *
     * Skenario:
     *   1. [READ]   Navigasi ke Company Settings dan verifikasi card detail tampil.
     *   2. [UPDATE] Klik Edit, isi form dengan data baru, simpan, dan verifikasi perubahan.
     *   3. [RESTORE] Klik Edit lagi, kembalikan data ke nilai asli, simpan, dan verifikasi.
     */
    public Company testVerifikasiCRUDCompany() {
        printTestHeader("Test 1: Verifikasi CRUD Company (Chaining Test)");

        try {
            new DashboardPage(driver)
                // --- READ: Navigasi & verifikasi tampilan ---
                .navigateToCompanyPage()
                .verifyCompanyDetailsDisplayed()

                // --- UPDATE: Edit dengan data baru ---
                .clickEditCompany()
                .verifyEditModalOpened()
                .fillCompanyDetails(
                    "Arinda Mart Updated",
                    "08987654321",
                    "arindamart-upd@gmail.com",
                    "Jl Cempaka No 20, Yogyakarta"
                )
                .clickSave()
                .verifyProfileDetails(
                    "Arinda Mart Updated",
                    "08987654321",
                    "arindamart-upd@gmail.com",
                    "Jl Cempaka No 20, Yogyakarta"
                )

                // --- RESTORE: Kembalikan data ke nilai asli ---
                .clickEditCompany()
                .verifyEditModalOpened()
                .fillCompanyDetails(
                    "Arinda Mart",
                    "081215414685",
                    "tomianggriawan@gmail.com",
                    "Jl Cempaka No 15 Gondokusuman"
                )
                .clickSave()
                .verifyProfileDetails(
                    "Arinda Mart",
                    "081215414685",
                    "tomianggriawan@gmail.com",
                    "Jl Cempaka No 15 Gondokusuman"
                );

        } catch (Exception e) {
            printFail("Verifikasi CRUD Company", e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        return this;
    }

    /**
     * Verifikasi Upload Logo Perusahaan
     *
     * Skenario (TANPA membuka modal Edit Company):
     *   1. Navigasi ke halaman Company Settings.
     *   2. Klik area "Click to upload" pada logo-container di card utama.
     *   3. Pilih file logo (sendKeys ke hidden file input).
     *   4. Klik tombol "Upload" yang muncul setelah file dipilih.
     *   5. Verifikasi logo tampil di logo-container pada card Company Details.
     */
    public Company testUploadCompanyLogo() {
        printTestHeader("Test 2: Verifikasi Upload Logo Company");

        try {
            // Pastikan direktori dan file logo tersedia sebelum pengujian
            java.io.File logoFile = new java.io.File("src/test/resources/arinda_mart_logo.png");
            if (!logoFile.exists()) {
                logoFile.getParentFile().mkdirs();
                // Buat gambar 1x1 sederhana secara programatis
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                javax.imageio.ImageIO.write(img, "png", logoFile);
                System.out.println("  [DEBUG] Gambar placeholder berhasil dibuat di: " + logoFile.getAbsolutePath());
            }

            // Path file logo dari folder resources
            String logoPath = logoFile.getAbsolutePath();
            System.out.println("  [DEBUG] Logo path: " + logoPath);

            new DashboardPage(driver)
                .navigateToCompanyPage()
                // Langkah 1: Klik area upload & pilih file (tanpa modal)
                .uploadLogoOnCard(logoPath)
                // Langkah 2: Klik tombol "Upload" yang muncul
                .clickUploadButton()
                // Langkah 3: Verifikasi logo tampil di card
                .verifyLogoDisplayedOnCard();

        } catch (Exception e) {
            printFail("Verifikasi Upload Logo Company", e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        return this;
    }
}
