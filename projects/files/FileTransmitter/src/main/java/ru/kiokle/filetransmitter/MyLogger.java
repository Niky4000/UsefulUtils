/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

/**
 *
 * @author Me
 */
public class MyLogger {

    public void log(String message) {
        System.out.println(message);
    }

    public void log(String message, Exception ex) {
        System.out.println(message);
        ex.printStackTrace();
    }
}
