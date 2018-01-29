/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.zzztestapplication;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.ibs.pmp.zzztestapplication.threads.TreadTest;

/**
 *
 * @author NAnishhenko
 */
public class SomeClass {

    public static void main(String[] args) throws Exception {
//        someTest();
//        testInterraptThreads();
//        formatSomeStr();
//        testFileLocks();
//        handleNames();
//        String hh="^(1|2)(0[1-9]|[1-2][0-9]|31(?!(?:0[2469]|11))|30(?!02))(0[1-9]|1[0-2])(\\d{2})([1-9]\\d?)$";
//        encodingTrials();
//
//        StringBuilder exeptionStackTraceStr = new StringBuilder();
//        String toString = exeptionStackTraceStr.toString();
//        String allLocalIP = getAllLocalIP();
//        System.out.println(allLocalIP);
//        testStack();
//        System.out.println(org.apache.commons.lang.StringUtils.substring("1234567", 2, 4));
//        System.out.println("1234567".substring(2, 4));

//        representXmlAsSql2(testSAX().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
//        boolean b1 = someCheck("s", "s");
//        boolean b2 = someCheck(null, null);
//        boolean b3 = someCheck("", "");
//        boolean b4 = someCheck(null, "s");
//        boolean b5 = someCheck("p", null);
//        debugPdfParsing();
//        base64Decode();
        testThreads();
    }

    private static void testThreads() throws InterruptedException {
        TreadTest treadTest = new TreadTest(Thread.currentThread());
        treadTest.start();
        Thread.sleep(10 * 1000);
        System.out.println("1");
        Thread.sleep(10 * 1000);
        System.out.println("2");
        Thread.sleep(10 * 1000);
        System.out.println("3");
        Thread.sleep(10 * 1000);
        System.out.println("4");
        Thread.sleep(10 * 1000);
        System.out.println("5");
        Thread.sleep(10 * 1000);
        System.out.println("6");
        Thread.sleep(10 * 1000);
        System.out.println("7");
        Thread.sleep(10 * 1000);
        System.out.println("8");
        treadTest.interrupt();
        treadTest.join();
        System.out.println("Finished!");
    }

    private static void base64Decode() throws IOException {
        String s = new String(Files.readAllBytes(new File("D:\\tmp\\urlencode.txt").toPath()));
//        String decode = URLDecoder.decode(s, "cp1251");
        String decode = new String(javax.xml.bind.DatatypeConverter.parseBase64Binary(s));
        System.out.println(decode);
        File saveFile = new File("D:\\tmp\\urldecode.txt");
        if (saveFile.exists()) {
            saveFile.delete();
        }
        Files.write(saveFile.toPath(), decode.getBytes(), StandardOpenOption.CREATE_NEW);
    }

