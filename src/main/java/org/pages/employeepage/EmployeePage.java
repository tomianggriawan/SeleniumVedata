package org.pages.employeepage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * EmployeePage - Page Object for HCM > Employee > Profile.
 * Supports CRU operations: Create, Read (verify), Update.
 *
 * Locator strategy mengikuti pola JobTitlePage:
 * - ID-based untuk form field utama (emp-code, emp-first-name, dst.)
 * - XPath label-based untuk dropdown Vuetify (v-combobox)
 * - JS dispatch untuk interaksi yang tidak ter-trigger oleh native Selenium
 */
public class EmployeePage extends BasePage {

    // ==================== Locators — List Page ====================

    private final By pageTitle        = By.xpath("//h1[contains(@class,'page-title')]");
    private final By addButton        = By.xpath("//button[contains(@class,'bg-primary') and contains(.,'Add')]");
    private final By tableRows        = By.xpath("//tbody//tr");
    private final By successNotif     = By.cssSelector("div.vue-notification.success");

    // ==================== Locators — Add/Edit Form (halaman /form) ====================

    // Section: Employment Information
    private final By inputEmpCode         = By.id("emp-code");
    private final By inputContractType    = By.id("emp-contract-type");
    private final By inputStatus          = By.id("emp-status");
    
    // Dates in Employment Info (Hire Date, Contract Start/End, Probation Start/End)
    private final String labelHireDate       = "emp-hire-date";
    private final String labelContractStart  = "emp-contract-start";
    private final String labelContractEnd    = "emp-contract-end";
    private final String labelProbationStart = "emp-probation-start";
    private final String labelProbationEnd   = "emp-probation-end";

    // Section: Personal Identity
    private final By inputIdentityNumber  = By.id("emp-identity");
    private final By inputFirstName       = By.id("emp-first-name");
    private final By inputLastName        = By.id("emp-last-name");
    private final By inputNickname        = By.id("emp-nickname");
    private final String labelDateOfBirth = "emp-date-of-birth";
    private final By inputPlaceOfBirth    = By.id("emp-place-of-birth");
    private final By inputGender          = By.id("emp-gender");
    private final By inputNationality     = By.id("emp-nationality");
    private final By inputMaritalStatus   = By.id("emp-marital-status");
    private final By inputPhoto           = By.id("emp-photo");

    // Section: Contact Information
    private final By inputPersonalPhone   = By.id("emp-personal-phone");
    private final By inputPersonalEmail   = By.id("emp-personal-email");
    private final By inputCorporateEmail  = By.id("emp-corporate-email");
    private final By inputResidentialAddress = By.id("emp-residential-address");
    private final By inputEmergencyPhone  = By.id("emp-emergency-phone");
    private final By inputEmergencyRelationship = By.id("emp-emergency-relationship");
    private final By inputIdCardAddress   = By.id("emp-id-card-address");

    // Section: Education & Skill
    private final By inputLastDegree      = By.id("emp-last-degree");
    private final By inputMajor           = By.id("emp-major");
    private final By inputGraduationYear  = By.id("emp-graduation-year");

    // Section: Payroll
    private final By inputTaxNumber       = By.id("emp-tax-number");
    private final String labelBasicSalary = "emp-basic-salary";
    private final By inputBank            = By.id("emp-bank");
    private final By inputPayrollName     = By.id("emp-payroll-name");
    private final By inputPayrollNumber   = By.id("emp-payroll-number");
    private final By inputBankAddress     = By.id("bank-bank-information");

    // Section: Position & Placement
    private final By inputJobTitle        = By.id("emp-job-title");
    private final By inputDepartment      = By.id("emp-departments");
    private final By inputBranch          = By.id("emp-branches");

    // Form action buttons
    private final By saveButton    = By.xpath("//button[contains(@class,'bg-primary') and contains(.,'Save')]");
    private final By cancelButton  = By.xpath("//button[contains(.,'Cancel')]");
    private final By backButton    = By.xpath("//button[contains(.,'Back')]");

    // Inline validation message elements
    private final By validationMessages = By.cssSelector(".v-input .v-messages__message, .v-messages__message");

    // ==================== Constructor ====================

    public EmployeePage(WebDriver driver) {
        super(driver);
    }

    // ==================== List Page Methods ====================

    /**
     * Verifikasi halaman Employee Profile List sudah tampil.
     */
    public EmployeePage verifyPageLoaded() {
        assertCondition("Halaman 'Employee Profile List' tampil", isPresent(addButton, 15));
        return this;
    }

    /**
     * Klik tombol Add untuk membuka form tambah karyawan baru.
     */
    public EmployeePage clickAddButton() {
        System.out.println("  [STEP] Klik tombol Add...");
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(addButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            // Tunggu navigasi ke halaman form
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(inputEmpCode));
            sleep(3000);
            System.out.println("  [INFO] Form tambah karyawan terbuka. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  [WARN] clickAddButton: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        return this;
    }

    /**
     * Periksa apakah karyawan dengan kode tertentu ada di tabel list.
     */
    public boolean isEmployeeExistInTable(String empCode) {
        // Wait until table is NOT showing "Loading" or "Memuat"
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> {
                    try {
                        List<WebElement> rows = d.findElements(By.xpath("//tbody//tr"));
                        for (WebElement row : rows) {
                            String txt = row.getText().trim();
                            if (txt.contains("Loading") || txt.contains("Memuat") || txt.contains("Tunggu")) {
                                return false; // Still loading
                            }
                        }
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                });
        } catch (Exception e) {
            System.out.println("  [WARN] Timeout waiting for table loading state to end: " + e.getMessage());
        }

        By rowLocator = By.xpath("//tbody//tr[td[contains(.,'" + empCode + "')]]");
        return isPresent(rowLocator, 10);
    }

    /**
     * Ambil data baris karyawan berdasarkan kode.
     *
     * @param empCode Kode karyawan
     * @return Array [action, code, name, ...] atau null jika tidak ditemukan
     */
    public String[] getEmployeeRowData(String empCode) {
        try {
            By rowCells = By.xpath("//tbody//tr[td[contains(.,'" + empCode + "')]]/td");
            List<WebElement> cells = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(rowCells));
            if (cells.size() >= 2) {
                return cells.stream()
                    .map(c -> c.getText().trim())
                    .toArray(String[]::new);
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Row data untuk emp code '" + empCode + "' tidak ditemukan.");
        }
        return null;
    }

    // ==================== Form Fill Methods ====================

    /**
     * Isi field Employment Information: Employee Code, Contract Type, Status.
     * Backward-compatible wrapper.
     */
    public EmployeePage fillEmploymentInfo(String empCode, String contractType, String status) {
        return fillEmploymentInfo(empCode, contractType, status, null, null, null, null, null);
    }

    /**
     * Isi field Employment Information lengkap, termasuk kolom tanggal.
     */
    public EmployeePage fillEmploymentInfo(String empCode, String contractType, String status,
                                          String hireDate, String contractStart, String contractEnd,
                                          String probationStart, String probationEnd) {
        if (empCode != null)       fillFieldById(inputEmpCode, empCode);
        if (contractType != null)  fillAutocompleteWithValidation(inputContractType, contractType, "Contract Type");
        if (status != null)        fillAutocompleteWithValidation(inputStatus, status, "Status");
        if (hireDate != null)      fillDateField(labelHireDate, hireDate);
        if (contractStart != null) fillDateField(labelContractStart, contractStart);
        if (contractEnd != null)   fillDateField(labelContractEnd, contractEnd);
        if (probationStart != null) fillDateField(labelProbationStart, probationStart);
        if (probationEnd != null)   fillDateField(labelProbationEnd, probationEnd);
        return this;
    }

    /**
     * Isi field Personal Identity.
     * Backward-compatible wrapper.
     */
    public EmployeePage fillPersonalIdentity(
            String identityNumber, String firstName, String lastName,
            String nickname, String placeOfBirth,
            String gender, String nationality, String maritalStatus) {
        return fillPersonalIdentity(identityNumber, firstName, lastName, nickname, null, placeOfBirth, gender, nationality, maritalStatus, null);
    }

