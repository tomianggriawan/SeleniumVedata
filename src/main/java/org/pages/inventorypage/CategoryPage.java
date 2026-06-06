package org.pages.inventorypage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryPage - Page Object untuk Tab CATEGORY di Inventory Setting.
 *
 * Mewarisi BasePage untuk helper Selenium (xpathString, clickEditButton,
 * clickDeleteButton, confirmDeleteDialog, checkBackendOrValidationError,
 * waitForDialogToClose).
 *
 * Spesifikasi alur:
 * LANGKAH 1 (Create Parent):
 *   Klik Add → biarkan field Parent KOSONG → isi Name → klik Save
 *
 * LANGKAH 2 (Create Child):
 *   Klik Add → klik dropdown Parent → pilih parentName → isi Name → klik Save
 *
 * Tombol Add HTML:
 *   <button><span class="v-btn__content">[SVG] Add</span></button>
 *
 * Tombol Save HTML:
 *   <button class="bg-primary px-3"><span class="v-btn__content">Save</span></button>
 *
 * Form halaman: /inventory/setting/category/form
 */
public class CategoryPage extends BasePage {

    private static final String CATEGORY_TAB_URL  = "https://web.vedata.id/inventory/setting?tab=category";
    private static final String CATEGORY_FORM_URL = "https://web.vedata.id/inventory/setting/category/form";

    public CategoryPage(WebDriver driver) {
        super(driver, 20);
    }

    // ==================== Navigation ====================

    /**
     * Navigasi ke tab Category via URL dan tunggu halaman siap.
     */
    public CategoryPage navigateToCategoryTab() throws InterruptedException {
        System.out.println("  [CategoryPage] Navigasi ke tab Category...");
        driver.get(CATEGORY_TAB_URL);
        Thread.sleep(4000);

        // Bersihkan OAuth hash yang mungkin muncul saat navigasi
        handleOAuthOnCurrentPage();
        Thread.sleep(1500);

        // Tunggu tabel/konten halaman muncul
        waitForListPageContent();
        return this;
    }

    public CategoryPage clickTabCategory() throws InterruptedException {
        return navigateToCategoryTab();
    }

    // ==================== Create Parent Category ====================

    /**
     * LANGKAH 1: Buat kategori induk (tanpa parent).
     * - Klik Add
     * - Biarkan field Parent KOSONG
     * - Isi field Name saja
     * - Klik Save
     */
    public CategoryPage createWithoutParent(String name) throws InterruptedException {
        System.out.println("  [CategoryPage] ===== CREATE PARENT CATEGORY =====");
        System.out.println("  [CategoryPage] Nama: '" + name + "'");

        // Step 1: Pastikan di halaman list
        ensureOnListPage();

        // Step 2: Klik Add dan masuk ke form
        navigateToFormViaAdd();

        // Step 3: Form terbuka — JANGAN isi parent, langsung isi Name
        System.out.println("  [CategoryPage] Langkah 3: Isi Name saja (Parent dikosongkan)...");
        fillNameOnForm(name);

        // Step 4: Klik Save
        System.out.println("  [CategoryPage] Langkah 4: Klik Save...");
        clickSaveOnForm();

        // Step 5: Tunggu kembali ke list
        waitForReturnToList();
        Thread.sleep(2000);

        System.out.println("  [CategoryPage] Parent category '" + name + "' berhasil dibuat.");
        return this;
    }

    // ==================== Create Child Category ====================

    /**
     * LANGKAH 2: Buat kategori anak (dengan parent).
     * - Klik Add
     * - Klik dropdown Parent → ketik parentName → pilih dari list
     * - Isi field Name dengan childName
     * - Klik Save
     */
    public CategoryPage createWithParent(String parentName, String childName) throws InterruptedException {
        System.out.println("  [CategoryPage] ===== CREATE CHILD CATEGORY =====");
        System.out.println("  [CategoryPage] Parent: '" + parentName + "' | Child: '" + childName + "'");

        // Step 1: Pastikan di halaman list
        ensureOnListPage();

        // Step 2: Klik Add dan masuk ke form
        navigateToFormViaAdd();

        // Step 3: Pilih Parent dari dropdown
        System.out.println("  [CategoryPage] Langkah 3: Pilih Parent '" + parentName + "' dari dropdown...");
        selectParentFromDropdown(parentName);
        Thread.sleep(1000);

        // Step 4: Isi Name (field Child)
        System.out.println("  [CategoryPage] Langkah 4: Isi Name (Child): '" + childName + "'...");
        fillNameOnForm(childName);

        // Step 5: Klik Save
        System.out.println("  [CategoryPage] Langkah 5: Klik Save...");
        clickSaveOnForm();

        // Step 6: Tunggu kembali ke list
        waitForReturnToList();
        Thread.sleep(2000);

        System.out.println("  [CategoryPage] Child category '" + childName + "' berhasil dibuat.");
        return this;
    }

