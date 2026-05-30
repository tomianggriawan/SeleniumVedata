package org.test.setting;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.test.pages.DashboardPage;
import org.test.pages.EmployeePage;
import org.test.pages.LoginPage;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

public class DiagnosticVueKeys {
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

            WebElement dpMain = chrome.findElement(By.className("dp__main"));
            WebElement input = chrome.findElement(By.xpath("//label[@for='emp-hire-date']/following::input[1]"));

            System.out.println("Inspecting Vue keys on dp__main element:");
            String dpKeys = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var keys = Object.keys(el);" +
                "return keys.join(', ');",
                dpMain
            );
            System.out.println("  dp__main keys: " + dpKeys);

            System.out.println("Inspecting Vue keys on input element:");
            String inputKeys = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var keys = Object.keys(el);" +
                "return keys.join(', ');",
                input
            );
            System.out.println("  input keys: " + inputKeys);

            // Let's print properties of __vnode if it exists
            String vnodeDetails = (String) ((JavascriptExecutor) chrome).executeScript(
                "var el = arguments[0];" +
                "var vnKey = Object.keys(el).find(k => k.startsWith('__vnode') || k.startsWith('__vue'));" +
                "if (!vnKey) return 'No __vnode or __vue key found';" +
                "var vn = el[vnKey];" +
                "var res = ['Key: ' + vnKey];" +
                "if (vn) {" +
                "  res.push('Keys of vnode: ' + Object.keys(vn).join(', '));" +
                "  if (vn.ctx) res.push('vnode.ctx keys: ' + Object.keys(vn.ctx).join(', '));" +
                "  if (vn.component) {" +
                "    res.push('vnode.component keys: ' + Object.keys(vn.component).join(', '));" +
                "    if (vn.component.proxy) res.push('vnode.component.proxy keys: ' + Object.keys(vn.component.proxy).join(', '));" +
                "    if (vn.component.setupState) res.push('vnode.component.setupState keys: ' + Object.keys(vn.component.setupState).join(', '));" +
                "  }" +
                "}" +
                "return res.join('\\n');",
                dpMain
            );
            System.out.println("VNode details:\n" + vnodeDetails);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chrome.quit();
        }
    }
}
