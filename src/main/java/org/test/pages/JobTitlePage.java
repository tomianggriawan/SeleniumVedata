package org.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test.common.BasePage;

import java.time.Duration;
import java.util.List;

/**
 * JobTitlePage - Page Object for HCM > Setting > Job Title.
 * Supports CRU operations: Create, Read (verify/search), Update.
 *
 * Uses @FindBy annotations with PageFactory.initElements() for WebElement
 * injection, plus By locators for dynamic/complex XPath queries.
 */
public class JobTitlePage extends BasePage {

    // ==================== @FindBy WebElements ====================

    /** Header halaman "Job Title List" */
    @FindBy(xpath = "//h1[contains(@class,'page-title')]")
    private WebElement pageTitleElement;

    /** Tombol "Add" untuk navigasi ke halaman form tambah Job Title */
    @FindBy(xpath = "//button[contains(@class,'bg-primary') and contains(.,'Add')]")
    private WebElement addButtonElement;

    /** Input "Code" di halaman form (id="job-code") */
    @FindBy(id = "job-code")
    private WebElement formCodeInput;

    /** Input "Name" di halaman form (id="job-name") */
    @FindBy(id = "job-name")
    private WebElement formNameInput;

    /** Tombol "Save" di halaman form */
    @FindBy(xpath = "//button[contains(.,'Save')]")
    private WebElement saveButtonElement;

    /** Tombol "Cancel" di halaman form */
    @FindBy(xpath = "//button[contains(.,'Cancel')]")
    private WebElement cancelButtonElement;

    /** Notifikasi sukses (Vue Notification: .vue-notification.success) */
    @FindBy(css = "div.vue-notification.success")
    private WebElement successNotification;

    // ==================== By Locators (untuk query dinamis) ====================

    private final By pageTitle = By.xpath("//h1[contains(@class,'page-title')]");
    private final By addButton = By.xpath("//button[contains(@class,'bg-primary') and contains(.,'Add')]");
    private final By tableHeaderAction = By.xpath("//th[.//span[text()='Action']]");
    private final By tableHeaderCode   = By.xpath("//th[.//span[text()='Code']]");
    private final By tableHeaderName   = By.xpath("//th[.//span[text()='Name']]");
    private final By formInputCode     = By.id("job-code");
    private final By formInputName     = By.id("job-name");
    private final By formSaveButton    = By.xpath("//button[contains(.,'Save')]");
    private final By formCancelButton  = By.xpath("//button[contains(.,'Cancel')]");
    private final By notificationGroup = By.cssSelector("div.vue-notification.success");
    private final By tableRows         = By.xpath("//tbody//tr");

    // Locators alternatif untuk form/modal Edit (ID berbeda atau di dalam dialog)
    // NOTE: All label XPaths use contains(normalize-space(.), ...) instead of exact
    // text() match to handle Vuetify's dynamic label rendering.
    private final By editDialog         = By.xpath("//div[contains(@class,'v-overlay') or contains(@class,'v-dialog')]");
    private final By editDialogActive   = By.xpath("//div[contains(@class,'v-overlay')]");
    private final By editModalLabelCode = By.xpath("//div[contains(@class, 'v-overlay')]//label[contains(normalize-space(.), 'Code')]");
    private final By editModalLabelName = By.xpath("//div[contains(@class, 'v-overlay')]//label[contains(normalize-space(.), 'Name')]");
    private final By editModalInputCode = By.xpath("//div[contains(@class, 'v-overlay')]//label[contains(normalize-space(.), 'Code')]/ancestor::div[contains(@class, 'v-field')]//input | //div[contains(@class, 'v-overlay')]//label[contains(normalize-space(.), 'Code')]/following::input[1]");
    private final By editModalInputName = By.xpath("//div[contains(@class, 'v-overlay')]//label[contains(normalize-space(.), 'Name')]/ancestor::div[contains(@class, 'v-field')]//input | //div[contains(@class, 'v-overlay')]//label[contains(normalize-space(.), 'Name')]/following::input[1]");
    private final By editModalSaveBtn   = By.xpath("//div[contains(@class, 'v-dialog') or contains(@class, 'v-overlay')]//button[contains(.,'Save')]");

