package org.test;

import org.openqa.selenium.By;

import java.time.Duration;

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

        boolean usernamePlaceHolderDisplayed = chrome.findElement(By.xpath("//input[@placeholder = 'Masukan username']")).isDisplayed();
        System.out.println("Username placeholder: " + usernamePlaceHolderDisplayed);

        boolean passwordDisplayed = chrome.findElement(By.xpath("//*[text()='Password']")).isDisplayed();
        System.out.println("Password: " + passwordDisplayed);

        boolean passwordPlaceHolderDisplayed = chrome.findElement(By.xpath("//input[@placeholder = 'Masukan password']")).isDisplayed();
        System.out.println("Password placeholder: " + passwordPlaceHolderDisplayed);

        chrome.findElement(By.id("input-0")).click();
        chrome.findElement(By.id("input-2")).click();

        boolean emailValidation = chrome.findElement(By.xpath("//*[text()='E-mail wajib diisi']")).isDisplayed();
        System.out.println("Email Validation: " + emailValidation);

        chrome.findElement(By.xpath("//*[text()='Password']")).click();

        boolean passwordValidation = chrome.findElement(By.xpath("//*[text()='Password wajib diisi']")).isDisplayed();
        System.out.println("Password Validation: " + passwordValidation);

        String title = chrome.getTitle();
        if (title.equals("VEDATA")){
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }

        chrome.findElement(By.id("input-0")).sendKeys("tomy@admin.info");
        chrome.findElement(By.id("input-2")).sendKeys("rahasia");
        chrome.findElement(By.xpath("//button[@type = 'submit']")).click();

        boolean dashboardDisplayed = chrome.findElement(By.xpath("//span[@class='text-h5' and @xpath='1']")).isDisplayed();
        System.out.println("Dashboard Displayed: " + dashboardDisplayed);

//        chrome.close();
    }
}
