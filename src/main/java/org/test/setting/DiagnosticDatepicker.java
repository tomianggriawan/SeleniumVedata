package org.test.setting;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test.common.BasePage;
import org.test.pages.DashboardPage;
import org.test.pages.EmployeePage;
import org.test.pages.LoginPage;

import java.io.FileWriter;
import java.time.Duration;
import java.util.List;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

public class DiagnosticDatepicker {
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

            // Find the input next to the label
            WebElement hireDateInput = new WebDriverWait(chrome, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[@for='emp-hire-date']/following::input[1]")));

            System.out.println("Removing 'readonly' attribute from Hire Date input...");
            ((JavascriptExecutor) chrome).executeScript("arguments[0].removeAttribute('readonly');", hireDateInput);
            Thread.sleep(200);

            System.out.println("Clicking and typing date '05112022'...");
            hireDateInput.click();
            hireDateInput.clear();
            hireDateInput.sendKeys("05112022");
            hireDateInput.sendKeys(org.openqa.selenium.Keys.TAB);
            Thread.sleep(1000);

            String finalValue = hireDateInput.getAttribute("value");
            System.out.println("Date input final value: '" + finalValue + "'");
            
            // Check if there are validation messages or errors
            String classes = hireDateInput.getAttribute("class");
            System.out.println("Input classes: " + classes);
            Thread.sleep(1000);

            // Let's dump the entire body HTML to see where the datepicker menu resides
            String bodyHtml = chrome.findElement(By.tagName("body")).getAttribute("outerHTML");
            try (FileWriter fw = new FileWriter("page_source_datepicker_open.html")) {
                fw.write(bodyHtml);
            }
            System.out.println("Successfully saved page_source_datepicker_open.html!");

            // Let's look for elements with class containing 'dp__'
            List<WebElement> dpElements = chrome.findElements(By.xpath("//*[contains(@class, 'dp__')]"));
            System.out.println("Found " + dpElements.size() + " elements with 'dp__' class:");
            for (WebElement el : dpElements) {
                String tagName = el.getTagName();
                String className = el.getAttribute("class");
                String id = el.getAttribute("id");
                String text = el.getText().trim();
                System.out.println("  Tag=" + tagName + " | Class=" + className + " | ID=" + id + " | Text=" + (text.length() > 50 ? text.substring(0, 50) + "..." : text));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
