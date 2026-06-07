package org.common;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import org.pages.LoginPage;

import static org.common.WebDriverTools.baseUrl;
import static org.common.WebDriverTools.chrome;

/**
 * BasePage - Base class untuk semua Page Object dan Test Runner.
 * Menyediakan method reusable untuk interaksi Selenium dan daur hidup testing.
 */
public abstract class BasePage {

    protected static final TestReportManager reporter = new TestReportManager();
    protected static final String REPORT_DIR = "c:/Users/LENOVO/vedata-test/src/main/java/org/test/report";


    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
    }

    public BasePage(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        this.js = (JavascriptExecutor) driver;
    }

    // ==================== Element Interaction ====================

    /**
     * Tunggu elemen visible lalu return
     */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Tunggu elemen clickable lalu return
     */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Klik elemen (tunggu sampai clickable)
     */
    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    /**
     * Ketik teks ke input field (clear dulu, lalu ketik)
     */
    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Ambil teks dari elemen
     */
    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    /**
     * Cek apakah elemen tampil (dengan timeout pendek)
     */
    protected boolean isDisplayed(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cek apakah elemen tampil dengan timeout custom
     */
    protected boolean isDisplayed(By locator, int timeoutSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cek apakah elemen ada di DOM (tanpa harus visible/dalam viewport)
     */
    protected boolean isPresent(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cek apakah elemen ada di DOM dengan timeout custom
     */
    protected boolean isPresent(By locator, int timeoutSeconds) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Navigasi ke URL
     */
    protected void navigateTo(String url) {
        driver.get(url);
    }

    /**
     * Refresh halaman
     */
    protected void refreshPage() {
        driver.navigate().refresh();
    }

    /**
     * Ambil URL saat ini
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Ambil title halaman
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    // ==================== Session / Auth Helper ====================

    /**
     * Memeriksa apakah URL saat ini adalah Keycloak OAuth2 callback.
     * Ditandai dengan parameter {@code code=} dan {@code session_state=} di URL hash.
     */
    protected boolean isKeycloakCallback() {
        String url = getCurrentUrl();
        return url.contains("code=") && url.contains("session_state=");
    }

    /**
     * Memeriksa apakah halaman saat ini adalah Keycloak login page.
     */
    protected boolean isOnKeycloakLoginPage() {
        String url = getCurrentUrl();
        return url.contains("keycloak") && (url.contains("login") || isDisplayed(By.id("kc-login"), 2));
    }

    /**
     * Navigasi kembali ke halaman URL yang aman (bukan Keycloak callback).
     * Berguna jika SPA gagal memproses callback OAuth.
     *
     * @param safeUrl URL tujuan (misal halaman list/dashboard)
     */
    protected void recoverFromKeycloakRedirect(String safeUrl) {
        System.out.println("  [WARN] Deteksi Keycloak redirect. Navigasi ke: " + safeUrl);
        driver.navigate().to(safeUrl);
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Memeriksa kesehatan sesi dengan menavigasi ke halaman utama.
     * Jika terdeteksi redirect ke Keycloak login, berarti sesi telah habis.
     *
     * @return true jika sesi masih aktif, false jika perlu login ulang
     */
    protected boolean isSessionValid() {
        String currentUrl = driver.getCurrentUrl();
        driver.navigate().to("https://web.vedata.id/");
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        boolean valid = !isOnKeycloakLoginPage();
        // Kembali ke halaman sebelumnya
        driver.navigate().to(currentUrl);
        try { Thread.sleep(4000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return valid;
    }

    // ==================== Test Helper ====================

    /**
     * Assert dan cetak hasil test
     */
    public static void assertCondition(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
        } else {
            System.out.println("  [FAIL] " + testName);
            throw new AssertionError("Test assertion failed: " + testName);
        }
    }

    /**
     * Cetak header section test
     */
    public static void printTestHeader(String testName) {
        System.out.println("--- " + testName + " ---");
    }

    /**
     * Cetak fail message
     */
    public static void printFail(String testName, String message) {
        System.out.println("  [FAIL] " + testName + ": " + message);
        throw new AssertionError("Test failed: " + testName + " - " + message);
    }

    // ==================== Shared Category / UI Page Helpers ====================

    protected String xpathString(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    protected void waitForDialogToClose() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector(".v-overlay--active")));
        } catch (Exception ignored) {}
        try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    protected void confirmDeleteDialog() {
        System.out.println("  [BasePage] Mencari tombol konfirmasi delete...");
        try {
            js.executeScript(
                "var btns = document.querySelectorAll('button');" +
                "var keywords = ['Ya','Yes','OK','Hapus','Delete','Konfirmasi','Confirm'];" +
                "for(var i=0;i<btns.length;i++){" +
                "  var t = btns[i].textContent.trim().toLowerCase();" +
                "  for(var k=0;k<keywords.length;k++){" +
                "    if(t === keywords[k].toLowerCase()){" +
                "      btns[i].click();" +
                "      return;" +
                "    }" +
                "  }" +
                "}"
            );
        } catch (Exception e) {
            System.out.println("  [WARN] confirmDeleteDialog JS error: " + e.getMessage());
        }
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    protected void checkBackendOrValidationError() {
        try {
            Thread.sleep(1500); // Tunggu sebentar agar UI terupdate jika ada error
            
            // 1. Cek toast / notification error
            String[] toastSelectors = {
                ".vue-notification.error",
                ".vue-notification.warn",
                ".v-snackbar__content",
                ".v-alert--type-error",
                ".v-alert.bg-error",
                "[role='alert']",
                ".error-message",
                ".alert-danger"
            };
            
            for (String selector : toastSelectors) {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                for (WebElement el : elements) {
                    if (el.isDisplayed()) {
                        String text = el.getText().trim();
                        if (!text.isEmpty()) {
                            System.out.println("  [FAIL] Backend/Toast Error terdeteksi: " + text);
                            Assert.fail("[FAIL] Backend/Toast Error terdeteksi: " + text);
                        }
                    }
                }
            }
            
            // 2. Cek inline validation error
            List<WebElement> inlineErrors = driver.findElements(By.cssSelector(".v-input--error .v-messages__message, .v-messages__message, .v-input--error"));
            for (WebElement el : inlineErrors) {
                if (el.isDisplayed()) {
                    String text = el.getText().trim();
                    if (!text.isEmpty()) {
                        System.out.println("  [FAIL] Validasi input terdeteksi: " + text);
                        Assert.fail("[FAIL] Validasi input terdeteksi: " + text);
                    }
                }
            }

            // 3. Cek teks error umum di body
            String bodyText = (String) js.executeScript("return document.body.innerText;");
            if (bodyText != null) {
                String bodyLower = bodyText.toLowerCase();
                if (bodyLower.contains("karakter terlalu panjang") || 
                    bodyLower.contains("too long") || 
                    bodyLower.contains("tidak boleh melebihi") ||
                    bodyLower.contains("maximum length") ||
                    bodyLower.contains("validation failed")) {
                    System.out.println("  [FAIL] Backend/Validation Error terdeteksi di halaman.");
                    Assert.fail("[FAIL] Backend/Validation Error terdeteksi: " + bodyText);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (AssertionError e) {
            throw e; // Lempar ulang AssertionError agar test fail
        } catch (Exception ignored) {
        }
    }

    protected void clickEditButton(String rowName) throws InterruptedException {
        System.out.println("  [BasePage] Klik Edit untuk: '" + rowName + "'");

        // Cari row dan tombol dots/edit
        By rowLocator = By.xpath(
            "//tr[contains(normalize-space(.)," + xpathString(rowName) + ")] | " +
            "//td[contains(normalize-space(text())," + xpathString(rowName) + ")]");
        
        WebElement row = null;
        try {
            row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
            js.executeScript("arguments[0].scrollIntoView({block:'center',inline:'nearest'});", row);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal menemukan/scroll row '" + rowName + "': " + e.getMessage());
            return;
        }

        WebElement btn = null;
        try {
            btn = row.findElement(By.tagName("button"));
        } catch (Exception e) {
            System.out.println("  [WARN] Tidak ada button di row '" + rowName + "'. Coba klik row...");
            row.click();
            return;
        }

        boolean menuOpened = false;
        try {
            new org.openqa.selenium.interactions.Actions(driver)
                .moveToElement(btn)
                .pause(Duration.ofMillis(500))
                .click()
                .perform();
            
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.or(
                    ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-overlay--active, .v-overlay-container .v-overlay, .v-menu"))
                ));
            menuOpened = true;
            System.out.println("  [BasePage] Dropdown menu terbuka (Actions).");
        } catch (Exception e) {
            System.out.println("  [WARN] Actions click tidak berhasil membuka menu. Coba JS dispatch...");
        }

        if (!menuOpened) {
            try {
                js.executeScript(
                    "var el=arguments[0];" +
                    "el.dispatchEvent(new MouseEvent('mouseenter',{bubbles:true}));" +
                    "el.dispatchEvent(new MouseEvent('mouseover',{bubbles:true}));" +
                    "el.dispatchEvent(new MouseEvent('mousedown',{bubbles:true,cancelable:true,view:window}));" +
                    "el.dispatchEvent(new MouseEvent('mouseup',{bubbles:true,cancelable:true,view:window}));" +
                    "el.dispatchEvent(new MouseEvent('click',{bubbles:true,cancelable:true,view:window}));",
                    btn
                );
                new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.or(
                        ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-overlay--active, .v-overlay-container .v-overlay, .v-menu"))
                    ));
                menuOpened = true;
                System.out.println("  [BasePage] Dropdown menu terbuka (JS dispatch).");
            } catch (Exception e) {
                System.out.println("  [WARN] JS dispatch gagal membuka menu: " + e.getMessage());
            }
        }

        if (!menuOpened) {
            try {
                btn.click();
                Thread.sleep(1000);
                System.out.println("  [BasePage] Dropdown menu terbuka (Native click fallback).");
            } catch (Exception e) {
                System.out.println("  [WARN] Native click fallback gagal: " + e.getMessage());
            }
        }

        Thread.sleep(800);

        try {
            List<WebElement> menuItems = driver.findElements(
                By.cssSelector(".v-overlay--active .v-list-item, .v-overlay-container .v-overlay .v-list-item, .v-menu .v-list-item"));
            System.out.println("  [DEBUG Edit] Jumlah opsi menu ditemukan: " + menuItems.size());
            for (WebElement item : menuItems) {
                if (item.isDisplayed()) {
                    String txt = item.getText().trim().toLowerCase();
                    System.out.println("  [DEBUG Edit] Teks opsi: '" + txt + "'");
                    if (txt.contains("edit") || txt.contains("ubah")) {
                        js.executeScript("arguments[0].click();", item);
                        System.out.println("  [BasePage] Opsi Edit diklik: '" + txt + "'");
                        Thread.sleep(1500);
                        return;
                    }
                }
            }
            for (WebElement item : menuItems) {
                if (item.isDisplayed()) {
                    js.executeScript("arguments[0].click();", item);
                    System.out.println("  [BasePage] Opsi Edit fallback diklik.");
                    Thread.sleep(1500);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal memilih opsi Edit: " + e.getMessage());
        }
    }

    protected void clickDeleteButton(String rowName) throws InterruptedException {
        System.out.println("  [BasePage] Klik Delete untuk: '" + rowName + "'");

        // Cari row dan tombol dots/delete
        By rowLocator = By.xpath(
            "//tr[contains(normalize-space(.)," + xpathString(rowName) + ")] | " +
            "//td[contains(normalize-space(text())," + xpathString(rowName) + ")]");
        
        WebElement row = null;
        try {
            row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
            js.executeScript("arguments[0].scrollIntoView({block:'center',inline:'nearest'});", row);
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal menemukan/scroll row '" + rowName + "': " + e.getMessage());
            return;
        }

        WebElement btn = null;
        try {
            btn = row.findElement(By.tagName("button"));
        } catch (Exception e) {
            System.out.println("  [WARN] Tidak ada button di row '" + rowName + "'. Coba klik row...");
            row.click();
            return;
        }

        boolean menuOpened = false;
        try {
            new org.openqa.selenium.interactions.Actions(driver)
                .moveToElement(btn)
                .pause(Duration.ofMillis(500))
                .click()
                .perform();
            
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.or(
                    ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-overlay--active, .v-overlay-container .v-overlay, .v-menu"))
                ));
            menuOpened = true;
            System.out.println("  [BasePage] Dropdown menu terbuka (Actions).");
        } catch (Exception e) {
            System.out.println("  [WARN] Actions click tidak berhasil membuka menu. Coba JS dispatch...");
        }

        if (!menuOpened) {
            try {
                js.executeScript(
                    "var el=arguments[0];" +
                    "el.dispatchEvent(new MouseEvent('mouseenter',{bubbles:true}));" +
                    "el.dispatchEvent(new MouseEvent('mouseover',{bubbles:true}));" +
                    "el.dispatchEvent(new MouseEvent('mousedown',{bubbles:true,cancelable:true,view:window}));" +
                    "el.dispatchEvent(new MouseEvent('mouseup',{bubbles:true,cancelable:true,view:window}));" +
                    "el.dispatchEvent(new MouseEvent('click',{bubbles:true,cancelable:true,view:window}));",
                    btn
                );
                new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.or(
                        ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".v-overlay--active, .v-overlay-container .v-overlay, .v-menu"))
                    ));
                menuOpened = true;
                System.out.println("  [BasePage] Dropdown menu terbuka (JS dispatch).");
            } catch (Exception e) {
                System.out.println("  [WARN] JS dispatch gagal membuka menu: " + e.getMessage());
            }
        }

        if (!menuOpened) {
            try {
                btn.click();
                Thread.sleep(1000);
                System.out.println("  [BasePage] Dropdown menu terbuka (Native click fallback).");
            } catch (Exception e) {
                System.out.println("  [WARN] Native click fallback gagal: " + e.getMessage());
            }
        }

        Thread.sleep(800);

        try {
            List<WebElement> menuItems = driver.findElements(
                By.cssSelector(".v-overlay--active .v-list-item, .v-overlay-container .v-overlay .v-list-item, .v-menu .v-list-item"));
            System.out.println("  [DEBUG Delete] Jumlah opsi menu ditemukan: " + menuItems.size());
            for (WebElement item : menuItems) {
                if (item.isDisplayed()) {
                    String txt = item.getText().trim().toLowerCase();
                    System.out.println("  [DEBUG Delete] Teks opsi: '" + txt + "'");
                    if (txt.contains("delete") || txt.contains("hapus")) {
                        js.executeScript("arguments[0].click();", item);
                        System.out.println("  [BasePage] Opsi Delete diklik: '" + txt + "'");
                        Thread.sleep(1500);
                        return;
                    }
                }
            }
            WebElement lastVisible = null;
            for (WebElement item : menuItems) {
                if (item.isDisplayed()) lastVisible = item;
            }
            if (lastVisible != null) {
                js.executeScript("arguments[0].click();", lastVisible);
                System.out.println("  [BasePage] Opsi Delete fallback diklik (opsi terakhir).");
                Thread.sleep(1500);
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal memilih opsi Delete: " + e.getMessage());
        }
    }

    protected void clearAndSetInputValueJS(String value, int index) throws InterruptedException {
        System.out.println("  [BasePage] Clear+Set input[" + index + "] = '" + value + "'");
        
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
            "var nativeSet = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
            "for(var s=0;s<selectors.length;s++){" +
            "  var inputs = document.querySelectorAll(selectors[s]);" +
            "  var visibleInputs = [];" +
            "  for(var i=0;i<inputs.length;i++){" +
            "    if(inputs[i].offsetParent !== null) visibleInputs.push(inputs[i]);" +
            "  }" +
            "  if(visibleInputs.length > arguments[1]){" +
            "    var inp = visibleInputs[arguments[1]];" +
            "    nativeSet.call(inp, '');" +
            "    inp.dispatchEvent(new Event('input',{bubbles:true}));" +
            "    nativeSet.call(inp, arguments[0]);" +
            "    inp.dispatchEvent(new Event('input',{bubbles:true}));" +
            "    inp.dispatchEvent(new Event('change',{bubbles:true}));" +
            "    inp.dispatchEvent(new Event('blur',{bubbles:true}));" +
            "    return true;" +
            "  }}" +
            "return false;",
            value, index
        );

        if (Boolean.FALSE.equals(success)) {
            List<WebElement> inputs = driver.findElements(
                By.xpath("//input[@type='text' and not(@readonly) and not(@disabled)]"));
            int count = 0;
            for (WebElement inp : inputs) {
                if (inp.isDisplayed()) {
                    if (count == index) {
                        inp.click();
                        inp.sendKeys(Keys.CONTROL + "a");
                        inp.clear();
                        Thread.sleep(100);
                        inp.sendKeys(value);
                        js.executeScript(
                            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", inp);
                        break;
                    }
                    count++;
                }
            }
        }
        Thread.sleep(400);
    }

    // ==================== Shared Test Runner Lifecycle & Utilities ====================

    /**
     * Sleep ringkas yang menelan InterruptedException.
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Drain CDP logs.
     */
    protected void drainLogs() {
        NetworkEventAnalyzer.drainLogs(driver);
    }

    /**
     * Melakukan analisis network & console. Jika ada error, catat ke report & throw AssertionError.
     */
    protected void inspectNetwork(String stepName) {
        reporter.logStep("[INSPEKSI] Analisis Network CDP & Console Log setelah " + stepName + " ...");
        NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
        if (analysis.hasErrors()) {
            reporter.logNetworkFail(
                "[NETWORK ANALYSIS] Ditemukan kegagalan sistem setelah " + stepName + " ("
                + analysis.getErrors().size() + " error terdeteksi)", analysis);
            throw new AssertionError("[NETWORK FAIL] " + analysis.buildSummary());
        }
    }

    /**
     * Menangkap error network jika skenario gagal.
     */
    protected void captureNetworkOnFail(String scenarioName) {
        try {
            NetworkEventAnalyzer.AnalysisResult analysis = NetworkEventAnalyzer.analyze(driver);
            if (analysis.hasErrors()) {
                reporter.logNetworkFail(
                    "[NETWORK ANALYSIS ON FAIL - " + scenarioName + "] Detail kegagalan sistem:",
                    analysis);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Interface fungsional untuk mendefinisikan alur pengujian.
     */
    @FunctionalInterface
    public interface TestRunner {
        void run() throws Throwable;
    }

    /**
     * Menjalankan test suite dengan siklus hidup: setup, login, run, exception handling, report generation, dan cleanup.
     */
    public static void runTest(String suiteName, String reportPrefix, TestRunner testRunner) {
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Hapus file laporan lama 'employee-test-report.html' jika ada di folder report
        File oldReportInDir = new File(reportDir, "employee-test-report.html");
        if (oldReportInDir.exists()) {
            oldReportInDir.delete();
            System.out.println("  [INFO] Deleted old employee-test-report.html from report directory.");
        }
        File oldReportInRoot = new File("c:/Users/LENOVO/vedata-test/employee-test-report.html");
        if (oldReportInRoot.exists()) {
            oldReportInRoot.delete();
            System.out.println("  [INFO] Deleted old employee-test-report.html from root.");
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String reportFileName = reportPrefix + "-test-report_" + timestamp + ".html";
        File reportFile = new File(reportDir, reportFileName);
        String reportPath = reportFile.getAbsolutePath();

        try {
            chrome.manage().window().maximize();
            chrome.get(baseUrl);
            Thread.sleep(3000);

            reporter.startSuite();
            System.out.println("=================================================");
            System.out.println("[SUITE START] " + suiteName + " Test Suite");
            System.out.println("=================================================\n");

            // Login
            System.out.println("  [INFO] Login sebagai tomi@tester.com ...");
            new LoginPage(chrome).login("tomi@tester.com", "1234");
            Thread.sleep(5000);

            // Bersihkan buffer log sesi login
            NetworkEventAnalyzer.drainLogs(chrome);

            // Jalankan skenario
            testRunner.run();

            System.out.println("\n========================================");
            System.out.println("  SEMUA TEST " + suiteName.toUpperCase() + " SELESAI");
            System.out.println("========================================");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST SUITE ERROR !!!");
            System.err.println("Message: " + e.getMessage());
            try {
                System.err.println("URL saat error: " + chrome.getCurrentUrl());
                Files.writeString(
                    Path.of("c:/Users/LENOVO/vedata-test/" + reportPrefix.toLowerCase() + "_page_source_error.html"),
                    chrome.getPageSource()
                );
                System.err.println("Page source dumped ke " + reportPrefix.toLowerCase() + "_page_source_error.html");
            } catch (Exception ex) {
                System.err.println("Gagal dump page source: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            reporter.generateHtmlReport(reportPath);
            System.out.println("\n  [REPORT] Laporan HTML disimpan di: " + reportPath);
            chrome.quit();
        }
    }
}