    // ==================== Constructor ====================

    public JobTitlePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ==================== Read (Verifikasi Halaman) ====================

    public JobTitlePage verifyPageLoaded() {
        assertCondition("Halaman 'Job Title List' tampil", isDisplayed(pageTitle, 10));
        return this;
    }

    public JobTitlePage verifyPageTitle() {
        String title = getText(pageTitle).trim();
        assertCondition("Judul halaman mengandung 'Job Title'", title.contains("Job Title"));
        return this;
    }

    public JobTitlePage verifyTableColumnsDisplayed() {
        assertCondition("Kolom 'Action' tampil", isPresent(tableHeaderAction, 10));
        assertCondition("Kolom 'Code' tampil",   isPresent(tableHeaderCode,   10));
        assertCondition("Kolom 'Name' tampil",   isPresent(tableHeaderName,   10));
        return this;
    }

    public JobTitlePage verifyAddButtonDisplayed() {
        assertCondition("Tombol 'Add' tampil", isDisplayed(addButton, 5));
        return this;
    }

    public JobTitlePage verifyTableHasData() {
        By firstRow = By.xpath("//tbody//tr[1]//td[2]");
        assertCondition("Tabel Job Title memiliki data", isPresent(firstRow, 10));
        return this;
    }

    public JobTitlePage verifyJobTitleInTable(String code, String name) {
        By row = By.xpath("//tbody//tr[contains(.,'" + code + "') and contains(.,'" + name + "')]");
        assertCondition("Job Title '" + code + "' - '" + name + "' ada di tabel",
            isPresent(row, 10));
        return this;
    }

    // ==================== Create (Tambah Job Title) ====================

    public JobTitlePage clickAddButton() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(addButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(formInputCode));
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Fill the Code field on the Job Title form using JavaScript + Vue-compatible events.
     * Handles Vuetify v-model binding issues that standard sendKeys() can miss.
     *
     * @param code Value to fill (if null, skips the field)
     * @return self
     */
    public JobTitlePage fillCodeField(String code) {
        if (code == null) return this;
        try {
            By locator = resolveFieldLocator(formInputCode, editModalInputCode);
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            sleep(200);
            clearAndTypeJS(locator, code);
            System.out.println("  [INFO] Code field filled: '" + code + "'");
        } catch (Exception e) {
            System.out.println("  [WARN] Failed to fill Code field: " + e.getClass().getSimpleName()
                + " - " + e.getMessage());
        }
        return this;
    }

    /**
     * Fill the Name field on the Job Title form using JavaScript + Vue-compatible events.
     * Automatically detects modal context and uses the correct locator.
     *
     * @param name Value to fill (if null, skips the field)
     * @return self
     */
    public JobTitlePage fillNameField(String name) {
        if (name == null) return this;
        try {
            By locator = resolveFieldLocator(formInputName, editModalInputName);
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            sleep(200);
            clearAndTypeJS(locator, name);
            System.out.println("  [INFO] Name field filled: '" + name + "'");
        } catch (Exception e) {
            System.out.println("  [WARN] Failed to fill Name field: " + e.getClass().getSimpleName()
                + " - " + e.getMessage());
        }
        return this;
    }

    /**
     * Fill both Code and Name fields.
     * Catches and logs errors per-field so you always know which field failed.
     */
    public JobTitlePage fillJobTitleForm(String code, String name) {
        return this.fillCodeField(code).fillNameField(name);
    }

    /**
     * Legacy alias — delegates to the robust fillJobTitleForm.
     */
    public JobTitlePage fillFormWithFindBy(String code, String name) {
        return fillJobTitleForm(code, name);
    }

