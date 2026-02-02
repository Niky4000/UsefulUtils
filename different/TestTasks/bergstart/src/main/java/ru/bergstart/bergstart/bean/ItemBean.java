package ru.bergstart.bergstart.bean;

import java.util.List;

public class ItemBean {

    private String code;
    private String name;
    private List<ItemBean> items;

    public ItemBean() {
    }

    public ItemBean(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public ItemBean(String code, String name, List<ItemBean> items) {
        this.code = code;
        this.name = name;
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemBean> getItems() {
        return items;
    }

    public void setItems(List<ItemBean> items) {
        this.items = items;
    }
}
