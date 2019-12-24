package ru.ibs.tomcatrestart.bean;

import java.beans.Transient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author NAnishhenko
 */
public class CommitBean implements Comparable {

    private static final String DATE_MASK = "yyyy-MM-dd HH:mm:ssZ";
    private String id;
    private String message;
    private String authorName;
    private String authorEmail;
    private String committerName;
    private String committerEmail;
    private String date;
    private List<Object> tags;

    public CommitBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getCommitterName() {
        return committerName;
    }

    public void setCommitterName(String committerName) {
        this.committerName = committerName;
    }

    public String getCommitterEmail() {
        return committerEmail;
    }

    public void setCommitterEmail(String committerEmail) {
        this.committerEmail = committerEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Transient
    public Date getDateAsDate() {
        try {
            return new SimpleDateFormat(DATE_MASK).parse(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setDate(Date date) {
        this.date = new SimpleDateFormat(DATE_MASK).format(date);
    }

    public List<Object> getTags() {
        return tags;
    }

    public void setTags(List<Object> tags) {
        this.tags = tags;
    }

    @Override
    public int compareTo(Object o) {
        return -this.getDateAsDate().compareTo(((CommitBean) o).getDateAsDate());
    }

}
