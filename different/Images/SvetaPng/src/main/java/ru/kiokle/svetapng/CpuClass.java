/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

/**
 *
 * @author me
 */
public class CpuClass {

    public static String getCPUId() {
        oshi.SystemInfo si = new oshi.SystemInfo();
        String procId = si.getHardware().getProcessor().getProcessorID();
        return procId;
    }
}