    private static void debugPdfParsing() throws Exception {
        File file = new File("D:\\tmp\\parcels\\2081\\77004\\20171101\\S_M1117.pdf");
        LocationTextExtractionStrategy locationTextExtractionStrategy = new LocationTextExtractionStrategy();
        PdfReader reader = new PdfReader(new FileInputStream(file));
        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1, locationTextExtractionStrategy);
        String matchedText = "Стоимость заявленного счета:";
        String matchedText2 = "коп.";
        String matchedText3 = "руб.,";
        int indexOfMatchedText = textFromPage.indexOf(matchedText);
        String substringTextFromPage = textFromPage.substring(indexOfMatchedText + matchedText.length(), textFromPage.length());
        String matchedString = substringTextFromPage.substring(0, substringTextFromPage.indexOf(matchedText2));
        String almostADigit = matchedString.replace(matchedText3, "").replace(" ", "");
        Long totalBillPrice = Long.valueOf(almostADigit);
        System.out.println(textFromPage);
    }

    private static String getAllLocalIP() {
        try {
            StringBuilder sb = new StringBuilder();
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static void encodingTrials() throws IOException {
//        Path path = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate\\src\\test\\resources\\soapResponses\\tmp_8193710398\\Insured_25100582.xml").toPath();
        Path path = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate\\src\\test\\resources\\soapResponses\\tmp_8193710398\\SQL.txt").toPath();
        File file2 = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate\\src\\test\\resources\\soapResponses\\tmp_8193710398\\Insured_2.xml");
        Path path2 = file2.toPath();
        byte[] readAllBytes = Files.readAllBytes(path);
        String originalString = new String(readAllBytes);

        Charset utf8charset = StandardCharsets.UTF_8;
        Charset cp1251charset = Charset.forName("cp1251");

        ByteBuffer inputBuffer = ByteBuffer.wrap(readAllBytes);
        CharBuffer data = cp1251charset.decode(inputBuffer);
        ByteBuffer outputBuffer = utf8charset.encode(data);
        byte[] outputData = outputBuffer.array();

        String reencoded = new String(outputData, utf8charset);
        reencoded = reencoded.replaceAll("\\x00", "");

        String reencoded2 = new String(readAllBytes, cp1251charset);

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byteArrayOutputStream.write(readAllBytes, 0, readAllBytes.length);
//        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream, "cp1251");
//        new ByteArrayInputStream(outputData);
        if (file2.exists()) {
            file2.delete();
        }
        Files.write(path2, reencoded2.getBytes(utf8charset), StandardOpenOption.CREATE_NEW);
//        Files.write(path2, outputData, StandardOpenOption.CREATE_NEW);
    }

    private static void testFileLocks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File("D:\\tmp\\someTestFile.txt");
                    try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                        final FileChannel fc = raf.getChannel();
                        final FileLock fl = fc.lock();
                        if (fl != null) {
//                raf.seek(file.length());
//                raf.writeChars("yyyyy");
                            String strToWrite = "yyyKKyy";
                            ByteBuffer bytes = ByteBuffer.allocate(strToWrite.getBytes().length);
                            bytes.put(strToWrite.getBytes(), 0, strToWrite.getBytes().length);
                            bytes.flip();
                            fc.position(file.length());
                            int write = fc.write(bytes);
                            fc.force(false);
                            fl.release();
                            raf.close();
                            file.delete();
                        } else {
//                Files.write(file.toPath(), "the text".getBytes(), StandardOpenOption.APPEND);
                            System.out.println("Failed to acquire lock!");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void testFileLocks_() throws Exception {
        File file = new File("D:\\tmp\\someTestFile.txt");
        try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            final FileChannel fc = raf.getChannel();
            final FileLock fl = fc.tryLock();
            if (fl == null) {
                // Failed to acquire lock
            } else {
                try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        final WritableByteChannel outChannel = Channels.newChannel(baos)) {
                    for (final ByteBuffer buffer = ByteBuffer.allocate(1024); fc.read(buffer) != -1;) {
                        buffer.flip();
                        outChannel.write(buffer);
                        buffer.clear();
                    }
                    Files.write(Paths.get("target", "example.txt"), baos.toByteArray());
                } finally {
                    fl.release();
                }
            }
        }
    }

    private static void testInterraptThreads() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    System.out.println("Finished normally!");
                } catch (InterruptedException ex) {
//                    Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Interrapted!");
                }
            }
        });
        thread.start();
        try {
            thread.join(1000);
        } catch (InterruptedException ex) {
            if (thread.isAlive()) {
                thread.interrupt();
                System.out.println("External interraption in Exception handler!");
            }
        }
        if (thread.isAlive()) {
            thread.interrupt();
            System.out.println("External interraption!");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException exx) {
            Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, exx);
        }
        String hello = "";
    }

    private static void someTest() throws ParseException {
        Date dateEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-09-25 12:00:00");
        Date dateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-09-21 09:00:00");
        double diff = dateEnd.getTime() - dateStart.getTime();
        int onlyDay = Long.valueOf(TimeUnit.DAYS.convert(new Double(diff).longValue(), TimeUnit.MILLISECONDS)).intValue();
        int dateOffTime = dateOffTime(diff);
        System.out.println("Hello!!!!!");
    }

    private static int dateOffTime(double diff) {
        double days = (double) (diff / (1000 * 60 * 60 * 24));
        return (int) Math.ceil(days);
    }

    private static void formatSomeStr() {
        String str = "sum(col_31),sum(col_32),sum(col_33),sum(col_34),sum(col_35),sum(col_36),  \n"
                + "sum(col_1),sum(col_2),sum(col_3),sum(col_4),sum(col_5),sum(col_6),  \n"
                + "sum(col_7),sum(col_8),sum(col_9),sum(col_10),sum(col_11),sum(col_12),\n"
                + "sum(col_13),sum(col_14),sum(col_15),sum(col_16),sum(col_17),sum(col_18),  \n"
                + "sum(col_19),sum(col_20),sum(col_21),sum(col_22),sum(col_23),sum(col_24),\n"
                + "sum(col_25),sum(col_26),sum(col_27),sum(col_28),sum(col_29),sum(col_30),\n"
                + "sum(col_37),sum(col_38),sum(col_39),sum(col_40),sum(col_41),sum(col_42),\n"
                + "sum(col_43),sum(col_44),sum(col_45),sum(col_46),sum(col_47),sum(col_48),\n"
                + "sum(col_49),sum(col_50),sum(col_51),sum(col_52),sum(col_53),sum(col_54),\n";
        String str2 = "sum(col_110),sum(col_111),sum(col_112),sum(col_113),sum(col_114),sum(col_115),sum(col_116),sum(col_117),sum(col_118),sum(col_119),sum(col_120),\n"
                + "sum(col_55),sum(col_56),sum(col_57),sum(col_58),sum(col_59),sum(col_60),sum(col_61),sum(col_62),sum(col_63),sum(col_64),sum(col_65),\n"
                + "sum(col_66),sum(col_67),sum(col_68),sum(col_69),sum(col_70),sum(col_71),sum(col_72),sum(col_73),sum(col_74),sum(col_75),sum(col_76),\n"
                + "sum(col_77),sum(col_78),sum(col_79),sum(col_80),sum(col_81),sum(col_82),sum(col_83),sum(col_84),sum(col_85),sum(col_86),sum(col_87),\n"
                + "sum(col_88),sum(col_89),sum(col_90),sum(col_91),sum(col_92),sum(col_93),sum(col_94),sum(col_95),sum(col_96),sum(col_97),sum(col_98),\n"
                + "sum(col_99),sum(col_100),sum(col_101),sum(col_102),sum(col_103),sum(col_104),sum(col_105),sum(col_106),sum(col_107),sum(col_108),sum(col_109),\n"
                + "sum(col_121),sum(col_122),sum(col_123),sum(col_124),sum(col_125),sum(col_126),sum(col_127),sum(col_128),sum(col_129),sum(col_130),sum(col_131),\n"
                + "sum(col_132),sum(col_133),sum(col_134),sum(col_135),sum(col_136),sum(col_137),sum(col_138),sum(col_139),sum(col_140),sum(col_141),sum(col_142),\n"
                + "sum(col_143),sum(col_144),sum(col_145),sum(col_146),sum(col_147),sum(col_148),sum(col_149),sum(col_150),sum(col_151),sum(col_152),sum(col_153)";
        String[] strArray = (str + str2).replace("\n", "").replace(" ", "").replace("sum(", "").replace(")", "").split(",");
        StringBuilder sb = new StringBuilder("");
        for (String el : strArray) {
            sb.append("    " + el + " number,\n");
        }
//        System.out.println(sb.toString());
//        StringBuilder sb2 = new StringBuilder("");
        //        Matcher matcher = Pattern.compile("\\((.+?)\\)", Pattern.DOTALL).matcher(str.replace("\n", "\r\n"));
//        while (matcher.find()) {
//            String group = matcher.group(1);
//            sb2.append("sum(" + group + ") as " + group + ",");
//        }
//        System.out.println(sb2.toString());

        handleElements(str, "q.");
        handleElements(str2, "qq.");
    }

    private static void handleElements(String str, String prefix) {
        Matcher matcher = Pattern.compile("(sum\\()(.+?)(\\))", Pattern.DOTALL).matcher(str);
//        String replaceAll = matcher.replaceAll("$1$2$3 as $2");
        String replaceAll = matcher.replaceAll("nvl(" + prefix + "$2,0) as $2");
        System.out.println(replaceAll);
    }

    private static void handleNames() throws UnsupportedEncodingException, IOException {
        String girlNames = " Вилена\n"
                + "Владлена\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Мадлена\n"
                + "Октябрина\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Сталина"
                + " Ада (Адель)\n"
                + "Амалия\n"
                + "Анна\n"
                + "Елизавета\n"
                + "Жанна\n"
                + "Лия\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Марианна\n"
                + "(Марьяна)\n"
                + "Мария (Марья)\n"
                + "Марта (Марфа)\n"
                + "Римма\n"
                + "Серафима\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Сусанна\n"
                + "Эвелина\n"
                + "Эдита\n"
                + "Яна (Янина)"
                + " Аврора\n"
                + "Агриппина\n"
                + "Альбина\n"
                + "Антонина\n"
                + "Валентина\n"
                + "Валерия\n"
                + "Варвара\n"
                + "Венера\n"
                + "Веста\n"
                + "Виктория\n"
                + "Виолетта (Виола)\n"
                + "Виталина\n"
                + "Дина\n"
                + "Инна\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Карина\n"
                + "Каролина\n"
                + "Клавдия\n"
                + "Клара (Кларисса)\n"
                + "Кристина\n"
                + "Лана\n"
                + "Лара\n"
                + "Лилия (Лилиана)\n"
                + "Лолита (Лола)\n"
                + "Маргарита\n"
                + "Марина\n"
                + "Мариэтта\n"
                + "Матрона\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Наталья\n"
                + "Нонна\n"
                + "Павла (Павлина)\n"
                + "Регина\n"
                + "Роза (Розалия)\n"
                + "Стелла\n"
                + "Татьяна\n"
                + "Тина\n"
                + "Ульяна\n"
                + "Устинья\n"
                + "Юлия"
                + " Авдотья\n"
                + "Агата\n"
                + "Аглая\n"
                + "Агния\n"
                + "Аделина\n"
                + "(Аделаида)\n"
                + "Аксинья\n"
                + "Акулина (Акилина)\n"
                + "Алевтина\n"
                + "Александра\n"
                + "Алиса (Каллиста)\n"
                + "Алла\n"
                + "Анастасия\n"
                + "Ангелина\n"
                + "Анжела (Ангела)\n"
                + "Анжелика\n"
                + "Анисия\n"
                + "Анфиса\n"
                + "Ариана\n"
                + "Ася\n"
                + "Афанасия\n"
                + "Аэлита\n"
                + "Василина\n"
                + "Василиса\n"
                + "(Весёна)\n"
                + "Вероника\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Галина\n"
                + "Георгина\n"
                + "Глафира\n"
                + "Гликерия\n"
                + "Дарья (Дарина)\n"
                + "Диана\n"
                + "Евангелина\n"
                + "Евгения\n"
                + "Евдокия\n"
                + "Евпраксия\n"
                + "Екатерина\n"
                + "Елена\n"
                + "Ефросиния\n"
                + "Зинаида\n"
                + "Зоя\n"
                + "Илона\n"
                + "Инесса\n"
                + "Ирина (Арина)\n"
                + "Камилла\n"
                + "Кира\n"
                + "Ксения\n"
                + "Лариса\n"
                + "Лидия\n"
                + "Лика\n"
                + "Лина\n"
                + "Мелания\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Нелли\n"
                + "Ника\n"
                + "Нина\n"
                + "Пелагея\n"
                + "Полина\n"
                + "Прасковья\n"
                + "Раиса\n"
                + "София (Софья)\n"
                + "Степанида\n"
                + "Стефания\n"
                + "(Стефанида)\n"
                + "Таисия (Таисья)\n"
                + "Тамара\n"
                + "Феврония\n"
                + "(Хавронья)\n"
                + "Фёкла\n"
                + "Феодора (Федора)\n"
                + "Феодосия\n"
                + "Элеонора\n"
                + "Элина\n"
                + "Элла\n"
                + "Эмилия\n"
                + "Эмма"
                + " Алена\n"
                + "Алина\n"
                + "Ассоль\n"
                + "Береслава\n"
                + "Богдана (Божена)\n"
                + "Борислава\n"
                + "Бронислава\n"
                + "Ванда\n"
                + "Вера\n"
                + "Веселина\n"
                + "Весна\n"
                + "Влада\n"
                + "Владана\n"
                + "Владислава\n"
                + "Власта\n"
                + "(Властелина)\n"
                + "Голуба\n"
                + "Горислава\n"
                + "Дарина\n"
                + "Дарьяна\n"
                + "Дидилия\n"
                + "Доля (Доляна)\n"
                + "Есения\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Желя (Журба)\n"
                + "Забава\n"
                + "Зарина\n"
                + "Злата\n"
                + "Искра\n"
                + "Июлия\n"
                + "Лада\n"
                + "Лукерья\n"
                + "(Гликерья)\n"
                + "Любава\n"
                + "Любовь\n"
                + "Любомила\n"
                + "Людмила\n"
                + "Малуша\n"
                + "Мила\n"
                + "Милада\n"
                + "Милана\n"
                + "Милена (Мелания)\n"
                + "Милолика\n"
                + "Милослава\n"
                + "Мирослава\n"
                + "Млада\n"
                + "Надежда\n"
                + "\n"
                + "	\n"
                + "\n"
                + "Оксана\n"
                + "Олеся (Леся)\n"
                + "Ольга (Вольга)\n"
                + "Предслава\n"
                + "Рада\n"
                + "Радмила\n"
                + "Радосвета\n"
                + "Ростислава\n"
                + "Руда\n"
                + "Русалина\n"
                + "Руслана\n"
                + "Сбыслава\n"
                + "Светлана\n"
                + "Снежанна\n"
                + "Станислава\n"
                + "Томила\n"
                + "Цветана\n"
                + "Чеслава\n"
                + "Юлиана\n"
                + "Ярина\n"
                + "Ярослава\n"
                + "\n"
                + "А\n"
                + "\n"
                + "    Агафья\n"
                + "    Агриппина\n"
                + "    Акулина\n"
                + "    Алевтина\n"
                + "    Александра\n"
                + "    Алина\n"
                + "    Алла\n"
                + "    Анастасия\n"
                + "    Ангелина\n"
                + "    Анжела\n"
                + "    Анжелика (имя)\n"
                + "    Анна (имя)\n"
                + "    Антонина\n"
                + "\n"
                + "В\n"
                + "\n"
                + "    Валентина\n"
                + "    Валерия\n"
                + "    Варвара\n"
                + "    Василиса\n"
                + "    Вера (имя)\n"
                + "    Вероника\n"
                + "    Виктория (имя)\n"
                + "\n"
                + "Г\n"
                + "\n"
                + "    Галина\n"
                + "    Глафира\n"
                + "\n"
                + "Д\n"
                + "\n"
                + "    Дана (женское имя)\n"
                + "    Дарья\n"
                + "\n"
                + "Е\n"
                + "\n"
                + "    Евгения\n"
                + "    Евдокия\n"
                + "    Евпраксия\n"
                + "    Евфросиния\n"
                + "    Екатерина\n"
                + "    Елена\n"
                + "    Елизавета\n"
                + "    Ермиония\n"
                + "\n"
                + "Ж\n"
                + "\n"
                + "    Жанна\n"
                + "\n"
                + "З\n"
                + "\n"
                + "    Зинаида\n"
                + "    Злата\n"
                + "    Зоя\n"
                + "\n"
                + "И\n"
                + "\n"
                + "    Инга (имя)\n"
                + "    Инесса\n"
                + "    Иоанна\n"
                + "    Ираида\n"
                + "    Ирина\n"
                + "    Ия (имя)\n"
                + "\n"
                + "К\n"
                + "\n"
                + "    Карина\n"
                + "    Каролина (имя)\n"
                + "    Кира\n"
                + "    Клавдия\n"
                + "    Ксения\n"
                + "\n"
                + "Л\n"
                + "\n"
                + "    Лада (имя)\n"
                + "    Лариса (имя)\n"
                + "    Лидия (имя)\n"
                + "    Лилия (имя)\n"
                + "    Любовь (имя)\n"
                + "    Людмила (имя)\n"
                + "\n"
                + "М\n"
                + "\n"
                + "    Маргарита\n"
                + "    Марина\n"
                + "    Мария (имя)\n"
                + "    Марфа\n"
                + "    Матрёна\n"
                + "    Мирослава\n"
                + "\n"
                + "Н\n"
                + "\n"
                + "    Надежда (имя)\n"
                + "    Наина\n"
                + "    Наталья\n"
                + "    Нина\n"
                + "    Нонна\n"
                + "\n"
                + "О\n"
                + "\n"
                + "    Оксана\n"
                + "    Октябрина\n"
                + "    Ольга\n"
                + "\n"
                + "П\n"
                + "\n"
                + "    Пелагея\n"
                + "    Полина\n"
                + "    Прасковья\n"
                + "\n"
                + "Р\n"
                + "\n"
                + "    Раиса\n"
                + "    Регина\n"
                + "\n"
                + "С\n"
                + "\n"
                + "    Светлана\n"
                + "    Серафима\n"
                + "    Снежана\n"
                + "    София (имя)\n"
                + "\n"
                + "Т\n"
                + "\n"
                + "    Таисия\n"
                + "    Тамара (имя)\n"
                + "    Татьяна\n"
                + "\n"
                + "У\n"
                + "\n"
                + "    Ульяна\n"
                + "\n"
                + "Ф\n"
                + "\n"
                + "    Фаина\n"
                + "    Феврония\n"
                + "    Фёкла\n"
                + "    Феодора (имя)\n"
                + "\n"
                + "Ц\n"
                + "\n"
                + "    Целестина\n"
                + "\n"
                + "Ю\n"
                + "\n"
                + "    Юлия\n"
                + "\n"
                + "Я\n"
                + "\n"
                + "    Яна (имя)\n"
                + "    Ярослава\n";

        String boyNames = "А\n"
                + "\n"
                + "    Авдей\n"
                + "    Аверкий\n"
                + "    Авксентий\n"
                + "    Агафон\n"
                + "    Александр\n"
                + "    Алексей\n"
                + "    Альберт\n"
                + "    Альвиан\n"
                + "    Анатолий\n"
                + "    Андрей\n"
                + "    Антон\n"
                + "    Антонин\n"
                + "    Анфим\n"
                + "    Аристарх (имя)\n"
                + "    Аркадий\n"
                + "    Арсений\n"
                + "    Артём (имя)\n"
                + "    Артур\n"
                + "    Архипп\n"
                + "    Афанасий\n"
                + "\n"
                + "Б\n"
                + "\n"
                + "    Богдан\n"
                + "    Борис\n"
                + "\n"
                + "В\n"
                + "\n"
                + "    Вадим\n"
                + "    Валентин\n"
                + "    Валерий\n"
                + "    Валерьян\n"
                + "    Варлам\n"
                + "    Варфоломей (имя)\n"
                + "    Василий\n"
                + "    Венедикт\n"
                + "    Вениамин (имя)\n"
                + "    Викентий (имя)\n"
                + "    Виктор (имя)\n"
                + "    Виссарион\n"
                + "    Виталий\n"
                + "    Владимир (имя)\n"
                + "    Владислав\n"
                + "    Владлен\n"
                + "    Влас\n"
                + "    Всеволод\n"
                + "    Вячеслав\n"
                + "\n"
                + "Г\n"
                + "\n"
                + "    Гавриил\n"
                + "    Галактион\n"
                + "    Геласий\n"
                + "    Геннадий\n"
                + "    Георгий\n"
                + "    Герасим\n"
                + "    Герман\n"
                + "    Глеб\n"
                + "    Гордей\n"
                + "    Григорий\n"
                + "\n"
                + "Д\n"
                + "\n"
                + "    Даниил (имя)\n"
                + "    Демид (имя)\n"
                + "    Демьян\n"
                + "    Денис\n"
                + "    Дмитрий\n"
                + "    Добрыня\n"
                + "    Донат\n"
                + "    Дорофей\n"
                + "\n"
                + "Е\n"
                + "\n"
                + "    Евгений\n"
                + "    Евграф\n"
                + "    Евдоким\n"
                + "    Евсей\n"
                + "    Евстафий\n"
                + "    Егор\n"
                + "    Емельян\n"
                + "    Еремей\n"
                + "    Ермолай\n"
                + "    Ефим\n"
                + "\n"
                + "Ж\n"
                + "\n"
                + "    Ждан\n"
                + "\n"
                + "З\n"
                + "\n"
                + "    Зиновий\n"
                + "\n"
                + "И\n"
                + "\n"
                + "    Иакинф\n"
                + "    Иван\n"
                + "    Игнатий\n"
                + "    Игорь\n"
                + "    Иларион\n"
                + "    Илья\n"
                + "    Иннокентий\n"
                + "    Ираклий (имя)\n"
                + "    Ириней\n"
                + "    Исидор\n"
                + "    Иулиан\n"
                + "\n"
                + "К\n"
                + "\n"
                + "    Касьян\n"
                + "    Ким (имя)\n"
                + "    Кирилл\n"
                + "    Климент\n"
                + "    Кондрат (имя)\n"
                + "    Константин\n"
                + "    Корнилий\n"
                + "    Кузьма\n"
                + "    Куприян\n"
                + "\n"
                + "Л\n"
                + "\n"
                + "    Лаврентий\n"
                + "    Леонид\n"
                + "    Леонтий\n"
                + "    Лука (имя)\n"
                + "    Лукий\n"
                + "    Лукьян\n"
                + "\n"
                + "М\n"
                + "\n"
                + "    Магистриан\n"
                + "    Макар\n"
                + "    Максим\n"
                + "    Марк (имя)\n"
                + "    Мартын\n"
                + "    Матвей\n"
                + "    Мелентий\n"
                + "    Мирон\n"
                + "    Мирослав (имя)\n"
                + "    Митрофан\n"
                + "    Михаил\n"
                + "    Мстислав\n"
                + "    Мэлор\n"
                + "\n"
                + "Н\n"
                + "\n"
                + "    Назар (имя)\n"
                + "    Нестор\n"
                + "    Никанор\n"
                + "    Никита\n"
                + "    Никифор\n"
                + "    Николай\n"
                + "    Никон\n"
                + "\n"
                + "О\n"
                + "\n"
                + "    Олег\n"
                + "    Онисим\n"
                + "\n"
                + "П\n"
                + "\n"
                + "    Павел\n"
                + "    Пантелеймон\n"
                + "    Парфений\n"
                + "    Пётр\n"
                + "    Порфирий\n"
                + "    Потап\n"
                + "    Прокопий\n"
                + "    Протасий\n"
                + "    Прохор\n"
                + "\n"
                + "Р\n"
                + "\n"
                + "    Разумник\n"
                + "    Родион\n"
                + "    Роман (имя)\n"
                + "    Ростислав\n"
                + "    Руслан\n"
                + "\n"
                + "С\n"
                + "\n"
                + "    Савва\n"
                + "    Савелий\n"
                + "    Самуил (имя)\n"
                + "    Святополк\n"
                + "    Святослав\n"
                + "    Севастьян\n"
                + "    Семён\n"
                + "    Сергей\n"
                + "    Сильвестр (имя)\n"
                + "    Созон\n"
                + "    Спиридон\n"
                + "    Станислав\n"
                + "    Степан\n"
                + "\n"
                + "Т\n"
                + "\n"
                + "    Тарас\n"
                + "    Тимофей\n"
                + "    Тимур\n"
                + "    Тихон (значения)\n"
                + "    Трифон\n"
                + "    Трофим\n"
                + "\n"
                + "Ф\n"
                + "\n"
                + "    Фаддей\n"
                + "    Фёдор\n"
                + "    Федосей\n"
                + "    Федот\n"
                + "    Феликс\n"
                + "    Феоктист\n"
                + "    Филат\n"
                + "    Филипп\n"
                + "    Фома\n"
                + "    Фрол\n"
                + "\n"
                + "Х\n"
                + "\n"
                + "    Харитон\n"
                + "    Христофор\n"
                + "\n"
                + "Э\n"
                + "\n"
                + "    Эдуард\n"
                + "    Эраст\n"
                + "\n"
                + "Ю\n"
                + "\n"
                + "    Юлиан\n"
                + "    Юрий\n"
                + "    Юстин\n"
                + "\n"
                + "Я\n"
                + "\n"
                + "    Яков\n"
                + "    Якун\n"
                + "    Ярослав"
                + "    Август\n"
                + "    Авдей\n"
                + "    Аверкий\n"
                + "    Аверьян\n"
                + "    Авксентий\n"
                + "    Автоном\n"
                + "    Агап\n"
                + "    Агафон\n"
                + "    Аггей\n"
                + "    Адам\n"
                + "    Адриан и Андриян\n"
                + "    Азарий\n"
                + "    Аким\n"
                + "    Александр\n"
                + "    Алексей\n"
                + "    Амвросий\n"
                + "    Амос\n"
                + "    Ананий\n"
                + "    Анатолий\n"
                + "    Андрей\n"
                + "    Андрон\n"
                + "    Андроник\n"
                + "    Аникей\n"
                + "    Аникита\n"
                + "    Анисим и Онисим\n"
                + "    Антип\n"
                + "    Антонин\n"
                + "    Аполлинарий\n"
                + "    Аполлон\n"
                + "    Арефий\n"
                + "    Аристарх\n"
                + "    Аркадий\n"
                + "    Арсений\n"
                + "    Артемий\n"
                + "    Артем\n"
                + "    Архип\n"
                + "    Аскольд\n"
                + "    Афанасий\n"
                + "    Афиноген\n"
                + "    Бажен\n"
                + "    Богдан\n"
                + "    Болеслав\n"
                + "    Борис\n"
                + "    Борислав\n"
                + "    Боян\n"
                + "    Бронислав\n"
                + "    Будимир\n"
                + "    Вадим\n"
                + "    Валентин\n"
                + "    Валерий\n"
                + "    Валерьян\n"
                + "    Варлаам\n"
                + "    Варфоломей\n"
                + "    Василий\n"
                + "    Вацлав\n"
                + "    Велимир\n"
                + "    Венедикт\n"
                + "    Вениамин\n"
                + "    Викентий\n"
                + "    Виктор\n"
                + "    Викторин\n"
                + "    Виссарион\n"
                + "    Виталий\n"
                + "    Владилен\n"
                + "    Владлен\n"
                + "    Владимир\n"
                + "    Владислав\n"
                + "    Влас\n"
                + "    Всеволод\n"
                + "    Всемил\n"
                + "    Всеслав\n"
                + "    Вышеслав\n"
                + "    Вячеслав\n"
                + "    Гаврила и Гавриил\n"
                + "    Галактион\n"
                + "    Гедеон\n"
                + "    Геннадий\n"
                + "    Георгий\n"
                + "    Герасим\n"
                + "    Герман\n"
                + "    Глеб\n"
                + "    Гордей\n"
                + "    Гостомысл\n"
                + "    Гремислав\n"
                + "    Григорий\n"
                + "    Гурий\n"
                + "    Давыд и Давид\n"
                + "    Данила и Даниил\n"
                + "    Дементий\n"
                + "    Демид\n"
                + "    Демьян\n"
                + "    Денис\n"
                + "    Дмитрий\n"
                + "    Добромысл\n"
                + "    Доброслав\n"
                + "    Дорофей\n"
                + "    Евгений\n"
                + "    Евграф\n"
                + "    Евдоким\n"
                + "    Евлампий\n"
                + "    Евсей\n"
                + "    Евстафий\n"
                + "    Евстигней\n"
                + "    Егор\n"
                + "    Елизар\n"
                + "    Елисей\n"
                + "    Емельян\n"
                + "    Епифан\n"
                + "    Еремей\n"
                + "    Ермил\n"
                + "    Ермолай\n"
                + "    Ерофей\n"
                + "    Ефим\n"
                + "    Ефрем\n"
                + "    Захар\n"
                + "    Зиновий\n"
                + "    Зосима\n"
                + "    Иван\n"
                + "    Игнатий\n"
                + "    Игорь\n"
                + "    Измаил\n"
                + "    Изот\n"
                + "    Изяслав\n"
                + "    Иларион\n"
                + "    Илья\n"
                + "    Иннокентий\n"
                + "    Иосиф (Осип)\n"
                + "    Ипат\n"
                + "    Ипатий\n"
                + "    Ипполит\n"
                + "    Ираклий\n"
                + "    Исай\n"
                + "    Исидор\n"
                + "    Казимир\n"
                + "    Каллистрат\n"
                + "    Капитон\n"
                + "    Карл\n"
                + "    Карп\n"
                + "    Касьян\n"
                + "    Ким\n"
                + "    Кир\n"
                + "    Кирилл\n"
                + "    Клавдий\n"
                + "    Климент\n"
                + "    Клементий\n"
                + "    Клим\n"
                + "    Кондрат\n"
                + "    Кондратий\n"
                + "    Конон\n"
                + "    Константин\n"
                + "    Корнил\n"
                + "    Корней\n"
                + "    Корнилий\n"
                + "    Кузьма\n"
                + "    Куприян\n"
                + "    Лавр\n"
                + "    Лаврентий\n"
                + "    Ладимир\n"
                + "    Ладислав\n"
                + "    Лазарь\n"
                + "    Лев\n"
                + "    Леон\n"
                + "    Леонид\n"
                + "    Леонтий\n"
                + "    Лонгин\n"
                + "    Лука\n"
                + "    Лукьян\n"
                + "    Лучезар\n"
                + "    Любим\n"
                + "    Любомир\n"
                + "    Любосмысл\n"
                + "    Макар\n"
                + "    Максим\n"
                + "    Максимильян\n"
                + "    Мариан\n"
                + "    Марк\n"
                + "    Мартын\n"
                + "    Мартьян\n"
                + "    Матвей\n"
                + "    Мефодий\n"
                + "    Мечислав\n"
                + "    Милан\n"
                + "    Милен\n"
                + "    Милий\n"
                + "    Милован\n"
                + "    Мина\n"
                + "    Мир\n"
                + "    Мирон\n"
                + "    Мирослав\n"
                + "    Митофан\n"
                + "    Михаил\n"
                + "    Михей\n"
                + "    Модест\n"
                + "    Моисей\n"
                + "    Мокей\n"
                + "    Мстислав\n"
                + "    Назар\n"
                + "    Наркис\n"
                + "    Натан\n"
                + "    Наум\n"
                + "    Нестор\n"
                + "    Никандр\n"
                + "    Никанор\n"
                + "    Никита\n"
                + "    Никифор\n"
                + "    Никодим\n"
                + "    Николай\n"
                + "    Никон\n"
                + "    Нифонт\n"
                + "    Олег\n"
                + "    Олимпий\n"
                + "    Онуфрий\n"
                + "    Орест\n"
                + "    Осип (Иосиф)\n"
                + "    Остап\n"
                + "    Остромир\n"
                + "    Павел\n"
                + "    Панкратий\n"
                + "    Панкрат\n"
                + "    Пантелеймон\n"
                + "    Панфил\n"
                + "    Парамон\n"
                + "    Парфен\n"
                + "    Пахом\n"
                + "    Петр\n"
                + "    Пимен\n"
                + "    Платон\n"
                + "    Поликарп\n"
                + "    Порфирий\n"
                + "    Потап\n"
                + "    Пров\n"
                + "    Прокл\n"
                + "    Прокофий\n"
                + "    Прохор\n"
                + "    Радим\n"
                + "    Радислав\n"
                + "    Радован\n"
                + "    Ратибор\n"
                + "    Ратмир\n"
                + "    Родион\n"
                + "    Роман\n"
                + "    Ростислав\n"
                + "    Рубен\n"
                + "    Руслан\n"
                + "    Рюрик\n"
                + "    Савва\n"
                + "    Савватий\n"
                + "    Савелий\n"
                + "    Самсон\n"
                + "    Самуил\n"
                + "    Светозар\n"
                + "    Святополк\n"
                + "    Святослав\n"
                + "    Севастьян\n"
                + "    Селиван\n"
                + "    Селиверст\n"
                + "    Семен\n"
                + "    Серафим\n"
                + "    Сергей\n"
                + "    Сигизмунд\n"
                + "    Сидор\n"
                + "    Сила\n"
                + "    Силантий\n"
                + "    Сильвестр\n"
                + "    Симон\n"
                + "    Сократ\n"
                + "    Соломон\n"
                + "    Софон\n"
                + "    Софрон\n"
                + "    Спартак\n"
                + "    Спиридон\n"
                + "    Станимир\n"
                + "    Станислав\n"
                + "    Степан\n"
                + "    Стоян\n"
                + "    Тарас\n"
                + "    Твердислав\n"
                + "    Творимир\n"
                + "    Терентий\n"
                + "    Тимофей\n"
                + "    Тимур\n"
                + "    Тит\n"
                + "    Тихон\n"
                + "    Трифон\n"
                + "    Трофим\n"
                + "    Ульян\n"
                + "    Устин\n"
                + "    Фадей\n"
                + "    Федор\n"
                + "    Федосий\n"
                + "    Федот\n"
                + "    Феликс\n"
                + "    Феоктист\n"
                + "    Феофан\n"
                + "    Ферапонт\n"
                + "    Филарет\n"
                + "    Филимон\n"
                + "    Филипп\n"
                + "    Фирс\n"
                + "    Флорентин\n"
                + "    Фока\n"
                + "    Фома\n"
                + "    Фортунат\n"
                + "    Фотий\n"
                + "    Фрол\n"
                + "    Харитон\n"
                + "    Харлампий\n"
                + "    Христофор\n"
                + "    Чеслав\n"
                + "    Эдуард\n"
                + "    Эммануил\n"
                + "    Эмиль\n"
                + "    Эраст\n"
                + "    Эрнест\n"
                + "    Эрнст\n"
                + "    Ювеналий\n"
                + "    Юлиан\n"
                + "    Юлий\n"
                + "    Юрий\n"
                + "    Яков\n"
                + "    Ян\n"
                + "    Якуб\n"
                + "    Януарий\n"
                + "    Ярополк\n"
                + "    Ярослав";

        String[] girlNamesArray = girlNames.split("\n");
        String[] boyNamesArray = boyNames.split("\n");
        List<String> boyNamesList = handleNameArray(boyNamesArray, (short) 1, 0);
        List<String> girlNamesList = handleNameArray(girlNamesArray, (short) 2, boyNamesList.size());
        List<String> allNames = new ArrayList<>(boyNamesList.size() + girlNamesList.size());
        allNames.addAll(boyNamesList);
        allNames.addAll(girlNamesList);
        saveNamesToFile(allNames);
    }

    private static List<String> handleNameArray(String[] nameArray, Short sex, int startIndex) throws UnsupportedEncodingException, IOException {
        Set<String> handeledNamesSet = new HashSet<>();
        for (String name : nameArray) {
            String[] handledNameArray = handleName(name);
            if (handledNameArray != null) {
                for (String handledName : handledNameArray) {
                    handeledNamesSet.add(handledName);
                }
            }
        }
        int i = startIndex;
        handeledNamesSet = new TreeSet<>(handeledNamesSet);
        List<String> sqlList = new LinkedList<>();
        for (String name : handeledNamesSet) {
            i++;
            sqlList.add("insert into pmp_patient_names (id,name,sex) values(" + i + ",'" + name + "'," + sex.toString() + ");");
        }
        return sqlList;
    }

    private static void saveNamesToFile(List<String> sqlList) throws IOException {
        StringBuilder sqlToFile = new StringBuilder();
        for (String sql : sqlList) {
            sqlToFile.append(sql).append("\r\n");
        }
        File file = new File("D:\\tmp\\sql.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), sqlToFile.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String[] handleName(String unformattedName) {
        String trim = unformattedName.trim();
        if (trim != null && trim.length() > 1) {
            if (trim.contains("женское имя") || trim.contains("значения")) {
                return null;
            }
            if (trim.contains(" и ")) {
                String[] split = trim.split(" и ");
                return new String[]{split[0].trim(), split[1].trim()};
            }
            Matcher matcher = Pattern.compile("(.+?)\\((.+?)\\)").matcher(trim);
            if (matcher.find()) {
                String group1 = matcher.group(1).trim();
                String group2 = matcher.group(2).trim().replace("(", "").replace(")", "");
                if (!group2.toLowerCase().equals("имя")) {
                    return new String[]{group1, group2};
                } else {
                    if (group2.contains(" и ")) {
                        String[] split = group2.split(" и ");
                        return new String[]{group1, split[0].trim(), split[1].trim()};
                    }
                    return new String[]{group1};
                }
            } else {
                if (trim.contains(" ")) {
                    String[] split = trim.split(" ");
                    List<String> splittedNames = new ArrayList<>(split.length);
                    for (String str : split) {
                        if (str != null && str.trim().length() > 1) {
                            splittedNames.add(str);
                        }
                    }
                    if (!splittedNames.isEmpty()) {
                        return splittedNames.toArray(new String[1]);
                    } else {
                        return null;
                    }
                }
                return new String[]{trim.replace("(", "").replace(")", "")};
            }
        }
        return null;
    }

    private static void testStack() {
        String moduleName = null;
        String cleanStackStr = "ru.ibs.pmp.dao.hibernate.aspect.SyncAspect.interceptSyncDAOHibernateBegining(SyncAspect.java:103)\n"
                + "ru.ibs.pmp.features.impl.IsLockOnMoAtPeriodExistsFeatureImpl.call(IsLockOnMoAtPeriodExistsFeatureImpl.java:23)\n"
                + "ru.ibs.pmp.features.impl.IsLockOnMoAtPeriodExistsFeatureImpl.call(IsLockOnMoAtPeriodExistsFeatureImpl.java:14)\n"
                + "ru.ibs.pmp.features.GetMedicalCasePojoFeature.call(GetMedicalCasePojoFeature.java:46)\n"
                + "ru.ibs.pmp.features.GetMedicalCasePojoFeature.call(GetMedicalCasePojoFeature.java:21)\n"
                + "ru.ibs.pmp.pmp.ws.impl.PmpWsImpl.getHospCaseV2(PmpWsImpl.java:1393)\n"
                + "ru.ibs.pmp.auth.aspect.AuditAspect.execute(AuditAspect.java:93)\n"
                + "ru.ibs.pmp.auth.aspect.AuditAspect.executeSoap(AuditAspect.java:66)";
        if (cleanStackStr.contains("ru.ibs.pmp.pmp.ws.impl.PmpWsImpl")) {
            moduleName = "PmpWsImpl";
            Pattern pmpWsImplPattern = Pattern.compile("ru.ibs.pmp.pmp.ws.impl.PmpWsImpl.(.+?)\\(");
            Matcher pmpWsImplMatcher = pmpWsImplPattern.matcher(cleanStackStr);
            if (pmpWsImplMatcher.find()) {
                String group = pmpWsImplMatcher.group(1);
                moduleName += "." + group;
            }
        }
        System.out.println(moduleName);
    }

    private static String testSAX() throws IOException {
        return new String(Files.readAllBytes(new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate\\src\\test\\resources\\recreate\\services_for_2397.sql").toPath()), "utf-8");
    }

    static class TableStructure {

        private String tableName;
        private List<String> fields = new ArrayList<>();
        private List<String> values = new ArrayList<>();

        public TableStructure(String tableName) {
            this.tableName = tableName;
        }

        public void addField(String field) {
            fields.add(field);
        }

        public void addValue(String value) {
            values.add(value);
        }

        public String representAsSql() {
            if (fields.size() != values.size()) {
                throw new RuntimeException("fields.size()!=values.size()");
            }
            Map<String, Object> objMap = new HashMap<>(fields.size());
            for (int i = 0; i < fields.size(); i++) {
                objMap.put(fields.get(i), values.get(i));
            }
            return composeSql(tableName, objMap);
        }

        protected String composeSql(String tableName, Map<String, Object> objMap) {
            List<String> columns = new ArrayList<String>(objMap.size());
            List<Object> values = new ArrayList<Object>(objMap.size());
            for (Map.Entry<String, Object> entry : objMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                columns.add(key);
                values.add(value);
            }
            StringBuilder sb = new StringBuilder("insert into " + tableName + " (");
            for (int i = 0; i < objMap.size(); i++) {
                sb.append(columns.get(i));
                if (i < objMap.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(") values(");
            for (int i = 0; i < objMap.size(); i++) {
                sb.append(getValueAsString(values.get(i)));
                if (i < objMap.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(");");
            return sb.toString();
        }

        protected String getValueAsString(Object value) {
            if (value != null && value instanceof Date) {
                return getDateAsString((Date) value);
            } else if (value != null && value instanceof GregorianCalendar) {
                return getDateAsString(((GregorianCalendar) value).getTime());
            } else if (value != null && value instanceof Number) {
                return "" + value + "";
            } else if (value != null && value instanceof Boolean) {
                return "" + (Boolean.valueOf(value.toString()) ? "1" : "0") + "";
            } else if (value != null) {
                return "'" + value + "'";
            } else {
                return "null";
            }
        }
        private static final boolean ORACLE = false;

        private String getDateAsString(Date date) {
            // to_timestamp('07.03.17 18:07:02,439000000','DD.MM.RR HH24:MI:SSXFF')
            if (ORACLE) {
                return "to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "','yyyy-MM-dd HH24:mi:ss')";
            } else {
                return "to_timestamp('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "','yyyy-MM-dd HH24:mi:ss')";
            }
        }
    }

    static class IntWrapper {

        private int counter = 0;
        private List<TableStructure> tableStructureList = new ArrayList<>();

        public int getCounter() {
            return counter;
        }

        public void inc() {
            counter++;
        }

        public void dec() {
            counter--;
        }

        public void addTableStructure(String tableName) {
            tableStructureList.add(new TableStructure(tableName));
        }

        public void addField(String field) {
            tableStructureList.get(tableStructureList.size() - 1).addField(field);
        }

        public void addValue(String value) {
            tableStructureList.get(tableStructureList.size() - 1).addValue(value);
        }

        public String representAsSql() {
            StringBuilder sb = new StringBuilder();
            for (TableStructure tableStructure : tableStructureList) {
                sb.append(tableStructure.representAsSql()).append("\n");
            }
            return sb.toString();
        }
    }

    private static String representXmlAsSql(String xml) throws SAXException, ParserConfigurationException, IOException {
        final IntWrapper counter = new IntWrapper();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = createHandler(counter);
        saxParser.parse(new ByteArrayInputStream(xml.replace("\r", "").replace("\n", "").getBytes()), handler);
        String sql = counter.representAsSql();
        return sql;
    }

    private static DefaultHandler createHandler(final IntWrapper counter) {
        DefaultHandler handler = new DefaultHandler() {

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                counter.inc();
                System.out.println("Start Element :" + qName);
                if (counter.getCounter() == 2) {
                    counter.addTableStructure(qName);
                }
                if (counter.getCounter() == 3) {
                    counter.addField(qName);
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                String value = new String(ch, start, length);
                if (value != null && value.startsWith("%") && value.substring(value.length() - 3, value.length() - 2).equals("%")) {
                    try {
                        value = URLDecoder.decode(value, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex);
                    }
                }
                System.out.println("Value : " + value);
                counter.addValue(value);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                counter.dec();
            }

        };
        return handler;
    }

    private static String representXmlAsSql2(String xml) throws SAXException {
        final IntWrapper counter = new IntWrapper();
        DefaultHandler handler = createHandler(counter);
        String parseXml = parseXml(xml, handler);
        String sql = counter.representAsSql();
        return sql;
    }

    private static String parseXml(String xml, DefaultHandler handler) throws SAXException {
        Matcher matcher = xmlPattern.matcher(xml);
        while (matcher.matches() && xml.replace("\n", "").replace("\r", "").length() > 0) {
            String tag = xml.substring(xml.indexOf("<") + 1, xml.indexOf(">"));
            String openTag = "<" + tag + ">";
            String closeTag = "</" + tag + ">";
            handler.startElement(null, null, tag, null);
            String value = xml.substring(xml.lastIndexOf(openTag) + openTag.length(), xml.lastIndexOf(closeTag));
//            depth++;
            System.out.println("openTag=" + openTag);
            String representXmlAsSql2 = parseXml(value, handler);
            handler.endElement(null, null, null);
//            return xml.substring(representXmlAsSql2.length(), xml.length());
//            xml = xml.substring(openTag.length() + representXmlAsSql2.length() + closeTag.length(), xml.length());
            xml = xml.substring(xml.indexOf(closeTag) + closeTag.length(), xml.length());
        }
//      else {
//            depth--;
        if (xml.replace("\n", "").replace("\r", "").length() > 0) {
            handler.characters(xml.toCharArray(), 0, xml.length());
        }
        return xml;
//        }
    }
    private static final Pattern xmlPattern = Pattern.compile("^.*<.+?>.*</.+?>.*$", Pattern.DOTALL);

    private static boolean someCheck(String STAC_NOMLPU, String STAC_CODWDR) {
//        boolean b = (STAC_NOMLPU.equals("s") && StringUtils.isNotBlank(STAC_CODWDR))
//                || (STAC_NOMLPU.equals("s") && !"s".equals(STAC_CODWDR))
//                || (STAC_NOMLPU.equals("p") && StringUtils.isNotBlank(STAC_CODWDR))
//                || (STAC_NOMLPU.equals("b") && !"b".equals(STAC_CODWDR));
//
//        boolean b = (STAC_NOMLPU.equals("s") && StringUtils.isNotBlank(STAC_CODWDR) && STAC_CODWDR.equals("b"))
//                || (STAC_NOMLPU.equals("p") && StringUtils.isNotBlank(STAC_CODWDR) && (STAC_CODWDR.equals("s") || STAC_CODWDR.equals("b")))
//                || (STAC_NOMLPU.equals("b") && (StringUtils.isBlank(STAC_CODWDR) || STAC_CODWDR.equals("s")));
//
        boolean b = ("s".equals(STAC_NOMLPU) && "s".equals(STAC_CODWDR))
                || ("s".equals(STAC_NOMLPU) && "b".equals(STAC_CODWDR))
                || ("p".equals(STAC_NOMLPU) && StringUtils.isBlank(STAC_CODWDR))
                || ("b".equals(STAC_NOMLPU) && "b".equals(STAC_CODWDR));

        return !b;
    }

}
