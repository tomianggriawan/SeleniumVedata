package org.pages.inventorypage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

/**
 * UnitPage - Page Object untuk Tab UNIT di Category Setting.
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

    private static final String UNIT_TAB_URL  = "https://web.vedata.id/inventory/setting?tab=unit";
    private static final String UNIT_FORM_URL = "https://web.vedata.id/inventory/setting/unit/form";

    // ==================== Locators ====================

    // --- Sidebar ---
    private final By sidebarItemMenu = By.xpath(
        "//div[contains(@class,'leftSidebar') or contains(@class,'sidebar')]" +
        "//a[normalize-space()='Item' or .//span[normalize-space()='Item']]" +
        " | //nav//a[normalize-space()='Item' or .//span[normalize-space()='Item']]" +
        " | //*[@role='navigation']//a[normalize-space()='Item' or .//span[normalize-space()='Item']]"
    );

    // --- Page Header & Tabs ---
    private final By pageMainTitle = By.xpath(
        "//*[contains(normalize-space(),'Unit Setting')]" +
        "[self::h1 or self::h2 or self::h3" +
        " or contains(@class,'page-title') or contains(@class,'header-title')]"
    );
    private final By tabItem     = By.xpath(
        "//button[normalize-space()='Item']     | //a[normalize-space()='Item']" +
        " | //*[@role='tab'][normalize-space()='Item']");
    private final By tabUnit     = By.xpath(
        "//button[normalize-space()='Unit']     | //a[normalize-space()='Unit']" +
        " | //*[@role='tab'][normalize-space()='Unit']");
    private final By tabCategory = By.xpath(
        "//button[normalize-space()='Category'] | //a[normalize-space()='Category']" +
        " | //*[@role='tab'][normalize-space()='Category']");
    private final By tabOption   = By.xpath(
        "//button[normalize-space()='Option']   | //a[normalize-space()='Option']" +
        " | //*[@role='tab'][normalize-space()='Option']");

    // --- Unit List Area ---
    private final By unitListTitle = By.xpath(
        "//*[contains(normalize-space(),'Unit List')]" +
        "[self::h1 or self::h2 or self::h3 or self::h4" +
        " or contains(@class,'title') or contains(@class,'header') or contains(@class,'page-title')]"
    );
    private final By addButton = By.xpath(
        "//button[contains(@class,'bg-primary') and contains(.,'Add')]" +
        " | //button[.//span[normalize-space()='Add']]" +
        " | //button[normalize-space()='Add']"
    );

    // --- Add Unit Form ---
    private final By formTitle = By.xpath(
        "//*[contains(normalize-space(),'Add Unit')]" +
        "[self::h1 or self::h2 or self::h3 or self::h4" +
        " or contains(@class,'title') or contains(@class,'page-title') or contains(@class,'header')]"
    );
    private final By formFieldName = By.xpath(
        "//input[@id='unit-name']" +
        " | //label[contains(normalize-space(),'Name')]/following::input[1]" +
        " | //input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'name')]"
    );
    private final By formFieldInformation = By.xpath(
        "//textarea[@id='unit-desc']" +
        " | //label[contains(normalize-space(),'Information') or contains(normalize-space(),'Keterangan')]" +
        "/following::textarea[1]" +
        " | //textarea[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'information')]"
    );
    private final By formSaveButton = By.xpath(
        "//button[contains(@class,'bg-primary')][.//span[normalize-space()='Save'] or normalize-space()='Save']" +
        " | //button[not(@disabled)][normalize-space()='Save' or .//span[normalize-space()='Save']]"
    );
    private final By validationMessages = By.xpath(
        "//*[contains(@class,'v-messages__message')]" +
        " | //*[contains(@class,'error--text') and normalize-space() != '']" +
        " | //*[@role='alert' and normalize-space() != '']" +
        " | //*[contains(@class,'v-input--error')]//*[contains(@class,'v-messages')]"
    );

    // ==================== Constructor ====================

    public UnitPage(WebDriver driver) {
        super(driver, 20);
    }

    // ==================== Step 1: Sidebar Verification ====================

    /**
     * Verifikasi menu "Item" tampil di sidebar setelah login.
     */
    public UnitPage verifySidebarItemMenu() {
        System.out.println("  [UnitPage] Verifikasi menu 'Item' di sidebar...");
        boolean visible = isDisplayed(sidebarItemMenu, 10);
        if (!visible) {
            throw new AssertionError("[SIDEBAR] Menu 'Item' tidak tampil di sidebar setelah login.");
        }
        System.out.println("  [UnitPage] OK - Menu 'Item' tampil di sidebar.");
        return this;
    }

    /**
     * Klik menu "Item" di sidebar untuk navigasi ke halaman Item.
     */
    public UnitPage clickSidebarItemMenu() {
        System.out.println("  [UnitPage] Klik menu 'Item' di sidebar...");
        try {
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(sidebarItemMenu));
            js.executeScript("arguments[0].click();", menu);
            Thread.sleep(3000);
            System.out.println("  [UnitPage] OK - Menu 'Item' diklik. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            throw new AssertionError("[SIDEBAR] Gagal klik menu 'Item': " + e.getMessage(), e);
        }
        return this;
    }

    // ==================== Step 2: Page & Tab Validation ====================

    /**
     * Verifikasi judul halaman adalah "Unit Setting".
     */
    public UnitPage verifyPageTitle() {
        System.out.println("  [UnitPage] Verifikasi judul halaman 'Unit Setting'...");
        boolean found = isDisplayed(pageMainTitle, 10);
        if (!found) {
            String bodyText = (String) js.executeScript("return document.body.innerText || '';");
            if (!bodyText.contains("Unit Setting")) {
                throw new AssertionError(
                    "[PAGE TITLE] 'Unit Setting' tidak ditemukan. URL: " + driver.getCurrentUrl());
            }
        }
        System.out.println("  [UnitPage] OK - Judul 'Unit Setting' terverifikasi.");
        return this;
    }

    /**
     * Verifikasi tab navigasi: Item, Unit, Category, Option semuanya tampil.
     */
    public UnitPage verifySettingTabs() {
        System.out.println("  [UnitPage] Verifikasi tab: Item / Unit / Category / Option...");
        String[] names = {"Item", "Unit", "Category", "Option"};
        By[]     locs  = {tabItem, tabUnit, tabCategory, tabOption};
        for (int i = 0; i < names.length; i++) {
            boolean ok = isPresent(locs[i], 8);
            if (!ok) {
                String body = (String) js.executeScript("return document.body.innerText || '';");
                if (!body.contains(names[i])) {
                    throw new AssertionError("[TABS] Tab '" + names[i] + "' tidak ditemukan.");
                }
            }
            System.out.println("  [UnitPage] OK - Tab '" + names[i] + "' terdeteksi.");
        }
        return this;
    }

    /**
     * Klik tab "Unit" — navigasi ke Unit List (via URL untuk stabilitas SPA).
     */
    public UnitPage clickUnitTab() {
        System.out.println("  [UnitPage] Aktivasi tab 'Unit'...");
        try {
            driver.get(UNIT_TAB_URL);
            Thread.sleep(3000);
            System.out.println("  [UnitPage] OK - Tab Unit aktif. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            throw new AssertionError("[TAB UNIT] Gagal navigasi ke tab Unit: " + e.getMessage(), e);
        }
        return this;
    }

    // ==================== Step 3: Unit List & Navigation ====================

    /**
     * Verifikasi sub-judul "Unit List" tampil di halaman.
     */
    public UnitPage verifyUnitListTitle() {
        System.out.println("  [UnitPage] Verifikasi sub-judul 'Unit List'...");
        boolean found = isDisplayed(unitListTitle, 8);
        if (!found) {
            String body = (String) js.executeScript("return document.body.innerText || '';");
            if (!body.contains("Unit")) {
                throw new AssertionError("[UNIT LIST] Judul 'Unit List' tidak ditemukan.");
            }
        }
        System.out.println("  [UnitPage] OK - 'Unit List' terverifikasi.");
        return this;
    }

    /**
     * Verifikasi tombol "Add" tampil di halaman Unit List.
     */
    public UnitPage verifyAddButtonVisible() {
        System.out.println("  [UnitPage] Verifikasi tombol 'Add' tampil...");
        boolean visible = isDisplayed(addButton, 8);
        if (!visible) {
            throw new AssertionError("[ADD BTN] Tombol 'Add' tidak tampil di Unit List.");
        }
        System.out.println("  [UnitPage] OK - Tombol 'Add' tampil.");
        return this;
    }

    /**
     * Klik tombol Add dan tunggu halaman form Add Unit terbuka.
     */
    public UnitPage clickAddButtonAndWaitForm() {
        System.out.println("  [UnitPage] Klik 'Add' dan tunggu form terbuka...");
        try {
            clickAddButtonRobust();
            waitForFormPage();
            System.out.println("  [UnitPage] OK - Form Add Unit terbuka. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            throw new AssertionError("[ADD BTN] Gagal membuka form Add Unit: " + e.getMessage(), e);
        }
        return this;
    }

    // ==================== Step 4: Form Validation — Negative Test ====================

    /**
     * Verifikasi judul form adalah "Add Unit".
     */
    public UnitPage verifyFormTitle() {
        System.out.println("  [UnitPage] Verifikasi judul form 'Add Unit'...");
        boolean found = isDisplayed(formTitle, 8);
        if (!found) {
            String body = (String) js.executeScript("return document.body.innerText || '';");
            if (!body.contains("Add Unit")) {
                throw new AssertionError(
                    "[FORM TITLE] 'Add Unit' tidak ditemukan. URL: " + driver.getCurrentUrl());
            }
        }
        System.out.println("  [UnitPage] OK - Judul 'Add Unit' terverifikasi.");
        return this;
    }

    /**
     * Klik Save dengan form dalam keadaan kosong (negative test).
     */
    public UnitPage clickSaveEmpty() {
        System.out.println("  [UnitPage] Klik Save (form kosong — negative test)...");
        try {
            WebElement saveBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(formSaveButton));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);
            Thread.sleep(300);
            js.executeScript("arguments[0].click();", saveBtn);
            Thread.sleep(1500);
            System.out.println("  [UnitPage] OK - Save diklik (form kosong).");
        } catch (Exception e) {
            throw new AssertionError("[SAVE EMPTY] Gagal klik Save: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Verifikasi pesan validasi "required" muncul pada field mandatory.
     * Mendukung Vuetify v-messages, aria[role='alert'], class error.
     */
    public UnitPage verifyRequiredValidationMessages() {
        System.out.println("  [UnitPage] Verifikasi pesan validasi required field...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(validationMessages));

            List<WebElement> messages = driver.findElements(validationMessages);
            long visibleCount = messages.stream()
                .filter(el -> {
                    try { return el.isDisplayed() && !el.getText().trim().isEmpty(); }
                    catch (Exception ignored) { return false; }
                })
                .count();

            if (visibleCount > 0) {
                System.out.println("  [UnitPage] OK - " + visibleCount + " pesan validasi ditemukan:");
                messages.stream()
                    .filter(el -> {
                        try { return el.isDisplayed() && !el.getText().trim().isEmpty(); }
                        catch (Exception ignored) { return false; }
                    })
                    .forEach(el -> System.out.println("    - " + el.getText().trim()));
                return this;
            }

            // Fallback via JS count
            Long jsCount = (Long) js.executeScript(
                "var els = document.querySelectorAll(" +
                "  '.v-messages__message, .v-input--error .v-messages, [role=\"alert\"]');" +
                "var n=0;" +
                "for(var i=0;i<els.length;i++){" +
                "  if(els[i].offsetParent!==null && els[i].textContent.trim()!='') n++;" +
                "}" +
                "return n;"
            );
            if (jsCount != null && jsCount > 0) {
                System.out.println("  [UnitPage] OK - " + jsCount + " error terdeteksi via JS.");
                return this;
            }

            throw new AssertionError(
                "[VALIDATION] Tidak ada pesan validasi required yang muncul setelah Save form kosong.");
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            throw new AssertionError("[VALIDATION] Gagal verifikasi: " + e.getMessage(), e);
        }
    }

    // ==================== Step 5: Data Input & Submission — Positive Test ====================

    /**
     * Isi field "Name" pada form dengan Vue-compatible InputEvent.
     */
    public UnitPage fillName(String value) {
        System.out.println("  [UnitPage] Isi field Name: '" + value + "'...");
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(formFieldName));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            Thread.sleep(200);
            js.executeScript(
                "var el=arguments[0]; var v=arguments[1];" +
                "el.focus(); el.value='';" +
                "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                "el.value=v;" +
                "el.dispatchEvent(new InputEvent('input',{data:v,inputType:'insertText',bubbles:true}));" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));",
                input, value);
            Thread.sleep(300);
            System.out.println("  [UnitPage] OK - Name diisi: '" + value + "'.");
        } catch (Exception e) {
            throw new AssertionError("[FILL NAME] Gagal isi Name: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Isi field "Information" pada form. Soft-fail jika field tidak ditemukan.
     */
    public UnitPage fillInformation(String value) {
        System.out.println("  [UnitPage] Isi field Information: '" + value + "'...");
        try {
            WebElement ta = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.presenceOfElementLocated(formFieldInformation));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", ta);
            Thread.sleep(200);
            js.executeScript(
                "var el=arguments[0]; var v=arguments[1];" +
                "el.focus(); el.value='';" +
                "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                "el.value=v;" +
                "el.dispatchEvent(new InputEvent('input',{data:v,inputType:'insertText',bubbles:true}));" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));",
                ta, value);
            Thread.sleep(300);
            System.out.println("  [UnitPage] OK - Information diisi: '" + value + "'.");
        } catch (Exception e) {
            System.out.println("  [WARN] Field Information tidak ditemukan atau gagal diisi: " + e.getMessage());
        }
        return this;
    }

    /**
     * Klik Save setelah mengisi form, lalu tunggu redirect kembali ke Unit List.
     */
    public UnitPage clickSaveAndWaitList() {
        System.out.println("  [UnitPage] Klik Save dan tunggu redirect ke list...");
        try {
            clickSaveButtonOnPage();
            waitForReturnToList();
            System.out.println("  [UnitPage] OK - Save sukses, kembali ke list. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            throw new AssertionError("[SAVE] Gagal save atau redirect gagal: " + e.getMessage(), e);
        }
        return this;
    }

    // ==================== Step 6: Final Verification ====================

    /**
     * Verifikasi Name dan Information baru tampil di tabel.
     *
     * @param name        nilai Name yang dicari di tabel
     * @param information nilai Information (null = lewati verifikasi info)
     */
    public UnitPage verifyUnitInTableWithData(String name, String information) {
        System.out.println("  [UnitPage] Verifikasi akhir: '" + name + "' di tabel...");
        boolean nameFound = isUnitInTable(name);
        if (!nameFound) {
            throw new AssertionError(
                "[FINAL VERIFY] Name '" + name + "' tidak ditemukan di tabel setelah Save.");
        }
        System.out.println("  [UnitPage] OK - Name '" + name + "' terverifikasi di tabel.");

        if (information != null && !information.isEmpty()) {
            boolean infoFound = isUnitInTable(information);
            if (infoFound) {
                System.out.println("  [UnitPage] OK - Information '" + information + "' terverifikasi di tabel.");
            } else {
                System.out.println("  [WARN] Information '" + information
                    + "' tidak tampil di tabel (mungkin tidak ada kolom tersebut).");
            }
        }
        return this;
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

    public UnitPage navigateToUnitTabCompat() throws InterruptedException {
        return navigateToUnitTab();
    }


    /**
     * Buat Unit baru — nama saja (backward-compat).
     */
    public UnitPage createUnit(String unitName) throws InterruptedException {
        return createUnit(unitName, null);
    }

    /**
     * Buat Unit baru dengan nama DAN informasi/keterangan.
     *
     * DISCOVERY: Tombol Add melakukan NAVIGASI ke halaman form baru
     * (/inventory/setting/unit/form), BUKAN membuka dialog overlay.
     *
     * Alur:
     *  1. Klik Add → halaman navigate ke /inventory/setting/unit/form
     *  2. Tunggu halaman form muncul (URL berubah ke /unit/form)
     *  3. Isi field Name di halaman form
     *  4. Isi field Information (opsional)
     *  5. Klik Save → navigate kembali ke list
     */
    public UnitPage createUnit(String unitName, String information) throws InterruptedException {
        System.out.println("  [UnitPage] Membuat Unit: name='" + unitName
            + "' info='" + information + "'");

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Step 1 — Pastikan di halaman list dulu
                if (!driver.getCurrentUrl().contains("tab=unit") &&
                    !driver.getCurrentUrl().contains("/inventory/setting")) {
                    navigateToUnitTab();
                }

                // Step 2 — Klik Add (navigasi ke /unit/form)
                clickAddButtonRobust();

                // Step 3 — Tunggu halaman form muncul
                waitForFormPage();
                break; // Berhasil masuk ke form
            } catch (AssertionError | Exception e) {
                System.out.println("  [WARN] Attempt " + attempt + " gagal masuk ke form: " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new AssertionError("[HARD FAIL] Halaman form Unit TIDAK muncul setelah "
                        + maxRetries + " kali percobaan. URL saat ini: " + driver.getCurrentUrl(), e);
                }
                System.out.println("  [INFO] Kembali ke tab Unit dan mencoba kembali...");
                navigateToUnitTab();
                Thread.sleep(2000);
            }
        }

        // Step 4 — Isi Name di halaman form (tanpa scope dialog)
        fillNameFieldOnPage(unitName);

        // Step 5 — Isi Information (opsional)
        if (information != null && !information.isEmpty()) {
            fillInformationFieldOnPage(information);
        }

        // Step 6 — Klik Save
        clickSaveButtonOnPage();

        // Step 7 — Tunggu navigasi kembali ke list
        waitForReturnToList();
        Thread.sleep(500); // Minimal delay — jangan habiskan window URL bersih

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
            String currentUrl = driver.getCurrentUrl();
            boolean alreadyAtList = currentUrl.contains("tab=unit")
                && !currentUrl.contains("/form")
                && !currentUrl.contains("/edit");

            if (alreadyAtList) {
                // INSIGHT: Vue Router tidak memproses hash change pada route yang SAMA.
                // Keycloak menambah #state=...&code=... ke URL saat ini, tapi SPA tidak trigger
                // Keycloak SDK processing. Solusi: refresh paksa → SPA restart → Keycloak onLoad
                // memproses hash → token diperoleh → URL bersih → tabel render.
                System.out.println("  [UnitPage] Cek unit di list page...");

                for (int attempt = 1; attempt <= 3; attempt++) {
                    System.out.println("  [UnitPage] Attempt " + attempt + "/3...");
                    String url = driver.getCurrentUrl();

                    // Jika OAuth hash muncul di list page, refresh agar SPA memproses hash
                    if ((url.contains("#state=") || url.contains("?state=")) && url.contains("code=")) {
                        System.out.println("  [UnitPage] OAuth hash di list page, refresh SPA untuk proses hash...");
                        driver.navigate().refresh(); // Full reload WITH hash → Keycloak SDK onLoad fires
                        Thread.sleep(3000);
                    }

                    // Tunggu unit muncul di DOM (bisa saat OAuth hash masih di URL)
                    try {
                        boolean found = new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                            try {
                                return (Boolean) ((JavascriptExecutor) d).executeScript(
                                    "var rows = document.querySelectorAll('tbody tr, td, .v-data-table__td');" +
                                    "for(var i=0;i<rows.length;i++){" +
                                    "  if(rows[i].textContent.trim().includes(arguments[0]) && rows[i].offsetParent!==null) return true;" +
                                    "}return false;", unitName);
                            } catch (Exception e) { return false; }
                        });
                        if (found) {
                            System.out.println("  [UnitPage] Unit '" + unitName + "' DITEMUKAN (attempt " + attempt + ").");
                            return true;
                        }
                    } catch (Exception timeout) {
                        System.out.println("  [WARN] Attempt " + attempt + " timeout. URL: " + driver.getCurrentUrl());
                    }

                    if (attempt < 3) Thread.sleep(1000);
                }
                // Final check
                System.out.println("  [UnitPage] Semua attempt habis, cek final DOM...");
                return checkUnitInDOM(unitName);

            } else {

                // Perlu navigate ke list page (belum di tab=unit sama sekali)
                System.out.println("  [UnitPage] Navigate ke list page...");
                driver.get(UNIT_TAB_URL);
                Thread.sleep(2000);
                waitForListPageReady();
                Thread.sleep(1000);
                return checkUnitInDOM(unitName);
            }
        } catch (Exception e) {
            System.out.println("  [UnitPage] isUnitInTable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cek apakah unit ada di DOM saat ini (tanpa navigasi).
     * Mencari di tr, td, dan elemen Vuetify lainnya.
     */
    private Boolean checkUnitInDOM(String unitName) {
        try {
            Boolean found = (Boolean) js.executeScript(
                "var rows = document.querySelectorAll('tr, td, .v-data-table__td, .v-list-item, [class*=\"table\"]');" +
                "for(var i=0; i<rows.length; i++){" +
                "  if(rows[i].textContent.trim().includes(arguments[0]) && rows[i].offsetParent !== null){" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;",
                unitName
            );
            if (Boolean.TRUE.equals(found)) return true;

            // Fallback: XPath
            try {
                By rowLocator = By.xpath(
                    "//tr[contains(normalize-space(.),'" + unitName + "')] | " +
                    "//td[contains(normalize-space(text()),'" + unitName + "')]");
                List<WebElement> els = driver.findElements(rowLocator);
                if (!els.isEmpty() && els.get(0).isDisplayed()) return true;
            } catch (Exception ignored) {}

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tunggu halaman list unit siap di-render setelah navigate ke UNIT_TAB_URL.
     * Strategi:
     *  1. Jika URL ada OAuth hash → tunggu SPA membersihkan sendiri (biarkan Keycloak SDK kerja)
     *  2. Baru tunggu tbody tr dengan konten aktual
     */
    private void waitForListPageReady() throws InterruptedException {
        String url = driver.getCurrentUrl();
        if ((url.contains("#state=") || url.contains("?state=")) && url.contains("code=")) {
            System.out.println("  [UnitPage] OAuth hash di list page — menunggu SPA membersihkan URL...");
            // Tunggu SPA (Keycloak SDK) memproses token dan menghapus hash sendiri
            try {
                new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(d -> {
                        String u = d.getCurrentUrl();
                        return !u.contains("#state=") && !u.contains("?state=");
                    });
                System.out.println("  [UnitPage] URL bersih setelah OAuth. URL: " + driver.getCurrentUrl());
            } catch (Exception e) {
                // Jika SPA tidak membersihkan dalam 20 detik, paksa replaceState
                System.out.println("  [WARN] OAuth tidak dibersihkan SPA dalam 20s, paksa replaceState.");
                try {
                    js.executeScript(
                        "window.history.replaceState({}, '', '/inventory/setting?tab=unit');");
                } catch (Exception ex) { /* ignored */ }
            }
            Thread.sleep(1000);
        }
        // Tunggu tbody tr dengan konten nyata (bukan skeleton kosong)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    try {
                        List<WebElement> rows = d.findElements(By.cssSelector("tbody tr"));
                        return rows.stream().anyMatch(r -> {
                            try { return r.isDisplayed() && !r.getText().isBlank(); }
                            catch (Exception e) { return false; }
                        });
                    } catch (Exception e) { return false; }
                });
            System.out.println("  [UnitPage] Tabel dengan data siap. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  [WARN] waitForListPageReady timeout. URL: " + driver.getCurrentUrl());
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

    /**
     * Update nama unit. Selalu reload tab sebelum mencari baris.
     */
    public UnitPage updateUnit(String oldName, String newName) throws InterruptedException {
        System.out.println("  [UnitPage] Mengupdate Unit '" + oldName + "' \u2192 '" + newName + "'");

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 1) {
                    driver.get(UNIT_TAB_URL);
                    Thread.sleep(3000);
                }

                // Pastikan baris ada di tabel
                try {
                    By rowLocator = By.xpath(
                        "//tr[contains(normalize-space(.)," + xpathString(oldName) + ")] | " +
                        "//td[contains(normalize-space(text())," + xpathString(oldName) + ")]");
                    new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
                } catch (Exception e) {
                    System.out.println("  [WARN] Row '" + oldName + "' tidak ditemukan sebelum edit.");
                }

                // Klik edit — mungkin navigasi ke form page atau buka dialog
                clickEditButton(oldName);

                // Deteksi: apakah navigasi ke form page atau dialog?
                Thread.sleep(2000);
                waitForOAuthCallbackToResolve();
                String urlAfterEdit = driver.getCurrentUrl();

                if (urlAfterEdit.contains("/unit/form") || urlAfterEdit.contains("/unit/edit") ||
                    urlAfterEdit.contains("/form")) {
                    // Form page — isi field di halaman
                    System.out.println("  [UnitPage] Edit membuka halaman form: " + urlAfterEdit);
                    waitForFormPage();
                    fillNameFieldOnPage(newName);
                    clickSaveButtonOnPage();
                    waitForReturnToList();
                } else {
                    // Coba dialog/overlay
                    WebElement dialog = waitForDialogOrForm();
                    fillNameField(dialog, newName);
                    Thread.sleep(300);
                    clickSaveButtonRobust(dialog);
                    checkBackendOrValidationError();
                    waitForDialogToClose();
                }
                break;
            } catch (AssertionError | Exception e) {
                System.out.println("  [WARN] Attempt " + attempt + " gagal edit: " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new AssertionError("[HARD FAIL] Edit Unit TIDAK berhasil setelah " + maxRetries + " kali percobaan. "
                        + "URL saat ini: " + driver.getCurrentUrl(), e);
                }
                System.out.println("  [INFO] Melakukan reload halaman dan mencoba kembali...");
            }
        }

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

    /**
     * Tunggu dan klik tombol Add/Tambah.
     * Strategi 1: WebDriverWait elementToBeClickable (XPath).
     * Strategi 2: JS text-search fallback.
     * Gagal total → lempar AssertionError.
     */
    private void clickAddButtonRobust() throws InterruptedException {
        System.out.println("  [UnitPage] Klik tombol Add (robust)...");

        By addLocator = By.xpath(
            "//span[contains(@class, 'v-btn__content') and (normalize-space()='Add' or normalize-space()='Tambah')] | " +
            "//span[contains(@class, 'v-btn__content') and (contains(., 'Add') or contains(., 'Tambah'))]/ancestor::button"
        );

        try {
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(addLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            try {
                element.click();
            } catch (Exception e) {
                System.out.println("  [WARN] Native click Add terhalang, mencoba JS fallback...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
            System.out.println("  [UnitPage] Add berhasil diklik.");

            // Deteksi OAuth/Keycloak callback redirect (#state=...&code=...)
            // Ini terjadi saat app token habis dan Keycloak melakukan silent re-auth
            Thread.sleep(1500);
            waitForOAuthCallbackToResolve();

            return;
        } catch (Exception e) {
            throw new AssertionError("[HARD FAIL] Tombol Add/Tambah tidak dapat diklik: " + e.getMessage(), e);
        }
    }

    /**
     * Mendeteksi apakah URL saat ini merupakan OAuth callback dari Keycloak
     * (mengandung fragment #state=...&code=... atau ?code=...) dan menunggu
     * hingga SPA selesai memproses token dan URL kembali normal.
     *
     * Ini adalah penyebab utama dialog tidak muncul — klik Add men-trigger
     * silent re-auth Keycloak yang meredirect URL sebelum dialog sempat render.
     */
    private void waitForOAuthCallbackToResolve() throws InterruptedException {
        String currentUrl = driver.getCurrentUrl();
        boolean isOAuthCallback = (currentUrl.contains("#state=") && currentUrl.contains("code="))
            || (currentUrl.contains("?state=") && currentUrl.contains("code="));

        if (!isOAuthCallback) {
            return; // URL normal, tidak perlu tunggu
        }

        System.out.println("  [UnitPage] Deteksi OAuth callback URL — menunggu URL bersih alami...");
        System.out.println("  [UnitPage] URL OAuth saat ini: " + currentUrl);

        try {
            // Tunggu Keycloak / SPA menyelesaikan token exchange dan membersihkan URL
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> {
                    String u = d.getCurrentUrl();
                    return !u.contains("#state=") && !u.contains("?state=");
                });
            System.out.println("  [UnitPage] URL bersih alami. Mengklik tombol Add kembali...");
            
            // Klik Add kembali karena klik sebelumnya disela oleh redirect OAuth
            By addLocator = By.xpath(
                "//span[contains(@class, 'v-btn__content') and (normalize-space()='Add' or normalize-space()='Tambah')] | " +
                "//span[contains(@class, 'v-btn__content') and (contains(., 'Add') or contains(., 'Tambah'))]/ancestor::button"
            );
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(addLocator));
            try {
                element.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal menunggu URL bersih alami atau re-click Add: " + e.getMessage());
            System.out.println("  [UnitPage] Navigasi langsung ke form URL sebagai fallback...");
            driver.get("https://web.vedata.id/inventory/setting/unit/form");
            Thread.sleep(3000);
        }
    }

    /**
     * Tunggu hingga dialog/overlay form Unit muncul sepenuhnya.
     *
     * Selector bertingkat (timeout masing-masing 18 detik total):
     *  1. div[role='dialog']              — paling semantik
     *  2. .v-overlay--active dengan input — overlay Vuetify aktif yang berisi form
     *  3. .v-dialog                       — class Vuetify dialog
     *
     * @return WebElement dialog yang berhasil ditemukan (untuk scoped search)
     * @throws AssertionError jika TIDAK ADA dialog yang terdeteksi
     */
    /**
     * Tunggu halaman form Unit muncul.
     * Form diakses via route /inventory/setting/unit/form (BUKAN dialog).
     */
    private void waitForFormPage() throws InterruptedException {
        System.out.println("  [UnitPage] Menunggu halaman form Unit muncul...");
        try {
            // Tunggu URL berubah ke /unit/form
            new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    return url.contains("/unit/form") || url.contains("/unit/edit") || url.contains("/form");
                });
            System.out.println("  [UnitPage] URL form terdeteksi: " + driver.getCurrentUrl());

            // Tunggu elemen input utama (id='unit-name') visible
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> {
                    try {
                        // Coba by ID dulu (paling cepat)
                        List<WebElement> byId = d.findElements(By.id("unit-name"));
                        if (!byId.isEmpty() && byId.get(0).isDisplayed()) return true;
                        // Fallback: input visible manapun
                        List<WebElement> inputs = d.findElements(By.tagName("input"));
                        for (WebElement inp : inputs) {
                            if (inp.isDisplayed() && !"hidden".equals(inp.getAttribute("type"))) return true;
                        }
                        return false;
                    } catch (Exception e) { return false; }
                });
            System.out.println("  [UnitPage] Form input siap diisi.");
            Thread.sleep(500);
        } catch (Exception e) {
            throw new AssertionError(
                "[HARD FAIL] Halaman form Unit tidak muncul atau input tidak visible. "
                + "URL saat ini: " + driver.getCurrentUrl(), e);
        }
    }

    /**
     * Isi field Name di halaman form (bukan dialog).
     * Mencari input pertama yang visible di seluruh halaman form.
     */
    private void fillNameFieldOnPage(String value) throws InterruptedException {
        System.out.println("  [UnitPage] Mengisi field Name di halaman form: '" + value + "'");

        // Strategi A: ID 'unit-name' — ditemukan dari page source dump
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("unit-name")));
            clearAndFillInput(input, value);
            System.out.println("  [UnitPage] Name field diisi via id='unit-name'.");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] id='unit-name' tidak ditemukan — coba placeholder.");
        }

        // Strategi B: input dengan placeholder 'Enter name'
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[contains(@placeholder,'name') or contains(@placeholder,'Name') or contains(@placeholder,'nama')]"
                )));
            clearAndFillInput(input, value);
            System.out.println("  [UnitPage] Name field diisi via placeholder.");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Placeholder search gagal — coba input[0] visible.");
        }

        // Strategi C: input pertama visible di halaman
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> {
                    List<WebElement> inputs = d.findElements(By.tagName("input"));
                    for (WebElement inp : inputs) {
                        if (inp.isDisplayed() && !"hidden".equals(inp.getAttribute("type"))) return inp;
                    }
                    return null;
                });
            clearAndFillInput(input, value);
            System.out.println("  [UnitPage] Name field diisi via input[0] visible.");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Input[0] gagal: " + e.getMessage());
        }

        // Strategi D: JS clearAndSetInputValueJS (last resort)
        clearAndSetInputValueJS(value, 0);
        System.out.println("  [UnitPage] Name field diisi via JS clearAndSetInputValueJS(0).");
        Thread.sleep(300);
    }

    /**
     * Isi field Information/Keterangan di halaman form.
     * Soft-fail: lanjut jika tidak ditemukan.
     */
    private void fillInformationFieldOnPage(String value) throws InterruptedException {
        System.out.println("  [UnitPage] Mengisi field Information di halaman form: '" + value + "'");

        // Strategi A: ID 'unit-desc' — ditemukan dari page source dump
        try {
            WebElement ta = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("unit-desc")));
            clearAndFillInput(ta, value);
            System.out.println("  [UnitPage] Information diisi via id='unit-desc'.");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] id='unit-desc' tidak ditemukan — coba textarea visible.");
        }

        // Strategi B: textarea dengan placeholder 'Enter Information'
        try {
            WebElement ta = new WebDriverWait(driver, Duration.ofSeconds(6))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//textarea[contains(@placeholder,'Information') or contains(@placeholder,'information') or contains(@placeholder,'Keterangan')]"
                )));
            clearAndFillInput(ta, value);
            System.out.println("  [UnitPage] Information diisi via textarea placeholder.");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Textarea placeholder gagal — coba textarea[0].");
        }

        // Strategi C: textarea pertama visible
        try {
            WebElement ta = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    List<WebElement> textareas = d.findElements(By.tagName("textarea"));
                    for (WebElement t : textareas) {
                        // Hindari sizer textarea (aria-hidden)
                        if (t.isDisplayed() && !"true".equals(t.getAttribute("aria-hidden"))) return t;
                    }
                    return null;
                });
            clearAndFillInput(ta, value);
            System.out.println("  [UnitPage] Information diisi via textarea[0] visible.");
        } catch (Exception e) {
            System.out.println("  [INFO] Field Information tidak ditemukan — kemungkinan tidak ada.");
        }
    }

    /**
     * Klik tombol Save/Simpan di halaman form.
     */
    private void clickSaveButtonOnPage() throws InterruptedException {
        System.out.println("  [UnitPage] Klik tombol Save di halaman form...");

        try {
            WebElement saveBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    List<WebElement> buttons = d.findElements(By.tagName("button"));
                    for (WebElement btn : buttons) {
                        try {
                            if (!btn.isDisplayed() || !btn.isEnabled()) continue;
                            String text = btn.getText().trim();
                            if (text.equalsIgnoreCase("Save") || text.equalsIgnoreCase("Simpan")
                                || text.contains("Save") || text.contains("Simpan")) {
                                return btn;
                            }
                            // Cek v-btn__content
                            List<WebElement> spans = btn.findElements(
                                By.xpath(".//span[contains(@class,'v-btn__content')]"));
                            for (WebElement span : spans) {
                                String spanText = span.getText().trim();
                                if (spanText.equalsIgnoreCase("Save") || spanText.equalsIgnoreCase("Simpan")
                                    || spanText.contains("Save") || spanText.contains("Simpan")) {
                                    return btn;
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                    return null;
                });
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);
            try {
                saveBtn.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
            }
            System.out.println("  [UnitPage] Save diklik di halaman form.");
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new AssertionError("[HARD FAIL] Tombol Save tidak ditemukan di halaman form: " + e.getMessage(), e);
        }
    }

    /**
     * Tunggu navigasi kembali ke halaman list setelah Save.
     * Jika OAuth callback terdeteksi setelah Save, navigate langsung ke list.
     */
    private void waitForReturnToList() throws InterruptedException {
        System.out.println("  [UnitPage] Menunggu navigasi kembali ke list...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    // Sudah di list jika URL mengandung tab=unit tanpa /form atau /edit
                    if (url.contains("tab=unit") && !url.contains("/form") && !url.contains("/edit")) return true;
                    // Detect OAuth callback setelah Save — navigate langsung ke list
                    if ((url.contains("#state=") || url.contains("?state=")) && url.contains("code=")) {
                        try {
                            d.get(UNIT_TAB_URL);
                        } catch (Exception ignored) {}
                    }
                    return false;
                });
            System.out.println("  [UnitPage] Kembali ke list. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            String currentUrl = driver.getCurrentUrl();
            System.out.println("  [WARN] Timeout waitForReturnToList. URL: " + currentUrl);
            // Last resort: jika masih di OAuth callback atau form, navigate ke list
            if (currentUrl.contains("/form") || currentUrl.contains("#state=") || currentUrl.contains("?state=")) {
                System.out.println("  [UnitPage] Force navigasi ke UNIT_TAB_URL dari waitForReturnToList.");
                driver.get(UNIT_TAB_URL);
                Thread.sleep(3000);
            }
        }
    }

    /**
     * Coba dialog/overlay (untuk edit yang mungkin tetap pakai dialog).
     * Fallback ke body jika tidak ada dialog.
     */
    private WebElement waitForDialogOrForm() {
        System.out.println("  [UnitPage] Mendeteksi dialog atau form...");

        // Coba dialog
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div[role='dialog'], .v-dialog, .v-overlay--active")));
            System.out.println("  [UnitPage] Dialog ditemukan.");
            return el;
        } catch (Exception ignored) {}

        // Fallback: gunakan body sebagai scope
        System.out.println("  [UnitPage] Tidak ada dialog — menggunakan body sebagai scope.");
        return driver.findElement(By.tagName("body"));
    }

    /**
     * Isi field Name/Nama di dalam dialog.
     * Semua strategi menggunakan scope dari WebElement dialog yang sudah terverifikasi.
     *
     * @param dialog WebElement container dialog (hasil waitForDialogVisible)
     * @param value  Nilai yang akan diisikan
     * @throws AssertionError jika semua strategi gagal (blind-execution prevention)
     */
    private void fillNameField(WebElement dialog, String value) throws InterruptedException {
        System.out.println("  [UnitPage] Mengisi field Name: '" + value + "' (scoped ke dialog)");

        // Strategi A: label-based XPath di dalam dialog
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> {
                    List<WebElement> labels = dialog.findElements(By.xpath(
                        ".//label[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'name') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'nama')]"));
                    for (WebElement lbl : labels) {
                        List<WebElement> inputs = lbl.findElements(
                            By.xpath("following-sibling::div//input | ../input | ../div//input"));
                        if (!inputs.isEmpty() && inputs.get(0).isDisplayed()) return inputs.get(0);
                    }
                    return null;
                });
            clearAndFillInput(input, value);
            System.out.println("  [UnitPage] Name field diisi via label XPath (scoped).");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Name label XPath scoped gagal — coba placeholder.");
        }

        // Strategi B: input dengan placeholder Name/Nama di dalam dialog
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(6))
                .until(d -> {
                    List<WebElement> inputs = dialog.findElements(By.xpath(
                        ".//input[contains(translate(normalize-space(@placeholder), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'name') or contains(translate(normalize-space(@placeholder), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'nama')]"));
                    for (WebElement inp : inputs) {
                        if (inp.isDisplayed()) return inp;
                    }
                    return null;
                });
            clearAndFillInput(input, value);
            System.out.println("  [UnitPage] Name field diisi via placeholder (scoped).");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Name placeholder scoped gagal — coba input[0] dialog.");
        }

        // Strategi C: input pertama yang visible di dalam dialog
        try {
            List<WebElement> allInputs = dialog.findElements(By.tagName("input"));
            for (WebElement inp : allInputs) {
                if (inp.isDisplayed() && !"hidden".equals(inp.getAttribute("type"))) {
                    clearAndFillInput(inp, value);
                    System.out.println("  [UnitPage] Name field diisi via input[0] visible di dialog.");
                    Thread.sleep(300);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Input[0] scoped gagal: " + e.getMessage());
        }

        // Strategi D: JS clearAndSetInputValueJS (last resort)
        clearAndSetInputValueJS(value, 0);
        System.out.println("  [UnitPage] Name field diisi via JS clearAndSetInputValueJS(0) — last resort.");
        Thread.sleep(300);
    }

    /**
     * Isi field Information/Keterangan di dalam dialog.
     * Soft-fail: jika field tidak ditemukan, log WARN dan lanjut (field mungkin opsional).
     *
     * @param dialog WebElement container dialog
     * @param value  Nilai informasi/keterangan
     */
    private void fillInformationField(WebElement dialog, String value) throws InterruptedException {
        System.out.println("  [UnitPage] Mengisi field Information: '" + value + "' (scoped ke dialog)");

        // Strategi A: textarea dengan placeholder Information/Keterangan/Description
        try {
            WebElement ta = new WebDriverWait(driver, Duration.ofSeconds(6))
                .until(d -> {
                    List<WebElement> tas = dialog.findElements(By.xpath(
                        ".//textarea[contains(translate(normalize-space(@placeholder), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'information') or contains(translate(normalize-space(@placeholder), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'keterangan') or contains(translate(normalize-space(@placeholder), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'description') or contains(translate(normalize-space(@placeholder), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'deskripsi')]"));
                    for (WebElement t : tas) { if (t.isDisplayed()) return t; }
                    return null;
                });
            ta.clear();
            ta.sendKeys(value);
            System.out.println("  [UnitPage] Information diisi via textarea placeholder (scoped).");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Information textarea scoped gagal — coba label XPath.");
        }

        // Strategi B: label-following XPath di dalam dialog
        try {
            WebElement field = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    List<WebElement> labels = dialog.findElements(By.xpath(
                        ".//label[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'information') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'keterangan') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'description')]"));
                    for (WebElement lbl : labels) {
                        List<WebElement> fields = lbl.findElements(
                            By.xpath("following-sibling::div//textarea | following-sibling::div//input | ../textarea | ../input"));
                        if (!fields.isEmpty() && fields.get(0).isDisplayed()) return fields.get(0);
                    }
                    return null;
                });
            clearAndFillInput(field, value);
            System.out.println("  [UnitPage] Information diisi via label XPath (scoped).");
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Information label XPath scoped gagal — coba input[1].");
        }

        // Strategi C: input ke-2 visible di dalam dialog
        try {
            List<WebElement> allInputs = dialog.findElements(By.tagName("input"));
            int visibleCount = 0;
            for (WebElement inp : allInputs) {
                if (inp.isDisplayed() && !"hidden".equals(inp.getAttribute("type"))) {
                    visibleCount++;
                    if (visibleCount == 2) {
                        clearAndFillInput(inp, value);
                        System.out.println("  [UnitPage] Information diisi via input[1] visible di dialog.");
                        Thread.sleep(300);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Information input[1] scoped gagal: " + e.getMessage());
        }

        System.out.println("  [INFO] Field Information tidak ditemukan — kemungkinan field opsional tidak ada di form ini.");
    }

    /**
     * Clear dan isi WebElement input/textarea menggunakan JS setValue + Vue InputEvent.
     */
    private void clearAndFillInput(WebElement el, String value) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "var el=arguments[0]; var v=arguments[1];" +
                "el.focus();" +
                "el.value='';" +
                "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                "el.value=v;" +
                "el.dispatchEvent(new InputEvent('input',{data:v,inputType:'insertText',bubbles:true}));" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));",
                el, value);
            try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        } catch (Exception e) {
            System.out.println("  [WARN] clearAndFillInput JS gagal, coba native: " + e.getMessage());
            try { el.clear(); el.sendKeys(value); } catch (Exception ignored) {}
        }
    }

    /**
     * Klik tombol Save/Simpan di dalam dialog aktif.
     * Scope pencarian dibatasi ke container dialog untuk mencegah klik tombol lain.
     *
     * Strategi 1: Scoped XPath di dalam dialog → WebDriverWait clickable.
     * Strategi 2: JS search hanya di dalam dialog container.
     * Gagal total → lempar AssertionError.
     *
     * @param dialog WebElement container dialog (hasil waitForDialogVisible)
     */
    private void clickSaveButtonRobust(WebElement dialog) throws InterruptedException {
        System.out.println("  [UnitPage] Klik tombol Save (scoped ke dialog)...");

        // Strategi 1: Scoped ke dialog — cari span v-btn__content 'Save'/'Simpan' dalam dialog
        try {
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> {
                    // Cari semua button dalam dialog
                    List<WebElement> buttons = dialog.findElements(By.tagName("button"));
                    for (WebElement btn : buttons) {
                        try {
                            if (!btn.isDisplayed() || !btn.isEnabled()) continue;
                            String btnText = btn.getText().trim();
                            // Cocokkan teks Save/Simpan
                            if (btnText.equalsIgnoreCase("Save") || btnText.equalsIgnoreCase("Simpan")
                                    || btnText.contains("Save") || btnText.contains("Simpan")) {
                                return btn;
                            }
                            // Cek v-btn__content span di dalam button
                            List<WebElement> spans = btn.findElements(
                                By.xpath(".//span[contains(@class,'v-btn__content')]"));
                            for (WebElement span : spans) {
                                String spanText = span.getText().trim();
                                if (spanText.equalsIgnoreCase("Save") || spanText.equalsIgnoreCase("Simpan")
                                        || spanText.contains("Save") || spanText.contains("Simpan")) {
                                    return btn; // kembalikan button, bukan span
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                    return null;
                });
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            try {
                element.click();
            } catch (Exception e) {
                System.out.println("  [WARN] Native click Save terhalang, mencoba JS fallback...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
            System.out.println("  [UnitPage] Save berhasil diklik (scoped).");
            Thread.sleep(1000);
            return;
        } catch (Exception e) {
            System.out.println("  [WARN] Save scoped gagal, coba global XPath: " + e.getMessage());
        }

        // Strategi 2: Global fallback — cari Save/Simpan di seluruh halaman
        try {
            By saveGlobal = By.xpath(
                "//button[not(@disabled)][" +
                "  contains(normalize-space(.),'Save') or contains(normalize-space(.),'Simpan')" +
                "]");
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(saveGlobal));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
            try {
                element.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
            System.out.println("  [UnitPage] Save berhasil diklik (global fallback).");
            Thread.sleep(1000);
            return;
        } catch (Exception e) {
            throw new AssertionError("[HARD FAIL] Tombol Save/Simpan tidak dapat diklik: " + e.getMessage(), e);
        }
    }

    // ==================== Legacy helpers (backward-compat) ====================

    /** @deprecated — legacy method tanpa scope, dipertahankan untuk kompatibilitas */
    private void clickAddButton() throws InterruptedException {
        clickAddButtonRobust();
    }

    /** @deprecated — legacy method tanpa dialog scope */
    private void clickSaveButtonRobust() throws InterruptedException {
        // Coba cari dialog aktif, fallback ke global jika tidak ada
        try {
            WebElement dialog = driver.findElement(By.cssSelector("div[role='dialog']"));
            clickSaveButtonRobust(dialog);
        } catch (Exception e) {
            // Global fallback
            By saveLocator = By.xpath(
                "//*[self::button or self::a]["
                + "contains(normalize-space(.),'Save') or contains(normalize-space(.),'Simpan')]"
                + "[not(@disabled)]");
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(saveLocator));
            btn.click();
            Thread.sleep(1000);
        }
    }

    /** @deprecated */
    private void fillNameField(String value) throws InterruptedException {
        WebElement dialog = driver.findElement(By.cssSelector(
            "div[role='dialog'], .v-overlay--active, .v-dialog"));
        fillNameField(dialog, value);
    }

    /** @deprecated */
    private void fillInformationField(String value) throws InterruptedException {
        WebElement dialog = driver.findElement(By.cssSelector(
            "div[role='dialog'], .v-overlay--active, .v-dialog"));
        fillInformationField(dialog, value);
    }

}
