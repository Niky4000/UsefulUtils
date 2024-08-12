/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author me
 */
public class StringAreaTableCell extends AutoCommitTableCell<ImageLabelTableBean, String> {

    @Override
    protected String getInputValue() {
        return ((TextArea) getInputField()).getText();
    }

    @Override
    protected void setInputValue(String value) {
        ((TextArea) getInputField()).setText(value);
    }

    @Override
    protected String getDefaultValue() {
        return "";
    }

    @Override
    protected Node newInputField() {
        TextArea textArea = new TextArea();
        textArea.setPrefRowCount(1);
        textArea.setPrefColumnCount(40);
        return textArea;
    }

    @Override
    protected String inputValueToText(String newValue) {
        return newValue;
    }

    @Override
    protected void addEvent() {
        field.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                commitEdit(getInputValue());
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelOnEscape();
            }
        });
        field.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            EventTarget target = event.getTarget();
            Object source = event.getSource();
//                if (!(target != null && target.toString().endsWith("TextAreaSkin$ContentView"))) {
//                    commitEdit(getInputValue());
//                }
            if (source == null || !(source instanceof TextArea)) {
                commitEdit(getInputValue());
            }
        });
    }
}
