package ru.kiokle.artpanda;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import ru.kiokle.artpanda.bean.ArtPandaDownloadBean;

/**
 *
 * @author me
 */
public class ArtPandaStart {

    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        String downloadFolder = args[1];
        String content = new String(Files.readAllBytes(new File(filePath).toPath()));
        List<ArtPandaDownloadBean> artPandaDownloadBeanList = Arrays.stream(content.split("\n")).filter(str -> str.length() > 0).map(str -> str.split(" - ")).map(ArtPandaDownloadBean::new).collect(Collectors.toList());
        System.out.println("Unique url size = " + artPandaDownloadBeanList.stream().map(ArtPandaDownloadBean::getUrl).collect(Collectors.toSet()).size() + "!");
        Integer from = 0;
        Integer to = artPandaDownloadBeanList.size() - 1;
        if (args.length == 4) {
            from = Integer.valueOf(args[2]);
            to = Integer.valueOf(args[3]);
        }
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        for (int i = from; i <= to; i++) {
            ArtPandaDownloadBean artPandaDownloadBean = artPandaDownloadBeanList.get(i);
            Thread downloadThread = new Thread(() -> download(artPandaDownloadBean.getId(), artPandaDownloadBean.getUrl(), downloadFolder + File.separator + artPandaDownloadBean.getId() + "_" + artPandaDownloadBean.getName() + (artPandaDownloadBean.getUrl().substring(artPandaDownloadBean.getUrl().indexOf(".")))));
            downloadThread.start();
            threadList.add(downloadThread);
        }
        threadList.stream().forEach(thread -> {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ArtPandaStart.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private static final int CONNECT_TIMEOUT = 60;
    private static final int READ_TIMEOUT = 60;

    public static void download(Integer id, String url, String downloadFolder) {
        try {
            System.out.println(id.toString() + " downloading started!");
            FileUtils.copyURLToFile(new URL(url), new File(downloadFolder), CONNECT_TIMEOUT, READ_TIMEOUT);

            System.out.println(id.toString() + " downloaded!");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
