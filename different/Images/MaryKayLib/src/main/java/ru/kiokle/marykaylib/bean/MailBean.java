/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author me
 */
public class MailBean {

    public final static String ACCEPTED = "accepted";
    public final static String SIGN = "sign";
    public final static String REASON = "reason";
    public final static String USER_NAME = "user.name";
    public final static String CPU_ID = "cpu.id";

    private final Date receiveDate;
    private final List<String> from;
    private final String cpuId;
    private final Boolean request;
    private final String subject;
    private final String text;

    public MailBean(Date receiveDate, List<String> from, String cpuId, Boolean request, String subject, String text) {
        this.receiveDate = receiveDate;
        this.from = from;
        this.cpuId = cpuId;
        this.request = request;
        this.subject = subject;
        this.text = text;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public List<String> getFrom() {
        return from;
    }

    public String getCpuId() {
        return cpuId;
    }

    public boolean isRequest() {
        return request;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public boolean isNull() {
        return receiveDate == null && from == null && subject == null && text == null;
    }

    @Override
    public String toString() {
        return "{" + "receiveDate=" + receiveDate + ", from=" + from + ", cpuId=" + cpuId + ", request=" + request + ", subject=" + subject + ", text=" + text + '}';
    }



    public Map<String, Object> toMap() {
        Map<String, Object> params = new LinkedHashMap<>();
        if (receiveDate != null) {
            params.put("receiveDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(receiveDate));
        }
        if (from != null && from.size() == 1) {
            params.put("from", from.get(0));
        }
        if (subject != null) {
            params.put("subject", subject);
        }
        if (text != null) {
            params.put("text", text);
        }
        return params;
    }

    public static MailBean fromString(String str) {
        int[] receiveDateIndexes = getIndexes(str, "receiveDate");
        int[] fromIndexes = getIndexes(str, "from");
        int[] cpuIdIndexes = getIndexes(str, "cpuId");
        int[] requestIndexes = getIndexes(str, "request");
        int[] subjectIndexes = getIndexes(str, "subject");
        int[] textIndexes = getIndexes(str, "text");
        return new MailBean(null,
                null,
                str.substring(cpuIdIndexes[0], cpuIdIndexes[1]),
                Boolean.valueOf(str.substring(requestIndexes[0], requestIndexes[1])),
                str.substring(subjectIndexes[0], subjectIndexes[1]),
                str.substring(textIndexes[0], str.length() - 1));
    }

    private static int[] getIndexes(String str, String text) {
        int index1 = str.indexOf(text + "=");
        int index2 = str.indexOf(",", index1);
        return new int[]{index1 + text.length() + 1, index2};
    }

}
