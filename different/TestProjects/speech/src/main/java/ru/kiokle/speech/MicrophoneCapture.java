/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.speech;

import ru.kiokle.speech.audio.AudioFileFormat;
import ru.kiokle.speech.audio.WaveFileWriter;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.vosk.Model;
import org.vosk.Recognizer;
import ru.kiokle.speech.audio.WaveFileWriterWithSpeech;

/**
 *
 * @author me
 */
public class MicrophoneCapture {

    public static void capture() {
        try {
            // 1. Define the audio format
            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // 2. Obtain a TargetDataLine
//            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Map<String, Mixer.Info> map = Stream.of(mixerInfos).collect(Collectors.toMap(Mixer.Info::getName, obj -> obj));
//            TargetDataLine line = AudioSystem.getTargetDataLine(format, mixerInfos[7]);
            TargetDataLine line = AudioSystem.getTargetDataLine(format, map.get("sofhdadsp [plughw:0,7]"));

            // 3. Open and start the line
            line.open(format);
            line.start(); // Start capturing

            // 4. Create an AudioInputStream
            AudioInputStream ais = new AudioInputStream(line);

            // Example: read data in a separate thread (for real-time processing or saving to file)
            System.out.println("Start capturing...");
            Thread stopper = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Example: record for 5 seconds
                        Thread.sleep(5000);
                        line.stop();
                        line.close();
                        System.out.println("Stopped capturing.");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            stopper.start();

            // 5. Read data (can also use AudioSystem.write() to save to a file directly)
            // This will block until the line is closed
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("/home/me/Downloads/recording.wav"));

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void capture2() {
        try {
            // 1. Define the audio format
            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // 2. Obtain a TargetDataLine
//            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Map<String, Mixer.Info> map = Stream.of(mixerInfos).collect(Collectors.toMap(Mixer.Info::getName, obj -> obj));
//            TargetDataLine line = AudioSystem.getTargetDataLine(format, mixerInfos[7]);
            TargetDataLine line = AudioSystem.getTargetDataLine(format, map.get("sofhdadsp [plughw:0,7]"));

            // 3. Open and start the line
            line.open(format);
            line.start(); // Start capturing

            // 4. Create an AudioInputStream
            AudioInputStream ais = new AudioInputStream(line);
//            AudioInputStream ais = new AudioInputStream(new AudioInputStream(line), format, format.getFrameSize());

//            long frameLength = calculateTotalFrames(audioData); // You must calculate this
//            AudioInputStream specifiedLengthStream = new AudioInputStream(inputStream, format, frameLength);
//            AudioSystem.write(specifiedLengthStream, AudioFileFormat.Type.WAVE, outputStream);
            // Example: read data in a separate thread (for real-time processing or saving to file)
            System.out.println("Start capturing...");
            Thread stopper = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Example: record for 5 seconds
                        Thread.sleep(5000);
                        line.stop();
                        line.close();
                        System.out.println("Stopped capturing.");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            stopper.start();

            // 5. Read data (can also use AudioSystem.write() to save to a file directly)
            // This will block until the line is closed
//            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("/home/me/Downloads/recording.wav"));
//            new WaveFileWriter().write(ais, AudioFileFormat.Type.WAVE, new File("/home/me/Downloads/recording.wav"));
//
//            File file = new File("/home/me/Downloads/recording.wav");
//            if (file.exists()) {
//                file.delete();
//            }
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            byte[] buffer = new byte[4096];
//            while (ais.available() > 0) {
//                ais.read(buffer);
//                out.writeBytes(buffer);
//            }
//            Files.write(file.toPath(), out.toByteArray(), StandardOpenOption.CREATE_NEW);
            //
//            byte[] buffer = new byte[4096];
//            int bytesRead = 0;
//            while ((bytesRead = ais.read(buffer, 0, buffer.length)) > 0) {
//                out.writeBytes(buffer);
//            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
            new WaveFileWriter().write(ais, AudioFileFormat.Type.WAVE, out);
            File file = new File("/home/me/Downloads/recording.wav");
            if (file.exists()) {
                file.delete();
            }
            Files.write(file.toPath(), out.toByteArray(), StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void captureWithSpeech() throws Exception {
        try (Model model = new Model("/home/me/Downloads/vosk-model-ru-0.42")) {
            System.out.println("READY!!!");
            try (InputStream ais = getAudioInputStream();
                    Recognizer recognizer = new Recognizer(model, 88200)) {
                int nbytes;
                byte[] b = new byte[4096];
                while ((nbytes = ais.read(b)) >= 0) {
                    if (recognizer.acceptWaveForm(b, nbytes)) {
                        System.out.println(recognizer.getResult());
                    } else {
                        System.out.println(recognizer.getPartialResult());
                    }
                }
                System.out.println(recognizer.getFinalResult());
            }
        }
    }

    private static InputStream getAudioInputStream() {
        try {
            // 1. Define the audio format
            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // 2. Obtain a TargetDataLine
//            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Map<String, Mixer.Info> map = Stream.of(mixerInfos).collect(Collectors.toMap(Mixer.Info::getName, obj -> obj));
//            TargetDataLine line = AudioSystem.getTargetDataLine(format, mixerInfos[7]);
            TargetDataLine line = AudioSystem.getTargetDataLine(format, map.get("sofhdadsp [plughw:0,7]"));

            // 3. Open and start the line
            line.open(format);
            line.start(); // Start capturing

            // 4. Create an AudioInputStream
            AudioInputStream ais = new AudioInputStream(line);

            // Example: read data in a separate thread (for real-time processing or saving to file)
            System.out.println("Start capturing...");
            Thread stopper = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Example: record for 5 seconds
                        Thread.sleep(30000);
                        line.stop();
                        line.close();
                        System.out.println("Stopped capturing.");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            stopper.start();
            // 5. Read data (can also use AudioSystem.write() to save to a file directly)
            // This will block until the line is closed
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[1]);
//            byteArrayInputStream.
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
            return ais;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void captureWithSpeech2() throws Exception {
        try (Model model = new Model("/home/me/Downloads/vosk-model-ru-0.42")) {
            System.out.println("READY!!!");
            try (Recognizer recognizer = new Recognizer(model, 44100)) {
                try (InputStream ais = getAudioInputStream2((b, nbytes) -> {
                    if (recognizer.acceptWaveForm(b, nbytes)) {
                        System.out.println(recognizer.getResult());
                    } else {
                        System.out.println(recognizer.getPartialResult());
                    }
                });) {
                }
                System.out.println(recognizer.getFinalResult());
            }
        }
    }

    private static InputStream getAudioInputStream2(BiConsumer<byte[], Integer> recognizer) {
        try {
            // 1. Define the audio format
            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // 2. Obtain a TargetDataLine
//            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            Map<String, Mixer.Info> map = Stream.of(mixerInfos).collect(Collectors.toMap(Mixer.Info::getName, obj -> obj));
//            TargetDataLine line = AudioSystem.getTargetDataLine(format, mixerInfos[7]);
            TargetDataLine line = AudioSystem.getTargetDataLine(format, map.get("sofhdadsp [plughw:0,7]"));

            // 3. Open and start the line
            line.open(format);
            line.start(); // Start capturing

            // 4. Create an AudioInputStream
            AudioInputStream ais = new AudioInputStream(line);

            // Example: read data in a separate thread (for real-time processing or saving to file)
            System.out.println("Start capturing...");
            Thread stopper = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Example: record for 5 seconds
                        Thread.sleep(30000);
                        line.stop();
                        line.close();
                        System.out.println("Stopped capturing.");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            stopper.start();
            // 5. Read data (can also use AudioSystem.write() to save to a file directly)
            // This will block until the line is closed
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[1]);
//            byteArrayInputStream.
//            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new WaveFileWriterWithSpeech().write(ais, AudioFileFormat.Type.WAVE, out, recognizer);
            File file = new File("/home/me/Downloads/recording.wav");
            if (file.exists()) {
                file.delete();
            }
            Files.write(file.toPath(), out.toByteArray(), StandardOpenOption.CREATE_NEW);
            return ais;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
