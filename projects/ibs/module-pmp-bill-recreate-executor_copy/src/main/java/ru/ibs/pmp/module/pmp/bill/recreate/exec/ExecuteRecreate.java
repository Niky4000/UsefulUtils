/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.module.pmp.bill.recreate.exec;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author NAnishhenko
 */
public class ExecuteRecreate {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("module.xml");
        ru.ibs.pmp.module.recreate.exec.ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
    }
}