    /**
     * Isi field Personal Identity Lengkap termasuk Date of Birth dan Upload Photo.
     */
    public EmployeePage fillPersonalIdentity(
            String identityNumber, String firstName, String lastName,
            String nickname, String dateOfBirth, String placeOfBirth,
            String gender, String nationality, String maritalStatus,
            String photoPath) {
        if (identityNumber != null) fillFieldById(inputIdentityNumber, identityNumber);
        if (firstName != null)      fillFieldById(inputFirstName, firstName);
        if (lastName != null)       fillFieldById(inputLastName, lastName);
        if (nickname != null)       fillFieldById(inputNickname, nickname);
        if (dateOfBirth != null)    fillDateField(labelDateOfBirth, dateOfBirth);
        if (placeOfBirth != null)   fillFieldById(inputPlaceOfBirth, placeOfBirth);
        if (gender != null)         fillAutocompleteWithValidation(inputGender, gender, "Gender");
        if (nationality != null)    fillAutocompleteWithValidation(inputNationality, nationality, "Nationality");
        if (maritalStatus != null)  fillAutocompleteWithValidation(inputMaritalStatus, maritalStatus, "Marital Status");
        if (photoPath != null)      uploadPhoto(photoPath);
        return this;
    }

    /**
     * Isi field Contact Information.
     * Backward-compatible wrapper.
     */
    public EmployeePage fillContactInfo(String personalPhone, String personalEmail, String corporateEmail) {
        return fillContactInfo(personalPhone, personalEmail, corporateEmail, null, null, null, null);
    }

    /**
     * Isi field Contact Information Lengkap.
     */
    public EmployeePage fillContactInfo(String personalPhone, String personalEmail, String corporateEmail,
                                        String residentialAddress, String emergencyPhone, String emergencyRelationship,
                                        String idCardAddress) {
        if (personalPhone != null)  fillFieldById(inputPersonalPhone, personalPhone);
        if (personalEmail != null)  fillFieldById(inputPersonalEmail, personalEmail);
        if (corporateEmail != null) fillFieldById(inputCorporateEmail, corporateEmail);
        if (residentialAddress != null) fillFieldById(inputResidentialAddress, residentialAddress);
        if (emergencyPhone != null)  fillFieldById(inputEmergencyPhone, emergencyPhone);
        if (emergencyRelationship != null) fillFieldById(inputEmergencyRelationship, emergencyRelationship);
        if (idCardAddress != null)   fillFieldById(inputIdCardAddress, idCardAddress);
        return this;
    }

    /**
     * Isi field Education & Skill.
     */
    public EmployeePage fillEducationInfo(String lastDegree, String major, String graduationYear) {
        if (lastDegree != null)     fillAutocompleteWithValidation(inputLastDegree, lastDegree, "Last Degree");
        if (major != null)          fillFieldById(inputMajor, major);
        if (graduationYear != null) fillFieldById(inputGraduationYear, graduationYear);
        return this;
    }

    /**
     * Isi field Payroll Information Lengkap.
     */
    public EmployeePage fillPayrollInfo(String taxNumber, String basicSalary, String bank,
                                        String accountName, String accountNumber, String bankAddress) {
        if (taxNumber != null)     fillFieldById(inputTaxNumber, taxNumber);
        if (basicSalary != null)   fillNumberField(labelBasicSalary, basicSalary);
        if (bank != null)          fillAutocompleteWithValidation(inputBank, bank, "Bank");
        if (accountName != null)   fillFieldById(inputPayrollName, accountName);
        if (accountNumber != null) fillFieldById(inputPayrollNumber, accountNumber);
        if (bankAddress != null)   fillFieldById(inputBankAddress, bankAddress);
        return this;
    }

    /**
     * Isi field Position & Placement.
     * Backward-compatible wrapper.
     */
    public EmployeePage fillPositionInfo(String jobTitle, String department) {
        return fillPositionInfo(jobTitle, department, null);
    }

    /**
     * Isi field Position & Placement lengkap dengan Branch.
     */
    public EmployeePage fillPositionInfo(String jobTitle, String department, String branch) {
        if (jobTitle != null)    fillAutocompleteWithValidation(inputJobTitle, jobTitle, "Job Title");
        if (department != null)  fillAutocompleteWithValidation(inputDepartment, department, "Department");
        if (branch != null)      fillAutocompleteWithValidation(inputBranch, branch, "Branch");
        return this;
    }

    // ==================== Save / Navigate ====================

    /**
     */
    public EmployeePage clickSave() {
        System.out.println("  [STEP] Klik tombol Save...");
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(saveButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        sleep(500);

        logValidationErrors("BEFORE save");

        // ---- Attempt save — try multiple click strategies, retry on date errors ----
        int maxAttempts = 3;
        boolean redirected = false;

        for (int attempt = 1; attempt <= maxAttempts && !redirected; attempt++) {
            System.out.println("  [DEBUG] Save attempt " + attempt + "/" + maxAttempts + "...");

            // Re-fetch & scroll button
            try {
                btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(saveButton));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
                sleep(300);
            } catch (Exception ignored) {}

            // Log button state
            try {
                String disabled = btn.getAttribute("disabled");
                String classes  = btn.getAttribute("class");
                System.out.println("  [DEBUG] Save button: disabled='" + disabled + "' class='" + classes + "'");
            } catch (Exception ignored) {}

            // Click strategy A: Actions (most realistic)
            try {
                new Actions(driver).moveToElement(btn).pause(java.time.Duration.ofMillis(300)).click().perform();
                System.out.println("  [DEBUG] Save clicked (Actions).");
            } catch (Exception e) {
                System.out.println("  [WARN] Actions click failed: " + e.getClass().getSimpleName());
            }

            sleep(800);
            if (!driver.getCurrentUrl().contains("/form")) { redirected = true; break; }

            // Click strategy B: JS pointer events
            try {
                ((JavascriptExecutor) driver).executeScript(
                    "var el=arguments[0];" +
                    "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(function(t){" +
                    "  el.dispatchEvent(new MouseEvent(t,{bubbles:true,cancelable:true,view:window}));" +
                    "});", btn);
                System.out.println("  [DEBUG] Save clicked (JS pointer events).");
            } catch (Exception ignored) {}

            sleep(800);
            if (!driver.getCurrentUrl().contains("/form")) { redirected = true; break; }

            // Click strategy C: plain JS click
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                System.out.println("  [DEBUG] Save clicked (plain JS click).");
            } catch (Exception ignored) {}

            sleep(1500);
            if (!driver.getCurrentUrl().contains("/form")) { redirected = true; break; }

            // Still on form — inspect errors
            List<WebElement> errMsgs = getVisibleValidationMessages();
            String toastError = getErrorToastText();

            if (!toastError.isEmpty()) {
                System.out.println("  [WARN] Server/toast error after save attempt " + attempt + ": " + toastError);
                if (toastError.toLowerCase().contains("terjadi kesalahan") ||
                    toastError.toLowerCase().contains("error") ||
                    toastError.toLowerCase().contains("salah") ||
                    toastError.toLowerCase().contains("failed")) {
                    throw new IllegalStateException("Server error detected: " + toastError);
                }
            }

            if (!errMsgs.isEmpty()) {
                System.out.println("  [DEBUG] Inline validation errors after attempt " + attempt + ":");
                boolean hasDateError = false;
                for (WebElement err : errMsgs) {
                    String txt = err.getText().trim();
                    if (!txt.isEmpty()) {
                        System.out.println("    - " + txt);
                        if (txt.toLowerCase().contains("date") || txt.toLowerCase().contains("tanggal")) {
                            hasDateError = true;
                        }
                    }
                }
                if (hasDateError && attempt < maxAttempts) {
                    System.out.println("  [DEBUG] Date error detected — retrying date fill...");
                    retryFillDateFieldsWithActions();
                    sleep(500);
                }
            } else {
                System.out.println("  [DEBUG] No inline validation errors after attempt " + attempt + ".");
            }
        }

