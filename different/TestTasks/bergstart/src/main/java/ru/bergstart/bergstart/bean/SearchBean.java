package ru.bergstart.bergstart.bean;

public class SearchBean {

    private final String key;
    private final ItemBean result;

    public SearchBean(String key, ItemBean result) {
        this.key = key;
        this.result = result;
    }

    public String getKey() {
        return key;
    }

    public ItemBean getResult() {
        return result;
    }
}
