package spam.attack.spamattack;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author NAnishhenko
 */
public class SpamAttack {

    public static void main(String args[]) throws InterruptedException {
//        htmlDriver2();
        chromiumDriver();
//        firefoxDriver();
//        htmlDriver();
    }

    protected static void chromiumDriver() throws InterruptedException {
//        org.openqa.selenium.chrome.ChromeDriver
        System.setProperty("webdriver.chrome.driver", "D:\\Distributives\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://NAnishhenko:Pagekeeper6@mail.ibs.ru/owa/");
        String title = driver.getTitle();
        String text = driver.switchTo().activeElement().getText();
        for (int i = 0; i < 14; i++) {
            driver.getKeyboard().pressKey(Keys.DOWN);
        }
        String text2 = "";
        WebElement activeElement = driver.switchTo().activeElement();
//        System.out.println(title + "_" + text + "_" + text2);
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        String pageSource = driver.getPageSource();
        Thread.sleep(4000);
        WebElement findElement = driver.findElement(By.id("divBdy"));
//        WebElement findElement = driver.findElement(By.id("divExp"));
        String tagName = findElement.getTagName();
        String text__ = findElement.getText();

        if (driver instanceof JavascriptExecutor) {
//            ((JavascriptExecutor) driver).executeScript("alert('Hello World!');");
            String executeScript = (String) ((JavascriptExecutor) driver).executeScript(""
                    + "var t=document.getElementById('divExp').innerHTML;"
                    + "return t;");
//            System.out.println(executeScript);
            Matcher matcher = Pattern.compile("URL=https%3a%2f%2fdoodle.com%2fpoll%2f(.+?)%3f", Pattern.DOTALL).matcher(executeScript);
            if (matcher.find()) {
                String linkId = matcher.group(1);
                String link = "https://doodle.com/poll/" + linkId;
                System.out.println("Link: "+link);
                driver.get(link);
                
            }
        }

//        driver.executeScript("", args)
//        for(String item:driver.getSessionStorage().keySet()){
//            System.out.println(item);
//        }
//        System.out.println(pageSource);
        driver.close();
    }

    protected static void firefoxDriver() throws InterruptedException {
        FirefoxDriver driver = new FirefoxDriver();
        driver.get("https://mail.ibs.ru/owa/");
        String title = driver.getTitle();
        String text = driver.switchTo().activeElement().getText();
        String text2 = "";
        System.out.println(title + "_" + text + "_" + text2);
    }

    protected static void htmlDriver2() throws InterruptedException {
//        org.openqa.selenium.chrome.ChromeDriver
        HtmlUnitDriver driver = new HtmlUnitDriver() {
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
//                creds.addCredentials("NAnishhenko", "Pagekeeper6");
                creds.addNTLMCredentials("NAnishhenko", "Pagekeeper6", null, -1, null, null);
                client.setCredentialsProvider(creds);
                return client;
            }
        };
        driver.setJavascriptEnabled(true);
        String title = driver.getTitle();
        String text = driver.switchTo().activeElement().getText();
        for (int i = 0; i < 14; i++) {
            driver.getKeyboard().pressKey(Keys.DOWN);
        }
        String text2 = "";
        WebElement activeElement = driver.switchTo().activeElement();
//        System.out.println(title + "_" + text + "_" + text2);
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        System.out.println("---------------------------------------");
        String pageSource = driver.getPageSource();
        Thread.sleep(8000);
//        WebElement findElement = driver.findElement(By.id("divBdy"));
//        WebElement findElement = driver.findElement(By.id("divExp"));
//        String tagName = findElement.getTagName();
//        String text__ = findElement.getText();

        if (driver instanceof JavascriptExecutor) {
//            ((JavascriptExecutor) driver).executeScript("alert('Hello World!');");
            Object executeScript = ((JavascriptExecutor) driver).executeScript(""
                    + "var t=document.getElementById('divExp').innerHTML;"
                    + "return t;");
            System.out.println(executeScript);
        }

//        driver.executeScript("", args)
//        for(String item:driver.getSessionStorage().keySet()){
//            System.out.println(item);
//        }
//        System.out.println(pageSource);
        driver.close();
    }

    protected static void htmlDriver() throws InterruptedException {
        //        FirefoxDriver driver = new FirefoxDriver();
        HtmlUnitDriver driver = new HtmlUnitDriver() {
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
//                creds.addCredentials("NAnishhenko", "Pagekeeper6");
                creds.addNTLMCredentials("NAnishhenko", "Pagekeeper6", null, -1, null, null);
                client.setCredentialsProvider(creds);
                return client;
            }
        };
        driver.get("https://mail.ibs.ru/owa/");
        String title = driver.getTitle();
        String text = driver.switchTo().activeElement().getText();
//        driver.switchTo().activeElement().sendKeys(Keys.ARROW_DOWN);
        driver.getKeyboard().pressKey(Keys.DOWN);
        driver.getKeyboard().pressKey(Keys.ENTER);
//        driver.getMouse().mouseDown(new Coordinates);
//        driver.
        Thread.sleep(2000);
        String text2 = driver.switchTo().activeElement().getText();
        boolean g = text.equals(text2);
        System.out.println(title + "_" + text + "_" + text2);
    }
}
