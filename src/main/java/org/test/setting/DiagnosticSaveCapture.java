package org.test.setting;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test.pages.DashboardPage;
import org.test.pages.EmployeePage;
import org.test.pages.LoginPage;

import java.io.FileWriter;
import java.time.Duration;
import java.util.List;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

/**
 * DiagnosticSaveCapture - fills the employee form then dumps:
 *  1. All input field values (to check Vue reactive binding)
 *  2. Page source after fill (before save) → page_source_before_save.html
 *  3. Page source after save click → page_source_after_save.html
 *  4. Network error/toast text
 */
public class DiagnosticSaveCapture {
    public static void main(String[] args) throws Exception {
        LoginPage loginPage = new LoginPage(chrome);
        chrome.get(baseUrl);
        chrome.manage().window().maximize();
        loginPage.login("tomi@tester.com", "1234");
        Thread.sleep(4000);

        new DashboardPage(chrome).navigateToEmployeeProfilePage();
        Thread.sleep(3000);

        EmployeePage page = new EmployeePage(chrome);
        page.clickAddButton();
        Thread.sleep(2000);

        // Fill all fields
        System.out.println("[DIAG] Filling Employment Info...");
        page.fillEmploymentInfo(
            "EMP-DIAG01", "FIRST_AVAILABLE", "FIRST_AVAILABLE",
            "12-09-2023", "12-09-2023", "12-09-2024", "12-09-2023", "12-12-2023"
        );
        System.out.println("[DIAG] Filling Personal Identity...");
        page.fillPersonalIdentity(
            "1234567890123456", "Tomi", "Testuser", "Tomi",
            "06-03-2000", "Jakarta", "Laki-Laki", "WNI", "Lajang", null
        );
        System.out.println("[DIAG] Filling Contact Info...");
        page.fillContactInfo(
            "081234567890", "tomi.test@retailtest.id", "tomi@company.id",
            "Jl. Test No. 1, Jakarta", "082111222333", "Orang Tua", "Jl. Test No. 1, Jakarta"
        );
        System.out.println("[DIAG] Filling Education Info...");
        page.fillEducationInfo("SMA", "Ekonomi", "2018");
        System.out.println("[DIAG] Filling Payroll Info...");
        page.fillPayrollInfo("123456789012345", "4500000", "BCA", "Tomi Testuser", "1234567890", "KCP Sudirman Jakarta");
        System.out.println("[DIAG] Filling Position Info...");
        page.fillPositionInfo("Consultant", "Administration", "FIRST_AVAILABLE");

        Thread.sleep(1500);

        // Dump all input values BEFORE save
        System.out.println("\n[DIAG] ===== FIELD VALUES BEFORE SAVE =====");
        String fieldDump = (String) ((JavascriptExecutor) chrome).executeScript(
            "var res = [];" +
            "document.querySelectorAll('input, textarea, select').forEach(function(el) {" +
            "  var label = '';" +
            "  if (el.id) {" +
            "    var lbl = document.querySelector('label[for=\"' + el.id + '\"]');" +
            "    if (lbl) label = lbl.textContent.trim();" +
            "  }" +
            "  if (!label) {" +
            "    var vf = el.closest('.v-field');" +
            "    if (vf) { var l2 = vf.querySelector('label'); if (l2) label = l2.textContent.trim(); }" +
            "  }" +
            "  res.push('id=' + el.id + ' | label=' + label + ' | type=' + el.type + ' | value=[' + el.value + ']');" +
            "});" +
            "return res.join('\\n');"
        );
        System.out.println(fieldDump);

        // Save page source before click
        String srcBefore = chrome.findElement(By.tagName("body")).getAttribute("outerHTML");
        try (FileWriter fw = new FileWriter("page_source_before_save.html")) { fw.write(srcBefore); }
        System.out.println("\n[DIAG] Saved page_source_before_save.html");

        // Now click Save
        System.out.println("[DIAG] Clicking Save...");
        WebElement saveBtn = new WebDriverWait(chrome, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@class,'bg-primary') and contains(.,'Save')]")));
        ((JavascriptExecutor) chrome).executeScript("arguments[0].click();", saveBtn);
        Thread.sleep(3000);

        // Capture toast notifications
        System.out.println("\n[DIAG] ===== NOTIFICATIONS AFTER SAVE =====");
        String notifDump = (String) ((JavascriptExecutor) chrome).executeScript(
            "var res = [];" +
            "document.querySelectorAll('.vue-notification, .v-snackbar__content, [role=alert], .v-alert').forEach(function(el) {" +
            "  if (el.offsetParent !== null || el.textContent.trim()) {" +
            "    res.push('class=' + el.className + ' | text=' + el.textContent.trim());" +
            "  }" +
            "});" +
            "return res.join('\\n') || '(none found)';"
        );
        System.out.println(notifDump);

        // Current URL after save
        System.out.println("[DIAG] URL after save: " + chrome.getCurrentUrl());

        // Save page source after click
        String srcAfter = chrome.findElement(By.tagName("body")).getAttribute("outerHTML");
        try (FileWriter fw = new FileWriter("page_source_after_save.html")) { fw.write(srcAfter); }
        System.out.println("[DIAG] Saved page_source_after_save.html");

        // Dump validation messages
        System.out.println("\n[DIAG] ===== VALIDATION MESSAGES =====");
        List<WebElement> validations = chrome.findElements(
            By.cssSelector(".v-messages__message, .v-input .v-messages"));
        for (WebElement v : validations) {
            String t = v.getText().trim();
            if (!t.isEmpty()) System.out.println("  VALIDATION: " + t);
        }

        chrome.quit();
        System.out.println("[DIAG] Done.");
    }
}
