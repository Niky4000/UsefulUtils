/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.filetransmitter;

import java.io.File;

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
				FileReader fileReader = new FileReader("/home/me/tmp/zzH2Database/reader", "/home/me/tmp/receivingResults", 8888, true);
				fileReader.start();
//                FileTransmitter fileTransmitter = new FileTransmitter("D:\\zzH2Database\\sender", "D:\\Media\\", "D:\\Media\\2014.07.31\\FreeStudio-6.3.6.716.exe", "localhost", 8888);
				FileTransmitter fileTransmitter = new FileTransmitter("/home/me/tmp/zzH2Database/sender", "/home/me/Downloads", "/home/me/Downloads/15408", "localhost", 8888, true);
//                FileTransmitter fileTransmitter = new FileTransmitter("D:\\zzH2Database\\sender", "D:\\Media\\", "D:\\Media\\2014.07.31\\FreeStudio", "localhost", 8888);
				fileTransmitter.start();
//                String md5Sum = CommonLogic.getMd5Sum(new File("D:\\Media\\2014.07.31\\Games\\Dark Souls II\\DARK SOULS™Ⅱ- DIGITAL EXTRAS\\DARK SOULS™Ⅱ- Original Soundtrack - MP3\\01 Departure.mp3"));
//                String md5Sum2 = CommonLogic.getMd5Sum(new File("D:\\zzzzzzz\\2014.07.31\\Games\\Dark Souls II\\DARK SOULS™Ⅱ- DIGITAL EXTRAS\\DARK SOULS™Ⅱ- Original Soundtrack - MP3\\01 Departure.mp3"));
//                boolean equals = md5Sum.equals(md5Sum2);
			}
		}
	}
}
