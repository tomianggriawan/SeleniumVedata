package org.test.setting;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test.pages.LoginPage;

import java.io.FileWriter;
import java.time.Duration;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

public class DumpEmployeeProfile {
    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);
            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("Logging in...");
            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(5000);

            System.out.println("Navigating to Employee Profile page...");
            chrome.get("https://web.vedata.id/hcm/employee/profile");
            Thread.sleep(5000);

            System.out.println("Clicking Add button...");
            WebElement addBtn = new WebDriverWait(chrome, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(normalize-space(.), 'Add')]")));
            ((JavascriptExecutor) chrome).executeScript("arguments[0].click();", addBtn);
            Thread.sleep(5000);

            System.out.println("Dumping Add Employee page source...");
            String pageSrc = chrome.getPageSource();
            FileWriter fwSource = new FileWriter("page_source_employee_add.html");
            fwSource.write(pageSrc);
            fwSource.close();
            System.out.println("Page source dumped to page_source_employee_add.html");

            // Diagnostic: dump input elements and button details
            System.out.println("Parsing DOM elements on Add Employee form...");
            String parsedDetails = (String) ((JavascriptExecutor) chrome).executeScript(
                "var result = ['=== HEADINGS ==='];\n" +
                "document.querySelectorAll('h1, h2, h3, h4, h5, h6').forEach(function(h) {\n" +
                "  result.push(h.tagName + ': ' + h.textContent.trim());\n" +
                "});\n" +
                "\n" +
                "result.push('\\n=== INPUTS ===');\n" +
                "document.querySelectorAll('input, select, textarea').forEach(function(input) {\n" +
                "  var label = '';\n" +
                "  if (input.id) {\n" +
                "    var lblEl = document.querySelector('label[for=\"' + input.id + '\"]');\n" +
                "    if (lblEl) label = lblEl.textContent.trim();\n" +
                "  }\n" +
                "  if (!label) {\n" +
                "    var parentField = input.closest('.v-field');\n" +
                "    if (parentField) {\n" +
                "      var lbl = parentField.querySelector('label');\n" +
                "      if (lbl) label = lbl.textContent.trim();\n" +
                "    }\n" +
                "  }\n" +
                "  result.push('Tag=' + input.tagName + ' | id=' + input.id + ' | name=' + input.name + ' | type=' + input.type + ' | label=' + label + ' | val=' + input.value);\n" +
                "});\n" +
                "\n" +
                "result.push('\\n=== BUTTONS ===');\n" +
                "document.querySelectorAll('button').forEach(function(btn) {\n" +
                "  result.push('text=' + btn.textContent.trim() + ' | class=' + btn.className);\n" +
                "});\n" +
                "\n" +
                "return result.join('\\n');"
            );

            FileWriter fwDetails = new FileWriter("parsed_employee_add_elements.txt");
            fwDetails.write(parsedDetails);
            fwDetails.close();
            System.out.println("Add Employee DOM elements parsed and saved to parsed_employee_add_elements.txt");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
