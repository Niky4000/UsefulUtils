/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.birt.birttest;

import org.eclipse.birt.report.engine.api.EngineException;

/**
 *
 * @author User
 */
public class StartBirtTest {

    public static void main(String[] args) throws EngineException {
        new PdfBirtTest().executeReport();
    }
}
