/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 *
 * @author me
 */
public class StringTableCell extends AutoCommitTableCell<ImageLabelTableBean, String> {

    @Override
    protected String getInputValue() {
        return ((TextField) getInputField()).getText();
    }

    @Override
    protected void setInputValue(String value) {
        ((TextField) getInputField()).setText(value);
    }

    @Override
    protected String getDefaultValue() {
        return "";
    }

    @Override
    protected Node newInputField() {
        return new TextField();
    }

    @Override
    protected String inputValueToText(String newValue) {
        return newValue;
    }
}
