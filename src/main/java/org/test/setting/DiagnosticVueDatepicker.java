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

public class DiagnosticVueDatepicker {
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

            // Let's find the first input under dp__main and inspect its Vue parent chain
            WebElement input = chrome.findElement(By.xpath("//label[@for='emp-hire-date']/following::input[1]"));
            
            System.out.println("Running Javascript inspection on the input element...");
            
            String inspectionResult = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var res = [];" +
                "var comp = el.__vueParentComponent;" +
                "var limit = 0;" +
                "while (comp && limit < 30) {" +
                "  var name = (comp.type && (comp.type.name || comp.type.__name)) || 'Unknown';" +
                "  var keys = Object.keys(comp.setupState || {});" +
                "  var exposedKeys = Object.keys(comp.exposed || {});" +
                "  var props = Object.keys(comp.props || {});" +
                "  res.push('Depth ' + limit + ': ' + name + ' | setupStateKeys=' + JSON.stringify(keys) + ' | exposedKeys=' + JSON.stringify(exposedKeys) + ' | props=' + JSON.stringify(props));" +
                "  comp = comp.parent;" +
                "  limit++;" +
                "}" +
                "return res.join('\\n');",
                input
            );
            
            System.out.println("Vue components found in parent chain:");
            System.out.println(inspectionResult);

            // Try programmatically opening the datepicker via Vue instance
            System.out.println("Attempting programmatically opening the datepicker menu via Vue...");
            Boolean opened = (Boolean) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var comp = el.__vueParentComponent;" +
                "var limit = 0;" +
                "while (comp && limit < 30) {" +
                "  var name = (comp.type && (comp.type.name || comp.type.__name)) || '';" +
                "  if (name.toLowerCase().includes('date') || name.toLowerCase().includes('picker') || comp.setupState && (comp.setupState.openMenu || comp.setupState.toggleMenu)) {" +
                "    if (typeof comp.setupState.openMenu === 'function') { comp.setupState.openMenu(); return true; }" +
                "    if (typeof comp.setupState.toggleMenu === 'function') { comp.setupState.toggleMenu(); return true; }" +
                "    if (typeof comp.exposed.openMenu === 'function') { comp.exposed.openMenu(); return true; }" +
                "  }" +
                "  comp = comp.parent;" +
                "  limit++;" +
                "}" +
                "return false;",
                input
            );
            System.out.println("Opened via Vue method? " + opened);
            Thread.sleep(2000);

            // Check if dp__menu is now open
            boolean dpMenuExists = !chrome.findElements(By.className("dp__menu")).isEmpty();
            System.out.println("Is dp__menu present? " + dpMenuExists);
            if (dpMenuExists) {
                WebElement menu = chrome.findElement(By.className("dp__menu"));
                System.out.println("dp__menu HTML:");
                System.out.println(menu.getAttribute("outerHTML"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
