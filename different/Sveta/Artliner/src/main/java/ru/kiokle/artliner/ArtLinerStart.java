package ru.kiokle.artliner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.v115.network.Network;
import org.openqa.selenium.devtools.v115.network.model.RequestId;

public class ArtLinerStart {

    static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {
        String base = args[0];
        Integer dirNumber = Integer.valueOf(args[1]);
        File baseDir = Stream.of(new File(base).listFiles()).filter(file -> file.getName().startsWith(dirNumber.toString() + " ")).findFirst().get();
        mergeFiles(baseDir, getFileName(baseDir, dirNumber));
//		String baseDir = "/home/me/tmp/2 Как работает маяк/";
        List<String> lines = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(new File(baseDir + File.separator + "links.txt")))) {
            String line = null;
            do {
                line = fileReader.readLine();
                if (line != null && line.startsWith("https")) {
                    lines.add(line);
                }
            } while (line != null);
        }
        List<File> fileList = lines.stream().map(s -> getFile2(s, baseDir)).collect(Collectors.toList());
        for (int i = 0; i < fileList.size(); i++) {
            String url = lines.get(i);
            File fileName = fileList.get(i);
            download(url, fileName);
            System.out.println(i + " was downloaded!");
        }
        String filelistContent = fileList.stream().map(File::getAbsolutePath).map(s -> "file '" + s + "'").reduce((s1, s2) -> s1 + "\n" + s2).get();
        File files = new File(baseDir + File.separator + "filelist.txt");
        if (files.exists()) {
            files.delete();
        }
        Files.writeString(files.toPath(), filelistContent, StandardOpenOption.CREATE_NEW);
        mergeFiles(baseDir, getFileName(baseDir, dirNumber));
        removeAllRedundantFiles(baseDir);
        System.out.println(lines.size());
    }

    private static File getFile(String s, File baseDir) {
        String substring = s.substring(0, s.indexOf("?"));
        String substring2 = substring.substring(substring.lastIndexOf("/") + "/".length());
        return new File(baseDir + File.separator + substring2);
    }

    private static File getFile2(String s, File baseDir) {
        String substring2 = s.substring(s.lastIndexOf("/") + "/".length());
        return new File(baseDir + File.separator + substring2);
    }

    static void download(String url, File fileName) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte dataBuffer[] = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }
    }

    static String getFileName(File baseDir, Integer dirNumber) {
        String name = baseDir.getName();
        return name.substring(dirNumber.toString().length()).trim();
    }

    static void mergeFiles(File baseDir, String fileName) throws IOException {
        // ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
        File mpeg = new File(baseDir.getAbsolutePath() + File.separator + fileName + ".mp4");
        if (mpeg.exists()) {
            mpeg.delete();
        }
        String[] exec = new String[]{"ffmpeg", "-f", "concat", "-safe", "0", "-i", baseDir.getAbsolutePath() + File.separator + "filelist.txt", "-c", "copy", mpeg.getAbsolutePath()};
        String execStr = Stream.of(exec).reduce((s1, s2) -> s1 + " " + s2).get();
        System.out.println(execStr);
        Process process = Runtime.getRuntime().exec(exec);
        waitForProcess(process);
        System.out.println("Existance: " + new File(baseDir.getAbsolutePath() + File.separator + fileName + ".mp4").exists());
//		System.exit(0);
    }

    static void waitForProcess(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String readLine = reader.readLine();
            System.out.println(readLine);
        }
    }

    static void removeAllRedundantFiles(File baseDir) {
        List<File> redundantFileList = Stream.of(baseDir.listFiles()).filter(file -> file.getName().endsWith(".ts")).collect(Collectors.toList());
        for (File redundantFile : redundantFileList) {
            redundantFile.delete();
        }
    }

    static void selenium() throws InterruptedException {
        System.out.println("Hello from ArtLiner application!");
        System.setProperty("webdriver.gecko.driver", "/home/me/Selenium/geckodriver"); // Setting system properties of FirefoxDriver
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        ChromeDriver driver = new ChromeDriver(); //Creating an object of FirefoxDriver
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        devTools.addListener(Network.requestWillBeSent(), request -> {
            if (request.getRequest().getUrl() != null && request.getRequest().getUrl().contains("player02.getcourse.ru")) {
                try {
                    System.out.println("Url: " + request.getRequest().getUrl() + " ResponseBody: " + devTools.send(Network.getResponseBody(request.getRequestId())).getBody());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
//			System.out.println("Request Method : " + request.getRequest().getMethod());
//			System.out.println("Request URL : " + request.getRequest().getUrl());
//			System.out.println("Request headers: " + request.getRequest().getHeaders().toString());
//			System.out.println("Request body: " + request.getRequest().getPostData().toString());
        });
        devTools.addListener(Network.responseReceived(), response -> {
            if (response.getResponse().getUrl() != null && response.getResponse().getUrl().contains("player02.getcourse.ru")) {
                try {
                    System.out.println("Url: " + response.getResponse().getUrl() + " ResponseBody: " + devTools.send(Network.getResponseBody(response.getRequestId())).getBody());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        });
        Event event = Network.requestIntercepted();
        devTools.addListener(Network.loadingFinished(), response -> handler(devTools, response.getRequestId()));
        devTools.addListener(Network.requestIntercepted(), response -> {
            if (response.getRequestId().isPresent()) {
                handler(devTools, response.getRequestId().get());
            }
        });
        devTools.addListener(Network.dataReceived(), response -> handler(devTools, response.getRequestId()));
        devTools.addListener(Network.eventSourceMessageReceived(), response -> handler(devTools, response.getRequestId()));
        devTools.addListener(Network.loadingFinished(), response -> handler(devTools, response.getRequestId()));
        devTools.addListener(Network.requestIntercepted(), response -> {
            if (response.getRequestId().isPresent()) {
                handler(devTools, response.getRequestId().get());
            }
        });
        driver.get("https://artlinerschool.ru/cms/system/login?required=1");
        List<WebElement> elementList = driver.findElements(By.className("form-field-email"));
        List<WebElement> elementList2 = driver.findElements(By.className("form-field-password"));
        List<WebElement> preButtonList = driver.findElements(By.className("btn-success"));
        List<WebElement> inputs = elementList.stream().filter(el -> el.getTagName().equals("input")).collect(Collectors.toList());
        List<WebElement> inputs2 = elementList2.stream().filter(el -> el.getTagName().equals("input")).collect(Collectors.toList());
        List<WebElement> buttonList = preButtonList.stream().filter(el -> el.getTagName().equals("button") && el.getText().equals("Войти")).collect(Collectors.toList());
//		driver.findElement(By.name("q")).sendKeys("Browserstack Guide"); //name locator for text box
//		WebElement searchbutton = driver.findElement(By.name("btnK"));//name locator for google search
//		searchbutton.click();
        inputs.get(0).sendKeys("vetich_89@mail.ru");
        inputs2.get(0).sendKeys("worldmaster");
        buttonList.get(0).click();
        List<WebElement> education = driver.findElements(By.className("menu-item-teach"));
        education.get(0).click();
        List<WebElement> education2 = driver.findElements(By.className("subitem-link"));
        education2.get(0).click();
        List<WebElement> iAmPainterList = driver.findElements(By.className("has-children"));
//		WebElement iAmPainterLink = iAmPainterList.stream().filter(el -> el.getTagName().equals("a")).findFirst().get();
        iAmPainterList.get(0).click();
        List<WebElement> iAmPainterList2 = driver.findElements(By.className("no-children"));
        iAmPainterList2.get(0).click();
        List<WebElement> firstLink = driver.findElements(By.className("title"));
        firstLink.get(0).click();
        List<WebElement> divs = driver.findElements(By.tagName("div"));
        for (WebElement el : divs) {
            System.out.println("TagName: " + el.getTagName() + " text: " + el.getText());
        }
        System.out.println("----------------------");
        System.out.println("----------------------");
        List<WebElement> questionButton = driver.findElements(By.tagName("button"));
        for (WebElement el : questionButton) {
            System.out.println("TagName: " + el.getTagName() + " text: " + el.getText());
        }
//		if (!questionButton.isEmpty()) {
//			List<WebElement> questionButton2 = driver.findElements(By.className("jtt-button--confirm"));
//			questionButton2.get(0).click();
//		}

        List<WebElement> playButton = driver.findElements(By.className("lite-page"));
//		devTools.addListener(Network.requestWillBeSent(), request -> {
//			System.out.println("Request Method : " + request.getRequest().getMethod());
//			System.out.println("Request URL : " + request.getRequest().getUrl());
//			System.out.println("Request headers: " + request.getRequest().getHeaders().toString());
//			System.out.println("Request body: " + request.getRequest().getPostData().toString());
//		});
//Event event=Network.responseReceived();
//		devTools.addListener(Network.responseReceived(), request -> {
//			System.out.println("dataReceived: " + request.toString());
////			if (request.getRequest().getPostData().isPresent()) {
////				System.out.println("Post Data: " + request.getRequest().getPostData().get());
////			}
//		});
//		devTools.addListener(Network.loadingFinished(), entry -> {
//			System.out.println("ResponseBody: " + devTools.send(Network.getResponseBody(entry.getRequestId())).getBody());
//		});
//		devTools.addListener(Network.responseReceived(), responseReceived -> {
//			String responseUrl = responseReceived.getResponse().getUrl();
//			RequestId requestId = responseReceived.getRequestId();
////			if (responseUrl.contains("makemytrip")) {
//			System.out.println("Url: " + responseUrl);
//			System.out.println("Response body: " + devTools.send(Network.getResponseBody(requestId)).getBody());
////			}
//		});
//		devTools.addListener(Network.requestWillBeSent(), request -> {
//			System.out.println("Request Method : " + request.getRequest().getMethod());
//			System.out.println("Request URL : " + request.getRequest().getUrl());
//			System.out.println("Request headers: " + request.getRequest().getHeaders().toString());
//			System.out.println("Request body: " + request.getRequest().getPostData().toString());
//		});
//		devTools.addListener(Network.responseReceived(), response -> {
//			System.out.println("responseReceived: " + response.getResponse().getUrl());
//		});
        playButton.get(0).click();
        Thread.sleep(20000L);
        playButton.get(0).click();
        Thread.sleep(6000000L);
        driver.quit();
    }

    static void handler(DevTools devTools, RequestId requestId) {
        try {
            String body = devTools.send(Network.getResponseBody(requestId)).getBody();
            if (body.contains("EXTINF") && body.contains("EXTM3U") && body.contains("EXT-X-ALLOW-CACHE") && !body.contains("function(t,e)")) {
                File file = new File("/home/me/tmp/extinf.txt");
                if (file.exists()) {
                    file.delete();
                }
                Files.writeString(file.toPath(), body, StandardOpenOption.CREATE_NEW);
                System.out.println(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
