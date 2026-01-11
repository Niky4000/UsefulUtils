package ru.kiokle.speech.audio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class AudioFileWriter {

    /**
     * Constructor for subclasses to call.
     */
    protected AudioFileWriter() {
    }

    /**
     * Obtains the file types for which file writing support is provided by this
     * audio file writer.
     *
     * @return array of file types. If no file types are supported, an array of
     * length 0 is returned.
     */
    public abstract AudioFileFormat.Type[] getAudioFileTypes();

    /**
     * Indicates whether file writing support for the specified file type is
     * provided by this audio file writer.
     *
     * @param fileType the file type for which write capabilities are queried
     * @return {@code true} if the file type is supported, otherwise
     * {@code false}
     * @throws NullPointerException if {@code fileType} is {@code null}
     */
    public boolean isFileTypeSupported(final AudioFileFormat.Type fileType) {
        return Arrays.stream(getAudioFileTypes()).anyMatch(fileType::equals);
    }

    /**
     * Obtains the file types that this audio file writer can write from the
     * audio input stream specified.
     *
     * @param stream the audio input stream for which audio file type support
     *               is queried
     * @return array of file types. If no file types are supported, an array of
     * length 0 is returned.
     * @throws NullPointerException if {@code stream} is {@code null}
     */
    public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream);

    /**
     * Indicates whether an audio file of the type specified can be written from
     * the audio input stream indicated.
     *
     * @param fileType file type for which write capabilities are queried
     * @param stream   for which file writing support is queried
     * @return {@code true} if the file type is supported for this audio input
     * stream, otherwise {@code false}
     * @throws NullPointerException if {@code fileType} or {@code stream} are
     *                              {@code null}
     */
    public boolean isFileTypeSupported(final AudioFileFormat.Type fileType,
                                       final AudioInputStream stream) {
        return Arrays.stream(getAudioFileTypes(stream))
                .anyMatch(fileType::equals);
    }

    /**
     * Writes a stream of bytes representing an audio file of the file type
     * indicated to the output stream provided. Some file types require that the
     * length be written into the file header, and cannot be written from start
     * to finish unless the length is known in advance. An attempt to write such
     * a file type will fail with an {@code IOException} if the length in the
     * audio file format is {@link AudioSystem#NOT_SPECIFIED}.
     *
     * @param stream   the audio input stream containing audio data to be written
     *                 to the output stream
     * @param fileType file type to be written to the output stream
     * @param out      stream to which the file data should be written
     * @return the number of bytes written to the output stream
     * @throws IOException              if an I/O exception occurs
     * @throws IllegalArgumentException if the file type is not supported by the
     *                                  system
     * @throws NullPointerException     if {@code stream} or {@code fileType} or
     *                                  {@code out} are {@code null}
     * @see #isFileTypeSupported(AudioFileFormat.Type, AudioInputStream)
     * @see #getAudioFileTypes
     */
    public abstract int write(AudioInputStream stream, AudioFileFormat.Type fileType,
                              OutputStream out) throws IOException;

    /**
     * Writes a stream of bytes representing an audio file of the file format
     * indicated to the external file provided.
     *
     * @param stream   the audio input stream containing audio data to be written
     *                 to the file
     * @param fileType file type to be written to the file
     * @param out      external file to which the file data should be written
     * @return the number of bytes written to the file
     * @throws IOException              if an I/O exception occurs
     * @throws IllegalArgumentException if the file format is not supported by
     *                                  the system
     * @throws NullPointerException     if {@code stream} or {@code fileType} or
     *                                  {@code out} are {@code null}
     * @see #isFileTypeSupported(AudioFileFormat.Type, AudioInputStream)
     * @see #getAudioFileTypes
     */
    public abstract int write(AudioInputStream stream, AudioFileFormat.Type fileType, File out)
            throws IOException;
}