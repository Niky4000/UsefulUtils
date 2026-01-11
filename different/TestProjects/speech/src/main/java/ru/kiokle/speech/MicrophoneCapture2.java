/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.speech;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author me
 */
public class MicrophoneCapture2 {

    public static void capture() throws IOException {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
        TargetDataLine microphone;
        SourceDataLine speakers;
        try {
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            microphone = AudioSystem.getTargetDataLine(format, mixerInfos[7]);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();
            while (bytesRead < 100000) {
                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                bytesRead += numBytesRead;
                // write the mic data to a stream for use later
                out.write(data, 0, numBytesRead);
                // write mic data to stream for immediate playback
                speakers.write(data, 0, numBytesRead);
            }
            speakers.drain();
            speakers.close();
            microphone.close();

            File file = new File("/home/me/Downloads/recording.wav");
            if (file.exists()) {
                file.delete();
            }
            Files.write(file.toPath(), out.toByteArray(), StandardOpenOption.CREATE_NEW);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
