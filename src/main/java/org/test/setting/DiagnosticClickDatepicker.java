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

import java.time.Duration;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

public class DiagnosticClickDatepicker {
    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);
            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            new DashboardPage(chrome).navigateToEmployeeProfilePage();
            Thread.sleep(2000);

            EmployeePage employeePage = new EmployeePage(chrome);
            employeePage.clickAddButton();
            Thread.sleep(2000);

            WebElement hireDateInput = chrome.findElement(By.xpath("//label[@for='emp-hire-date']/following::input[1]"));
            WebElement hireDateField = chrome.findElement(By.xpath("//label[@for='emp-hire-date']/following::div[contains(@class,'v-field')][1]"));
            WebElement hireDateAppend = chrome.findElement(By.xpath("//label[@for='emp-hire-date']/following::div[contains(@class,'v-field__append-inner')][1]"));

            System.out.println("Executing DOM-monitoring click scripts...");

            // 1. Click input and monitor
            String resInput = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var dpBefore = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpBefore.push(e.tagName + '.' + e.className));" +
                "el.click();" +
                "var dpAfter = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpAfter.push(e.tagName + '.' + e.className));" +
                "return 'CLICK INPUT: before=' + dpBefore.length + ' (' + dpBefore.join(', ') + ') | after=' + dpAfter.length + ' (' + dpAfter.join(', ') + ')';"
                , hireDateInput
            );
            System.out.println(resInput);

            // Close if opened (blur)
            ((JavascriptExecutor) chrome).executeScript("arguments[0].blur();", hireDateInput);
            Thread.sleep(500);

            // 2. Click field container and monitor
            String resField = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var dpBefore = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpBefore.push(e.tagName + '.' + e.className));" +
                "el.click();" +
                "var dpAfter = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpAfter.push(e.tagName + '.' + e.className));" +
                "return 'CLICK V-FIELD: before=' + dpBefore.length + ' (' + dpBefore.join(', ') + ') | after=' + dpAfter.length + ' (' + dpAfter.join(', ') + ')';"
                , hireDateField
            );
            System.out.println(resField);

            // Close if opened
            ((JavascriptExecutor) chrome).executeScript("arguments[0].blur();", hireDateInput);
            Thread.sleep(500);

            // 3. Click append-inner (calendar icon) and monitor
            String resAppend = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var dpBefore = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpBefore.push(e.tagName + '.' + e.className));" +
                "el.click();" +
                "var dpAfter = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpAfter.push(e.tagName + '.' + e.className));" +
                "return 'CLICK APPEND-INNER: before=' + dpBefore.length + ' (' + dpBefore.join(', ') + ') | after=' + dpAfter.length + ' (' + dpAfter.join(', ') + ')';"
                , hireDateAppend
            );
            System.out.println(resAppend);

            // 4. Try JS pointer down/up sequence on input
            String resEvents = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var dpBefore = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpBefore.push(e.tagName + '.' + e.className));" +
                "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(t => {" +
                "  el.dispatchEvent(new MouseEvent(t, {bubbles:true, cancelable:true, view:window}));" +
                "});" +
                "var dpAfter = [];" +
                "document.querySelectorAll('[class*=\"dp__\"]').forEach(e => dpAfter.push(e.tagName + '.' + e.className));" +
                "return 'JS MOUSE EVENTS ON INPUT: before=' + dpBefore.length + ' (' + dpBefore.join(', ') + ') | after=' + dpAfter.length + ' (' + dpAfter.join(', ') + ')';" +
                ""
                , hireDateInput
            );
            System.out.println(resEvents);
            Thread.sleep(2000);

            // Let's dump the HTML body
            String bodyHtml = chrome.findElement(By.tagName("body")).getAttribute("outerHTML");
            try (java.io.FileWriter fw = new java.io.FileWriter("page_source_datepicker_open.html")) {
                fw.write(bodyHtml);
            }
            System.out.println("Successfully saved page_source_datepicker_open.html!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
