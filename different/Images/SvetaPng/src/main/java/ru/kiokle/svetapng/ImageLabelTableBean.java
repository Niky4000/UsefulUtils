/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author me
 */
public class ImageLabelTableBean {

    private SimpleStringProperty text;
    private SimpleStringProperty textSize;
    private SimpleStringProperty valign;
    private SimpleStringProperty count;

    public ImageLabelTableBean(String text, String textSize, String valign, String count) {
        this.text = new SimpleStringProperty(text);
        this.textSize = new SimpleStringProperty(textSize);
        this.valign = new SimpleStringProperty(valign);
        this.count = new SimpleStringProperty(count);
    }

    public SimpleStringProperty getTextProperty() {
        return text;
    }

    public SimpleStringProperty getTextSizeProperty() {
        return textSize;
    }

    public SimpleStringProperty getValignProperty() {
        return valign;
    }

    public SimpleStringProperty getCountProperty() {
        return count;
    }

    public String getText() {
        return text.get();
    }

    public String getTextSize() {
        return textSize.get();
    }

    public String getValign() {
        return valign.get();
    }

    public String getCount() {
        return count.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public void setTextSize(String textSize) {
        this.textSize.set(textSize);
    }

    public void setValign(String valign) {
        this.valign.set(valign);
    }

    public void setCount(String count) {
        this.count.set(count);
    }
}
