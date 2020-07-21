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
public class StartTransmit {

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            if (args.length == 3 && args[0].equals("-t")) {

            } else if (args.length == 3 && args[0].equals("-r")) {

            } else {
                FileReader fileReader = new FileReader("D:\\zzH2Database\\reader", "D:\\zzzzzzz", 8888);
                fileReader.start();
//                FileTransmitter fileTransmitter = new FileTransmitter("D:\\zzH2Database\\sender", "D:\\Media\\", "D:\\Media\\2014.07.31\\FreeStudio-6.3.6.716.exe", "localhost", 8888);
                FileTransmitter fileTransmitter = new FileTransmitter("D:\\zzH2Database\\sender", "D:\\Media\\", "D:\\Media\\2014.07.31", "localhost", 8888);
                fileTransmitter.start();
            }
        }
    }
}