        // Wait for redirect (extended timeout)
        if (!redirected) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.not(ExpectedConditions.urlContains("/form")));
                System.out.println("  [DEBUG] Redirected. URL: " + driver.getCurrentUrl());
                redirected = true;
            } catch (Exception waitEx) {
                System.out.println("  [WARN] Timeout waiting for redirect. URL: " + driver.getCurrentUrl());
                logValidationErrors("POST-TIMEOUT");
                String toast = getErrorToastText();
                if (!toast.isEmpty()) {
                    System.out.println("  [WARN] Toast after timeout: " + toast);
                    throw new IllegalStateException("Timeout waiting for redirect. Server/toast error: " + toast);
                }
                throw new IllegalStateException("Timeout waiting for redirect from form page.");
            }
        } else {
            System.out.println("  [DEBUG] URL after save: " + driver.getCurrentUrl());
        }

        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(ExpectedConditions.presenceOfElementLocated(addButton));
        return this;
    }


    /**
     * Retry filling all date fields using native Actions keyboard input.
     * Called when save fails due to 'Date is required' validation.
     */
    private void retryFillDateFieldsWithActions() {
        // We don't have the date values here directly, so we refill visible empty date inputs
        try {
            List<WebElement> dateInputs = driver.findElements(
                By.xpath("//label[contains(@for,'date') or contains(@for,'Date')]/following::input[@type='date' or @type='text' or @type='tel'][1]")
            );
            for (WebElement inp : dateInputs) {
                try {
                    String val = inp.getAttribute("value");
                    if (val == null || val.trim().isEmpty()) {
                        System.out.println("  [DEBUG] Date input (id='" + inp.getAttribute("id") + "') still empty, skipping retry for this input.");
                        continue;
                    }
                    // Re-send existing value via keyboard to trigger change event
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", inp);
                    sleep(100);
                    String existing = val;
                    // Click, select-all, retype, tab
                    new Actions(driver).click(inp).keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
                    sleep(50);
                    inp.sendKeys(existing);
                    sleep(100);
                    inp.sendKeys(Keys.TAB);
                    sleep(200);
                    System.out.println("  [DEBUG] Retried date field id='" + inp.getAttribute("id") + "' value='" + existing + "'");
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.out.println("  [WARN] retryFillDateFieldsWithActions: " + e.getMessage());
        }
    }

    /**
     * Log visible validation error messages.
     */
    private void logValidationErrors(String context) {
        try {
            List<WebElement> errMsgs = driver.findElements(validationMessages);
            List<WebElement> visible = new java.util.ArrayList<>();
            for (WebElement e : errMsgs) {
                try { if (e.isDisplayed() && !e.getText().trim().isEmpty()) visible.add(e); } catch (Exception ignored) {}
            }
            if (!visible.isEmpty()) {
                System.out.println("  [DEBUG] Validation errors " + context + " (" + visible.size() + "):");
                for (WebElement e : visible) {
                    String txt = e.getText().trim();
                    String fieldInfo = "";
                    try {
                        WebElement vInput = e.findElement(By.xpath("./ancestor::div[contains(@class,'v-input')][1]"));
                        try {
                            WebElement lbl = vInput.findElement(By.cssSelector("label"));
                            fieldInfo = " [Label: " + lbl.getText().trim() + "]";
                        } catch (Exception noLbl) {
                            try {
                                WebElement inp = vInput.findElement(By.cssSelector("input"));
                                fieldInfo = " [id='" + inp.getAttribute("id") + "' placeholder='" + inp.getAttribute("placeholder") + "']";
                            } catch (Exception noInp) {}
                        }
                    } catch (Exception ignored) {}
                    System.out.println("    - '" + txt + "'" + fieldInfo);
                }
            } else {
                System.out.println("  [DEBUG] No validation errors visible (" + context + ").");
            }
        } catch (Exception ignored) {}
    }

    /**
     * Return currently visible, non-empty validation messages.
     */
    private List<WebElement> getVisibleValidationMessages() {
        List<WebElement> result = new java.util.ArrayList<>();
        try {
            for (WebElement e : driver.findElements(validationMessages)) {
                try { if (e.isDisplayed() && !e.getText().trim().isEmpty()) result.add(e); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return result;
    }

    /**
     * Read any visible error toast / notification text (server-side errors shown as banners).
     * Checks: .vue-notification.error, .v-snackbar, .alert-error, common toast patterns.
     */
    private String getErrorToastText() {
        try {
            String[] selectors = {
                ".vue-notification.error",
                ".vue-notification.warn",
                ".v-snackbar__content",
                ".v-alert--type-error",
                "[class*='notification'][class*='error']",
                "[class*='toast'][class*='error']",
                "[role='alert']"
            };
            StringBuilder sb = new StringBuilder();
            for (String sel : selectors) {
                try {
                    List<WebElement> els = driver.findElements(By.cssSelector(sel));
                    for (WebElement el : els) {
                        if (el.isDisplayed()) {
                            String txt = el.getText().trim();
                            if (!txt.isEmpty()) sb.append("[").append(sel).append("] ").append(txt).append(" | ");
                        }
                    }
                } catch (Exception ignored) {}
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * Klik tombol Save namun toleran jika terjadi error/tidak redirect (untuk skenario gagal).
     */
    public EmployeePage clickSaveExpectingFailure() {
        System.out.println("  [STEP] Klik tombol Save (mengharapkan kegagalan simpan)...");
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            sleep(300);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            sleep(1000); // Tunggu proses validasi front-end
        } catch (Exception e) {
            System.out.println("  [WARN] clickSaveExpectingFailure: " + e.getMessage());
        }
        return this;
    }

    /**
     * Klik tombol Edit untuk karyawan dengan kode tertentu.
     * Navigasi ke halaman /form?id=xxx
     */
    public EmployeePage clickEditEmployee(String empCode) {
        System.out.println("  [STEP] Klik Edit untuk karyawan '" + empCode + "'...");

        // Scroll baris ke tengah layar
        By rowLocator = By.xpath("//tbody//tr[td[contains(.,'" + empCode + "')]]");
        try {
            WebElement row = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(rowLocator));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center',inline:'nearest'});", row);
            sleep(300);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal scroll baris: " + e.getClass().getSimpleName());
        }

        // Dismiss notifikasi sukses jika ada
        dismissSuccessNotifIfPresent();

        // Cari dan klik tombol Edit/three-dots pada baris yang sesuai
        By editBtnPrimary = By.xpath(
            "//tbody//tr[td[contains(.,'" + empCode + "')]]//button" +
            "[@data-testid='edit-btn' or contains(@aria-label,'Edit')" +
            " or contains(.,'Edit') or contains(.,'edit')]"
        );
        By editBtnFallback = By.xpath(
            "//tbody//tr[td[contains(.,'" + empCode + "')]]//button[1]"
        );

        WebElement btn = null;
        try {
            btn = new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(editBtnPrimary));
        } catch (Exception e) {
            System.out.println("  [WARN] Primary edit btn tidak ditemukan, coba fallback...");
            try {
                btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(editBtnFallback));
            } catch (Exception e2) {
                System.out.println("  [WARN] Fallback edit btn juga tidak ditemukan: " + e2.getMessage());
            }
        }

        if (btn != null) {
            String menuId = null;
            try { menuId = btn.getAttribute("aria-controls"); } catch (Exception ignored) {}

            // Coba Actions hover + click (reliable untuk Vuetify v-menu)
            boolean menuOpened = false;
            try {
                new Actions(driver).moveToElement(btn).pause(Duration.ofMillis(500)).click().perform();
                new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"));
                menuOpened = true;
                System.out.println("  [DEBUG] Dropdown menu terbuka (Actions+pause).");
            } catch (Exception e) {
                System.out.println("  [WARN] Actions+pause tidak buka menu. Coba JS dispatch...");
            }

            // Fallback: JS event dispatch
            if (!menuOpened) {
                try {
                    final WebElement btnRef = btn;
                    ((JavascriptExecutor) driver).executeScript(
                        "var el=arguments[0];" +
                        "el.dispatchEvent(new MouseEvent('mouseenter',{bubbles:true}));" +
                        "el.dispatchEvent(new MouseEvent('mouseover',{bubbles:true}));" +
                        "el.dispatchEvent(new MouseEvent('mousedown',{bubbles:true,cancelable:true,view:window}));" +
                        "el.dispatchEvent(new MouseEvent('mouseup',{bubbles:true,cancelable:true,view:window}));" +
                        "el.dispatchEvent(new MouseEvent('click',{bubbles:true,cancelable:true,view:window}));",
                        btnRef
                    );
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.attributeToBe(btn, "aria-expanded", "true"));
                    menuOpened = true;
                    System.out.println("  [DEBUG] Dropdown menu terbuka (JS dispatch).");
                } catch (Exception e) {
                    System.out.println("  [WARN] JS dispatch juga gagal buka menu: " + e.getMessage());
                }
            }

            sleep(300);

            if (menuOpened) {
                // Klik item Edit di dalam dropdown
                clickMenuEditOption(menuId);
            }
        }

        // Tunggu navigasi ke halaman /form untuk edit
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.urlContains("/form"));
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(inputEmpCode));
            sleep(3000);
            System.out.println("  [INFO] Halaman edit form terbuka. URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  [WARN] Edit form tidak terbuka dalam waktu: " + e.getMessage());
        }

        return this;
    }

    // ==================== Validation Error Capture ====================

    /**
     * Memeriksa apakah ada pesan error validasi yang tampil pada halaman form.
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        try {
            List<WebElement> els = driver.findElements(validationMessages);
            for (WebElement el : els) {
                if (el.isDisplayed() && !el.getText().trim().isEmpty()) {
                    errors.add(el.getText().trim());
                }
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal membaca pesan validasi: " + e.getMessage());
        }
        return errors;
    }

    // ==================== Private Helper Methods ====================

    /**
     * Mengisi regular text input field menggunakan JavaScript + Vue event dispatch.
     * Memastikan v-model terbind sebelum Save.
     */
    private void fillFieldById(By locator, String value) {
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
            sleep(150);
            clearAndTypeJS(locator, value);
            System.out.println("  [INFO] Field [" + locator + "] diisi: '" + value + "'");
        } catch (Exception e) {
            System.out.println("  [WARN] fillFieldById [" + locator + "]: "
                + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
    private void fillDateField(String labelFor, String dateValue) {
        try {
            By locator = By.xpath(
                "//label[@for='" + labelFor + "']/following::input[1]"
            );
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
            sleep(300);

            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOf(el));

            String inputType = el.getAttribute("type");
            System.out.println("  [DEBUG] Date field [" + labelFor + "] type='" + inputType + "'");

            // ========================
            // Strategy 1: Calendar UI interaction (MOST RELIABLE in production Vue 3)
            // __vueParentComponent is unavailable in production builds.
            // Clicking the datepicker calendar directly triggers Vue's own internal
            // event chain, updating the reactive model correctly.
            // ========================
            boolean filled = false;
            try {
                System.out.println("  [DEBUG] Trying calendar UI for [" + labelFor + "]");
                filled = fillDateByCalendarUI(el, labelFor, dateValue);
            } catch (Exception calEx) {
                System.out.println("  [WARN] Calendar UI failed [" + labelFor + "]: " +
                    (calEx.getMessage() != null ? calEx.getMessage().split("\n")[0] : "null"));
            }

            // ========================
            // Strategy 2: Remove readonly + chained Actions (keyboard input)
            // ========================
            if (!filled) {
                String digitsOnly = dateValue.replaceAll("[^0-9]", "");
                try {
                    ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].removeAttribute('readonly'); arguments[0].removeAttribute('disabled');", el);
                    sleep(100);
                    Actions actions = new Actions(driver);
                    actions.click(el).pause(java.time.Duration.ofMillis(200));
                    actions.sendKeys(Keys.HOME);
                    for (char c : digitsOnly.toCharArray()) {
                        actions.sendKeys(String.valueOf(c));
                    }
                    actions.sendKeys(Keys.TAB);
                    actions.perform();
                    sleep(400);
                    String afterVal = el.getAttribute("value");
                    System.out.println("  [INFO] Date [for=" + labelFor + "] via Actions '" + digitsOnly + "' → actual='" + afterVal + "'");
                    if (afterVal != null && !afterVal.isEmpty()) filled = true;
                } catch (Exception actEx) {
                    System.out.println("  [WARN] Actions fill failed [" + labelFor + "]: " + actEx.getMessage());
                }
            }

            // ========================
            // Strategy 3: Native value setter (DOM fallback — display only)
            // ========================
            if (!filled) {
                System.out.println("  [WARN] Falling back to native setter for [" + labelFor + "]");
                try {
                    ((JavascriptExecutor) driver).executeScript(
                        "var el = arguments[0]; var val = arguments[1];" +
                        "var ns = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "ns.call(el, val);" +
                        "el.dispatchEvent(new Event('input',  {bubbles:true, cancelable:true}));" +
                        "el.dispatchEvent(new Event('change', {bubbles:true, cancelable:true}));" +
                        "el.dispatchEvent(new Event('blur',   {bubbles:true, cancelable:true}));",
                        el, dateValue
                    );
                    sleep(200);
                    String afterVal = el.getAttribute("value");
                    System.out.println("  [INFO] Date [for=" + labelFor + "] via native setter '" + dateValue + "' → actual='" + afterVal + "'");
                } catch (Exception ignored) {}
            }

            String finalVal = el.getAttribute("value");
            System.out.println("  [INFO] Date [for=" + labelFor + "] FINAL DOM value: '" + finalVal + "'");

        } catch (Exception e) {
            System.out.println("  [WARN] fillDateField [for=" + labelFor + "]: " + e.getMessage());
        }
    }

    /**
     * Fill date field by interacting with the vue-datepicker calendar UI.
     * Strategy: click input → calendar opens → click year (overlay) → click month → click day.
     * This triggers Vue's own internal event chain (no Vue internals access needed).
     * @return true if date was successfully selected via calendar
     */
    private boolean fillDateByCalendarUI(WebElement input, String labelFor, String dateValue) {
        String[] parts = dateValue.split("[-/]");
        if (parts.length != 3) return false;

        int targetDay   = Integer.parseInt(parts[0].trim());
        int targetMonth = Integer.parseInt(parts[1].trim());
        int targetYear  = Integer.parseInt(parts[2].trim());

        System.out.println("  [DEBUG] Calendar target: day=" + targetDay + " month=" + targetMonth + " year=" + targetYear);

        // Step A: Open calendar by clicking input
        try {
            new Actions(driver).click(input).perform();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
        }
        sleep(800);

        // Also try clicking the dp__ wrapper if calendar didn't open
        WebElement calendar = null;
        try {
            calendar = new WebDriverWait(driver, Duration.ofSeconds(4))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dp__menu")));
        } catch (Exception e) {
            try {
                WebElement dpWrapper = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "var el = arguments[0]; var p = el.parentElement;" +
                    "while (p && p.className && !p.className.includes('dp__')) p = p.parentElement;" +
                    "return p || el.parentElement;", input);
                if (dpWrapper != null) {
                    ((JavascriptExecutor) driver).executeScript(
                        "var el=arguments[0];" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(function(t){" +
                        "  el.dispatchEvent(new MouseEvent(t,{bubbles:true,cancelable:true,view:window}));" +
                        "});", dpWrapper);
                    sleep(600);
                }
            } catch (Exception ignored) {}
            try {
                calendar = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dp__menu")));
            } catch (Exception e2) {
                System.out.println("  [WARN] Calendar menu not opened for [" + labelFor + "]");
                return false;
            }
        }

        System.out.println("  [DEBUG] Calendar menu opened for [" + labelFor + "]");

        // Step B: Navigate to target year via the year overlay
        List<WebElement> monthYearBtns = driver.findElements(
            By.cssSelector(".dp__menu .dp__month_year_select"));
        WebElement yearBtn = null;
        for (WebElement btn : monthYearBtns) {
            try {
                if (btn.getText().trim().matches("\\d{4}")) { yearBtn = btn; break; }
            } catch (Exception ignored) {}
        }

        boolean yearSelected = false;
        if (yearBtn != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yearBtn);
            sleep(500);

            for (int attempt = 0; attempt < 30 && !yearSelected; attempt++) {
                List<WebElement> yearCells = driver.findElements(
                    By.cssSelector(".dp__overlay_cell, .dp__year_select"));

                for (WebElement cell : yearCells) {
                    try {
                        if (cell.getText().trim().equals(String.valueOf(targetYear))) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cell);
                            sleep(400);
                            yearSelected = true;
                            System.out.println("  [DEBUG] Year " + targetYear + " selected");
                            break;
                        }
                    } catch (Exception ignored) {}
                }

                if (!yearSelected && !yearCells.isEmpty()) {
                    int firstYear = 9999, lastYear = 0;
                    for (WebElement c : yearCells) {
                        try {
                            int y = Integer.parseInt(c.getText().trim());
                            if (y < firstYear) firstYear = y;
                            if (y > lastYear)  lastYear  = y;
                        } catch (Exception ignored) {}
                    }
                    List<WebElement> navBtns = driver.findElements(By.cssSelector(".dp__menu .dp__inner_nav"));
                    if (navBtns.isEmpty()) break;
                    if (targetYear < firstYear) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navBtns.get(0));
                    } else if (targetYear > lastYear) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navBtns.get(navBtns.size()-1));
                    } else {
                        break;
                    }
                    sleep(300);
                }
            }
        }

        // Step C: Select target month (from overlay if shown, else navigate month-by-month)
        boolean monthSelected = false;
        String[] monthEn = {"January","February","March","April","May","June",
                             "July","August","September","October","November","December"};
        String[] monthShort = {"Jan","Feb","Mar","Apr","May","Jun",
                                "Jul","Aug","Sep","Oct","Nov","Dec"};
        String[] monthId = {"Januari","Februari","Maret","April","Mei","Juni",
                             "Juli","Agustus","September","Oktober","November","Desember"};

        List<WebElement> monthOverlay = driver.findElements(By.cssSelector(".dp__overlay_cell"));
        if (!monthOverlay.isEmpty()) {
            for (WebElement cell : monthOverlay) {
                try {
                    String t = cell.getText().trim();
                    int mi = targetMonth - 1;
                    if (t.equalsIgnoreCase(monthEn[mi]) || t.equalsIgnoreCase(monthShort[mi]) ||
                        t.equalsIgnoreCase(monthId[mi])) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cell);
                        sleep(400);
                        monthSelected = true;
                        System.out.println("  [DEBUG] Month " + monthShort[mi] + " selected via overlay");
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        if (!monthSelected) {
            // Navigate month-by-month (max 60 steps = 5 years)
            for (int nav = 0; nav < 60; nav++) {
                List<WebElement> myBtns = driver.findElements(By.cssSelector(".dp__menu .dp__month_year_select"));
                if (myBtns.size() < 2) break;
                try {
                    int dispMonth = parseMonthFromText(myBtns.get(0).getText().trim());
                    int dispYear  = Integer.parseInt(myBtns.get(1).getText().trim());
                    if (dispYear == targetYear && dispMonth == targetMonth) { monthSelected = true; break; }
                    boolean fwd = dispYear < targetYear || (dispYear == targetYear && dispMonth < targetMonth);
                    List<WebElement> navBtns = driver.findElements(By.cssSelector(".dp__menu .dp__inner_nav"));
                    if (navBtns.isEmpty()) break;
                    WebElement navBtn = fwd ? navBtns.get(navBtns.size()-1) : navBtns.get(0);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navBtn);
                    sleep(200);
                } catch (Exception e) { break; }
            }
        }

        // Step D: Click target day cell
        sleep(300);
        List<WebElement> dayItems = driver.findElements(By.cssSelector(".dp__menu .dp__calendar_item"));
        for (WebElement item : dayItems) {
            try {
                String itemClass = item.getAttribute("class");
                if (itemClass != null && (itemClass.contains("dp__calendar_header") ||
                    itemClass.contains("dp__cell_offset"))) continue;
                WebElement inner = item.findElement(By.cssSelector(".dp__cell_inner"));
                String dayText = inner.getText().trim();
                if (dayText.equals(String.valueOf(targetDay))) {
                    String innerClass = inner.getAttribute("class");
                    if (innerClass != null && innerClass.contains("dp__cell_disabled")) continue;
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", inner);
                    sleep(600);
                    String afterVal = input.getAttribute("value");
                    System.out.println("  [INFO] Calendar: selected " + targetDay + "/" + targetMonth + "/" + targetYear +
                        " for [" + labelFor + "] → input='" + afterVal + "'");

                    // Force-set Vue datepicker modelValue via __vueParentComponent
                    // VueDatePicker stores the selected date in modelValue as a Date object or ISO string
                    // Without this, the Vue reactive model may remain null even if the input displays a value
                    try {
                        String isoDate = String.format("%04d-%02d-%02d", targetYear, targetMonth, targetDay);
                        ((JavascriptExecutor) driver).executeScript(
                            "var inp = arguments[0];" +
                            "var isoDate = arguments[1];" +
                            // Walk up to find VueDatePicker __vueParentComponent
                            "var comp = inp.__vueParentComponent;" +
                            "var limit = 0;" +
                            "while (comp && limit < 30) {" +
                            "  var name = (comp.type && (comp.type.name || comp.type.__name)) || '';" +
                            "  if (name.toLowerCase().includes('datepicker') || name.toLowerCase().includes('date-picker') || name.toLowerCase().includes('dp-')) {" +
                            "    if (comp.props && comp.props.modelValue !== undefined) {" +
                            "      var d = new Date(isoDate);" +
                            "      if (comp.emit) { comp.emit('update:modelValue', d); }" +
                            "    }" +
                            "    if (comp.setupState && comp.setupState.internalModelValue !== undefined) {" +
                            "      comp.setupState.internalModelValue.value = new Date(isoDate);" +
                            "    }" +
                            "    break;" +
                            "  }" +
                            "  comp = comp.parent; limit++;" +
                            "}" +
                            // Also dispatch change event on the input to ensure form binding picks it up
                            "inp.dispatchEvent(new Event('change', {bubbles: true, cancelable: true}));",
                            input, isoDate
                        );
                        System.out.println("  [DEBUG] Vue modelValue set for [" + labelFor + "] → " + isoDate);
                    } catch (Exception vueEx) {
                        System.out.println("  [WARN] Could not set Vue modelValue for [" + labelFor + "]: " + vueEx.getClass().getSimpleName());
                    }

                    return true;
                }
            } catch (Exception ignored) {}
        }

        System.out.println("  [WARN] Day " + targetDay + " not found in calendar for [" + labelFor + "]");
        // Close calendar
        try { new Actions(driver).sendKeys(Keys.ESCAPE).perform(); sleep(200); } catch (Exception ignored) {}
        return false;
    }


    /**
     * Parse month name (English or Indonesian) to 1-based month number.
     */
    private int parseMonthFromText(String monthText) {
        String t = monthText.toLowerCase().trim();
        String[][] names = {
            {"january","jan","januari"},
            {"february","feb","februari"},
            {"march","mar","maret"},
            {"april"},
            {"may","mei"},
            {"june","jun","juni"},
            {"july","jul","juli"},
            {"august","aug","agustus"},
            {"september","sep","sept"},
            {"october","oct","oktober","okt"},
            {"november","nov"},
            {"december","dec","desember"}
        };
        for (int i = 0; i < names.length; i++) {
            for (String n : names[i]) {
                if (t.equals(n) || (t.length() >= 3 && t.startsWith(n.substring(0, Math.min(3, n.length()))))) {
                    return i + 1;
                }
            }
        }
        try { return Integer.parseInt(t); } catch (Exception ignored) {}
        throw new IllegalArgumentException("Unknown month: " + monthText);
    }


    /**
     * Mengisi field bertipe numerik (seperti Basic Salary) menggunakan label-based XPath.
     */
    private void fillNumberField(String labelFor, String value) {
        try {
            By locator = By.xpath("//label[@for='" + labelFor + "']/following::input[1]");
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
            sleep(150);
            clearAndTypeJS(locator, value);
            System.out.println("  [INFO] Number field [for=" + labelFor + "] diisi: '" + value + "'");
        } catch (Exception e) {
            System.out.println("  [WARN] fillNumberField [for=" + labelFor + "]: " + e.getMessage());
        }
    }

    /**
     * Expose upload photo secara public untuk mendukung pengisian formulir sekuensial.
     */
    public EmployeePage uploadPhotoPublic(String filePath) {
        System.out.println("  [STEP] Unggah foto secara native: '" + filePath + "'");
        uploadPhoto(filePath);
        return this;
    }

    /**
     * Isi field Department (tag multiselect Vuetify v-select).
     * Mengklik kontainer dropdown, memilih opsi dari overlay, lalu memverifikasi
     * bahwa tag chip ter-render di dalam kontainer field.
     */
    public EmployeePage fillDepartment(String departmentName) {
        System.out.println("  [STEP] Pilih Department: '" + departmentName + "'");
        fillTagSelect(inputDepartment, departmentName, "Department");
        return this;
    }

    /**
     * Isi field Branch (tag multiselect Vuetify v-select).
     * Mengklik kontainer dropdown, memilih opsi dari overlay, lalu memverifikasi
     * bahwa tag chip ter-render di dalam kontainer field.
     */
    public EmployeePage fillBranch(String branchName) {
        System.out.println("  [STEP] Pilih Branch: '" + branchName + "'");
        fillTagSelect(inputBranch, branchName, "Branch");
        return this;
    }

    /**
     * Strategi pengisian khusus untuk komponen tag/chip multiselect Vuetify (v-select dengan inputmode=none).
     * Berdasarkan DOM inspection: field ini menggunakan div[role='combobox'] dengan hidden input —
     * typing tidak mungkin dilakukan. Kita harus klik kontainer untuk membuka overlay.
     *
     * Langkah:
     * 1. Scroll ke kontainer field, lalu klik untuk membuka overlay.
     * 2. Tunggu overlay dengan .v-list-item muncul.
     * 3. Temukan item yang cocok berdasarkan teks dan klik via JS.
     * 4. Tutup overlay (Escape).
     * 5. Verifikasi tag chip (.v-chip atau .v-select__selection) ter-render di dalam kontainer.
     */
    private void fillTagSelect(By inputLocator, String value, String fieldName) {
        try {
            // Step 1 — Temukan input dan naik ke kontainer .v-field
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(inputLocator));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", input);
            sleep(300);

            // Cari ancestor .v-field[role='combobox'] sebagai pemicu click
            WebElement vField = null;
            try {
                vField = input.findElement(
                    By.xpath("./ancestor::div[contains(@class,'v-field')][@role='combobox']"));
            } catch (Exception ignored) {}
            if (vField == null) {
                // Fallback via JS tree walk
                vField = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "var el = arguments[0].parentElement;" +
                    "while (el) {" +
                    "  if (el.getAttribute && el.getAttribute('role') === 'combobox') return el;" +
                    "  el = el.parentElement;" +
                    "}" +
                    "return null;", input);
            }
            if (vField == null) {
                throw new IllegalStateException(
                    "[" + fieldName + "] Tidak dapat menemukan div[role='combobox'] container.");
            }

            // Step 2 — Buka dropdown dengan beberapa strategi berurutan
            boolean opened = false;

            // Strategi A: Klik chevron (.v-field__append-inner) via JS
            try {
                WebElement chevron = vField.findElement(By.cssSelector(".v-field__append-inner"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chevron);
                sleep(400);
                opened = isOverlayWithItemsVisible();
                if (opened) System.out.println("  [DEBUG] [" + fieldName + "] dibuka via klik chevron (JS).");
            } catch (Exception ignored) {}

            // Strategi B: Actions klik pada chevron
            if (!opened) {
                try {
                    WebElement chevron = vField.findElement(By.cssSelector(".v-field__append-inner"));
                    new Actions(driver).moveToElement(chevron).click().perform();
                    sleep(400);
                    opened = isOverlayWithItemsVisible();
                    if (opened) System.out.println("  [DEBUG] [" + fieldName + "] dibuka via Actions klik chevron.");
                } catch (Exception ignored) {}
            }

            // Strategi C: Vue3 internal — set menu.value = true
            if (!opened) {
                try {
                    Boolean vueMod = (Boolean) ((JavascriptExecutor) driver).executeScript(
                        "var el = arguments[0];" +
                        "var comp = el.__vueParentComponent; var limit = 0;" +
                        "while (comp && limit < 25) {" +
                        "  var name = (comp.type && (comp.type.name || comp.type.__name)) || '';" +
                        "  if (name.toLowerCase().includes('select') || name.toLowerCase().includes('combobox')) {" +
                        "    if (comp.setupState && comp.setupState.menu !== undefined) {" +
                        "      comp.setupState.menu.value = true; return true;" +
                        "    }" +
                        "    if (comp.exposed && comp.exposed.menu !== undefined) {" +
                        "      comp.exposed.menu.value = true; return true;" +
                        "    }" +
                        "  }" +
                        "  comp = comp.parent; limit++;" +
                        "} return false;",
                        input);
                    if (Boolean.TRUE.equals(vueMod)) {
                        sleep(400);
                        opened = isOverlayWithItemsVisible();
                        if (opened) System.out.println("  [DEBUG] [" + fieldName + "] dibuka via Vue3 internal.");
                    }
                } catch (Exception ignored) {}
            }

            // Strategi D: JS pointer events pada vField
            if (!opened) {
                try {
                    ((JavascriptExecutor) driver).executeScript(
                        "var el = arguments[0];" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(function(t){" +
                        "  el.dispatchEvent(new MouseEvent(t,{bubbles:true,cancelable:true,view:window}));" +
                        "});", vField);
                    sleep(400);
                    opened = isOverlayWithItemsVisible();
                    if (opened) System.out.println("  [DEBUG] [" + fieldName + "] dibuka via JS pointer events.");
                } catch (Exception ignored) {}
            }

            if (!opened) {
                System.out.println("  [WARN] [" + fieldName + "] Overlay tidak terkonfirmasi terbuka — tetap melanjutkan.");
            }

            // Step 3 — Log semua item yang tersedia
            try {
                System.out.println("  [DEBUG] Items tersedia di overlay [" + fieldName + "]:");
                List<WebElement> overlays = driver.findElements(
                    By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                for (WebElement ov : overlays) {
                    if (ov.isDisplayed()) {
                        for (WebElement item : ov.findElements(By.cssSelector(".v-list-item"))) {
                            System.out.println("    - '" + item.getText().trim() + "'");
                        }
                    }
                }
            } catch (Exception ignored) {}

            // Step 4 — Tunggu dan temukan item yang cocok lalu klik
            WebElement matchedItem = null;
            try {
                matchedItem = new WebDriverWait(driver, Duration.ofSeconds(7))
                    .until(d -> {
                        List<WebElement> overlays = d.findElements(
                            By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                        for (WebElement ov : overlays) {
                            if (ov.isDisplayed()) {
                                for (WebElement item : ov.findElements(By.cssSelector(".v-list-item"))) {
                                    String text = item.getText().trim();
                                    if ("FIRST_AVAILABLE".equals(value) && !text.isEmpty()) return item;
                                    if (text.toLowerCase().contains(value.toLowerCase())) return item;
                                }
                            }
                        }
                        return null;
                    });
            } catch (Exception e) {
                // Debug: print semua overlay yang ada saat ini
                try {
                    List<WebElement> all = driver.findElements(
                        By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                    System.out.println("  [DEBUG] Overlay count: " + all.size());
                    for (WebElement ov : all) {
                        System.out.println("    Overlay visible=" + ov.isDisplayed() + " text='" + ov.getText().substring(0, Math.min(200, ov.getText().length())) + "'");
                    }
                } catch (Exception ignored) {}
                throw new IllegalStateException(
                    "[" + fieldName + "] Tidak ada opsi yang cocok untuk '" + value + "': " + e.getMessage(), e);
            }

            String selectedText = matchedItem.getText().trim();
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matchedItem);
            System.out.println("  [INFO] [" + fieldName + "] Dipilih: '" + selectedText + "'");
            sleep(400);

            // Step 5 — Tutup overlay dengan Escape
            try {
                if (isOverlayWithItemsVisible()) {
                    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                    sleep(300);
                }
            } catch (Exception ignored) {}

            // Step 6 — Verifikasi tag chip ter-render di dalam kontainer field
            final WebElement finalVField = vField; // final ref agar bisa dipakai di dalam lambda
            boolean tagVerified = false;
            try {
                tagVerified = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> {
                        try {
                            // Cari chip/selection di dalam field container atau seluruh halaman
                            List<WebElement> chips = finalVField.findElements(
                                By.cssSelector(".v-chip, .v-select__selection, .v-combobox__selection, [class*='chip'], [class*='tag']"));
                            for (WebElement chip : chips) {
                                if (chip.isDisplayed() && chip.getText().toLowerCase().contains(value.toLowerCase())) {
                                    return true;
                                }
                            }
                            // Fallback: cek teks dari seluruh vField apakah sudah mengandung value
                            String fieldText = finalVField.getText().toLowerCase();
                            return fieldText.contains(value.toLowerCase());
                        } catch (Exception ex) {
                            return false;
                        }
                    });
            } catch (Exception ignored) {}

            if (tagVerified) {
                System.out.println("  [ASSERT OK] Tag '" + value + "' berhasil ter-render di field [" + fieldName + "].");
            } else {
                System.out.println("  [ASSERT WARN] Tag '" + value + "' TIDAK terdeteksi di field [" + fieldName + "] setelah pemilihan.");
            }

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                "Gagal mengisi tag-select '" + fieldName + "' dengan nilai '" + value + "': " + e.getMessage(), e);
        }
    }

    /**
     * Helper: cek apakah ada overlay Vuetify yang tampil dan memiliki setidaknya satu .v-list-item.
     */
    private boolean isOverlayWithItemsVisible() {
        try {
            List<WebElement> overlays = driver.findElements(
                By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
            for (WebElement ov : overlays) {
                if (ov.isDisplayed() && !ov.findElements(By.cssSelector(".v-list-item")).isEmpty()) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Upload photo ke input file.
     */
    private void uploadPhoto(String filePath) {
        try {
            WebElement fileInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(inputPhoto));
            fileInput.sendKeys(filePath);
            System.out.println("  [INFO] Photo diupload: '" + filePath + "'");
            sleep(300);
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal upload photo: " + e.getMessage());
        }
    }

    /**
     * Isi Vuetify autocomplete/combobox field (id-based) dengan pengecekan ketersediaan data.
     * Mengangkat IllegalStateException jika dropdown kosong atau pilihan tidak muncul.
     */
    public void fillAutocompleteWithValidation(By locator, String value, String fieldName) {
        try {
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
            sleep(150);

            // Find parent .v-field container (start search from parent of input to skip the input itself)
            WebElement vField = null;
            try {
                vField = input.findElement(By.xpath("./ancestor::div[contains(@class,'v-field') and @role='combobox']"));
            } catch (Exception e) {
                vField = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "let el = arguments[0].parentElement; while(el) { if(el.getAttribute && el.getAttribute('role')==='combobox') return el; el = el.parentElement; } return null;",
                    input
                );
            }
            if (vField == null) {
                throw new IllegalStateException("Could not find combobox container for: " + fieldName);
            }
            
            String menuId = input.getAttribute("aria-controls");
            if (menuId == null || menuId.isEmpty()) {
                menuId = input.getAttribute("aria-owns");
            }
            if (menuId == null || menuId.isEmpty()) {
                menuId = vField.getAttribute("aria-controls");
            }
            
            final String finalMenuId = menuId;
            final WebElement finalVField = vField;
            boolean opened = tryOpenDropdown(input, finalVField, finalMenuId, fieldName);

            if (!opened) {
                System.out.println("  [WARN] Dropdown [" + fieldName + "] did not confirm open (proceeding anyway).");
            }

            // Wait for dropdown menu overlay to be visible and have items
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    try {
                        List<WebElement> overlays = d.findElements(By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                        for (WebElement overlay : overlays) {
                            if (overlay.isDisplayed() && !overlay.findElements(By.cssSelector(".v-list-item")).isEmpty()) {
                                return true;
                            }
                        }
                    } catch (Exception ignored) {}
                    return false;
                });

            // Log all available items in the dropdown overlay
            try {
                List<WebElement> overlays = driver.findElements(By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                System.out.println("  [DEBUG] All open overlay items for [" + fieldName + "]:");
                for (WebElement overlay : overlays) {
                    if (overlay.isDisplayed()) {
                        List<WebElement> currentItems = overlay.findElements(By.cssSelector(".v-list-item"));
                        for (int i = 0; i < currentItems.size(); i++) {
                            System.out.println("    - " + currentItems.get(i).getText().trim());
                        }
                    }
                }
            } catch (Exception ignored) {}

            // Check if input is typing-enabled (combobox vs select)
            String inputmode = input.getAttribute("inputmode");
            if (!"none".equals(inputmode) && !"FIRST_AVAILABLE".equals(value)) {
                clearAndTypeJS(locator, value);
                sleep(300);
            }

            // Wait for matching item to be present and visible in the active overlay
            WebElement match = null;
            try {
                match = new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(d -> {
                        try {
                            List<WebElement> overlays = d.findElements(By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                            for (WebElement overlay : overlays) {
                                if (overlay.isDisplayed()) {
                                    List<WebElement> els = overlay.findElements(By.cssSelector(".v-list-item"));
                                    for (WebElement el : els) {
                                        String text = el.getText().trim();
                                        if ("FIRST_AVAILABLE".equals(value)) {
                                            if (!text.isEmpty()) return el;
                                        } else {
                                            if (text.toLowerCase().contains(value.toLowerCase())) {
                                                return el;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                        return null;
                    });
            } catch (Exception e) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] matching failed. Exception: " + e.toString());
                // Collect whatever items are currently in the active dropdown on failure
                try {
                    List<WebElement> overlays = driver.findElements(By.cssSelector(".v-overlay__content, .v-menu, .v-overlay--active"));
                    for (WebElement overlay : overlays) {
                        if (overlay.isDisplayed()) {
                            List<WebElement> currentItems = overlay.findElements(By.cssSelector(".v-list-item"));
                            System.out.println("  [DEBUG] Available items in visible dropdown overlay on match failure:");
                            for (int i = 0; i < currentItems.size(); i++) {
                                System.out.println("    " + i + ": '" + currentItems.get(i).getText() + "'");
                            }
                        }
                    }
                } catch (Exception printEx) {
                    System.out.println("  [DEBUG] Failed to print dropdown items: " + printEx.toString());
                }
                throw new IllegalStateException(
                    "Dropdown '" + fieldName + "' tidak memiliki pilihan matching untuk: '" + value + "'. Original error: " + e.getMessage(), e);
            }

            String chosenText = match.getText().trim();
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", match);
            System.out.println("  [INFO] Dropdown [" + fieldName + "] dipilih: '" + chosenText + "' (via value '" + value + "')");
            sleep(300);

            // Close dropdown by sending ESCAPE if any active overlay remains visible (for multi-select Vuetify elements)
            try {
                List<WebElement> activeOverlays = driver.findElements(By.cssSelector(".v-overlay--active, .v-menu, .v-overlay__content"));
                boolean overlayVisible = false;
                for (WebElement overlay : activeOverlays) {
                    if (overlay.isDisplayed()) {
                        overlayVisible = true;
                        break;
                    }
                }
                if (overlayVisible) {
                    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                    sleep(300);
                }
            } catch (Exception ignored) {}

        } catch (IllegalStateException e) {
            throw e; // Rethrow to let test runner capture as FAIL
        } catch (Exception e) {
            throw new IllegalStateException("Gagal mengisi dropdown '" + fieldName + "' dengan nilai '" + value + "': " + e.getMessage(), e);
        }
    }

    /**
     * Helper: try multiple strategies to open a Vuetify v-select/v-combobox dropdown.
     * Returns true if the dropdown menu confirmed open.
     */
    private boolean tryOpenDropdown(WebElement input, WebElement vField, String menuId, String fieldName) {

        // Strategy K: Use Vue3 internal __vueParentComponent to set menu.value = true directly
        // This bypasses the data-no-activator div that intercepts all DOM clicks
        try {
            Boolean opened = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "var comp = el.__vueParentComponent;" +
                "var limit = 0;" +
                "while (comp && limit < 20) {" +
                "  var name = (comp.type && (comp.type.name || comp.type.__name)) || '';" +
                "  if (name.toLowerCase().includes('select') || name.toLowerCase().includes('combobox') || name.toLowerCase().includes('autocomplete')) {" +
                "    if (comp.setupState && comp.setupState.menu !== undefined) {" +
                "      comp.setupState.menu.value = true;" +
                "      return true;" +
                "    }" +
                "    if (comp.exposed && comp.exposed.menu !== undefined) {" +
                "      comp.exposed.menu.value = true;" +
                "      return true;" +
                "    }" +
                "  }" +
                "  comp = comp.parent; limit++;" +
                "}" +
                "return false;",
                input
            );
            if (Boolean.TRUE.equals(opened) && isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy K (Vue3 internal component).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy K failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        // Strategy L: Actions click on the chevron (.v-field__append-inner) area — offset right edge
        try {
            int w = vField.getSize().getWidth();
            int h = vField.getSize().getHeight();
            // Click at right edge where chevron is, not on the data-no-activator div center
            new Actions(driver).moveToElement(vField, (w / 2) - 5, 0).click().perform();
            if (isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy L (Actions offset click).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy L failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        // Strategy M: JS dispatchEvent with pointer events on vField
        try {
            ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(function(t) {" +
                "  el.dispatchEvent(new MouseEvent(t, {bubbles:true, cancelable:true, view:window}));" +
                "});",
                vField
            );
            if (isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy M (JS pointer events).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy M failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        // Strategy N: JS dispatchEvent on .v-field__append-inner (chevron)
        try {
            WebElement appendInner = vField.findElement(By.cssSelector(".v-field__append-inner"));
            ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(function(t) {" +
                "  el.dispatchEvent(new MouseEvent(t, {bubbles:true, cancelable:true, view:window}));" +
                "});",
                appendInner
            );
            if (isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy N (JS appendInner pointer events).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy N failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        // Strategy O: JS click on appendInner
        try {
            WebElement appendInner = vField.findElement(By.cssSelector(".v-field__append-inner"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", appendInner);
            if (isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy O (JS appendInner click).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy O failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        // Strategy P: Find the v-select component and call its toggle/open method via Vue
        try {
            Boolean opened = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "var comp = el.__vueParentComponent;" +
                "var limit = 0;" +
                "while (comp && limit < 20) {" +
                "  if (comp.exposed) {" +
                "    var exp = comp.exposed;" +
                "    if (typeof exp.open === 'function') { exp.open(); return true; }" +
                "    if (typeof exp.toggle === 'function') { exp.toggle(); return true; }" +
                "    if (typeof exp.openMenu === 'function') { exp.openMenu(); return true; }" +
                "    if (exp.isMenuActive !== undefined) { exp.isMenuActive.value = true; return true; }" +
                "  }" +
                "  comp = comp.parent; limit++;" +
                "}" +
                "return false;",
                vField
            );
            if (Boolean.TRUE.equals(opened) && isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy P (Vue3 exposed methods).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy P failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        // Last resort: Native click on appendInner
        try {
            WebElement appendInner = vField.findElement(By.cssSelector(".v-field__append-inner"));
            new Actions(driver).moveToElement(appendInner).click().perform();
            if (isDropdownOpen(input, vField, menuId)) {
                System.out.println("  [DEBUG] Dropdown [" + fieldName + "] opened via Strategy Q (Actions appendInner click).");
                return true;
            }
        } catch (Exception e) {
            System.out.println("  [DEBUG] Strategy Q failed [" + fieldName + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage().split("\n")[0]);
        }

        return false;
    }

    /**
     * Helper: check if a Vuetify dropdown menu is currently open with dynamic wait/polling and active overlay fallbacks.
     */
    private boolean isDropdownOpen(WebElement input, WebElement vField, String menuId) {
        for (int i = 0; i < 6; i++) {
            try {
                if ("true".equals(vField.getAttribute("aria-expanded")) || "true".equals(input.getAttribute("aria-expanded"))) {
                    return true;
                }
                if (menuId != null && !menuId.isEmpty()) {
                    List<WebElement> elements = driver.findElements(By.id(menuId));
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        return true;
                    }
                }
                // Fallback: check if there's any active overlay with list items
                List<WebElement> activeOverlays = driver.findElements(By.cssSelector(".v-overlay--active, .v-menu, .v-overlay__content"));
                for (WebElement overlay : activeOverlays) {
                    if (overlay.isDisplayed()) {
                        List<WebElement> items = overlay.findElements(By.cssSelector(".v-list-item"));
                        if (!items.isEmpty()) {
                            return true;
                        }
                    }
                }
            } catch (Exception ignored) {}
            sleep(100);
        }
        return false;
    }

    /**
     * Fallback atau legacy compatibility method.
     */
    private void fillAutocompleteById(By locator, String value) {
        fillAutocompleteWithValidation(locator, value, locator.toString());
    }

    /**
     * Klik item "Edit" atau "Ubah" di dalam dropdown menu Vuetify.
     * Menggunakan JS text-matching (sama seperti di JobTitlePage).
     */
    private void clickMenuEditOption(String menuId) {
        // Strategi 1: JS text-match langsung (paling reliable di Vuetify)
        Boolean jsClicked = false;
        try {
            jsClicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var menuId = arguments[0];" +
                "var menuContainer = document.getElementById(menuId);" +
                "if (!menuContainer) {" +
                "  var overlays = document.querySelectorAll('.v-overlay--active,.v-menu,.v-overlay');" +
                "  for (var i=0;i<overlays.length;i++) {" +
                "    if (overlays[i].textContent.includes('Edit')||overlays[i].textContent.includes('Ubah')) {" +
                "      menuContainer=overlays[i]; break;" +
                "    }" +
                "  }" +
                "}" +
                "if (menuContainer) {" +
                "  var items=menuContainer.querySelectorAll('.v-list-item,.v-list-item-title,button,a,div,span');" +
                "  for (var j=0;j<items.length;j++) {" +
                "    var t=items[j].textContent.trim();" +
                "    if (t==='Edit'||t==='Ubah'||t.toLowerCase()==='edit'||t.toLowerCase()==='ubah') {" +
                "      items[j].focus();" +
                "      items[j].dispatchEvent(new MouseEvent('mouseenter',{bubbles:true}));" +
                "      items[j].click();" +
                "      return true;" +
                "    }" +
                "  }" +
                "  for (var j=0;j<items.length;j++) {" +
                "    var t=items[j].textContent.trim();" +
                "    if ((t.includes('Edit')||t.includes('Ubah'))&&" +
                "        (items[j].classList.contains('v-list-item')||items[j].tagName==='BUTTON'||items[j].children.length===0)) {" +
                "      items[j].focus(); items[j].click(); return true;" +
                "    }" +
                "  }" +
                "}" +
                "return false;",
                menuId
            );
        } catch (Exception e) {
            System.out.println("  [WARN] JS menu click failed: " + e.getMessage());
        }

        if (Boolean.TRUE.equals(jsClicked)) {
            System.out.println("  [DEBUG] Klik 'Edit' berhasil via JS text-match.");
            sleep(1500);
        } else {
            // Strategi 2: Selenium locator fallback
            System.out.println("  [WARN] JS text-match gagal. Coba locator fallback...");
            List<By> strategies = new java.util.ArrayList<>();
            if (menuId != null && !menuId.isEmpty()) {
                strategies.add(By.xpath(
                    "//*[@id='" + menuId + "']//*[self::span or self::button or contains(@class,'v-list-item')]" +
                    "[contains(normalize-space(.),'Edit') or contains(normalize-space(.),'Ubah')]"
                ));
            }
            strategies.add(By.xpath(
                "//*[contains(@class,'v-list-item') or contains(@class,'v-list-item-title')]" +
                "[contains(normalize-space(.),'Edit') or contains(normalize-space(.),'Ubah')]"
            ));

            for (By locator : strategies) {
                try {
                    WebElement el = new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
                    if (el.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                        System.out.println("  [DEBUG] Klik 'Edit' via locator fallback.");
                        sleep(1200);
                        return;
                    }
                } catch (Exception ignored) {}
            }
            System.out.println("  [WARN] 'Edit' option tidak ditemukan di dropdown.");
        }
    }

    /**
     * Set Vuetify input menggunakan JavaScript dengan Vue-compatible InputEvent.
     * Metode ini sama dengan yang dipakai JobTitlePage untuk konsistensi.
     */
    private void clearAndTypeJS(By locator, String text) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript(
                "var el=arguments[0]; var text=arguments[1];" +
                "el.focus(); el.value='';" +
                "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));" +
                "for (var i=0;i<text.length;i++) {" +
                "  var ch=text.charAt(i); el.value+=ch;" +
                "  el.dispatchEvent(new InputEvent('input',{data:ch,inputType:'insertText',bubbles:true}));" +
                "}" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));",
                el, text
            );
        } catch (Exception e) {
            System.out.println("  [WARN] clearAndTypeJS gagal [" + locator + "]: "
                + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * Tunggu dan dismiss vue-notification.success jika ada (mencegah overlay button).
     */
    private void dismissSuccessNotifIfPresent() {
        try {
            WebElement notif = driver.findElement(successNotif);
            if (notif.isDisplayed()) {
                System.out.println("  [DEBUG] Notifikasi sukses terdeteksi, tunggu fade-out...");
                new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(ExpectedConditions.invisibilityOf(notif));
                sleep(200);
            }
        } catch (Exception ignored) {}
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
