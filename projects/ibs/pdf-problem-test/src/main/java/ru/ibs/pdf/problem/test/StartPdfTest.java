/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pdf.problem.test;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import static com.itextpdf.text.Element.ALIGN_CENTER;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author me
 */
public class StartPdfTest {

    private static final String PDF = "pdf";
    private static final String PDF2 = ".pdf";
    private static final int TIME_TO_WAIT = 60;

    public static void main(String[] args) throws DocumentException, IOException {
        System.out.println("Hello from pdf test!");
        File jarPath = getPathToSelf();
        List<String> argsList = Arrays.asList(args);
        File dir = getDir(argsList);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!isItMaster(argsList)) {
//            waitForSomeTime();
            createTestPdf(dir, getFileNumber(argsList));
        } else {
            String javaPath = getJavaPath(argsList);
            long maxFiles = getMaxFiles(argsList);
            AtomicLong fileIndex = new AtomicLong(Arrays.stream(dir.listFiles()).map(file -> file.getName()).filter(fileName -> fileName.startsWith(PDF) && fileName.endsWith(PDF2)).map(fileName -> fileName.substring(PDF.length(), fileName.lastIndexOf(PDF2))).map(Long::valueOf).max(Long::compare).map(d -> d + 1L).orElse(0L));
            int threadNumber = getThreadNumber(argsList);
//            ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNumber / 4, threadNumber, TIME_TO_WAIT, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(threadNumber));
            ArrayBlockingQueue<String[]> arrayBlockingQueue = new ArrayBlockingQueue<String[]>(threadNumber);
            createThreadList(threadNumber, arrayBlockingQueue);
            for (long i = 0L; i < maxFiles; i++) {
                final long andIncrement = fileIndex.getAndIncrement();
                final String threadName = "pdf_" + andIncrement;
                final String[] executeParams;
                if (!isWindows()) {
                    executeParams = new String[]{javaPath, "-jar", jarPath.getAbsolutePath(), "-d", dir.getAbsolutePath(), "-n", andIncrement + ""};
                } else {
                    executeParams = new String[]{"cmd.exe", "/c", "start", "/wait", javaPath, "-jar", jarPath.getAbsolutePath(), "-d", dir.getAbsolutePath(), "-n", andIncrement + ""};
                }
                while (true) {
                    try {
                        arrayBlockingQueue.put(executeParams);
                        break;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            for (int i = 0; i < threadNumber; i++) {
                while (true) {
                    try {
                        arrayBlockingQueue.put(new String[]{DEAD_PILL});
                        break;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            System.out.println("Finished!");
        }
    }

    private static void waitForSomeTime() {
        while (true) {
            try {
                Thread.sleep(20000);
                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(StartPdfTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static final String DEAD_PILL = "DEAD_PILL";

    private static void createThreadList(int threadNumber, final ArrayBlockingQueue<String[]> queue) {
        IntStream.range(0, threadNumber).mapToObj(n -> {
            final String threadName = "pdf_" + n;
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        final String[] executeParams = queue.take();
                        if (executeParams.length == 1 && executeParams[0].equals(DEAD_PILL)) {
                            break;
                        }
                        try {
                            Process process = Runtime.getRuntime().exec(executeParams);
                            while (true) {
                                try {
                                    process.waitFor();
                                    break;
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StartPdfTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("Thread " + threadName + " is finished!");
            });
            thread.setName(threadName);
            return thread;
        }).forEach(thread -> thread.start());
    }

    private static final String errorSignString = ".notdef ";
    private static final String ERROR_MESSAGE = " sign was found in reportData!";
    private static final byte[] errorSign = errorSignString.getBytes();

    private static boolean isItErroneous(byte[] reportData) {
        if (reportData == null) {
            return false;
        }
        for (int i = 0; i < reportData.length; i++) {
            boolean error = true;
            for (int j = 0; j < errorSign.length; j++) {
                if (reportData[i + j] != errorSign[j]) {
                    error = false;
                    break;
                }
            }
            if (error) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }

    private static File getPathToSelf() {
        File file = new File(getPathToRootFolder().getAbsolutePath() + File.separator + "target" + File.separator + "pdf-problem-test.jar");
        if (!file.exists()) {
            return new File(getPathToRootFolder().getAbsolutePath() + File.separator + "pdf-problem-test.jar");
        } else {
            return file;
        }
    }

    private static File getPathToRootFolder() {
        try {
            File file = new File(StartPdfTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
                file = Arrays.stream(file.getParentFile().listFiles()).filter(localFile -> localFile.getName().endsWith(".jar")).findFirst().get();
            }
            File parentFile = file.getParentFile();
            if (parentFile.getAbsolutePath().contains("target")) {
                parentFile = parentFile.getParentFile();
            }
            return parentFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long getMaxFiles(List<String> args) {
        return Long.valueOf(args.get(args.indexOf("-m") + 1)).longValue();
    }

    private static int getThreadNumber(List<String> args) {
        return Integer.valueOf(args.get(args.indexOf("-t") + 1)).intValue();
    }

    private static boolean isItMaster(List<String> args) {
        return args.contains("-master");
    }

    private static long getFileNumber(List<String> args) {
        return Long.valueOf(args.get(args.indexOf("-n") + 1)).longValue();
    }

    private static String getJavaPath(List<String> args) {
        return args.get(args.indexOf("-j") + 1);
    }

    private static File getDir(List<String> args) {
        return new File(args.get(args.indexOf("-d") + 1));
    }

    private static final String ENCODING = "Cp1251";
    public static final String TIMES_FONT = "Times_New_Roman.ttf";
    public static final String TIMES_FONT2 = "LiberationSans-Regular.ttf";
    private static Font baseFont;

    static {
        try {
            baseFont = new Font(BaseFont.createFont(TIMES_FONT, ENCODING, BaseFont.EMBEDDED), 10);
            System.out.println(TIMES_FONT + " is used!");
        } catch (Exception e) {
            try {
                baseFont = new Font(BaseFont.createFont(TIMES_FONT2, ENCODING, BaseFont.EMBEDDED), 10);
                System.out.println(TIMES_FONT2 + " is used!");
            } catch (Exception ex) {
                try {
                    baseFont = new Font(BaseFont.createFont(), 10);
                    System.out.println("Default font is used!");
                } catch (Exception ex2) {
                    throw new RuntimeException(ex2);
                }
            }
        }
    }

    private static Paragraph createParagraph(String text, Font font) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(ALIGN_CENTER);
        return paragraph;
    }

    private static void createTestPdf(File dir, long fileNumber) throws DocumentException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 25, 25, 30, 25);
        PdfWriter.getInstance(document, stream);
        document.open();
        for (int i = 0; i < 50; i++) {
            document.add(createParagraph("Это тестовый документ!", baseFont));
        }
        document.close();
        byte[] byteArray = stream.toByteArray();
        File file = new File(dir.getAbsolutePath() + File.separator + PDF + fileNumber + PDF2);
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), byteArray, StandardOpenOption.CREATE_NEW);
        byte[] readAllBytes = Files.readAllBytes(file.toPath());
        if (!isItErroneous(readAllBytes)) {
            file.delete();
        }
    }
}
