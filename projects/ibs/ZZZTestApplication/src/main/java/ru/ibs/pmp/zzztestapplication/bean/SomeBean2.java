package ru.ibs.pmp.zzztestapplication.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author NAnishhenko
 */
public class SomeBean2 {

    private Long id;
    private String data;
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        return "SomeBean2{" + "id=" + id + ", data=" + data + ", created=" + simpleDateFormat.format(created) + '}';
    }
}
