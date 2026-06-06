package org.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.logging.Level;

/**
 * WebDriverTools - Singleton ChromeDriver dengan Performance Logging diaktifkan.
 *
 * Performance Logging (bukan Network DevTools CDP) dipilih karena:
 * - Tersedia di semua versi ChromeDriver tanpa memerlukan CDP version matching
 * - Dapat membaca network response status code dari JSON log Performance
 * - Tidak memerlukan dependency tambahan di pom.xml
 *
 * Cara membaca log: driver.manage().logs().get(LogType.PERFORMANCE)
 * setiap entry mengandung JSON "method": "Network.responseReceived" dengan "status" code.
 */
public class WebDriverTools {

    /** Shared ChromeDriver instance dengan Performance Logging aktif. */
    public static final WebDriver chrome = buildChromeDriver();

    /** Base URL aplikasi Vedata. */
    public static final String baseUrl = "https://web.vedata.id/";

    /**
     * Membuat ChromeDriver dengan opsi:
     * - Performance logging diaktifkan untuk monitoring HTTP status code
     * - Log level ALL agar semua network request/response tertangkap
     */
    private static WebDriver buildChromeDriver() {
        // Konfigurasi logging preference untuk ChromeDriver
        LoggingPreferences logPrefs = new LoggingPreferences();
        // Aktifkan Browser log untuk menangkap error console (SyntaxError, TypeError, fetch fail)
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        // Aktifkan Performance log pada level ALL agar Network events tertangkap
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions options = new ChromeOptions();
        // Sertakan log preferences ke dalam ChromeOptions
        options.setCapability("goog:loggingPrefs", logPrefs);

        // Izinkan semua cookie (termasuk cookie pihak ketiga/cross-site)
        // untuk menghentikan Keycloak re-auth loop yang dipicu oleh pemblokiran cookie cross-origin.
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("profile.cookie_controls_mode", 0); // 0 = Allow all cookies
        prefs.put("profile.default_content_setting_values.cookies", 1); // 1 = Allow cookies
        options.setExperimentalOption("prefs", prefs);

        // FIX: Keycloak checkLoginIframe mengembalikan "login_required" karena Chrome
        // memblokir third-party cookies dari keycloak.rumahaplikasi.com saat di web.vedata.id.
        // Ini menyebabkan SPA melakukan OAuth redirect main-window tanpa henti.
        // Solusi: izinkan cross-site cookies agar Keycloak session cookie terbaca oleh iframe.
        options.addArguments("--disable-features=SameSiteByDefaultCookies,CookiesWithoutSameSiteMustBeSecure");
        options.addArguments("--enable-features=SameSiteDefaultChecks");
        options.addArguments("--disable-site-isolation-trials");

        // Aktifkan logging network secara eksplisit melalui experimental options
        java.util.Map<String, Object> perfLoggingPrefs = new java.util.HashMap<>();
        perfLoggingPrefs.put("enableNetwork", true);
        perfLoggingPrefs.put("enablePage", false);
        options.setExperimentalOption("perfLoggingPrefs", perfLoggingPrefs);

        return new ChromeDriver(options);
    }

}