    /**
     * Wrapper createCategory: jika mengandung "/" berarti Parent/Child,
     * jika tidak berarti hanya Parent.
     */
    public CategoryPage createCategory(String rawCategory) throws InterruptedException {
        System.out.println("  [CategoryPage] createCategory: '" + rawCategory + "'");
        if (rawCategory.contains("/")) {
            String[] parts = rawCategory.split("/", 2);
            createWithParent(parts[0].trim(), parts[1].trim());
        } else {
            createWithoutParent(rawCategory.trim());
        }
        return this;
    }

    // ==================== Read / Verify ====================

    /**
     * Verifikasi apakah category tersedia di tabel list.
     * Reload tab dan tunggu tabel render sepenuhnya sebelum cek.
     */
    public boolean isCategoryInTable(String rawCategory) {
        String searchName = rawCategory.contains("/")
            ? rawCategory.split("/", 2)[1].trim()
            : rawCategory.trim();
        System.out.println("  [CategoryPage] Verifikasi category '" + searchName + "'...");

        try {
            // Navigasi ke list dan tunggu tabel benar-benar siap
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(4000);
            handleOAuthOnCurrentPage();
            Thread.sleep(1500);
            waitForListPageContent();
            Thread.sleep(1000);

            // Coba 3x dengan jeda untuk antisipasi lazy loading
            for (int attempt = 1; attempt <= 3; attempt++) {
                System.out.println("  [CategoryPage] Cek tabel attempt " + attempt + "/3...");

                Boolean found = (Boolean) js.executeScript(
                    "var cells = document.querySelectorAll('tbody td, td, .v-data-table__td');" +
                    "for(var i=0; i<cells.length; i++){" +
                    "  var txt = cells[i].textContent.trim();" +
                    "  if(txt.includes(arguments[0]) && cells[i].offsetParent !== null) return true;" +
                    "}" +
                    "return false;",
                    searchName
                );

                if (Boolean.TRUE.equals(found)) {
                    System.out.println("  [CategoryPage] Category '" + searchName + "' DITEMUKAN di tabel.");
                    return true;
                }

                if (attempt < 3) {
                    Thread.sleep(2000);
                    // Scroll down untuk memastikan semua baris visible
                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    Thread.sleep(500);
                    js.executeScript("window.scrollTo(0, 0);");
                    Thread.sleep(500);
                }
            }

            // Fallback XPath
            try {
                By rowLocator = By.xpath(
                    "//tr[contains(normalize-space(.)," + xpathString(searchName) + ")] | " +
                    "//td[contains(normalize-space(text())," + xpathString(searchName) + ")]");
                new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
                System.out.println("  [CategoryPage] Category '" + searchName + "' DITEMUKAN (via XPath).");
                return true;
            } catch (Exception ignored) {}

            System.out.println("  [CategoryPage] Category '" + searchName + "' TIDAK DITEMUKAN.");
            return false;

        } catch (Exception e) {
            System.out.println("  [CategoryPage] isCategoryInTable error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assert category ada di tabel.
     */
    public void assertCategoryInTable(String rawCategory) {
        String displayName = rawCategory.contains("/")
            ? rawCategory.split("/", 2)[1].trim()
            : rawCategory.trim();
        Assert.assertTrue(isCategoryInTable(rawCategory),
            "[FAIL] Category '" + displayName + "' tidak ditemukan di tabel.");
        System.out.println("  [PASS] Category '" + displayName + "' terverifikasi.");
    }

    // ==================== Update ====================

    /**
     * Update nama category. Reload tab → cari baris → klik Edit → isi nama baru → Save.
     */
    public CategoryPage updateCategory(String oldName, String newName) throws InterruptedException {
        System.out.println("  [CategoryPage] Update: '" + oldName + "' → '" + newName + "'");
        navigateToCategoryTab();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//tr[contains(normalize-space(.)," + xpathString(oldName) + ")] | " +
                    "//td[contains(normalize-space(text())," + xpathString(oldName) + ")]")));
        } catch (Exception e) {
            System.out.println("  [WARN] Row '" + oldName + "' tidak ditemukan sebelum edit.");
        }

        clickEditButton(oldName);
        Thread.sleep(2500);

