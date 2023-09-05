/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.artliner2;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author me
 */
public class ArtLiner2Start {

	static WebDriver driver;
	static BrowserMobProxyServer proxy;
	static Proxy seleniumProxy;

	public static void main(String[] args) throws InterruptedException, IOException {
		//Proxy Operations
		proxy = new BrowserMobProxyServer();
		proxy.start(8080);
		seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
		String hostIp = Inet4Address.getLocalHost().getHostAddress();
		seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
		seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		System.setProperty("webdriver.chrome.driver", "/home/me/Selenium/geckodriver");
		System.setProperty("webdriver.chrome.whitelistedIps", "");
		DesiredCapabilities seleniumCapabilities = new DesiredCapabilities();
		seleniumCapabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
		seleniumCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-web-security");
		options.addArguments("--allow-insecure-localhost");
		options.addArguments("--ignore-urlfetcher-cert-requests");
		ChromeDriver driver = new ChromeDriver(seleniumCapabilities);

		proxy.newHar("artlinerschool.ru");
		driver.get("https://artlinerschool.ru/cms/system/login?required=1");
		Thread.sleep(2000);
		Har har = proxy.getHar();
		File harFile = new File("artlinerschool.har");
		har.writeTo(harFile);

		proxy.stop();
		driver.quit();
	}
}