    public JobTitlePage clickSave() {
        String currentUrl = driver.getCurrentUrl();
        System.out.println("  [DEBUG] Current URL before Save: " + currentUrl);
        boolean isModalMode = !currentUrl.contains("/form");

        try {
            // Cari tombol Save — coba di /form page dulu, fallback ke dalam modal
            WebElement saveBtn = null;
            try {
                saveBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(formSaveButton));
            } catch (Exception e) {
                System.out.println("  [WARN] Primary Save button not found, trying modal Save locator...");
                saveBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(editModalSaveBtn));
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);
            sleep(300);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
            System.out.println("  [DEBUG] Save button clicked (JS).");

            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

            if (isModalMode) {
                // Mode modal: tunggu modal menutup, lalu pageTitle muncul
                System.out.println("  [INFO] Modal mode: waiting for dialog to close...");
                try {
                    longWait.until(ExpectedConditions.invisibilityOfElementLocated(editDialogActive));
                    System.out.println("  [DEBUG] Edit modal closed.");
                } catch (Exception e) {
                    System.out.println("  [WARN] Modal did not close within 30s.");
                }
            } else {
                // Mode halaman /form: tunggu redirect
                try {
                    longWait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/form")));
                    System.out.println("  [DEBUG] Redirected away from /form.");
                } catch (Exception e) {
                    System.out.println("  [WARN] Still on /form after 30s. URL: " + driver.getCurrentUrl());
                    String pageSrc = driver.getPageSource();
                    System.out.println("  [DEBUG] Page source snippet: "
                        + pageSrc.substring(0, Math.min(pageSrc.length(), 500)));
                    handlePostSaveRedirect();
                }
            }

            try {
                longWait.until(ExpectedConditions.presenceOfElementLocated(pageTitle));
                System.out.println("  [DEBUG] List page confirmed (pageTitle found).");
            } catch (Exception e) {
                System.out.println("  [WARN] pageTitle not found. URL: " + driver.getCurrentUrl());
            }

            sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    public JobTitlePage clickCancel() {
        WebElement cancelBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(formCancelButton));
        cancelBtn.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> !d.getCurrentUrl().contains("/form"));
        sleep(500);
        return this;
    }

    // ==================== Read (Search / Verifikasi Data di Tabel) ====================

    /**
     * Cari Job Title di tabel berdasarkan Code.
     * Return true jika baris dengan code tersebut ditemukan.
     */
    public boolean isJobTitleExistInTable(String code) {
        By rowLocator = By.xpath("//tbody//tr[td[contains(.,'" + code + "')]]");
        return isPresent(rowLocator, 5);
    }

    /**
     * Ambil teks dari kolom Name pada baris yang mengandung code tertentu.
     *
     * @param code Kode job title
     * @return Nama job title atau string kosong jika tidak ditemukan
     */
    public String getJobTitleNameByCode(String code) {
        try {
            By nameCell = By.xpath("//tbody//tr[td[contains(.,'" + code + "')]]/td[3]");
            return getText(nameCell).trim();
        } catch (Exception e) {
            System.out.println("  [WARN] Job Title dengan code '" + code + "' tidak ditemukan.");
            return "";
        }
    }

    /**
     * Ambil semua data dari baris Job Title tertentu.
     *
     * @param code Kode job title
     * @return Array [actionButtonText, code, name] atau null jika tidak ditemukan
     */
    public String[] getJobTitleRowData(String code) {
        try {
            By rowCells = By.xpath("//tbody//tr[td[contains(.,'" + code + "')]]/td");
            List<WebElement> cells = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(rowCells));
            if (cells.size() >= 3) {
                String action = cells.get(0).getText().trim();
                String codeText = cells.get(1).getText().trim();
                String nameText = cells.get(2).getText().trim();
                return new String[]{action, codeText, nameText};
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Row data untuk code '" + code + "' tidak ditemukan.");
        }
        return null;
    }

    /**
     * Hitung jumlah baris data di tabel.
     */
    public int getTableRowCount() {
        try {
            List<WebElement> rows = driver.findElements(tableRows);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== Update (Edit Job Title) ====================

    public JobTitlePage clickEditJobTitle(String code) {
        // XPath: cari baris yang mengandung code, lalu cari button dengan icon/tooltip/teks Edit
        By editBtn = By.xpath(
            "//tbody//tr[td[contains(.,'" + code + "')]]//button" +
            "[@data-testid='edit-btn' or contains(@aria-label,'Edit')" +
            " or .//*[name()='svg' and contains(@class,'icon-pencil')]" +
            " or contains(.,'Edit') or contains(.,'edit')]"
        );
        By editBtnFallback = By.xpath(
            "//tbody//tr[td[contains(.,'" + code + "')]]//button[1]"
        );

        System.out.println("  [DEBUG] Scrolling row with code '" + code + "' into view...");
        try {
            WebElement row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tbody//tr[td[contains(.,'" + code + "')]]")));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", row);
            sleep(300);
        } catch (Exception e) {
            System.out.println("  [WARN] Could not scroll row into view: " + e.getClass().getSimpleName());
        }

        dismissSuccessNotificationIfPresent();

        // Cari dan klik Edit button
        WebElement btn = null;
        try {
            btn = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(editBtn));
        } catch (Exception e) {
            System.out.println("  [WARN] Primary Edit locator failed, trying fallback...");
            btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(editBtnFallback));
        }

        // Diagnostic result: Vuetify v-menu requires the hover state (mouseenter/mouseover)
        // to be established BEFORE a click is accepted.
        // - Native btn.click() and Actions.click() WITHOUT a pause both fail (aria-expanded stays false).
        // - Actions.moveToElement.pause(500).click() WORKS (confirmed by Diagnostic2).
        // - JS dispatching mouseenter+mouseover+mousedown+mouseup+click WORKS (confirmed by Diagnostic2).
        String menuId = null;
        try {
            menuId = btn.getAttribute("aria-controls");
        } catch (Exception ignored) {}

        // Primary: Actions hover + 500ms pause + click
        boolean menuOpened = false;
        try {
            new Actions(driver).moveToElement(btn).pause(Duration.ofMillis(500)).click().perform();
            System.out.println("  [DEBUG] Three-dots button clicked via Actions+pause (menuId=" + menuId + ").");
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"));
            menuOpened = true;
            System.out.println("  [DEBUG] Menu opened (aria-expanded=true via Actions+pause).");
        } catch (Exception e) {
            System.out.println("  [WARN] Actions+pause click did not open menu. Trying JS dispatch...");
        }

        // Fallback: JS dispatch mouseenter+mouseover+mousedown+mouseup+click
        if (!menuOpened) {
            try {
                final WebElement btnFinal = btn;
                ((JavascriptExecutor) driver).executeScript(
                    "var el = arguments[0];" +
                    "el.dispatchEvent(new MouseEvent('mouseenter', {bubbles:true}));" +
                    "el.dispatchEvent(new MouseEvent('mouseover',  {bubbles:true}));" +
                    "el.dispatchEvent(new MouseEvent('mousedown',  {bubbles:true, cancelable:true, view:window}));" +
                    "el.dispatchEvent(new MouseEvent('mouseup',    {bubbles:true, cancelable:true, view:window}));" +
                    "el.dispatchEvent(new MouseEvent('click',      {bubbles:true, cancelable:true, view:window}));",
                    btnFinal
                );
                System.out.println("  [DEBUG] Three-dots button clicked via JS dispatch (menuId=" + menuId + ").");
                new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"));
                menuOpened = true;
                System.out.println("  [DEBUG] Menu opened (aria-expanded=true via JS dispatch).");
            } catch (Exception e) {
                System.out.println("  [WARN] JS dispatch also did not open menu: " + e.getMessage());
            }
        }
        sleep(300);

        // If menu opened, click the Edit/Ubah item inside it
        if (menuOpened) {
            System.out.println("  [DEBUG] Looking for Edit/Ubah option in dropdown menu (id=" + menuId + ")...");
            WebElement menuOption = locateMenuEditOption(menuId);
            if (menuOption != null) {
                System.out.println("  [DEBUG] Clicking Edit option in dropdown menu...");
                try {
                    // Use Actions with hover pause — same technique as opening the menu
                    new Actions(driver).moveToElement(menuOption).pause(Duration.ofMillis(200)).click().perform();
                    System.out.println("  [DEBUG] Clicked Edit option (Actions+pause).");
                } catch (Exception e) {
                    try {
                        menuOption.click();
                        System.out.println("  [DEBUG] Clicked Edit option (native).");
                    } catch (Exception e2) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuOption);
                        System.out.println("  [DEBUG] Clicked Edit option (JS fallback).");
                    }
                }
                sleep(500);
            } else {
                System.out.println("  [WARN] Edit option not found in dropdown. Proceeding anyway.");
            }
        } else {
            // Menu did not open — check if form/modal opened directly
            System.out.println("  [DEBUG] No dropdown detected. Checking if form/modal is already open.");
        }

        // 5. Deteksi apakah Edit membuka halaman baru (/form) atau modal dialog
        String currentUrl = driver.getCurrentUrl();
        System.out.println("  [DEBUG] URL after Edit click: " + currentUrl);

        if (currentUrl.contains("/form")) {
            System.out.println("  [INFO] Edit navigated to /form page. Waiting for formInputCode...");
            waitForFormField(formInputCode);
        } else {
            System.out.println("  [INFO] Edit opened a modal dialog. Waiting for modal...");
            waitForEditModalField();
        }

        sleep(500);
        return this;
    }

    /**
     * Wait for a form field to be present.
     * Tries the primary By locator first, then falls back to printing debug info.
     */
    private void waitForFormField(By primaryLocator) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(primaryLocator));
            System.out.println("  [INFO] Form field located via: " + primaryLocator);
        } catch (Exception e) {
            System.out.println("  [WARN] Primary locator " + primaryLocator + " failed: " + e.getClass().getSimpleName());
            debugPageState("form-inputs");
            throw e;
        }
    }

    /**
     * Wait for Vuetify modal to load form inputs using flexible label-based XPaths.
     * Uses multi-strategy fallback: 1) label + ancestor v-field, 2) label + following input,
     * 3) positional index inside overlay.
     * Ensures fields are pre-cleared for edit mode.
     */
    private void waitForEditModalField() {
        boolean codeFound = false;
        boolean nameFound = false;

        // 1. Wait for modal overlay to be present and visible
        // Use broader locator that matches any visible v-overlay (no --active class required)
        WebDriverWait modalWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            modalWait.until(ExpectedConditions.visibilityOfElementLocated(editDialogActive));
            System.out.println("  [INFO] Vuetify overlay is active and visible.");
            sleep(500);
        } catch (TimeoutException e) {
            System.out.println("  [WARN] No visible overlay detected after 10s.");
            debugPageState("modal-check");
            throw e;
        }

        // 2. Wait for labels to appear inside overlay (lightweight form-rendered check)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.presenceOfElementLocated(editModalLabelCode));
            new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.presenceOfElementLocated(editModalLabelName));
            System.out.println("  [INFO] Code and Name labels found in modal.");
        } catch (TimeoutException e) {
            System.out.println("  [WARN] Form labels not found in modal after 8s.");
            debugPageState("modal-labels");
        }

        // 3. Find and clear Code input
        WebElement codeEl = locateModalField(
            new By[]{editModalInputCode, overlayInputByIndex(1)}, "Code");
        if (codeEl != null) {
            clearFieldJS(codeEl);
            codeFound = true;
        }

        // 4. Find and clear Name input
        WebElement nameEl = locateModalField(
            new By[]{editModalInputName, overlayInputByIndex(2)}, "Name");
        if (nameEl != null) {
            clearFieldJS(nameEl);
            nameFound = true;
        }

        System.out.println("  [DEBUG] Modal field detection: Code=" + codeFound + ", Name=" + nameFound);

        if (!codeFound || !nameFound) {
            debugPageState("modal-inputs");
            throw new TimeoutException(
                "Edit modal fields not found. Code=" + codeFound + ", Name=" + nameFound);
        }

        System.out.println("  [INFO] Modal input fields located and cleared.");
    }

    /**
     * Build a positional XPath for the Nth input inside the active overlay.
     */
    private By overlayInputByIndex(int index) {
        return By.xpath("(//div[contains(@class, 'v-overlay')]//input)[" + index + "]");
    }

    /**
     * Clear an input field using JavaScript + sendKeys fallback.
     * Ensures the field is empty before typing new value (critical for edit mode).
     */
    private void clearFieldJS(WebElement el) {
        try {
            // Try JS clear first (more reliable for Vuetify)
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';" +
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", el);
            sleep(200);
        } catch (Exception jsEx) {
            // Fallback to Selenium clear + sendKeys
            try {
                el.clear();
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                sleep(200);
            } catch (Exception seleniumEx) {
                System.out.println("  [WARN] Could not clear field: " + seleniumEx.getMessage());
            }
        }
    }

    /**
     * Try each By locator in sequence until one finds a visible element.
     * Returns the WebElement if found, null otherwise.
     *
     * @param strategies Array of By locators to try in order
     * @param fieldName  Human-readable field name ("Code" or "Name")
     * @return found WebElement, or null if all strategies failed
     */
    private WebElement locateModalField(By[] strategies, String fieldName) {
        for (By locator : strategies) {
            try {
                WebElement el = new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
                if (el.isDisplayed()) {
                    System.out.println("  [INFO] " + fieldName + " field found via: " + locator);
                    return el;
                }
            } catch (Exception ignored) {
            }
        }
        System.out.println("  [WARN] " + fieldName + " not found with any strategy.");
        return null;
    }

    /**
     * Locate the Edit menu option in the Vuetify dropdown menu.
     * Tries the menu container identified by menuId (aria-controls on the trigger button) first,
     * then falls back to broader strategies.
     *
     * @param menuId The value of aria-controls on the trigger button (e.g. "v-menu-v-61"), may be null.
     */
    private WebElement locateMenuEditOption(String menuId) {
        // Build locators, starting with the most specific (scoped to the exact menu container)
        List<By> strategies = new java.util.ArrayList<>();

        if (menuId != null && !menuId.isEmpty()) {
            // Vuetify renders the menu content inside a div with id="{menuId}"
            strategies.add(By.xpath("//*[@id='" + menuId + "']//*[contains(normalize-space(.), 'Edit') or contains(normalize-space(.), 'Ubah')]"));
            strategies.add(By.xpath("//*[@id='" + menuId + "']//div | //*[@id='" + menuId + "']//span | //*[@id='" + menuId + "']//a | //*[@id='" + menuId + "']//button"));
        }

        // Broader fallbacks: any v-list-item containing Edit/Ubah text
        strategies.add(By.xpath("//*[contains(@class, 'v-list-item') or contains(@class, 'v-list-item-title')][contains(normalize-space(.), 'Edit') or contains(normalize-space(.), 'Ubah')]"));
        strategies.add(By.xpath("//div[contains(@class, 'v-overlay') or contains(@class, 'v-menu')]//*[contains(normalize-space(.), 'Edit') or contains(normalize-space(.), 'Ubah')]"));
        strategies.add(By.xpath("//*[self::div or self::span or self::a or self::button][contains(normalize-space(.), 'Edit') or contains(normalize-space(.), 'Ubah')]"));

        for (By locator : strategies) {
            try {
                WebElement el = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
                if (el.isDisplayed()) {
                    System.out.println("  [INFO] Edit menu option found via: " + locator);
                    return el;
                }
            } catch (Exception ignored) {
            }
        }

        // Last resort: use JS to find and return the element by text content
        try {
            WebElement el = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "var allEls = document.querySelectorAll('div, span, a, button, li');" +
                "for (var i = 0; i < allEls.length; i++) {" +
                "  var t = allEls[i].textContent.trim();" +
                "  if ((t === 'Edit' || t === 'Ubah') && allEls[i].offsetParent !== null) {" +
                "    return allEls[i];" +
                "  }" +
                "}" +
                "return null;"
            );
            if (el != null) {
                System.out.println("  [INFO] Edit menu option found via JS text search.");
                return el;
            }
        } catch (Exception ignored) {}

        System.out.println("  [WARN] Edit menu option not found via any strategy.");
        return null;
    }

    /**
     * Select the correct By locator based on the current UI context.
     * Returns the modal locator if a visible v-overlay is detected,
     * otherwise returns the form page locator.
     */
    private By resolveFieldLocator(By formLocator, By modalLocator) {
        if (driver.getCurrentUrl().contains("/form")) {
            return formLocator;
        }
        try {
            List<WebElement> overlays = driver.findElements(
                By.xpath("//div[contains(@class, 'v-overlay')]"));
            for (WebElement overlay : overlays) {
                if (overlay.isDisplayed()) {
                    System.out.println("  [DEBUG] Modal context detected, using modal locator.");
                    return modalLocator;
                }
            }
        } catch (Exception ignored) {
        }
        return formLocator;
    }

    /**
     * Debug helper: print current URL, page title, and dump relevant DOM snippets.
     */
    private void debugPageState(String context) {
        System.out.println("  [DEBUG] === Page State (" + context + ") ===");
        System.out.println("  [DEBUG] URL: " + driver.getCurrentUrl());
        System.out.println("  [DEBUG] Title: " + driver.getTitle());

        // Dump all input elements on the page
        String inputInfo = (String) ((JavascriptExecutor) driver).executeScript(
            "var inputs = document.querySelectorAll('input');" +
            "var result = [];" +
            "for (var i = 0; i < inputs.length; i++) {" +
            "  var el = inputs[i];" +
            "  result.push('#' + el.id + ' [name=' + (el.name || '') + '] [placeholder=' + (el.placeholder || '') + ']');" +
            "}" +
            "return result.join(' | ');"
        );
        System.out.println("  [DEBUG] Inputs on page: " + inputInfo);

        // Dump all button elements
        String btnInfo = (String) ((JavascriptExecutor) driver).executeScript(
            "var btns = document.querySelectorAll('button');" +
            "var result = [];" +
            "for (var i = 0; i < btns.length; i++) {" +
            "  result.push('<' + btns[i].textContent.trim().substring(0,30) + '>');" +
            "}" +
            "return result.join(' | ');"
        );
        System.out.println("  [DEBUG] Buttons on page: " + btnInfo);

        // Check for modal/dialog containers
        String dialogInfo = (String) ((JavascriptExecutor) driver).executeScript(
            "var dialogs = document.querySelectorAll('.v-dialog, .v-overlay, .v-dialog--active, .v-overlay--active');" +
            "var result = [];" +
            "for (var i = 0; i < dialogs.length; i++) {" +
            "  var d = dialogs[i];" +
            "  result.push(d.className.substring(0,60) + ' [displayed=' + (d.offsetParent !== null) + ']');" +
            "}" +
            "return result.join(' | ');"
        );
        System.out.println("  [DEBUG] Dialogs on page: " + dialogInfo);
        System.out.println("  [DEBUG] ===============================");
    }

    /**
     * Dismiss Vue success notification if it is covering the edit button.
     * The notification auto-fades, but clicking it or waiting for it to disappear
     * ensures the button underneath is unobstructed.
     */
    private void dismissSuccessNotificationIfPresent() {
        try {
            WebElement notif = driver.findElement(By.cssSelector("div.vue-notification.success"));
            if (notif.isDisplayed()) {
                System.out.println("  [DEBUG] Success notification detected, waiting for fade-out...");
                new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(ExpectedConditions.invisibilityOf(notif));
                sleep(300);
            }
        } catch (Exception ignored) {
            // Notification not present or already gone — proceed
        }
    }

    /**
     * Update nama Job Title: edit baris dengan code tertentu, isi name baru, save.
     * Method convenience yang menggabungkan clickEdit + fill + save.
     *
     * @param code       Kode job title yang akan di-edit
     * @param newName    Nama baru
     * @return JobTitlePage (setelah redirect ke halaman list)
     */
    public JobTitlePage updateJobTitleName(String code, String newName) {
        return this
            .clickEditJobTitle(code)
            .fillFormWithFindBy(null, newName)  // null code = tetap menggunakan nilai existing
            .clickSave();
    }

    /**
     * Update nama menggunakan By-based fill (mempertahankan code lama).
     */
    public JobTitlePage updateJobTitleNameBy(String code, String newName) {
        clickEditJobTitle(code);
        // Isi hanya field Name, biarkan Code dengan nilai yang sudah ada
        By locator = resolveFieldLocator(formInputName, editModalInputName);
        WebElement nameInput = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(locator));
        nameInput.clear();
        nameInput.sendKeys(newName);
        sleep(300);
        return clickSave();
    }

    // ==================== Notifikasi / Toast ====================

    /**
     * Ambil teks notifikasi sukses (Vue Notification).
     * Tunggu hingga notifikasi muncul (max 10 detik).
     *
     * @return Teks notifikasi, atau string kosong jika tidak muncul
     */
    public String getSuccessNotificationMessage() {
        try {
            WebElement notif = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(successNotification));
            String message = notif.getText().trim();
            System.out.println("  [INFO] Success notification: '" + message + "'");
            return message;
        } catch (Exception e) {
            System.out.println("  [WARN] Success notification tidak muncul dalam 10 detik.");
            return "";
        }
    }

    /**
     * Tunggu notifikasi sukses lalu verifikasi teksnya mengandung keyword tertentu.
     */
    public JobTitlePage verifySuccessNotificationContains(String keyword) {
        String message = getSuccessNotificationMessage();
        assertCondition(
            "Notifikasi sukses mengandung '" + keyword + "'",
            message.toLowerCase().contains(keyword.toLowerCase())
        );
        return this;
    }

    // ==================== Private Helpers ====================

    /**
     * Set a Vuetify input value using JavaScript with Vue-compatible events.
     * <p>
     * Vuetify v-model relies on InputEvent with insertText to update its internal state.
     * Standard Selenium sendKeys() does not always trigger this correctly.
     * This method sets the value via native JS and dispatches the proper events
     * so that Vue binds the value before Save.
     */
    private void clearAndTypeJS(By locator, String text) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "var text = arguments[1];" +
                "el.focus();" +
                "el.value = '';" +
                "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));" +
                "for (var i = 0; i < text.length; i++) {" +
                "  var ch = text.charAt(i);" +
                "  el.value += ch;" +
                "  el.dispatchEvent(new InputEvent('input', { data: ch, inputType: 'insertText', bubbles: true }));" +
                "}" +
                "el.dispatchEvent(new Event('change', { bubbles: true }));",
                el, text
            );
        } catch (Exception e) {
            System.out.println("  [WARN] clearAndTypeJS failed for " + locator + ": "
                + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handler untuk redirect setelah Save.
     * Jika browser terjebak di Keycloak OAuth callback, navigasi langsung ke list page.
     */
    private void handlePostSaveRedirect() throws InterruptedException {
        String currentUrl = driver.getCurrentUrl();

        if (currentUrl.contains("code=") && currentUrl.contains("session_state=")) {
            System.out.println("  [WARN] Keycloak callback detected after Save. Waiting for SPA to process token...");
            Thread.sleep(5000);

            currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("code=")) {
                String listUrl = "https://web.vedata.id/hcm/setting/job-title";
                System.out.println("  [INFO] Navigating directly to list: " + listUrl);
                driver.navigate().to(listUrl);
                Thread.sleep(3000);
            }
        } else {
            System.out.println("  [WARN] Redirect not detected after 30s, refreshing...");
            driver.navigate().refresh();
            Thread.sleep(2000);
        }
    }
}