        String urlAfterEdit = driver.getCurrentUrl();
        if (isFormPage(urlAfterEdit)) {
            System.out.println("  [CategoryPage] Edit → form page: " + urlAfterEdit);
            waitForFormInputReady();
            fillNameOnForm(newName);
            clickSaveOnForm();
            waitForReturnToList();
        } else {
            // Dialog overlay
            waitForDialogInputReady();
            fillNameInOverlay(newName);
            clickSaveInOverlay();
            checkBackendOrValidationError();
            waitForDialogToClose();
        }

        Thread.sleep(2500);
        System.out.println("  [CategoryPage] Category diupdate ke '" + newName + "'");
        return this;
    }

    // ==================== Delete ====================

    /**
     * Hapus category. Navigasi ke tab → cari baris → klik Delete → konfirmasi.
     */
    public CategoryPage deleteCategory(String categoryName) throws InterruptedException {
        System.out.println("  [CategoryPage] Hapus: '" + categoryName + "'");
        navigateToCategoryTab();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                    "//tr[contains(normalize-space(.)," + xpathString(categoryName) + ")] | " +
                    "//td[contains(normalize-space(text())," + xpathString(categoryName) + ")]")));
        } catch (Exception e) {
            System.out.println("  [WARN] Row '" + categoryName + "' tidak ditemukan sebelum delete.");
        }

        clickDeleteButton(categoryName);
        Thread.sleep(1000);
        confirmDeleteDialog();
        checkBackendOrValidationError();
        Thread.sleep(2000);
        return this;
    }

    // ==================== Private: Ensure On List Page ====================

    /**
     * Pastikan browser berada di halaman list category.
     * Jika tidak, navigasi ke sana dan tunggu siap.
     */
    private void ensureOnListPage() throws InterruptedException {
        String url = driver.getCurrentUrl();
        boolean alreadyAtList = url.contains("tab=category")
            && !url.contains("/form") && !url.contains("/edit");

        if (!alreadyAtList) {
            System.out.println("  [CategoryPage] Tidak di list page, navigasi ke tab=category...");
            navigateToCategoryTab();
        } else {
            handleOAuthOnCurrentPage();
        }
    }

    // ==================== Private: Navigate To Form via Add Button ====================

    /**
     * Klik tombol Add → tunggu navigasi ke form page.
     *
     * Jika OAuth callback muncul setelah klik Add:
     *   1. Tunggu SPA selesai memproses token (URL bersih alami)
     *   2. Jika URL sudah di form page → langsung gunakan (tidak perlu balik ke list)
     *   3. Jika URL kembali ke list → klik Add lagi
     */
    private void navigateToFormViaAdd() throws InterruptedException {
        int maxRetry = 3;
        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            System.out.println("  [CategoryPage] Klik Add (attempt " + attempt + "/" + maxRetry + ")...");

            // Klik tombol Add
            clickAddButtonJS();
            Thread.sleep(2500);

            String urlAfterClick = driver.getCurrentUrl();
            System.out.println("  [CategoryPage] URL setelah klik Add: " + urlAfterClick);

            // Cek langsung masuk ke form page
            if (isFormPage(urlAfterClick)) {
                System.out.println("  [CategoryPage] Form page terdeteksi langsung.");
                waitForFormInputReady();
                return;
            }

            // Cek OAuth callback
            boolean isOAuth = (urlAfterClick.contains("#state=") || urlAfterClick.contains("?state="))
                && urlAfterClick.contains("code=");

            if (isOAuth) {
                System.out.println("  [CategoryPage] OAuth callback terdeteksi. Menunggu SPA proses token...");

                // Tunggu SPA memproses token — URL akan bersih ATAU berubah ke form/list
                waitForOAuthToClean(25);

                String urlAfterOAuth = driver.getCurrentUrl();
                System.out.println("  [CategoryPage] URL setelah OAuth selesai: " + urlAfterOAuth);

                // KUNCI: Jika setelah OAuth URL sudah di form page → langsung gunakan!
                if (isFormPage(urlAfterOAuth)) {
                    System.out.println("  [CategoryPage] OAuth redirect ke form page. Langsung gunakan form!");
                    waitForFormInputReady();
                    return;
                }

                // Jika kembali ke list atau halaman lain → navigasi ke list dan retry
                System.out.println("  [CategoryPage] URL bukan form setelah OAuth (" + urlAfterOAuth + "). Navigasi ke list lalu retry...");
                driver.get(CATEGORY_TAB_URL);
                Thread.sleep(4000);
                waitForListPageContent();
                Thread.sleep(1000);
                continue;
            }

            // Cek dialog overlay
            if (isDialogOpen()) {
                System.out.println("  [CategoryPage] Dialog/overlay terbuka.");
                return;
            }

            System.out.println("  [WARN] Belum di form/dialog. Retry...");
            Thread.sleep(1500);
        }

        throw new AssertionError("[HARD FAIL] Tidak berhasil masuk ke form/dialog setelah " + maxRetry + " percobaan. URL: " + driver.getCurrentUrl());
    }

    // ==================== Private: Click Add Button ====================

    /**
     * Klik tombol Add via JS — mencari button dengan v-btn__content yang mengandung "Add"/"Tambah".
     * HTML: <button><span class="v-btn__content">[SVG] Add</span></button>
     */
    public void clickAddButtonJS() throws InterruptedException {
        System.out.println("  [CategoryPage] Eksekusi klik tombol Add...");

        // Strategi 1: Temukan button lewat span.v-btn__content
        Boolean clicked = (Boolean) js.executeScript(
            "var buttons = document.querySelectorAll('button');" +
            "for(var i=0; i<buttons.length; i++){" +
            "  var btn = buttons[i];" +
            "  if(btn.disabled) continue;" +
            "  var spans = btn.querySelectorAll('span.v-btn__content');" +
            "  for(var j=0; j<spans.length; j++){" +
            "    var txt = spans[j].textContent.trim();" +
            "    if(txt === 'Add' || txt === 'Tambah' || txt.endsWith('Add') || txt.endsWith('Tambah')){" +
            "      btn.click();" +
            "      return true;" +
            "    }" +
            "  }" +
            "}" +
            "return false;"
        );

        if (Boolean.TRUE.equals(clicked)) {
            System.out.println("  [CategoryPage] Add diklik via span.v-btn__content.");
            return;
        }

        // Strategi 2: WebDriverWait + selenium click
        System.out.println("  [WARN] JS span tidak ketemu, coba WebDriverWait...");
        try {
            WebElement addBtn = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> {
                    List<WebElement> buttons = d.findElements(By.tagName("button"));
                    for (WebElement btn : buttons) {
                        try {
                            if (!btn.isDisplayed() || !btn.isEnabled()) continue;
                            String text = btn.getText().trim();
                            if (text.contains("Add") || text.contains("Tambah")) return btn;
                        } catch (Exception ignored) {}
                    }
                    return null;
                });
            js.executeScript("arguments[0].click();", addBtn);
            System.out.println("  [CategoryPage] Add diklik via WebDriverWait.");
        } catch (Exception e) {
            // Strategi 3: XPath fallback
            System.out.println("  [WARN] WebDriverWait gagal: " + e.getMessage());
            js.executeScript(
                "var btns = document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  if(btns[i].textContent.trim().includes('Add') && !btns[i].disabled)" +
                "  { btns[i].click(); break; }}"
            );
            System.out.println("  [CategoryPage] Add diklik via JS textContent fallback.");
        }
    }

    // ==================== Private: Form Page Helpers ====================

    private boolean isFormPage(String url) {
        return url.contains("/category/form") || url.contains("/category/edit")
            || (url.contains("/form") && !url.contains("tab="));
    }

    private boolean isDialogOpen() {
        try {
            List<WebElement> overlays = driver.findElements(
                By.cssSelector(".v-overlay--active, div[role='dialog'], .v-dialog--active"));
            return overlays.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) { return false; }
    }

    public void waitForFormInputReady() throws InterruptedException {
        System.out.println("  [CategoryPage] Menunggu form input siap...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                try {
                    List<WebElement> inputs = d.findElements(By.tagName("input"));
                    return inputs.stream().anyMatch(inp -> {
                        try {
                            return inp.isDisplayed() && !"hidden".equals(inp.getAttribute("type"));
                        } catch (Exception e) { return false; }
                    });
                } catch (Exception e) { return false; }
            });
            Thread.sleep(500);
            System.out.println("  [CategoryPage] Form input siap. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  [WARN] waitForFormInputReady timeout: " + e.getMessage());
        }
    }

    private void waitForDialogInputReady() throws InterruptedException {
        System.out.println("  [CategoryPage] Menunggu dialog/overlay siap...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".v-overlay--active, .v-dialog, div[role='dialog']")));
        } catch (Exception ignored) {}
        Thread.sleep(500);
    }

    // ==================== Private: Fill Name on Form ====================

    /**
     * Isi field Name di halaman form page.
     * Menggunakan pendekatan bertingkat:
     *  A. sendKeys langsung (paling reliable untuk Vuetify reactivity)
     *  B. JS native setter sebagai fallback
     *
     * PENTING: Vuetify 3 memerlukan event 'input' yang dipicu oleh interaksi
     * keyboard nyata (sendKeys), bukan hanya dari JS value setter.
     */
    public void fillNameOnForm(String value) throws InterruptedException {
        System.out.println("  [CategoryPage] Mengisi field Name: '" + value + "'");

        // Temukan input yang tepat: Name field (bukan Parent dropdown)
        WebElement targetInput = null;

        // A: Cari input dengan id 'category-name'
        try {
            targetInput = new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
                WebElement input = d.findElement(By.id("category-name"));
                if (input.isDisplayed() && !"hidden".equals(input.getAttribute("type"))) return input;
                return null;
            });
            System.out.println("  [CategoryPage] Target input ditemukan via By.id(\"category-name\").");
        } catch (Exception e) {
            System.out.println("  [WARN] Input By.id(\"category-name\") tidak ditemukan: " + e.getMessage());
        }

        // B: Cari input dengan id/placeholder 'name' atau 'nama' sebagai fallback
        if (targetInput == null) {
            try {
                targetInput = new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
                    List<WebElement> inputs = d.findElements(
                        By.xpath("//input[" +
                            "contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'name') or " +
                            "contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'name') or " +
                            "contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'nama') or " +
                            "contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'category')" +
                        "]"));
                    for (WebElement i : inputs) {
                        if (i.isDisplayed() && !"hidden".equals(i.getAttribute("type"))) return i;
                    }
                    return null;
                });
                System.out.println("  [CategoryPage] Target input ditemukan via id/placeholder.");
            } catch (Exception e) {
                System.out.println("  [WARN] Input by id/placeholder tidak ditemukan: " + e.getMessage());
            }
        }

        // C: Ambil semua input visible, pilih yang paling relevan (bukan dropdown parent) as second fallback
        if (targetInput == null) {
            try {
                List<WebElement> allInputs = driver.findElements(By.tagName("input"));
                List<WebElement> visibleInputs = new ArrayList<>();
                for (WebElement i : allInputs) {
                    try {
                        String type = i.getAttribute("type");
                        if (i.isDisplayed() && (type == null || type.equals("text") || type.isEmpty())) {
                            visibleInputs.add(i);
                        }
                    } catch (Exception ignored) {}
                }
                System.out.println("  [CategoryPage] Jumlah input visible: " + visibleInputs.size());
                // Pilih input terakhir jika ada parent dropdown (dropdown = input pertama)
                // Input Name biasanya ada di bawah Parent dropdown
                if (visibleInputs.size() >= 2) {
                    targetInput = visibleInputs.get(visibleInputs.size() - 1);
                    System.out.println("  [CategoryPage] Target input: input terakhir (setelah dropdown).");
                } else if (!visibleInputs.isEmpty()) {
                    targetInput = visibleInputs.get(0);
                    System.out.println("  [CategoryPage] Target input: input[0].");
                }
            } catch (Exception e) {
                System.out.println("  [WARN] Input scan gagal: " + e.getMessage());
            }
        }

        if (targetInput != null) {
            // Gunakan sendKeys untuk trigger Vuetify reactivity dengan benar
            typeIntoVuetifyInput(targetInput, value);
            System.out.println("  [CategoryPage] Name diisi via sendKeys (Vuetify-safe).");
        } else {
            // Last resort: JS native setter
            System.out.println("  [WARN] Input tidak ditemukan, coba JS native setter...");
            js.executeScript(
                "var nativeSet = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
                "var inputs = Array.from(document.querySelectorAll('input')).filter(function(i){" +
                "  return i.offsetParent!==null && i.type!=='hidden';" +
                "});" +
                "if(inputs.length>0){" +
                "  var inp = inputs[inputs.length-1];" + // ambil input terakhir
                "  inp.focus();" +
                "  nativeSet.call(inp,'');" +
                "  inp.dispatchEvent(new Event('input',{bubbles:true}));" +
                "  nativeSet.call(inp,arguments[0]);" +
                "  inp.dispatchEvent(new InputEvent('input',{bubbles:true,data:arguments[0],inputType:'insertText'}));" +
                "  inp.dispatchEvent(new Event('change',{bubbles:true}));" +
                "  inp.dispatchEvent(new Event('blur',{bubbles:true}));" +
                "}",
                value
            );
            Thread.sleep(400);
        }
    }

    /**
     * Ketik nilai ke input Vuetify dengan cara yang benar:
     * 1. Scroll dan fokus ke input
     * 2. Clear dengan Ctrl+A + Delete
     * 3. sendKeys karakter per karakter untuk trigger Vue reactivity
     * 4. Dispatch events tambahan
     */
    private void typeIntoVuetifyInput(WebElement input, String value) throws InterruptedException {
        try {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            Thread.sleep(200);
            input.click();
            Thread.sleep(200);

            // Clear existing value
            input.sendKeys(Keys.CONTROL + "a");
            Thread.sleep(100);
            input.sendKeys(Keys.DELETE);
            Thread.sleep(100);
            input.clear();
            Thread.sleep(100);

            // Type value — sendKeys triggers proper DOM events untuk Vue reactivity
            input.sendKeys(value);
            Thread.sleep(300);

            // Pastikan blur untuk trigger validasi
            input.sendKeys(Keys.TAB);
            Thread.sleep(200);

            // Kembali focus ke input
            input.click();
            Thread.sleep(100);

            System.out.println("  [CategoryPage] typeIntoVuetifyInput berhasil: '" + value + "'");
        } catch (Exception e) {
            System.out.println("  [WARN] typeIntoVuetifyInput error: " + e.getMessage());
            // Fallback: JS native setter
            js.executeScript(
                "var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
                "s.call(arguments[0],''); arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                "s.call(arguments[0],arguments[1]); " +
                "arguments[0].dispatchEvent(new InputEvent('input',{bubbles:true,data:arguments[1],inputType:'insertText'}));" +
                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                input, value);
            Thread.sleep(300);
        }
    }

    /**
     * Isi field Name di dalam dialog overlay (untuk edit).
     */
    private void fillNameInOverlay(String value) throws InterruptedException {
        System.out.println("  [CategoryPage] Mengisi Name di overlay: '" + value + "'");
        try {
            List<WebElement> overlayInputs = driver.findElements(By.cssSelector(
                ".v-overlay--active input:not([type='hidden'])," +
                ".v-dialog input:not([type='hidden'])," +
                ".v-card input:not([type='hidden'])"));
            List<WebElement> visible = new ArrayList<>();
            for (WebElement i : overlayInputs) {
                if (i.isDisplayed()) visible.add(i);
            }
            if (!visible.isEmpty()) {
                clearAndFillInput(visible.get(0), value);
                System.out.println("  [CategoryPage] Name diisi di overlay input[0].");
                return;
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Overlay input tidak ditemukan: " + e.getMessage());
        }
        fillNameOnForm(value); // fallback
    }

    // ==================== Private: Parent Dropdown ====================

    /**
     * Pilih Parent dari Vuetify dropdown/select.
     *
     * Langkah:
     * 1. Cari elemen field Parent (label 'Parent' atau 'Induk')
     * 2. Klik field untuk buka dropdown
     * 3. Ketik parentName di input yang aktif
     * 4. Klik item yang cocok di overlay list
     */
    private void selectParentFromDropdown(String parentName) throws InterruptedException {
        System.out.println("  [CategoryPage] Memilih Parent: '" + parentName + "'");

        // Langkah 1: Klik field Parent untuk membuka dropdown
        boolean fieldClicked = false;

        // Coba via label "Parent" atau "Induk"
        try {
            Boolean clicked = (Boolean) js.executeScript(
                "var labels = document.querySelectorAll('label');" +
                "for(var i=0;i<labels.length;i++){" +
                "  var t = labels[i].textContent.trim().toLowerCase();" +
                "  if(t.includes('parent') || t.includes('induk')){" +
                "    var field = labels[i].closest('.v-field') || labels[i].closest('.v-input') || labels[i].parentElement;" +
                "    if(field){ field.click(); return true; }" +
                "  }" +
                "}" +
                "return false;"
            );
            if (Boolean.TRUE.equals(clicked)) {
                fieldClicked = true;
                System.out.println("  [CategoryPage] Field Parent diklik via label.");
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Label click gagal: " + e.getMessage());
        }

        if (!fieldClicked) {
            // Fallback: klik input pertama (sebelum Name field)
            try {
                List<WebElement> inputs = driver.findElements(
                    By.xpath("//input[@type='text' and not(@readonly)]"));
                if (!inputs.isEmpty() && inputs.get(0).isDisplayed()) {
                    inputs.get(0).click();
                    fieldClicked = true;
                    System.out.println("  [CategoryPage] Field Parent diklik via input[0].");
                }
            } catch (Exception e) {
                System.out.println("  [WARN] Input[0] click gagal: " + e.getMessage());
            }
        }

        Thread.sleep(800);

        // Langkah 2: Ketik nama parent di input yang sekarang aktif (filter dropdown)
        try {
            List<WebElement> textInputs = driver.findElements(
                By.xpath("//input[@type='text' and not(@readonly)]"));
            for (WebElement inp : textInputs) {
                if (inp.isDisplayed()) {
                    inp.clear();
                    inp.sendKeys(parentName);
                    System.out.println("  [CategoryPage] Nama parent diketik di input aktif.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal ketik di input dropdown: " + e.getMessage());
        }

        Thread.sleep(1200);

        // Langkah 3: Klik item yang cocok di overlay/dropdown list
        try {
            Boolean picked = (Boolean) js.executeScript(
                "var selectors = [" +
                "  '.v-overlay--active .v-list-item'," +
                "  '.v-menu__content .v-list-item'," +
                "  '.v-overlay .v-list-item'," +
                "  '.v-list-item'" +
                "];" +
                "for(var s=0; s<selectors.length; s++){" +
                "  var items = document.querySelectorAll(selectors[s]);" +
                "  for(var i=0; i<items.length; i++){" +
                "    if(items[i].offsetParent !== null && items[i].textContent.trim().includes(arguments[0])){" +
                "      items[i].click();" +
                "      return true;" +
                "    }" +
                "  }" +
                "}" +
                "return false;",
                parentName
            );

            if (Boolean.TRUE.equals(picked)) {
                System.out.println("  [CategoryPage] Parent '" + parentName + "' berhasil dipilih dari dropdown.");
            } else {
                System.out.println("  [WARN] Parent '" + parentName + "' tidak ditemukan di dropdown list.");
                // Tekan Enter sebagai fallback (pilih item pertama yang difilter)
                try {
                    List<WebElement> visibleInputs = driver.findElements(
                        By.xpath("//input[@type='text']"));
                    for (WebElement inp : visibleInputs) {
                        if (inp.isDisplayed()) {
                            inp.sendKeys(Keys.RETURN);
                            System.out.println("  [CategoryPage] Enter ditekan sebagai fallback selection.");
                            break;
                        }
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal klik item dropdown: " + e.getMessage());
        }

        Thread.sleep(600);
    }

    // ==================== Private: Save Button ====================

    /**
     * Klik tombol Save di form page.
     * HTML: <button class="bg-primary px-3"><span class="v-btn__content">Save</span></button>
     */
    public void clickSaveOnForm() throws InterruptedException {
        System.out.println("  [CategoryPage] Klik tombol Save...");

        // Strategi 1: JS via span.v-btn__content
        Boolean clicked = (Boolean) js.executeScript(
            "var buttons = document.querySelectorAll('button');" +
            "for(var i=0; i<buttons.length; i++){" +
            "  var btn = buttons[i];" +
            "  if(btn.disabled) continue;" +
            "  var spans = btn.querySelectorAll('span.v-btn__content');" +
            "  for(var j=0; j<spans.length; j++){" +
            "    var txt = spans[j].textContent.trim();" +
            "    if(txt === 'Save' || txt === 'Simpan'){" +
            "      btn.click();" +
            "      return true;" +
            "    }" +
            "  }" +
            "}" +
            "return false;"
        );

        if (Boolean.TRUE.equals(clicked)) {
            System.out.println("  [CategoryPage] Save diklik via span.v-btn__content.");
            Thread.sleep(1500);
            return;
        }

        // Strategi 2: WebDriverWait button text
        System.out.println("  [WARN] JS span tidak ketemu, coba WebDriverWait Save...");
        try {
            WebElement saveBtn = new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> {
                List<WebElement> btns = d.findElements(By.tagName("button"));
                for (WebElement btn : btns) {
                    try {
                        if (!btn.isDisplayed() || !btn.isEnabled()) continue;
                        String txt = btn.getText().trim();
                        if (txt.equalsIgnoreCase("Save") || txt.equalsIgnoreCase("Simpan")) return btn;
                    } catch (Exception ignored) {}
                }
                return null;
            });
            js.executeScript("arguments[0].click();", saveBtn);
            System.out.println("  [CategoryPage] Save diklik via WebDriverWait.");
            Thread.sleep(1500);
        } catch (Exception e) {
            // Strategi 3: textContent fallback
            System.out.println("  [WARN] WebDriverWait Save gagal: " + e.getMessage());
            js.executeScript(
                "var btns = document.querySelectorAll('button');" +
                "for(var i=0;i<btns.length;i++){" +
                "  if((btns[i].textContent.trim().includes('Save')||btns[i].textContent.trim().includes('Simpan'))" +
                "     && !btns[i].disabled){ btns[i].click(); break; }}"
            );
            Thread.sleep(1500);
            System.out.println("  [CategoryPage] Save diklik via JS textContent fallback.");
        }
    }

    /**
     * Klik Save di dalam overlay/dialog.
     */
    private void clickSaveInOverlay() throws InterruptedException {
        System.out.println("  [CategoryPage] Klik Save di overlay...");
        js.executeScript(
            "var scopes = ['.v-overlay--active button','.v-dialog button','.v-card button','button'];" +
            "for(var s=0;s<scopes.length;s++){" +
            "  var btns = document.querySelectorAll(scopes[s]);" +
            "  for(var i=0;i<btns.length;i++){" +
            "    var t = btns[i].textContent.trim();" +
            "    if((t.includes('Save')||t.includes('Simpan')) && !btns[i].disabled){ btns[i].click(); return; }" +
            "  }" +
            "}"
        );
        Thread.sleep(1000);
    }

    // ==================== Private: Wait After Save ====================

    /**
     * Tunggu navigasi kembali ke list setelah Save.
     * Setelah Save berhasil, app akan navigate ke:
     *   - tab=category (sukses langsung)
     *   - OAuth callback lalu redirect ke list
     *   - Tetap di form jika ada error validasi
     */
    public void waitForReturnToList() throws InterruptedException {
        System.out.println("  [CategoryPage] Menunggu kembali ke list setelah Save...");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(d -> {
                String url = d.getCurrentUrl();
                // Sudah di list
                if (url.contains("tab=category") && !url.contains("/form") && !url.contains("/edit"))
                    return true;
                // OAuth callback setelah save → tunggu bersih
                if ((url.contains("#state=") || url.contains("?state=")) && url.contains("code=")) {
                    return false; // tunggu terus
                }
                return false;
            });
            System.out.println("  [CategoryPage] Kembali ke list. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            String url = driver.getCurrentUrl();
            System.out.println("  [WARN] waitForReturnToList timeout. URL: " + url);

            // Jika OAuth masih muncul, tunggu dulu bersih
            if ((url.contains("#state=") || url.contains("?state=")) && url.contains("code=")) {
                System.out.println("  [CategoryPage] OAuth di URL — tunggu bersih alami...");
                waitForOAuthToClean(15);
            }

            // Navigasi paksa ke list (data mungkin sudah tersimpan)
            System.out.println("  [CategoryPage] Force navigasi ke list...");
            driver.get(CATEGORY_TAB_URL);
            Thread.sleep(3500);
            waitForListPageContent();
        }
    }

    // ==================== Private: OAuth & Page Content ====================

    /**
     * Tangani OAuth hash yang muncul di URL saat ini.
     * Tunggu SPA memproses token dan URL bersih secara alami.
     */
    private void handleOAuthOnCurrentPage() throws InterruptedException {
        String url = driver.getCurrentUrl();
        boolean isDirty = (url.contains("#state=") || url.contains("?state=")) && url.contains("code=");
        if (!isDirty) return;

        System.out.println("  [CategoryPage] OAuth hash di halaman saat ini, menunggu bersih...");
        boolean cleaned = waitForOAuthToClean(15);
        if (!cleaned) {
            System.out.println("  [WARN] OAuth tidak bersih dalam 15s, paksa replaceState...");
            try {
                js.executeScript(
                    "window.history.replaceState({}, '', '/inventory/setting?tab=category');");
                Thread.sleep(500);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Tunggu hingga URL tidak mengandung OAuth hash.
     * @return true jika URL berhasil bersih, false jika timeout
     */
    private boolean waitForOAuthToClean(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(d -> {
                String u = d.getCurrentUrl();
                return !u.contains("#state=") && !u.contains("?state=");
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tunggu konten tabel/halaman list category muncul.
     */
    private void waitForListPageContent() throws InterruptedException {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                try {
                    // Cek ada tbody atau minimal ada tombol Add
                    List<WebElement> tbody = d.findElements(By.tagName("tbody"));
                    List<WebElement> addBtns = d.findElements(By.xpath(
                        "//button[.//span[contains(@class,'v-btn__content') and (contains(text(),'Add') or contains(text(),'Tambah'))]]"));
                    return !tbody.isEmpty() || !addBtns.isEmpty();
                } catch (Exception e) { return false; }
            });
            System.out.println("  [CategoryPage] Halaman list siap. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  [WARN] waitForListPageContent timeout: " + e.getMessage());
        }
    }

    // ==================== Private: Fill Input ====================

    /**
     * Clear dan isi input element dengan dispatch Vue events.
     */
    private void clearAndFillInput(WebElement input, String value) throws InterruptedException {
        try {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            Thread.sleep(100);
            input.click();
            input.sendKeys(Keys.CONTROL + "a");
            input.sendKeys(Keys.DELETE);
            input.clear();
            Thread.sleep(100);
            input.sendKeys(value);
            js.executeScript(
                "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", input);
        } catch (Exception e) {
            // JS native setter fallback
            js.executeScript(
                "var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
                "s.call(arguments[0],''); arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                "s.call(arguments[0],arguments[1]); arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                input, value);
        }
        Thread.sleep(300);
    }
}
