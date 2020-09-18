/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

/**
 *
 * @author me
 */
public class MailHandlerFactory {

    public static MailHandler createInstance() {
        return new MailHandlerWebImpl();
    }
}
