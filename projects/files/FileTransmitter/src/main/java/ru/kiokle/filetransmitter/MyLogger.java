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

    private static LogLevel logLevel = LogLevel.DEBUG;

    public enum LogLevel {
        INFO, DEBUG
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void log(String message, LogLevel logLevel) {
        if (this.logLevel.equals(logLevel) || logLevel.equals(LogLevel.INFO)) {
            System.out.println(message);
        }
    }

    public void log(String message, Exception ex) {
        System.out.println(message);
        ex.printStackTrace();
    }
}
