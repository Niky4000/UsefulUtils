package ru.ibs.testpumputils.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author me
 */
@Entity
@Table(name = "UnloadZipBean")
public class UnloadZipBean4 {

    @Id
    @Column(name = "qq")
    private Long qq;

    @Column(name = "lpu_id")
    private String lpuId;

    @Lob
    @Column(name = "message_file")
    private byte[] messageFile;

    @Lob
    @Column(name = "message_file_name")
    private String messageFileName;

    @Lob
    @Column(name = "response_file")
    private byte[] responseFile;

    @Lob
    @Column(name = "response_file_name")
    private String responseFileName;

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getLpuId() {
        return lpuId;
    }

    public void setLpuId(String lpuId) {
        this.lpuId = lpuId;
    }

    public byte[] getMessageFile() {
        return messageFile;
    }

    public void setMessageFile(byte[] messageFile) {
        this.messageFile = messageFile;
    }

    public String getMessageFileName() {
        return messageFileName;
    }

    public void setMessageFileName(String messageFileName) {
        this.messageFileName = messageFileName;
    }

    public byte[] getResponseFile() {
        return responseFile;
    }

    public void setResponseFile(byte[] responseFile) {
        this.responseFile = responseFile;
    }

    public String getResponseFileName() {
        return responseFileName;
    }

    public void setResponseFileName(String responseFileName) {
        this.responseFileName = responseFileName;
    }

}
