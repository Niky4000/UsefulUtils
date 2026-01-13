/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.speech;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.vosk.LogLevel;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import org.vosk.Model;

/**
 *
 * @author me
 */
public class StartSpeech {

    // java -jar /home/me/GIT/UsefulUtils/different/TestProjects/speech/target/speech.jar -model /home/me/Downloads/vosk-model-ru-0.42 -microphone "sofhdadsp [plughw:0,7]"
    public static void main(String[] args) throws Exception {
//        speech();
//        MicrophoneCapture.capture();
//        MicrophoneCapture.capture2();
        MicrophoneCapture.captureWithSpeech2(args);
//        speech();
//        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
//        JavaSoundRecorder.main(args);
    }

    private static void speech() throws IOException, UnsupportedAudioFileException {
        LibVosk.setLogLevel(LogLevel.DEBUG);

        try (Model model = new Model("/home/me/Downloads/vosk-model-ru-0.42");
//                InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream("/home/me/Downloads/some.wav")));
                InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream("/home/me/Downloads/recording.wav")));
//                InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream("/home/me/Downloads/recording2.wav")));
//                Recognizer recognizer = new Recognizer(model, 88200);
                Recognizer recognizer = new Recognizer(model, 44100);
                ) {

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
