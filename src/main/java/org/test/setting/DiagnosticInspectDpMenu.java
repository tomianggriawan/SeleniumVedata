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

public class DiagnosticInspectDpMenu {
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

            System.out.println("Opening datepicker...");
            ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(t => {" +
                "  el.dispatchEvent(new MouseEvent(t, {bubbles:true, cancelable:true, view:window}));" +
                "});",
                hireDateInput
            );
            Thread.sleep(2000);

            boolean dpMenuExists = !chrome.findElements(By.className("dp__menu")).isEmpty();
            System.out.println("Is dp__menu present? " + dpMenuExists);
            if (dpMenuExists) {
                WebElement menu = chrome.findElement(By.className("dp__menu"));
                
                System.out.println("Inspecting dp__menu keys:");
                String menuKeys = (String) ((JavascriptExecutor) chrome).executeScript(
                    "var el = arguments[0];" +
                    "return Object.keys(el).join(', ');",
                    menu
                );
                System.out.println("  dp__menu keys: " + menuKeys);

                System.out.println("Querying all clickable items inside dp__menu...");
                String items = (String) ((JavascriptExecutor) chrome).executeScript(
                    "var el = arguments[0];" +
                    "var res = [];" +
                    "var els = el.querySelectorAll('*');" +
                    "for (var i = 0; i < els.length; i++) {" +
                    "  var e = els[i];" +
                    "  var txt = e.textContent.trim();" +
                    "  var cls = e.className;" +
                    "  if (cls || txt) {" +
                    "    res.push(e.tagName + ' [class=\"' + cls + '\"] Text: ' + txt);" +
                    "  }" +
                    "}" +
                    "return res.join('\\n');",
                    menu
                );
                System.out.println("All child elements:\n" + items);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
