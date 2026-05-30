package org.test.setting;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test.pages.LoginPage;

import java.time.Duration;
import java.util.List;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

public class InspectExistingEmployee {
    public static void main(String[] args) {
        try {
            LoginPage loginPage = new LoginPage(chrome);
            chrome.get(baseUrl);
            chrome.manage().window().maximize();

            System.out.println("Logging in...");
            loginPage.login("tomi@tester.com", "1234");
            Thread.sleep(4000);

            System.out.println("Navigating to Employee Profile list...");
            chrome.get("https://web.vedata.id/hcm/employee/profile");
            Thread.sleep(5000);

            System.out.println("Waiting for table rows...");
            WebDriverWait wait = new WebDriverWait(chrome, Duration.ofSeconds(15));
            List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tbody//tr")));

            System.out.println("Found " + rows.size() + " rows in table:");
            for (int i = 0; i < rows.size(); i++) {
                List<WebElement> cells = rows.get(i).findElements(By.xpath("./td"));
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < cells.size(); j++) {
                    sb.append("[").append(j).append("] ").append(cells.get(j).getText().trim()).append(" | ");
                }
                System.out.println("  Row " + i + ": " + sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
