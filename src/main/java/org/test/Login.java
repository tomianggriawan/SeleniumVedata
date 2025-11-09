package org.test;

import org.openqa.selenium.By;

import static org.test.common.WebDriverTools.baseUrl;
import static org.test.common.WebDriverTools.chrome;

public class Login {

    public static void main(String[] args) {
        chrome.get(baseUrl);
        chrome.manage().window().maximize();

        boolean logoDisplayed = chrome.findElement(By.cssSelector(".logo")).isDisplayed();
        System.out.println("Logo: " + logoDisplayed);

        boolean usernameDisplayed = chrome.findElement(By.xpath("//*[text()='Username']")).isDisplayed();
        System.out.println("Username: " + usernameDisplayed);


        String title = chrome.getTitle();
        if (title.equals("VEDATA")){
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }

        chrome.close();
    }
}
