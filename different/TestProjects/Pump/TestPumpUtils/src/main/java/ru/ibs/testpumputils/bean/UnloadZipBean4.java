package ru.ibs.testpumputils.bean;

import java.io.Serializable;
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
public class UnloadZipBean4 implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "qq")
    private String qq;

    @Column(name = "lpu_id")
    private Long lpuId;

    @Lob
    @Column(name = "message_file")
    private byte[] messageFile;

    @Column(name = "message_file_name")
    private String messageFileName;

    @Lob
    @Column(name = "response_file")
    private byte[] responseFile;

    @Column(name = "response_file_name")
    private String responseFileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public Long getLpuId() {
        return lpuId;
    }

    public void setLpuId(Long lpuId) {
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
