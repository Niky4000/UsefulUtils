/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.artliner3;

import net.lightbody.bmp.core.har.*;
import net.lightbody.bmp.proxy.ProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author me
 */
public class ArtLiner3Start {

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.chrome.driver", "/home/me/Selenium/geckodriver");
		ProxyServer bmp = new ProxyServer(8071);
		bmp.start();

		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability(CapabilityType.PROXY, bmp.seleniumProxy());

		WebDriver driver = new ChromeDriver(caps);

		bmp.newHar("artlinerschool.ru");

		driver.get("https://artlinerschool.ru/cms/system/login?required=1");

		Har har = bmp.getHar();

		for (HarEntry entry : har.getLog().getEntries()) {
			HarRequest request = entry.getRequest();
			HarResponse response = entry.getResponse();

			System.out.println(request.getUrl() + " : " + response.getStatus()
					+ ", " + entry.getTime() + "ms");

//			assertThat(response.getStatus(), is(200));
		}

		driver.quit();

		bmp.stop();
	}
}
