/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.instagramutil;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author Me
 */
public class InstagramUtilStart {

    public static void main(String[] args) throws Exception {
//        Instagram4j instagram = Instagram4j.builder().username("Meatcoins").password("Worldmaster7").build();
//        instagram.setup();
//        InstagramLoginResult login = instagram.login();
//        System.out.println(login.toString());
    	System.setProperty("webdriver.gecko.driver","D:\\GIT\\UsefulUtils\\different\\FireFoxDriver\\geckodriver-v0.26.0-win64\\geckodriver.exe");
    	WebDriver driver = new FirefoxDriver();
        driver.get("https://www.instagram.com/");
//        String tagName = driver.findElement(By.id("email")).getTagName();
//        System.out.println(tagName);
        driver.close();
        System.exit(0);
    }
}
