package ru.kiokle.artpanda.bean;

/**
 *
 * @author me
 */
public class ArtPandaDownloadBean {

    private final Integer id;
    private final String name;
    private final String url;

    public ArtPandaDownloadBean(String[] str) {
        this.id = Integer.valueOf(str[0]);
        this.name = str[1];
        this.url = str[2];
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
