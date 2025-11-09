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

        boolean usernamePlaceHolderDisplayed = chrome.findElement(By.xpath("//input[@placeholder = 'Masukan username']")).isDisplayed();
        System.out.println("Username placeholder: " + usernamePlaceHolderDisplayed);

        boolean passwordDisplayed = chrome.findElement(By.xpath("//*[text()='Password']")).isDisplayed();
        System.out.println("Password: " + passwordDisplayed);

        boolean passwordPlaceHolderDisplayed = chrome.findElement(By.xpath("//input[@placeholder = 'Masukan password']")).isDisplayed();
        System.out.println("Password placeholder: " + passwordPlaceHolderDisplayed);

        chrome.findElement(By.id("input-0")).click();
        chrome.findElement(By.id("input-2")).click();

        boolean emailValidation = chrome.findElement(By.xpath("//*[text()='E-mail wajib diisi']")).isDisplayed();
        System.out.println("Password Validation: " + emailValidation);

        chrome.findElement(By.tagName("input")).click();

        boolean passwordValidation = chrome.findElement(By.xpath("//*[text()='Password wajib diisi']")).isDisplayed();
        System.out.println("Email Validation: " + passwordValidation);

        String title = chrome.getTitle();
        if (title.equals("VEDATA")){
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }

//        chrome.close();
    }
}
