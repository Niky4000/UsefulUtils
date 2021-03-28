package ru.kiokle.artpanda;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
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
        Integer from = Integer.valueOf(args[1]);
        Integer to = Integer.valueOf(args[2]);
        String downloadFolder = args[3];
        String content = new String(Files.readAllBytes(new File(filePath).toPath()));
        List<ArtPandaDownloadBean> artPandaDownloadBeanList = Arrays.stream(content.split("\n")).filter(str -> str.length() > 0).map(str -> str.split(" - ")).map(ArtPandaDownloadBean::new).collect(Collectors.toList());
        System.out.println("Unique url size = " + artPandaDownloadBeanList.stream().map(ArtPandaDownloadBean::getUrl).collect(Collectors.toSet()).size() + "!");
        for (int i = from; i <= to; i++) {
            ArtPandaDownloadBean artPandaDownloadBean = artPandaDownloadBeanList.get(i);
            download(artPandaDownloadBean.getId(), artPandaDownloadBean.getUrl(), downloadFolder + File.separator + artPandaDownloadBean.getId() + "_" + artPandaDownloadBean.getName() + (artPandaDownloadBean.getUrl().substring(artPandaDownloadBean.getUrl().indexOf("."))));
        }
    }

    private static final int CONNECT_TIMEOUT = 60;
    private static final int READ_TIMEOUT = 60;

    public static void download(Integer id, String url, String downloadFolder) throws MalformedURLException, IOException {
        System.out.println(id.toString() + " downloading started!");
        FileUtils.copyURLToFile(new URL(url), new File(downloadFolder), CONNECT_TIMEOUT, READ_TIMEOUT);
        System.out.println(id.toString() + " downloaded!");
    }
}
