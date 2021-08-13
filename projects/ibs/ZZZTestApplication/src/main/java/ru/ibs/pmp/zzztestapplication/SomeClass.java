/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.zzztestapplication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.sun.jna.Library;
import com.sun.jna.Native;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlOptions;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.joda.time.LocalTime;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.mozilla.universalchardet.UniversalDetector;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.ibs.pmp.zzztestapplication.bean.BillStatisticsShortBean;
import ru.ibs.pmp.zzztestapplication.bean.CommitBean;
import ru.ibs.pmp.zzztestapplication.bean.SomeBean1;
import ru.ibs.pmp.zzztestapplication.bean.SomeBean2;
import ru.ibs.pmp.zzztestapplication.bean.TestBean;
import ru.ibs.pmp.zzztestapplication.threads.ConnectionMonitorDaemon;
import ru.ibs.pmp.zzztestapplication.threads.TreadTest;
import ru.ibs.pmp.zzztestapplication.threads.bean.MonitorBean;

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
//        testThreads();
//        TreadTest2.testService();
//        handleHttpResponseString("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:getPersonsInfoResponse xmlns:ns2=\"http://erzl.org/services\"><ns2:getPersonsInfoRequest><ns2:client><ns2:orgCode>-1</ns2:orgCode><ns2:bpCode>1</ns2:bpCode><ns2:system>PUMP</ns2:system><ns2:user>mgms</ns2:user><ns2:password>ibs</ns2:password></ns2:client><ns2:ukl>111333</ns2:ukl><ns2:ukl>222777</ns2:ukl><ns2:ukl>25080285</ns2:ukl><ns2:ukl>30976035</ns2:ukl><ns2:date>2017-11-30</ns2:date></ns2:getPersonsInfoRequest><ns2:totalResults>0</ns2:totalResults></ns2:getPersonsInfoResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>");
//        testPattern();
//        testSemaphore();
//        testMapReduce();
//        String pidForAix = getPidForAix(args);
//        String env = System.getenv(args[0]);
//        int pid = CLibrary.INSTANCE.getpid();
//        System.out.println("PID: "+pid + "");
//        Thread.sleep(100 * 1000);
//        String tableName = getTableName();
//        String formatTableName = formatTableName(tableName);
//        testFilePattern();
//        cut("hg12vjehvjkdabkjbnok12jnkoenoi1j777778888899999944444444444444444440000000000000", 30);
//        createQuery();
//        new SomeClass().testInvoiceModelHandling();
//        new PdfTest().createSomePdf2();
//        new PdfTest().createSomePdf();
//        SkypeTest.testSkype();
//        new SkypeTest2().testSkype();
//        SkypeTest3.testSkype();
//        new SomeTestClass().testA();
//        SomeTestClass2.test();
//        bigDecimalTest();
//        bigDecimalTest2("1.274");
//        bigDecimalTest2("1.275");
//        testCursors();
//        System.out.println(LongestWord("d aads qwertyuio asd asd oiuytrewq ff"));
//        System.out.println(LongestWord("d aads qwertyuio asd asd oiuytrewq ff"));
//        System.out.println(Palindrome("eye g  eye "));
//        System.out.println(StringScramble("he3llko", "hello"));
//        System.out.println(CoinDeterminer(250));
//        System.out.println(GasStation(new String[]{"4", "1:1", "2:2", "1:2", "0:1"}));
//        System.out.println(GasStation(new String[] {"5","0:1","2:1","3:2","4:6","4:3"}));
//        testJoinString("XXX, YYY, KKK, DDD");
//        testJoinString("XXX, YYY");
//        testJoinString("XXX");
//        testConversion();
//        appendFile("Hello!");
//        appendFile("\nWorld!");
//        checkPattern();
//        checkStrings();
//        checkDate();
//        checkArchiveFiles("D:\\GIT\\pmp\\pmp\\build\\target\\pmp-dist-all\\modules\\module-lpu-registry-pmp.war", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-12-01 12:54:32"));
//        checkArchiveFiles("D:\\GIT\\pmp\\pmp\\build\\target\\pmp-dist-all\\modules\\module-pmp.war", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-12-01 12:54:32"));
//        Set<String> set = new HashSet<>();
//        IntStream.rangeClosed('A', 'Z').forEach(c -> set.add(Character.toString((char) c)));
//        System.out.println(set);
//        logEntity("1234567890", 4);
//        testBillStatisticsShortBean();
//        testDigitPattern();
//        Semaphore semaphore = new Semaphore(0);
//        semaphore.acquire();
//        testPatternForMemory();
//        OrganizationsThatDidNotSendBills.getReport();
//        testDateSort();
//        System.out.println(getMonthsDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-05-05 22:14:44"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-08-08 20:20:20")));
//        staticTest();
//        testAtomicReference();
//        getCertificateDateEnd(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-05-15 00:00:00"));
//        getCertificateDateEnd(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-03-15 00:00:00"));
//        System.out.println(getMonthsDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-05-28 00:00:00"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-08-15 00:00:00")));
//        System.out.println("1234567890".substring(3, 6));
//        new TransparentWatermark().create();
//        new TransparentWatermark2().create();
//        new TransparentWatermark3().create();
//        testDateTrunc();
//        testDateTrunc2(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-12 20:30:32"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-10 12:12:12"));
//        parseResponseFromPhpServer();
//        encodeDecodeTest();
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-system-auth-api/src/main/java/ru/ibs/pmp/auth/filters/SecurityContextFilter.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp_core/pmp-common-min/src/main/java/ru/ibs/pmp/auth/model/Glue.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-persons-api/src/main/java/ru/ibs/pmp/api/patients/model/PatientPolicy.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-persons-api/src/main/java/ru/ibs/pmp/api/patients/model/PatientDocument.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-pmp-api/src/main/java/ru/ibs/pmp/service/SimpleServiceService.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-pmp/src/main/java/ru/ibs/pmp/dao/hibernate/HospDeptStayDAOHibernate.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-pmp/src/main/java/ru/ibs/pmp/service/impl/SimpleServiceServiceImpl.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-pmp-api/src/main/java/ru/ibs/pmp/service/utils/pdf/PdfUtils.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp_core/pmp-common-min/src/main/java/ru/ibs/pmp/auth/model/SmoEntity.java");
//        fixStringEndings2("/home/me/GIT/pmp/pmp/module-pmp-api/src/main/java/ru/ibs/pmp/service/utils/pdf/CellBuilder.java");
//        getAllBadFiles();
//        testSortingMtrReestrFileStatus();
//        practionerNamePatternTest("Аль Баварид", "(^\\s*([А-ЯЁ][а-яё]*)([- `''.][А-ЯЁ][а-яё]*)*\\s*)$");
//        practionerNamePatternTest("Омар", "(^\\s*([А-ЯЁ][а-яё]*)([- `''.][А-ЯЁ][а-яё]*)*\\s*)$");
//        practionerNamePatternTest("Абед Аль Хафез Мофлех", "^$|(^\\s*([А-ЯЁ][а-яё]*)([- `''.][А-ЯЁ][а-яё]*)*\\s*)$|^$");
//        SpringTest.testRestTemplate();
//        UniqueStringTest.test();
//        String get = OPLATA_DESCRIPTION.get(null);
//        System.out.println("get = " + get);
//        urlAnalisys("http://10.170.89.2/123456789–0–C18.1–037061–20200125–01–20200303");
//        urlAnalisys("http://10.170.89.2/123456789–0–C18.14–037061–20200125–01–20200303");
//        urlAnalisys("http://10.170.89.2/123456789–0–C18–037061–20200125–01–20200303");
//        urlAnalisys("http://10.170.89.2/123456789–0–C18.158–037061–20200125–01–20200303");
//        urlAnalisys("http://10.170.89.2/ggh@#56/123456789–0–C18.18–037061–20200125–01–20200303");
//        urlAnalisys("http://10.170.89.2/ggh@#56/123456789–0–C18.18–037061–20200125–01");
//        urlAnalisys("http://10.170.89.2/ggh@#56/123456789–0–C18.18–037061–20200125");
//        urlAnalisys("http://10.170.89.2/ggh@#56/123456789–0–C18.18–037061–20200125–20200303");
//        urlAnalisys("http://127.0.0.5/2C00BB94-9D3F-4CFB-B740-CC9549C5A551/12345–2–C01.01–12345–20200112–01");
//        urlAnalisys("https://cf.mosmedzdrav.ru/documentService/v1/getHtml?url=16417293-0-C61-60075-20201202-01-20201204");
//        urlAnalisys("http://127.0.0.8/getHtml?url=16417293-0-C61-60075-20201202-01-20201204");
//        urlAnalisys("https://cf.mosmedzdrav.ru/documentService/v1/getHtml?url=7149689-0-A00-001781-20210101-01-20210101");
//        System.out.println(getUrlInfo("https://cf.mosmedzdrav.ru/documentService/v1/getHtml?url=19790806-0-C43.5-60075-20201201-01-20201201"));
//        urlAnalisys("https://cf.mosmedzdrav.ru/documentService/v1/getHtml?url=19132168-0-C25.0-60074-20210415-01-20210415");
//        urlAnalisys("https://cf.mosmedzdrav.ru/documentService/v1/getHtml?url=19132168-0-C25.0-60074-20210415-01-20210415");
//        barcodeTest();
//        Date truncatedDate = DateUtils.truncate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-04 00:00:00"), Calendar.DAY_OF_MONTH);
//        System.out.println(truncatedDate);
//        String str = "qwertyuiop2020111100";
//        System.out.println(new SimpleDateFormat("yyyyMMdd").parse(str.substring(str.length() - 10, str.length() - 2)).toString());
//        System.out.println(checkEN3(d("2020-10-02"), d("2020-10-04"), "qwertyuiop2020111100", false));
//        System.out.println(checkEN3(d("2020-10-04"), d("2020-10-04"), "qwertyuiop20201018xx", true));
//        System.out.println(checkEN3(d("2020-10-04"), d("2020-10-04"), "qwertyuiopxx", true));
//        System.out.println(checkEN3(null, d("2020-10-04"), "qwertyuiopxx", true));
//        priorityQueueTest();
//        testOutOfMemory();
//        boolean contains = new HashSet<>(Arrays.asList("1", "2", "3")).contains(null);
//        System.out.println("contains = " + contains);
//        BillSum.analyze();
//        System.out.println("1234567890".substring(0, 3));
//        countFiles();
//        testDate();
//        fixFileNames();
//        testCheckCountNumber(null);
//        testCheckCountNumber(0);
//        testCheckCountNumber(1);
//        testCheckCountNumber(2);
//        String gg = "select ID,PERIOD,MO_ID,CASE_TYPE,URL_POSITION_TYPE,URL_TABLE_NAME,URL_COLUMN_NAME,"
//                + "URL_ID,URL_TEXT,SERVICE_CODE,DIAGNOSIS_CODE,SERVICE_DATE,PATIENT_ID,PATIENT_TYPE,PRIM_CHECK_DATE,PRIM_CHECK_ERROR,PRIM_CHECK_KSUM,LAST_CHECK_DATE,"
//                + "LAST_CHECK_ERROR,LAST_CHECK_KSUM,CHECK_COUNT_NUMBER from pmp_medcase_url_validation"
//                + "\n";
//        System.out.println(gg.toLowerCase());
//        threadTest();
//        System.out.println(Boolean.valueOf("TRUE") + " " + Boolean.valueOf("FALSE"));
//        mkdirsHandler(new File("/home/me/zzzDel"));
//        waitSomeTime();
//        lpuList();
//        iterableTest(IntStream.rangeClosed(1, 4000).asLongStream().mapToObj(i -> i).collect(Collectors.toList()));
//        String sql = "SELECT medicalcas0_.id AS id1_74_0_, medicalcas0_.AMOUNT_ERR_FLK_BILL AS AMOUNT_ERR_FLK_BIL2_74_0_, medicalcas0_.PATIENT_BIRTH_DATE AS PATIENT_BIRTH_DATE3_74_0_, medicalcas0_.AMOUNT AS AMOUNT4_74_0_, medicalcas0_.AMOUNT_ERR AS AMOUNT_ERR5_74_0_, medicalcas0_.case_date AS case_date6_74_0_, medicalcas0_.case_number AS case_number7_74_0_, medicalcas0_.case_type AS case_type8_74_0_, medicalcas0_.CHANGE_DATE AS CHANGE_DATE9_74_0_, medicalcas0_.changed AS changed10_74_0_, medicalcas0_.cure_result AS cure_result11_74_0_, medicalcas0_.DIAGNOSIS AS DIAGNOSIS12_74_0_, medicalcas0_.DIRECTION_COMPOSITE_NUMBER AS DIRECTION_COMPOSI13_74_0_, medicalcas0_.direction_date AS direction_date14_74_0_, medicalcas0_.DIRECTION_DIAGNOSIS AS DIRECTION_DIAGNOS15_74_0_, medicalcas0_.direction_doctor_spec AS direction_doctor_16_74_0_, medicalcas0_.DIRECTION_GOAL AS DIRECTION_GOAL17_74_0_, medicalcas0_.DIRECTION_LPU_ID AS DIRECTION_LPU_ID18_74_0_, medicalcas0_.DIRECTION_LPU_RF_ID AS DIRECTION_LPU_RF_19_74_0_, medicalcas0_.direction_number AS direction_number20_74_0_, medicalcas0_.DISEASE_OUTCOME AS DISEASE_OUTCOME21_74_0_, medicalcas0_.doctor_job_id AS doctor_job_id22_74_0_, medicalcas0_.DS_ONK AS DS_ONK23_74_0_, medicalcas0_.HOSPITALIZATION_TYPE AS HOSPITALIZATION_T24_74_0_, medicalcas0_.injury AS injury25_74_0_, medicalcas0_.lpu_id AS lpu_id26_74_0_, medicalcas0_.n_card AS n_card27_74_0_, medicalcas0_.mo_id AS mo_id28_74_0_, medicalcas0_.NEWBORN_WEIGHT_GR AS NEWBORN_WEIGHT_GR29_74_0_, medicalcas0_.nurse_job_id AS nurse_job_id30_74_0_, medicalcas0_.patient_id AS patient_id31_74_0_, medicalcas0_.PATIENT_TYPE AS PATIENT_TYPE32_74_0_, medicalcas0_.payment_source AS payment_source33_74_0_, medicalcas0_.PERIOD AS PERIOD34_74_0_, medicalcas0_.CNT_PARCEL_S AS CNT_PARCEL_S35_74_0_, medicalcas0_.PATIENT_SEX AS PATIENT_SEX36_74_0_, medicalcas0_.special_case AS special_case37_74_0_, medicalcas0_.status AS status38_74_0_, medicalcas0_.STATUS_CHANGE_DATE AS STATUS_CHANGE_DAT39_74_0_, medicalcas0_.system_source AS system_source40_74_0_, medicalcas0_.DIRECTION_TARGET_DOCTOR_SPEC AS DIRECTION_TARGET_41_74_0_, medicalcas0_.USER_UNIQUEID AS USER_UNIQUEID42_74_0_, medicalcas0_1_.ARRIVAL_DATE AS ARRIVAL_DATE1_125_0_, medicalcas0_1_.CALL_TYPE AS CALL_TYPE2_125_0_, medicalcas0_1_.DOCTOR_JOB_ID2 AS DOCTOR_JOB_ID3_125_0_, medicalcas0_1_.DOCTOR_JOB_ID3 AS DOCTOR_JOB_ID4_125_0_, medicalcas0_1_.N_CARD AS N_CARD5_125_0_, medicalcas0_1_.MEDICAL_CASE_ID AS MEDICAL_CASE_ID6_125_0_, medicalcas0_1_.RECID AS RECID7_125_0_, medicalcas0_1_.service_code AS service_code8_125_0_, medicalcas0_1_.mo_id AS mo_id9_125_0_, medicalcas0_1_.PERIOD AS PERIOD10_125_0_, medicalcas0_1_.TEAM_NAME AS TEAM_NAME11_125_0_, medicalcas0_1_.TEAM_NUMBER AS TEAM_NUMBER12_125_0_, medicalcas0_1_.TEAM_PROFILE AS TEAM_PROFILE13_125_0_, medicalcas0_2_.service_place AS service_place1_129_0_, medicalcas0_2_.mo_id AS mo_id2_129_0_, medicalcas0_2_.PERIOD AS PERIOD3_129_0_, medicalcas0_2_.TREATMENT_FINISH AS TREATMENT_FINISH4_129_0_, medicalcas0_2_.TREATMENT_OBJECTIVE AS TREATMENT_OBJECTIV5_129_0_, medicalcas0_2_.TREATMENT_PRIMARY AS TREATMENT_PRIMARY6_129_0_, medicalcas0_2_.visit_purpose AS visit_purpose7_129_0_, medicalcas0_2_.VISIT_PURPOSE_FFOMS AS VISIT_PURPOSE_FFOM8_129_0_, medicalcas0_3_.CURE_END_DATE AS CURE_END_DATE1_44_0_, medicalcas0_3_.CURE_START_DATE AS CURE_START_DATE2_44_0_, medicalcas0_3_.HOSP_ANNUAL_TYPE AS HOSP_ANNUAL_TYPE3_44_0_, medicalcas0_3_.mo_id AS mo_id4_44_0_, medicalcas0_3_.PERIOD AS PERIOD5_44_0_, medicalcas0_3_.INTERRAPT_MS AS INTERRAPT_MS6_44_0_, medicalcas0_3_.MEDICAL_CASE_ID AS MEDICAL_CASE_ID7_44_0_, medicalcas0_3_.VMP_DATE AS VMP_DATE8_44_0_, medicalcas0_3_.VMP_NUMBER AS VMP_NUMBER9_44_0_, CASE WHEN medicalcas0_1_.MEDICAL_CASE_ID IS NOT NULL THEN 1 WHEN medicalcas0_2_.MEDICAL_CASE_ID IS NOT NULL THEN 2 WHEN medicalcas0_3_.MEDICAL_CASE_ID IS NOT NULL THEN 3 WHEN medicalcas0_.id IS NOT NULL THEN 0 END AS clazz_0_ FROM PMP_MEDICAL_CASE medicalcas0_ LEFT OUTER JOIN PMP_SMP_CASE medicalcas0_1_ ON medicalcas0_.id = medicalcas0_1_.MEDICAL_CASE_ID and medicalcas0_.mo_id = medicalcas0_1_.mo_id and medicalcas0_.period = medicalcas0_1_.period LEFT OUTER JOIN PMP_TAP_INFO medicalcas0_2_ ON medicalcas0_.id = medicalcas0_2_.MEDICAL_CASE_ID and medicalcas0_.mo_id = medicalcas0_2_.mo_id and medicalcas0_.period = medicalcas0_2_.period LEFT OUTER JOIN PMP_HOSP_CASE medicalcas0_3_ ON medicalcas0_.id = medicalcas0_3_.MEDICAL_CASE_ID and medicalcas0_.mo_id = medicalcas0_3_.mo_id and medicalcas0_.period = medicalcas0_3_.period WHERE medicalcas0_.id = ? AND medicalcas0_.mo_id = ? AND medicalcas0_.PERIOD = ?";
//        addHints(sql);
//        System.out.println("1234567".substring(1, 3));
//        System.out.println(lpad(1234L));
//        System.out.println(lpad(12345L));
//        System.out.println(lpad(123456L));
//        System.out.println("1234567".substring(0, 3));
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getTruncatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-18 22:44:22"))));
//        System.out.println(getLpuOrdForJoining(774522L));
//        System.out.println(getLpuOrdForJoining(1876L));
//        System.out.println("checkForBugsInFiles started!");
//        checkForBugsInFiles("/home/me/VMwareShared/stage6AskInsuranceStatusResponse_4708_2021_02_2");
//        checkForBugsInFiles("/home/me/VMwareShared/stage6AskInsuranceStatusResponse_4708_2021_02_510650200_test");
//        checkForBugsInFiles("/home/me/VMwareShared/serializedObjects_4708");
//        System.out.println("checkForBugsInFiles finished!");
//        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
//        List<Integer> subList = list.subList(0, list.size());
//        List<Integer> subList2 = list.subList(0, list.size() - 1);
//        handleLpuList();
//        checkException();
//        logMap("eeeeeeeeeeffffffffffyyyyyyyyyykkkkkkkkkkuuuuuuuuuuiiii");
//        logMap("eeeeeeeeeeffffffffffyyyyyyyyyykkkkkkkkkkuuuuuuuuuu");
//        logMap("eeeeeeeeee\nffffffffff\nyyyyyyyyyy\nkkkkkkkkkk\nuuuuuuuuuu");
//        logMap("eeeeeeeeee\n\nffffffffff\n\nyyyyyyyyyy\n\nkkkkkkkkkk\n\nuuuuuuuuuu");
//        Integer maxPoolSize = 10;
//        ArrayBlockingQueue<FutureTask> mainQueue = new ArrayBlockingQueue<FutureTask>(maxPoolSize);
//        List<Thread> threadList = createThreadList(maxPoolSize, mainQueue);
//        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(() -> true);
//        mainQueue.add(futureTask);
//        System.out.println(futureTask.get());
//        System.out.println(checkPrimVolumeValue(BigDecimal.ZERO).equals(checkPrimVolumeValue(BigDecimal.valueOf(0.0))));
//        fixJsonFiles();
//        lookupForBadStringsInJsonFiles();
//        futureExample();
//        ActEkmpReportFileExporter2Test.test();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        merge(new FileInputStream("/home/me/Downloads/act_ecmp2.docx"), Arrays.asList(new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx"), new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx"), new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx"), new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx"), new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx"), new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx"), new FileInputStream("/home/me/Downloads/act_ecmp2_2.docx")), byteArrayOutputStream);
//        File reportFile = new File("/home/me/tmp/reportsPdf/report.docx");
//        if (reportFile.exists()) {
//            reportFile.delete();
//        }
//        Files.write(reportFile.toPath(), byteArrayOutputStream.toByteArray(), StandardOpenOption.CREATE_NEW);
//        System.out.println(Optional.ofNullable("Hello my sweetty girl!").filter(description -> description.length() > 0).map(description -> description.split(" ")).filter(array -> array.length > 0).map(array -> array[0]).orElse(null));
//        System.out.println(Optional.ofNullable("Hello").filter(description -> description.length() > 0).map(description -> description.split(" ")).filter(array -> array.length > 0).map(array -> array[0]).orElse(null));
//        testJsonParsing();
//        System.out.println("Digit = " + getUnidentPersonPetitionsInfoWrapper(4));
//        getPeriodList(new Date()).forEach(date -> System.out.println(date));
//        Date truncatedDate = getTruncatedDate(new Date());
//        Date endDate = getEndDate(truncatedDate);
//        System.out.println(truncatedDate);
//        System.out.println(endDate);
        dateToString();
    }

    private static void dateToString() {
//    2021-03-25T13:16:53.163+03:00
        Date now = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(now) + "T" + new SimpleDateFormat("HH:mm:ss.SSSZ").format(now);
        dateString = dateString.substring(0, dateString.length() - 2) + ":" + dateString.substring(dateString.length() - 2, dateString.length());
        System.out.println("2021-03-25T13:16:53.163+03:00");
        System.out.println(dateString);
    }

    private static List<java.sql.Date> getPeriodList(Date date) {
        Date truncatedDate = DateUtils.truncate(date, Calendar.MONTH);
        return Arrays.asList(toDate(truncatedDate), toDate(DateUtils.addMonths(truncatedDate, 1)), toDate(DateUtils.addMonths(truncatedDate, -1)));
    }

    private static java.sql.Date toDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    private static final int ATTEMPTS_COUNT2 = 2;
    private static final int TIME_TO_WAIT = 10 * 1000;

    private static int someTestDigitState = 0;

    private static int getUnidentPersonPetitionsInfoWrapper(int someDigit) {
        someTestDigitState = someDigit;
        Exception lastException = null;
        for (int i = 0; i < ATTEMPTS_COUNT2; i++) {
            try {
                if (someTestDigitState > i) {
                    throw new RuntimeException("someTestDigitState > i!");
                } else {
                    return someDigit + 10;
                }
            } catch (Exception e) {
                System.out.println("getUnidentPersonPetitionsInfoWrapper Exception!");
                lastException = e;
                try {
                    Thread.sleep(TIME_TO_WAIT);
                } catch (InterruptedException ex) {
                    System.out.println("getUnidentPersonPetitionsInfoWrapper InterruptedException!");
                }
                continue;
            }
        }
        throw new RuntimeException(lastException);
    }

    private static final String jsonCode = "\"code\":";
    private static final String jsonMessage = "\"message\":";

    private static void testJsonParsing() {
        String json = "{\"result\":\"FAIL\",\"code\":\"010\",\"message\":\"Сущность не найдена, url = {17247114-0-C15.8-56080-20210423-01-20210423}\",\"details\":[]}";
        if (json.contains("\"code\":") && json.contains("\"message\":")) {
            String codeValue = extractValue(json, jsonCode);
            String messageValue = extractValue(json, jsonMessage);
            System.out.println(codeValue + " " + messageValue);
        }
    }

    private static String extractValue(String json, String key) {
        int jsonCodeIndex = json.indexOf(key);
        String codeValue = json.substring(jsonCodeIndex + key.length() + 1, json.indexOf("\"", jsonCodeIndex + key.length() + 1));
        return codeValue;
    }

    private static void merge(InputStream src1, List<InputStream> src2List, OutputStream dest) throws Exception {
        OPCPackage src1Package = OPCPackage.open(src1);
        XWPFDocument src1Document = new XWPFDocument(src1Package);
        XWPFParagraph paragraph_ = src1Document.createParagraph();
        paragraph_.setPageBreak(true);
        CTBody src1Body = src1Document.getDocument().getBody();
        for (int i = 0; i < src2List.size(); i++) {
            InputStream src2 = src2List.get(i);
            OPCPackage src2Package = OPCPackage.open(src2);
            XWPFDocument src2Document = new XWPFDocument(src2Package);
            if (i < src2List.size() - 1) {
                XWPFParagraph paragraph = src2Document.createParagraph();
                paragraph.setPageBreak(true);
            }
            CTBody src2Body = src2Document.getDocument().getBody();
            appendBody(src1Body, src2Body);
        }
        src1Document.write(dest);
    }

    private static void appendBody(CTBody src, CTBody append) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String appendString = append.xmlText(optionsOuter);
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
        src.set(makeBody);
    }

    private static void futureExample() {
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Это не надо! Он должен быть @Autowired!
        List<Future<Long>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Future<Long> submit = executorService.submit(() -> { // Тут что-то делаем! Делаем какие-то select'ы прочее!
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
                }
                return 10L;
            });
            results.add(submit); // Запихиваем результаты от отдельных потоков!
        }
        Long reduce = results.stream().map(future -> { // Ждём эти потоки!
            try {
                return future.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).reduce(0L, (d1, d2) -> d1 + d2); // Что-то делаем с этими результами!
        executorService.shutdown(); // Это тоже не надо делать!
        System.out.println(reduce);
    }

    private <T> T deSerializeObjectFromJson(String str, TypeReference<T> ref) throws IOException {
        return new ObjectMapper().readValue(str, ref);
    }

    private static void lookupForBadStringsInJsonFiles() {
        Arrays.stream(new File("/home/me/GIT/pmp/pmp/module-pmp-bill-recreate/src/test/resources/RecreateCheckUtil").listFiles()).filter(file -> file.getName().startsWith("ResultModels_")).forEach(file -> {
            try {
                String string = new String(Files.readAllBytes(file.toPath()));
                int indexOf = string.indexOf("identifier");
                System.out.println(indexOf + " : " + file.getName());
            } catch (IOException ex) {
                Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static void fixJsonFiles() {
        Arrays.stream(new File("/home/me/GIT/pmp/pmp/module-pmp-bill-recreate/src/test/resources/RecreateCheckUtil").listFiles()).filter(file -> file.getName().startsWith("ResultModels_")).forEach(file -> {
            try {
                String string = new String(Files.readAllBytes(file.toPath()));
                String string2 = string.replace(",\"identifier\":null", "").replace(",\"caseDatePrintable\":\"\"", ",\"caseDatePrintable\":null")
                        .replace(",\"primaryDiagnosisCode\":null", "").replace(",\"secondaryDiagnosisCode\":null", "").replace(",\"itASpecialService\":false", "")
                        .replace(",\"itASpecialService\":true", "");
                Files.write(file.toPath(), string2.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static BigDecimal checkPrimVolumeValue(BigDecimal primVolumeValue) {
        if (primVolumeValue != null) {
            return primVolumeValue.longValue() > 0 ? primVolumeValue : null;
        } else {
            return primVolumeValue;
        }
    }

    private static List<Thread> createThreadList(int threadsCount, final ArrayBlockingQueue<FutureTask> queue) {
        List<Thread> threadList = IntStream.range(0, threadsCount).mapToObj(i -> new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        FutureTask take = queue.take();
                        take.run();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }).collect(Collectors.toList());
        threadList.forEach(thread -> thread.start());
        return threadList;
    }

    static final int MAX_STRING_SIZE = 10;
    static final String NEW_LINE = "\n";
    static final String NEW_DOUBLE_LINE = "\n\n";

    private static void logMap(String details) {
        if (details.length() <= MAX_STRING_SIZE) {
//            AuditEntry auditEntry = AuditUtils.createSmoSyncAuditEntry(exception, details, smoRequest, smoRequest.getUser(), smoRequest.getSmoId(), getLocalIP(), time, false);
//            return Arrays.asList(auditEntry);
        } else {
            List<String> splittedString = Arrays.asList(details.split(NEW_DOUBLE_LINE));
            if (splittedString.stream().anyMatch(str -> str.length() > MAX_STRING_SIZE)) {
                splittedString = Arrays.asList(details.split(NEW_LINE));
                if (splittedString.stream().anyMatch(str -> str.length() > MAX_STRING_SIZE)) {
                    splittedString = new ArrayList<>();
                    for (int i = 0; i < (details.length() / MAX_STRING_SIZE) + (details.length() % MAX_STRING_SIZE > 0 ? 1 : 0); i++) {
                        splittedString.add(details.substring(i * MAX_STRING_SIZE, Math.min((i + 1) * MAX_STRING_SIZE, details.length())));
                    }
                }
            }
//            List<AuditEntry> list = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<String> iterator = splittedString.iterator();
            int i = 0;
            int length = 0;
            while (iterator.hasNext()) {
                String str = iterator.next();
                if (length + str.length() + NEW_LINE.length() <= MAX_STRING_SIZE) {
                    length += str.length();
                    length += NEW_LINE.length();
                    stringBuilder.append(str + NEW_LINE);
                } else {
//                    AuditEntry auditEntry = AuditUtils.createSmoSyncAuditEntry(exception, details, smoRequest, smoRequest.getUser(), smoRequest.getSmoId(), getLocalIP(), time, false);
//                    list.add(auditEntry);
                    System.out.println(stringBuilder.toString());
                    stringBuilder = new StringBuilder(str);
                    length = str.length();
                    i++;
                }
            }
            System.out.println(stringBuilder.toString());
//            AuditEntry auditEntry = AuditUtils.createSmoSyncAuditEntry(exception, details, smoRequest, smoRequest.getUser(), smoRequest.getSmoId(), getLocalIP(), time, false);
//            list.add(auditEntry);
//            return list;
        }
    }

    private static void checkException() {
        String exceptionStackTrace = "";
        try {
            String hello = null;
            hello.length();
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            exceptionStackTrace = sw.toString();
        }
        System.out.println("----------------------");
        System.out.println(exceptionStackTrace);
    }

    private static void handleLpuList() {
        String lpuList = "1816,1835,1840,1844,1863,1891,1905,1917,1962,2051,2084,2085,2086,2115,2116,2146,2157,2214,2245,2250,2275,2285,2336,2688,2834,2841,3434,4044,4087,4102,4281,4351,4397,4470,4475,4518,4575,4578,4588,4986,5165,5287,5298,5352";
//        Set<String> notSet = new HashSet<>(Arrays.asList("1872", "1909", "2078", "2082", "2266", "2778", "4455", "4522"));
//        Set<String> notSet = new HashSet<>(Arrays.asList("4639","2038","1874","4623","3546","2046","4504","4455","1909"));
        Set<String> notSet = new HashSet<>(Arrays.asList("1933", "1864", "2078", "5365", "2346", "2266", "2078", "5004", "4939", "2038", "4639", "1874", "4623", "3546", "2046", "4504", "4455", "1909"));
        HashSet<String> lpuSet = new HashSet<>(Arrays.asList(lpuList.split(",")));
//        lpuSet.removeAll(notSet);
        String update = lpuSet.stream().map(str -> "update pmp_bill set status='DRAFT' where mo_id=" + str + " and period=to_date('2021-05-01','yyyy-MM-dd') and bill_type='SMO';").reduce("", (str1, str2) -> str1 + "\n" + str2);
        String lpus = lpuSet.stream().map(str -> "select " + str + " as lpu_id from dual").reduce("", (str1, str2) -> str1 + " union all\n" + str2);
        lpus = lpus.substring(lpus.indexOf("union all") + "union all".length() + 1);
        String insert = "with main_q as(\n" + lpus + "\n)\n"
                + "select \n"
                + "'insert into pmp_sync (LPU_ID,PERIOD,CALL_DATA,FEATURE_NAME,CREATED,FAILED,IN_PROGRESS,IS_PROCESS_ALIVE,BILL_STATISTICS_ID,SERVER_IP,PARAMETERS,USER_UNIQUEID,PROCESS_ID)\n"
                + "values('||q.lpu_id||',to_date(''2021-05-01'',''yyyy-MM-dd''),''RecreateBillsVirtualRequest'',''recreateBillsFeature'',SYSDATE,''0'',null,null,null,null,\n"
                + "''[NO-FLK] '||\n"
                + "(\n"
                + "select \n"
                + "listagg(b.id,',') within group(order by b.id) as fds\n"
                + "from pmp_bill b\n"
                + "inner join pmp_requirement re on re.id=b.requirement_id\n"
                + "where re.period=to_date('2021-05-01','yyyy-MM-dd') and re.mo_id=q.lpu_id and b.bill_type<>'SPECIAL'\n"
                + ")||\n"
                + "''',''Me'',null);\n"
                + "commit;\n"
                + "' as str from main_q q;";
        String send = "with main_q as(\n" + lpus + "\n)\n"
                + ", second_q as(\n"
                + "select \n"
                + "'insert into pmp_sync (LPU_ID,PERIOD,CALL_DATA,FEATURE_NAME,CREATED,FAILED,IN_PROGRESS,IS_PROCESS_ALIVE,BILL_STATISTICS_ID,SERVER_IP,PARAMETERS,USER_UNIQUEID,PROCESS_ID)\n"
                + "values('||q.lpu_id||',to_date(''2021-05-01'',''yyyy-MM-dd''),''SendBillsVirtualRequest'',''sendBillsFeature'',SYSDATE,''0'',null,null,null,null,\n"
                + "'''||\n"
                + "(\n"
                + "select \n"
                + "listagg(b.id,',') within group(order by b.id) as fds\n"
                + "from pmp_bill b\n"
                + "inner join pmp_requirement re on re.id=b.requirement_id\n"
                + "where re.period=to_date('2021-05-01','yyyy-MM-dd') and re.mo_id=q.lpu_id and b.status like 'GENERATED%'\n"
                + ")||\n"
                + "''',''Me'',null);\n"
                + "commit;\n"
                + "' as str\n"
                + ",(\n"
                + "select \n"
                + "listagg(b.id,',') within group(order by b.id) as fds\n"
                + "from pmp_bill b\n"
                + "inner join pmp_requirement re on re.id=b.requirement_id\n"
                + "where re.period=to_date('2021-05-01','yyyy-MM-dd') and re.mo_id=q.lpu_id and b.status like 'GENERATED%'\n"
                + ") as str2 \n"
                + "from main_q q)\n"
                + "select str from second_q where str2 is not null;";
//        System.out.println(update + "\ncommit;\n" + insert + "\n");
//        System.out.println(update + "\ncommit;\n" + insert + "\n" + send + "\ncommit;\n");
        System.out.println(send);
//        System.out.println(lpuSet.stream().reduce("", (str1, str2) -> str1 + "," + str2));
    }

    private static void checkForBugsInFiles(String dir) {
        String str1 = "ns2:InsuranceStatusRequest";
        String str2 = "ns2:InsuranceStatusResponse";
        String str3 = "PetitionsInfo";
        class LocalFileBean implements Comparable<LocalFileBean> {

            private final String fileName;
            private final Date created;
            private final int requestCount;
            private final int responseCount;

            public LocalFileBean(String fileName, Date created, int requestCount, int responseCount) {
                this.fileName = fileName;
                this.created = created;
                this.requestCount = requestCount;
                this.responseCount = responseCount;
            }

            public String getFileName() {
                return fileName;
            }

            public Date getCreated() {
                return created;
            }

            public Integer getRequestCount() {
                return requestCount;
            }

            public Integer getResponseCount() {
                return responseCount;
            }

            @Override
            public int compareTo(LocalFileBean o) {
                return -this.created.compareTo(o.getCreated());
            }

            @Override
            public String toString() {
                return fileName + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(created) + " " + requestCount + " " + responseCount;
            }
        }
        List<LocalFileBean> fileList = Arrays.stream(new File(dir).listFiles()).filter(file -> {
            try {
                String string = new String(Files.readAllBytes(file.toPath()));
                return string.contains(str1) && string.contains(str2) && !string.contains(str3);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).map(file -> {
            try {
                String string = new String(Files.readAllBytes(file.toPath()));
                Integer count1 = findSubstringCount(string, str1);
                Integer count2 = findSubstringCount(string, str2);
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                return new LocalFileBean(file.getName(), new Date(attr.creationTime().toMillis()), count1, count2);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).filter(localFileBean -> {
            try {
                return !localFileBean.getRequestCount().equals(localFileBean.getResponseCount());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).sorted().collect(Collectors.toList());
        fileList.stream().forEach(System.out::println);
    }

    private static Integer findSubstringCount(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = str.indexOf(findStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }

    private static String getLpuOrdForJoining(Long S_LPU_ORD) {
        if (S_LPU_ORD != null) {
            if (S_LPU_ORD.toString().length() == 4) {
                return S_LPU_ORD.toString();
            } else if (S_LPU_ORD.toString().length() == 6 && S_LPU_ORD.toString().substring(0, 2).equals("77")) {
                return S_LPU_ORD.toString().substring(2);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static Date getTruncatedDate(Date date) {
        return date != null ? DateUtils.truncate(date, Calendar.DAY_OF_MONTH) : null;
    }

    private static Date getEndDate(Date truncatedDate) {
        return DateUtils.addMilliseconds(DateUtils.addDays(truncatedDate, 1), -1);
    }

    private static String lpad(Long value) {
        return Strings.repeat("0", 6 - value.toString().length()) + value.toString();
    }

    private static String addHints(String sql) {
        if (sql.startsWith("SELECT ")
                && sql.contains("FROM PMP_MEDICAL_CASE")
                && sql.contains("doctor_job_id")) {
            return "SELECT /*+ index(this_ AK1_MEDICAL_CASE_N_L) */ " + sql.substring(7);
        } else {
            return sql;
        }
    }

    private static int SLICE_SIZE = 1000;

    private static void iterableTest(Iterable<Long> itrbl) {
        Iterator<Long> iterator = itrbl.iterator();
        if (iterator.hasNext()) {
            List<List<Long>> listOfLists = new ArrayList<>();
            while (iterator.hasNext()) {
                List<Long> idList = new ArrayList<>();
                do {
                    idList.add(iterator.next());
                } while (idList.size() < SLICE_SIZE && iterator.hasNext());
                listOfLists.add(idList);
            }
            System.out.println(listOfLists.size());
        }
    }

    static String strLaunchTemplate = "/usr/java8_64/bin/java -Xdisablejavadump -Xdump:none -XX:GCTimeRatio=19 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=30 -Xmx40G -Dlogs_dir=/u01/recreateFor119/2021_02_24__09_49_11/logs -Dpmp.config.path=/u01/recreateFor119/2021_02_24__09_49_11/conf/runtime.properties -jar /u01/recreateFor119/2021_02_24__09_49_11/module-pmp-bill-recreate.jar -m LPU_ID 2021-01 &";

    private static void lpuList() {
        StringBuilder sb = new StringBuilder("2074, 5240, 1873, 2085, 2346, 3602, 1872, 1901, 4305, 2285, 4628, 4475, 2342, 3915, 4986, 4510, 4620, 1874, 5114, 1851, 2858, 2186, 2161, 2123, 1909, 1896, 4522, 2778, 4575, 4508, 2060, 2290, 2157, 1892, 4623, 2390,4963,4723, 5149, 5243, 5302, 5320, 5350, 5393, 5398, 5400, 5401, 5429, 5450, 5453, 5454, 5455, 5350");
        String lpuList = sb.toString();
        Set<String> launchSet = Arrays.stream(lpuList.split(",")).map(String::trim).map(Integer::valueOf).map(lpuId -> strLaunchTemplate.replace("LPU_ID", lpuId.toString())).collect(Collectors.toSet());
        List<String> launchList = new ArrayList<>(launchSet);
        Collections.sort(launchList);
        launchList.stream().forEach(System.out::println);
    }

    private static final int ATTEMPTS_COUNT = 10;
    private static final int WAIT_TIME = 10 * 1000;

    private static void mkdirsHandler(File outputDir) {
        if (!outputDir.exists()) {
            for (int i = 0; i < ATTEMPTS_COUNT; i++) {
                boolean mkdirs = outputDir.mkdirs();
                if (mkdirs) {
                    break;
                } else {
                    if (i >= ATTEMPTS_COUNT - 1) {
                        throw new RuntimeException("Mkdirs failed!");
                    }
                    waitSomeTime();
                }
            }
        }
    }

    private static void waitSomeTime() {
        while (true) {
            try {
                Thread.sleep(WAIT_TIME);
                break;
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    private static void threadTest() {
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(() -> {
            try {
                Thread.sleep(10000);
                String gg = null;
                gg.length();
                return true;
            } catch (Exception e) {
                throw new RuntimeException(stackTraceToString(e), e);
            }
        });
        Thread thread = new Thread(futureTask);
        thread.setName("ExceptionThread");
        thread.start();
        FutureTask<Boolean> futureTask2 = createTestFutureTask();
        Thread thread2 = new Thread(futureTask2);
        thread2.setName("ExceptionThread2");
        thread2.start();
        List<FutureTask<Boolean>> futureTaskList = Arrays.asList(futureTask, futureTask2);
        try {
            join(futureTaskList);
            System.out.println("Everything is ok!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Everything is bad!");
        }
    }

    private static void join(List<FutureTask<Boolean>> futureTaskList) {
        List<Exception> exceptionList = futureTaskList.stream().map(thread -> {
            try {
                thread.get();
                return null;
            } catch (Exception ex) {
                return ex;
            }
        }).filter(ex -> ex != null).collect(Collectors.toList());
        if (!exceptionList.isEmpty()) {
            throw new RuntimeException(exceptionList.stream().map(e -> e.getMessage()).reduce((str1, str2) -> str1 + str2).get());
        }
    }

    private static String stackTraceToString(Exception exception) {
        return "Exception Message:" + exception.getMessage() + "\n\n" + Arrays.stream(exception.getStackTrace()).map(element -> element.toString() + "\n").reduce("", (str1, str2) -> str1 + str2) + "\n\n";
    }

    private static FutureTask<Boolean> createTestFutureTask() {
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(() -> {
            try {
                Thread.sleep(10000);
                String gg = null;
                gg.length();
                return true;
            } catch (Exception e) {
                throw new RuntimeException(stackTraceToString(e), e);
            }
        });
        return futureTask;
    }

    private static void testCheckCountNumber(Integer checkCountNumber) {
        final int n = checkCountNumber != null ? ++checkCountNumber : 0;
        System.out.println("n = " + n + "!");
    }

    public static final String connectionString2 = "jdbc:oracle:thin:@omsdb-scan.mgf.msk.oms:1528/PUMPN";
    public static final String user2 = "PMP_PROD";
    public static final String password2 = "PMP_PROD";

    private static void fixFileNames() throws SQLException {
        Pattern pattern = Pattern.compile("^PDF(.+?)S.+$");
        Map<String, Long> map = new HashMap<>();
        Connection connection = DriverManager.getConnection(connectionString2, user2, password2);
        ResultSet resultSet = connection.prepareCall("select mcod,moid from MOSPRLPU").executeQuery();
        while (resultSet.next()) {
            String mcod = resultSet.getString(1);
            long filId = resultSet.getLong(2);
            map.put(mcod, filId);
        }
        List<File> fileList = Arrays.stream(new File("C:\\tmp\\parcels3").listFiles()).filter(file -> file.getName().endsWith(".pdf")).collect(Collectors.toList());
        for (File file : fileList) {
            String fileName = file.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                String mcod = matcher.group(1);
                Long filId = map.get(mcod);
                if (filId != null) {
                    String newFileName = fileName.replace(mcod, filId.toString());
                    File newFile = new File(file.getParentFile().getAbsolutePath() + File.separator + newFileName);
                    file.renameTo(newFile);
                } else {
                    System.out.println("mcodFromFile = " + mcod + "!");
                }
            }
        }
    }

    private static void testDate() {
        String dateStr = new SimpleDateFormat("yyyy-MM").format(new Date());
        String str = "1234-56";
        System.out.println(str.substring(3, 4) + " & " + str.substring(str.length() - 2));
        System.out.println(dateStr.substring(3, 4) + " & " + dateStr.substring(dateStr.length() - 2));
    }

    private static void countFiles() {
        Set<String> set1 = Arrays.stream(new File("/home/me/tmp/org/erzl/services").listFiles()).map(File::getName).collect(Collectors.toSet());
        Set<String> set2 = Arrays.stream(new File("/home/me/tmp/org2/erzl/services").listFiles()).map(File::getName).collect(Collectors.toSet());
        Set<String> set3 = Arrays.stream(new File("/home/me/GIT/pmp/pmp/module-persons-api/src/main/java/org/erzl/services").listFiles()).map(File::getName).collect(Collectors.toSet());
        set1.stream().sorted().forEach(System.out::println);
        System.out.println("---------------");
        System.out.println("---------------");
        HashSet<Object> set = new HashSet<>();
        set.addAll(set1);
        set.addAll(set2);
        System.out.println(set.size());
        set1.retainAll(set2);
        set1.forEach(System.out::println);
        set3.removeAll(set);
        System.out.println("---------------");
        System.out.println("---------------");
        System.out.println(set3.size());
        System.out.println("---------------");
        System.out.println("---------------");
        set3.forEach(System.out::println);
    }

    private static void testOutOfMemory() {
        try {
            throw new OutOfMemoryError("Critical level of memory detected!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("Exceptions was handled!");
    }

//    private static void priorityQueueTest() {
//        PriorityQueue<InstanceQueueBean> queue = new PriorityQueue();
//        queue.add(new InstanceQueueBean("Instance1", new AtomicInteger(10)));
//        queue.add(new InstanceQueueBean("Instance2", new AtomicInteger(4)));
//        for (int i = 0; i < 12; i++) {
//            InstanceQueueBean instanceQueueBean = queue.poll();
//            System.out.println(instanceQueueBean.getInstanceName());
//            queue.offer(instanceQueueBean);
////            System.out.println(queue.peek().getInstanceName());
//        }
//    }
    private static boolean checkEN3(Date vmpDate, Date birthDay, String caseNumber, boolean newborn) {
        return vmpDate != null
                && ((birthDay != null && !newborn && vmpDate.before(birthDay))
                || (newborn && tryToExctractDate(caseNumber).map(date -> vmpDate.before(date)).orElse(false)));
    }

    private static Optional<Date> tryToExctractDate(String str) {
        try {
            return Optional.of(new SimpleDateFormat("yyyyMMdd").parse(str.substring(str.length() - 10, str.length() - 2)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Date d(String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(str);
    }

    public static void barcodeTest() throws Exception {
        byte[] generate = generate("Hello World!!!");
        File file = new File("/home/me/tmp/barcode.jpeg");
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), generate, StandardOpenOption.CREATE_NEW);
    }

    private static Configuration buildCfg(String type) {
        DefaultConfiguration cfg = new DefaultConfiguration("barcode");
        DefaultConfiguration child = new DefaultConfiguration(type);
        cfg.addChild(child);
        DefaultConfiguration attr = new DefaultConfiguration("human-readable");
        DefaultConfiguration subAttr = new DefaultConfiguration("placement");
        subAttr.setValue("bottom");
        attr.addChild(subAttr);
        child.addChild(attr);
        return cfg;
    }

    public static byte[] generate(String text) {
        try {
            BarcodeGenerator barcodeGenerator = BarcodeUtil.getInstance().createBarcodeGenerator(buildCfg("code128"));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int resolution = 600;
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(byteArrayOutputStream, "image/jpeg", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            barcodeGenerator.generateBarcode(canvas, text);
            canvas.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getUrlInfo(String url) {
        Matcher matcher = Pattern.compile("^.+?(" + patientIdRegexp + delimeter + patientTypeRegexp + delimeter + diagnosisRegexp + delimeter + serviceCodeRegexp + delimeter + serviceDateRegexp + serviceNumberRegexp + changeDateRegexp + ")$").matcher(url);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group;
        } else {
            return null;
        }
    }

    private static final String delimeter = "[–-]";
    private static final String HTTPS = "https?://";
    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
    private static final String DOMAIN_NAME_REGEXP = "[\\w.]+?";
    private static final String prefixRegexp = ".*?/?";
    private static final String patientIdRegexp = "\\d+?";
    private static final String patientTypeRegexp = "[0-3]";
    private static final String diagnosisRegexp = "\\w\\d+\\.?\\d{0,2}";
    private static final String serviceCodeRegexp = "\\d+?";
    private static final String serviceDateRegexp = "\\d{8}";
    private static final String serviceNumberRegexp = "(" + delimeter + "\\d{2})?";
    private static final String changeDateRegexp = "(" + delimeter + "\\d{8})?";

    private static final void urlAnalisys(String urlToAnalyse) {
        if (urlToAnalyse != null) {
            boolean matches = Pattern.compile("^" + HTTPS + IP_REGEXP + "/" + prefixRegexp + patientIdRegexp + delimeter + patientTypeRegexp + delimeter + diagnosisRegexp + delimeter + serviceCodeRegexp + delimeter + serviceDateRegexp + serviceNumberRegexp + changeDateRegexp + "$").matcher(urlToAnalyse).matches();
            boolean matches2 = Pattern.compile("^" + HTTPS + DOMAIN_NAME_REGEXP + "/" + prefixRegexp + patientIdRegexp + delimeter + patientTypeRegexp + delimeter + diagnosisRegexp + delimeter + serviceCodeRegexp + delimeter + serviceDateRegexp + serviceNumberRegexp + changeDateRegexp + "$").matcher(urlToAnalyse).matches();
            if (matches || matches2) {
                System.out.println(urlToAnalyse + " is Ok!");
            } else {
                System.out.println(urlToAnalyse + " is Bad!");
            }
        }
//Описание
//
//Если ссылка не соответствует маске, то формируется сообщение об ошибке: «Ссылка не соответствует установленному формату строки (URL): протокол//ХОСТ/префикс/Идентификатор пациента–Тип пациента–Диагноз–Услуга–Дата услуги–порядковый номер ссылки для этого документа–дата редактирования».
//
//Требования к формату ссылки (строки (URL):
//Формат строки (URL): протокол//ХОСТ/префикс/Идентификатор пациента–Тип пациента–Диагноз–Услуга–Дата услуги–порядковый номер ссылки для этого документа–дата редактирования, здесь:
//Протокол – http: или https:
//ХОСТ – IP адрес хоста
//Префикс – произвольный путь
//– символ разделитель «дефис» (код юникод 002D);
//Идентификатор пациента – patientId, значение идентификатора пациента, полученное из РС ЕЗРЛ (УКЛ для ЗЛ, ИД ИН для иногороднего, ИД НИЛ для неидентифицированного, ИД НР для новорожденного);
//Тип пациента – код из справочника «patient - Тип пациента»:
//0 Застрахованное лицо г. Москвы (ЗЛ);
//1 Иногородний (ИН);
//2 Незарегистрированный новорожденный (НР);
//3 Неидентифицированный (НИЛ);
//Диагноз – код диагноза из МКБ-10 по справочнику «mkb10_» до максимально возможной подрубрики или конечного диагноза, например: S01, S01.1, S01.11;
//Услуга – код из справочника reesus / reesms или идентификатор шаблона документа ЕМИАС;
//Дата услуги в формате YYYYMMDD;
//порядковый номер ссылки для этого документа – целое число от 1 до 99;
//Дата редактирования в формате YYYYMMDD.
//
//Пример ссылки:
//http://10.170.89.2/123456789–0–C18.1–037061–20200125–01–20200303

    }

    static final Map<Long, String> OPLATA_DESCRIPTION = ImmutableMap.<Long, String>builder()
            .put(0L, "не принято решение об оплате")
            .put(1L, "полная")
            .put(2L, "полный отказ")
            .put(3L, "частичный отказ").build();

    private static void practionerNamePatternTest(String name, String patternStr) {
        if (Pattern.compile(patternStr).matcher(name).matches()) {
            System.out.println(name + " - Ok!");
        } else {
            System.out.println(name + " - Bad!");
        }
    }

//    private static void testSortingMtrReestrFileStatus() throws ParseException {
//        List<MtrReestrFileStatus> list = Arrays.asList(new MtrReestrFileStatus(new Date(), 1L, "111", "okato", "okatoOms", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-04 00:00:00"), new Date(), new Date(), FileType.R, 0, 0, 0, "FileName", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, 0, BigDecimal.ZERO),
//                new MtrReestrFileStatus(new Date(), 2L, "111", "okato", "okatoOms", null, new Date(), new Date(), FileType.R, 0, 0, 0, "FileName", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, 0, BigDecimal.ZERO),
//                new MtrReestrFileStatus(new Date(), 3L, "111", "okato", "okatoOms", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-03 00:00:00"), new Date(), new Date(), FileType.R, 0, 0, 0, "FileName", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, 0, BigDecimal.ZERO),
//                new MtrReestrFileStatus(new Date(), 4L, "111", "okato", "okatoOms", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-04 00:00:00"), new Date(), new Date(), FileType.R, 0, 0, 0, "FileName", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, 0, BigDecimal.ZERO));
//        Collections.sort(list, (obj1, obj2) -> {
//            if (obj1.dschet != null && obj2.dschet != null) {
//                int i = -obj1.dschet.compareTo(obj2.dschet);
//                if (i == 0) {
//                    return -Long.valueOf(obj1.id).compareTo(obj2.id);
//                } else {
//                    return i;
//                }
//            } else if (obj1.dschet == null && obj2.dschet != null) {
//                return 1;
//            } else if (obj1.dschet != null && obj2.dschet == null) {
//                return -1;
//            } else {
//                return -Long.valueOf(obj1.id).compareTo(obj2.id);
//            }
//        });
//        list.forEach(obj -> System.out.println(obj.id));
//    }
    private static void fixStringEndings(String fileName) throws Exception {
        File file = new File(fileName);
        byte[] readAllBytes = Files.readAllBytes(file.toPath());
        byte[] readAllBytes2 = new byte[readAllBytes.length];
        int j = 0;
        byte[] rb = "\r".getBytes();
        for (int i = 0; i < readAllBytes.length; i++) {
            if (readAllBytes[i] != rb[0]) {
                readAllBytes2[j] = readAllBytes[i];
                j++;
            }
        }
        byte[] readAllBytes3 = new byte[j];
        System.arraycopy(readAllBytes2, 0, readAllBytes3, 0, j);
        Files.write(file.toPath(), readAllBytes3, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void showTree(final File dir, final int nest, final List<String> badFiles, String baseDir) throws IOException {
        if (!dir.isDirectory()) {
            return;
        }
        StringBuilder whiteSpace = new StringBuilder();
        for (int i = 0; i < nest; i++) {
            whiteSpace.append(' ');
        }
        List<File> files = Arrays.stream(dir.listFiles()).filter(file -> file.isDirectory() || file.getName().endsWith(".java")).collect(Collectors.toList());
        for (File file : files) {
            System.out.println(whiteSpace.toString() + file.getName());
            if (file.isDirectory()) {
                showTree(file, nest + 1, badFiles, baseDir);
            } else if (isJavaFileBad(file)) {
//                badFiles.add(file.getAbsolutePath().replace(baseDir, ""));
                badFiles.add(file.getAbsolutePath());
            }
        }
    }

    private static boolean isJavaFileBad(File file) throws IOException {
//        String string = new String(Files.readAllBytes(file.toPath()));
//        return string.contains("\r\n");
        Integer count1 = 0;
        Integer count2 = 0;
        byte b1 = 0x0A;
        byte b2 = 0x0D;
        byte[] readAllBytes = Files.readAllBytes(file.toPath());
        for (byte b : readAllBytes) {
            if (b == b1) {
                count1++;
            }
            if (b == b2) {
                count2++;
            }
        }
        return count1 > 0 && count2 > 0 && count1.equals(count2);
    }

    private static void getAllBadFiles() throws IOException {
        List<String> badFiles = new ArrayList<>();
//        showTree(new File("/home/me/GIT/pmp/pmp_core/pmp-common-min"), 0, badFiles);
        showTree(new File("/home/me/GIT/pmp"), 0, badFiles, "/home/me/GIT/pmp");
        showTree(new File("/home/me/GIT/pmp_core"), 0, badFiles, "/home/me/GIT/pmp");
        badFiles.stream().forEach(System.out::println);
    }

    private static void fixStringEndings2(String fileName) throws Exception {
        File file = new File(fileName);
        String string = new String(Files.readAllBytes(file.toPath()));
        String string2 = string.replaceAll("\r", "").replaceAll("\n", "\r\n");
        Files.write(file.toPath(), string2.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void encodeDecodeTest() {
        String encodeValue = encodeValue("Привет!!!");
        String decodeValue = decodeValue(encodeValue);
        System.out.println(decodeValue);
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String decodeValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    static Pattern fileListPattern = Pattern.compile("[(\\d+?)]\\s=>\\s(.+?)", Pattern.MULTILINE);

    private static void parseResponseFromPhpServer() {
        String response = "Array(    [0] => .    [1] => ..    [2] => A7C1FBFF002006A7_request)";
        getFileList(response);
    }

    private static final String delimiter = "=> ";

    private static List<String> getFileList(String sendPost) {
//        Map<String, Object> params = new HashMap<>();
//        params.put(GET_FILE_LIST, TRUE_PARAM);
//        String sendPost = sendPost(url, params);

        List<String> fileList = Arrays.stream(sendPost.replace("Array(", "").replace(")", "").split("    ")).filter(str -> str.contains(delimiter)).map(str -> str.substring(str.indexOf(delimiter) + delimiter.length())).filter(fileName -> !fileName.equals(".") && !fileName.equals("..")).collect(Collectors.toList());
        return fileList;
    }

    private static void testDateTrunc() {
        org.joda.time.LocalDateTime dateTime = org.joda.time.LocalDateTime.now();
        org.joda.time.LocalDateTime withSecondOfMinute = dateTime.withSecondOfMinute(0).withMillisOfSecond(0);
        System.out.println(withSecondOfMinute.toString());
    }

    private static Date testDateTrunc2(Date date, Date time) throws ParseException {
        if (time == null) {
            return date;
        }
        org.joda.time.LocalDate datePart = new org.joda.time.LocalDate(date);
        LocalTime timePart = new LocalTime(time);
        org.joda.time.LocalDateTime dateTime = datePart.toLocalDateTime(timePart);
        Date result = dateTime.withSecondOfMinute(0).withMillisOfSecond(0).toDate();
        Date truncatedDate = DateUtils.truncate(result, Calendar.MINUTE);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(truncatedDate));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtils.truncate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-12 14:44:44"), Calendar.MINUTE)));
        return truncatedDate;
    }

    private static void testAtomicReference() throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-05-05 22:14:44");
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-08-08 20:20:20");
        final AtomicReference<Long> result = new AtomicReference<>();
        Supplier<Long> getMonthsDifferenceSupplier = () -> result.updateAndGet((obj) -> obj != null ? obj : getMonthsDifference(date1, date2));
        System.out.println(getMonthsDifferenceSupplier.get());
        System.out.println(getMonthsDifferenceSupplier.get());
        System.out.println(getMonthsDifferenceSupplier.get());
    }

    private static final long getMonthsDifference(Date date1, Date date2) {
        Date truncate1 = DateUtils.truncate(date1, Calendar.MONTH);
        Date truncate2 = DateUtils.truncate(date2, Calendar.MONTH);
        LocalDate localDate1 = truncate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = truncate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.MONTHS.between(localDate1, localDate2);
    }

    private static final Date THRESHOLD_DATE;
    private static final Date PROLONGATION_DATE;

    static {
        try {
            THRESHOLD_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-03-15 00:00:00");
            PROLONGATION_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-31 00:00:00");
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }
    private static final long CERTIFICATE_DURATION_IN_YEARS = 5L;

    // Подкорректировано Юрием 2020-03-10 в 18:28!
    private static Date getCertificateDateEnd(Date dateStart) {
        LocalDateTime dt = LocalDateTime.ofInstant(dateStart.toInstant(), ZoneId.systemDefault());
        dt = dt.minusDays(1).plusYears(CERTIFICATE_DURATION_IN_YEARS);
        Date dateEnd = Date.from(dt.toInstant(ZoneOffset.systemDefault().getRules().getOffset(dt)));
        if (dateEnd.after(THRESHOLD_DATE) || dateEnd.equals(THRESHOLD_DATE)) { // #11985
            return PROLONGATION_DATE;
        } else {
            return dateEnd;
        }
    }

    private static void staticTest() {
        try {
            System.out.println(TestBean.class);
            TestBean.test2();
            TestBean testBean = new TestBean();
            testBean.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Hello!");
    }

    private static void testDateSort() throws ParseException {
        List<Date> dates = Arrays.asList(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-02-01 10:00:00"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-02-02 10:00:00"), null, null);
        Set<Date> set = dates.stream().collect(Collectors.toSet());
        Collections.sort(dates, (obj1, obj2) -> {
            if (obj1 == null && obj2 != null) {
                return -1;
            } else if (obj1 != null && obj2 == null) {
                return 1;
            } else if (obj1 == null && obj2 == null) {
                return 0;
            } else {
                return -obj1.compareTo(obj2);
            }
        });
        System.out.println(dates);
    }

    private static void testPatternForMemory() {
        String str = "memory     98304.00    36521.68    61782.32    12534.26    39297.56   61747.70     Ded";
        String digit = "[\\d\\.]+?\\s+?";
        Matcher matcher = Pattern.compile("^memory\\s+?" + digit + digit + "(" + digit + ")" + ".+$").matcher(str);
        if (matcher.find()) {
            String digitStr = matcher.group(1);
            Long freeMemory = digitStr.contains(".") ? Long.valueOf(digitStr.substring(0, digitStr.indexOf("."))) : Long.valueOf(digitStr);
//            return freeMemory; // Megabytes
            System.out.println(freeMemory);
        }
    }

    private static void testDigitPattern() {
        boolean matches = Pattern.compile("^\\d+$").matcher("123-4567890").matches();
        if (matches) {
            System.out.println("Matches!");
        } else {
            System.out.println("Not matches!");
        }
    }

    private static void testBillStatisticsShortBean() {
        List<Object[]> billStatisticsList = Arrays.asList(new Object[]{1L, new Date(), "1,2", "Hello!"}, new Object[]{1L, new Date(), "1,3", "Hello2!"}, new Object[]{2L, new Date(), "1,3", "Hello3!"});
        Collection<BillStatisticsShortBean> list = billStatisticsList.stream().collect(Collectors.toMap(objArr -> (Long) objArr[0], BillStatisticsShortBean::new, (obj1, obj2) -> {
            obj1.getOperationDescriptionList().addAll(obj2.getOperationDescriptionList());
            return obj1;
        })).values();
        System.out.println();
    }

    private static void logEntity(Object entity, int operationDescriptionFieldLength) {
        String entityStringRepresentation = entity.toString();
        int index = 0;
        while (index < entityStringRepresentation.length()) {
            String entityStringRepresentationPart = entityStringRepresentation.substring(index, Math.min(entityStringRepresentation.length(), index + operationDescriptionFieldLength));
            index += operationDescriptionFieldLength;
            System.out.println(entityStringRepresentationPart);
        }
    }

    private static Map<String, CommitBean> parseJSONcommits(byte[] string) throws Exception {
//        File file = new File("D:\\GIT\\pmp\\pmp\\build\\target\\pmp-dist-all\\modules\\module-pmp\\WEB-INF\\classes\\changelog.json");
//        byte[] bytes = new String(Files.readAllBytes(file.toPath()), "cp1251").getBytes("utf-8");
        byte[] bytes = new String(string, "cp1251").getBytes("utf-8");
//        if (file.exists()) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<CommitBean> readValue = objectMapper.readValue(bytes, new TypeReference<List<CommitBean>>() {
            });
            if (readValue != null) {
                return readValue.stream().collect(Collectors.toMap(CommitBean::getId, obj -> obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return null;
    }

    static byte[] jsonBytes;

    private static void getGitLogs(byte[] stringBytes) throws Exception {
        if (jsonBytes == null) {
            Map<String, CommitBean> parseJSONcommits = parseJSONcommits(stringBytes);
//    Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
            CommitBean earliest = parseJSONcommits.values().stream().min((obj1, obj2) -> obj1.getDateAsDate().compareTo(obj2.getDateAsDate())).get();
            CommitBean latest = parseJSONcommits.values().stream().max((obj1, obj2) -> obj1.getDateAsDate().compareTo(obj2.getDateAsDate())).get();
//        parseJSONcommits.values().stream().collect(Collectors.toMap(obj->obj.getCommitterName(), obj->obj.getAuthorName()));
            System.out.println(earliest.getId() + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(earliest.getDateAsDate()));
            File repositoryDir = new File("D:\\GIT\\pmp");
            Git git = Git.open(new File(repositoryDir.getAbsolutePath() + "/.git"));
//        Iterable<RevCommit> iterable = git.log().call();
            Iterable<RevCommit> iterable = git.log().add(git.getRepository().resolve("heads/develop")).call();
            Iterator<RevCommit> iterator = iterable.iterator();
            List<CommitBean> commitBeanList = new ArrayList<>();
            while (iterator.hasNext()) {
                CommitBean commitBean = new CommitBean();
                RevCommit commit = iterator.next();
                PersonIdent authorIdent = commit.getAuthorIdent();
                Date authorDate = authorIdent.getWhen();
                TimeZone authorTimeZone = authorIdent.getTimeZone();
                if (authorDate.before(earliest.getDateAsDate())) {
                    break;
                }
                String id = commit.getId().toString();
                Date date = new Date(commit.getCommitTime() * 1000);
                String name = commit.getCommitterIdent().getName();
                String emailAddress = commit.getCommitterIdent().getEmailAddress();
                String fullMessage = commit.getFullMessage();
//            System.out.println(id + " " + " authorDate: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(authorDate) + " name: " + name + " emailAddress: " + emailAddress + " fullMessage: " + fullMessage);
                commitBean.setId(id);
                commitBean.setAuthorName(name);
                commitBean.setAuthorEmail(emailAddress);
                commitBean.setCommitterEmail(emailAddress);
                commitBean.setCommitterName(name);
                commitBean.setDate(authorDate);
                commitBean.setMessage(fullMessage);
                commitBeanList.add(commitBean);
            }
            Map<String, CommitBean> commitBeanMap = commitBeanList.stream().collect(Collectors.toMap(CommitBean::getId, obj -> obj));
            parseJSONcommits.entrySet().removeIf(entry -> commitBeanMap.containsKey(entry.getKey()));
            List<CommitBean> list = commitBeanMap.values().stream().filter(obj -> !obj.getMessage().startsWith("Merge branch")).filter(obj -> latest.getDateAsDate().before(obj.getDateAsDate())).collect(Collectors.toList());
            Collection<CommitBean> values = parseJSONcommits.values();
            list.addAll(values);
            List<CommitBean> resultList = list.stream().sorted().collect(Collectors.toList());
//        List<CommitBean> resultList = parseJSONcommits.values().stream().sorted().collect(Collectors.toList());
            ObjectMapper objectMapper = new ObjectMapper();
//            File outputfile = new File("D:\\GIT\\pmp\\pmp\\build\\target\\pmp-dist-all\\modules\\module-pmp\\WEB-INF\\classes\\changelogOut.json");
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            objectMapper.writeValue(byteOutputStream, resultList);
            String string = new String(byteOutputStream.toByteArray());

            String replaceAll = string.replaceAll(",", ", ").replace("{", "{ ").replace("},", " }\r\n  ,").replaceFirst("\\[", "[\r\n    ");
            replaceAll = replaceAll.substring(0, replaceAll.lastIndexOf("}]")) + " }\r\n]";
            replaceAll = replaceAll.replace("}]", " } ]");
            jsonBytes = replaceAll.getBytes();
//            if (outputfile.exists()) {
//                outputfile.delete();
//            }
//            Files.write(outputfile.toPath(), jsonBytes, StandardOpenOption.CREATE_NEW);
            System.out.println();
        }
    }

    private static void checkArchiveFiles(String fileName, Date archiveDate) throws Exception {
        File file = new File(fileName);
        File file2 = new File(file.getParentFile().getAbsolutePath() + File.separator + file.getName().substring(0, file.getName().indexOf(".")) + "2" + file.getName().substring(file.getName().indexOf(".")));
        if (file.getName().endsWith(".war")) {
            ZipFile zipFile = new ZipFile(file);
            final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file2));
            for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry entryIn = (ZipEntry) e.nextElement();
                InputStream is = zipFile.getInputStream(entryIn);
                int available = is.available();
                byte[] buf = new byte[available];
                int len;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while ((len = is.read(buf)) > 0) {
                    byteArrayOutputStream.write(buf, 0, len);
                }
                byte[] toByteArray = byteArrayOutputStream.toByteArray();
                if (!entryIn.getName().endsWith(".class") && !entryIn.getName().endsWith("/") && !entryIn.getName().endsWith(".wsdl") && !entryIn.getName().endsWith(".jar")) {
                    System.out.println(entryIn.getName());
                    String s = new String(toByteArray);

                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    s = falsifyString(entryIn.getName(), toByteArray, s, archiveDate);
                    if (!entryIn.getName().endsWith("changelog.json")) {
                        System.out.println(s);
                    }
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    zos.putNextEntry(new ZipEntry(entryIn.getName()));
                    zos.write(s.getBytes(), 0, s.getBytes().length);
                } else {
                    zos.putNextEntry(entryIn);
                    zos.write(toByteArray, 0, toByteArray.length);
                }

//                if (!entryIn.getName().endsWith(".class")) {
//                    zos.putNextEntry(entryIn);
//                    InputStream is = zipFile.getInputStream(entryIn);
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = is.read(buf)) > 0) {
//                        zos.write(buf, 0, len);
//                    }
//                } else {
//                    zos.putNextEntry(new ZipEntry("abc.txt"));
//
//                    InputStream is = zipFile.getInputStream(entryIn);
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = (is.read(buf))) > 0) {
//                        String s = new String(buf);
//                        if (s.contains("key1=value1")) {
//                            buf = s.replaceAll("key1=value1", "key1=val2").getBytes();
//                        }
//                        zos.write(buf, 0, (len < buf.length) ? len : buf.length);
//                    }
//                }
                zos.closeEntry();
            }
            zos.close();
        }
    }

    static Pattern builtBy = Pattern.compile("(Built-By: )(.+?)[\\s\n$]");
    static Pattern buildJdk = Pattern.compile("(Build-Jdk: )(.+?)[\\s\n$]");
    static Pattern maven = Pattern.compile("(#Generated by Maven).*?\n(.+?)\n", Pattern.DOTALL);
    static Pattern buildTimestamp = Pattern.compile("(build.timestamp=)(.+?\\s.+?)[\\s\n$]");
    static Pattern json = Pattern.compile("^[.+]$", Pattern.DOTALL);

    private static String falsifyString(String fileName, byte[] toByteArray, String string, Date archiveDate) throws Exception {
        //        string = string.replaceAll("Built-By: IBS_ERZL", "Built-By: IBS");
//        string = string.replaceAll("Build-Jdk: 1.8.0_102", "Build-Jdk: 1.8.0");
//        string = string.replaceAll("(#Generated by Maven.+\\n).+?\\n", "$1");
        string = applyChange(string, builtBy, "IBS");
        string = applyChange(string, buildJdk, "1.8.0");
        string = applyChange(string, maven, "\n#" + new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", new Locale("en", "EN")).format(archiveDate) + "\n");
        string = applyChange(string, buildTimestamp, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(archiveDate));
        if (json.matcher(string).matches() || fileName.endsWith("changelog.json")) {
            getGitLogs(toByteArray);
            string = new String(jsonBytes, "utf-8");
        }
        return string;
    }

    private static String applyChange(String string, Pattern pattern, String valueToChange) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            string = matcher.replaceAll("$1" + valueToChange);
        }
        return string;
    }

    private static void checkDate() throws ParseException {
//        Date date = new SimpleDateFormat("yyyy MMM dd",new Locale("en", "EN")).parse("2019 Jul 01");
//        Date date = new SimpleDateFormat("yyyy mm dd").parse("2019 12 01");
//        System.out.println(date.toString());
//        Pattern pattern = Pattern.compile("^.+?\\s.+?\\s.+?\\s.+?\\s.+?\\s.+?\\s(.+?)\\s(.+?)\\s(.+?)\\s(.+?)$", Pattern.DOTALL);
        Pattern pattern = Pattern.compile("^.+?\\s.+?\\s.+?\\s.+?\\s.+?\\s(.+?)\\s(.+?)\\s(.+?)\\s(.+?)$", Pattern.DOTALL);
        String kk = "-rw-r--r-- 1 root root  67725237 Dec 11 16:15 admin-panel.war";
        String yy = kk.replaceAll("\\s+?([^\\s])", " $1");
        Matcher matcher = pattern.matcher(yy);
        if (matcher.find()) {
            String month = matcher.group(1);
            String day = matcher.group(2);
            String time = matcher.group(3);
            String moduleName = matcher.group(4);
            String gg = "";
        }
    }

    private static void checkStrings() {
        String str1 = StringUtils.join(new ArrayList<String>(), ", ");
        String str2 = StringUtils.join(Arrays.asList("1", "2", "3"), ", ");
        System.out.println("str1 = " + str1);
        System.out.println("str2 = " + str2);
    }

    private static void checkPattern() {
        Pattern checkPattern = Pattern.compile("^.+[а-яА-Я].+$");
        String checkString = "dcv42-4323??!!АЯва";
        String checkString2 = "!#1234''???><><%%%";
        boolean matches = checkPattern.matcher(checkString).matches();
        boolean matches2 = checkPattern.matcher(checkString2).matches();
        System.out.println((matches ? "matches - true" : "matches - false") + (matches2 ? " matches2 - true" : " matches2 - false"));
    }

    private static void appendFile(String join) throws IOException {
        File file = new File("D:\\tmp\\zzzz\\trace.log");
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), join.getBytes(), StandardOpenOption.APPEND);
    }

    private static void testConversion() {
        final long size = 100000L;
        final int loopSize = 20;
        List<Long> elapsed1List = new ArrayList<>(loopSize);
        List<Long> elapsed2List = new ArrayList<>(loopSize);
        List<Long> elapsed3List = new ArrayList<>(loopSize);
//        List<Long> differenceList = new ArrayList<>(loopSize);
        for (int j = 0; j < loopSize; j++) {
            List<SomeBean1> list1 = new ArrayList<>((int) size);
            for (long i = 0L; i < size; i++) {
                SomeBean1 someBean1 = new SomeBean1();
                someBean1.setId(i);
                someBean1.setData(i + " Some Data!");
                someBean1.setCreated(new Date());
                list1.add(someBean1);
            }
            Stopwatch stopwatch1 = Stopwatch.createStarted();
            List<SomeBean2> list21 = list1.stream().map(obj -> convert(obj)).collect(Collectors.toList());
            long elapsed1 = stopwatch1.elapsed(TimeUnit.MICROSECONDS);
            Stopwatch stopwatch2 = Stopwatch.createStarted();
            List<SomeBean2> list22 = list1.stream().map(obj -> convert2(obj)).collect(Collectors.toList());
            long elapsed2 = stopwatch2.elapsed(TimeUnit.MICROSECONDS);
            Stopwatch stopwatch3 = Stopwatch.createStarted();
            List<SomeBean2> list23 = list1.stream().map(obj -> convert3(obj)).collect(Collectors.toList());
            long elapsed3 = stopwatch3.elapsed(TimeUnit.MICROSECONDS);
//            long difference = elapsed1 - elapsed2;
            elapsed1List.add(elapsed1);
            elapsed2List.add(elapsed2);
            elapsed3List.add(elapsed3);
//            differenceList.add(difference);
            System.out.println("j = " + j + "!");
        }
        LongSummaryStatistics elapsed1SummaryStatistics = elapsed1List.stream().mapToLong(x -> x).summaryStatistics();
        LongSummaryStatistics elapsed2SummaryStatistics = elapsed2List.stream().mapToLong(x -> x).summaryStatistics();
        LongSummaryStatistics elapsed3SummaryStatistics = elapsed3List.stream().mapToLong(x -> x).summaryStatistics();
//        LongSummaryStatistics differenceSummaryStatistics = differenceList.stream().mapToLong(x -> x).summaryStatistics();
        System.out.println("elapsed1 = " + elapsed1SummaryStatistics.getAverage() + " elapsed2 = " + elapsed2SummaryStatistics.getAverage() + " elapsed3 = " + elapsed3SummaryStatistics.getAverage() + "!");
    }

    static Map<String, Field> wsAuthInfoFieldNameMap = Arrays.stream(SomeBean2.class.getDeclaredFields()).collect(Collectors.toMap(field -> field.getName(), field -> field));
    static Map<String, Field> authInfoFieldNameMap = Arrays.stream(SomeBean1.class.getDeclaredFields()).collect(Collectors.toMap(field -> field.getName(), field -> field));

    private static SomeBean2 convert(SomeBean1 obj1) {
        try {
            SomeBean2 obj2 = new SomeBean2();
            for (Entry<String, Field> entry : wsAuthInfoFieldNameMap.entrySet()) {
                Field field = entry.getValue();
                Field authInfoField = authInfoFieldNameMap.get(entry.getKey());
                field.setAccessible(true);
                authInfoField.setAccessible(true);
                field.set(obj2, authInfoField.get(obj1));
            }
            return obj2;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SomeBean2 convert2(SomeBean1 obj1) {
        SomeBean2 obj2 = new SomeBean2();
        obj2.setId(obj1.getId());
        obj2.setData(obj1.getData());
        obj2.setCreated(obj1.getCreated());
        return obj2;
    }

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SomeBean2 convert3(SomeBean1 obj1) {
        SomeBean2 obj2 = new SomeBean2();
        String str = obj1.toString();
        Map<String, String> propertyMap = Arrays.stream(str.substring(str.indexOf("{") + 1, str.indexOf("}")).split(",")).map(arr -> arr.split("=")).collect(Collectors.toMap(arr -> ((String) arr[0]).trim(), arr -> (String) arr[1]));
        obj2.setId(Long.valueOf(propertyMap.get("id")));
        obj2.setData(propertyMap.get("data"));
        try {
            obj2.setCreated(simpleDateFormat.parse(propertyMap.get("created")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj2;
    }

    private static void testJoinString(String join) {
        if (join.contains(",")) {
            join = join.substring(0, join.lastIndexOf(",")) + " and" + join.substring(join.lastIndexOf(",") + 1);
        }
        System.out.println(join);
    }

    public static String GasStation(String[] strArr) {
        String IMPOSSIBLE = "impossible";
        List<Integer> possibleStartPoints = new ArrayList<>();
        for (int i = 1; i < strArr.length; i++) {
            if (getGas(strArr[i]) >= getGasConsumption(strArr[i])) {
                possibleStartPoints.add(i);
            }
        }
        if (possibleStartPoints.isEmpty()) {
            return IMPOSSIBLE;
        } else {
            for (int startPoint : possibleStartPoints) {
                boolean success = true;
                int totalGas = 0;
                for (int i = 0; i < strArr.length - 1; i++) {
                    int index = startPoint + i > strArr.length - 1 ? i : startPoint + i;
                    int gas = getGas(strArr[index]);
                    int gasConsumption = getGasConsumption(strArr[index]);
                    totalGas += gas - gasConsumption;
                    if (totalGas < 0) {
                        success = false;
                        break;
                    }
                }
                if (success) {
                    return startPoint + "";
                }
            }
            return IMPOSSIBLE;
        }
    }

    private static int getGas(String string) {
        return Integer.valueOf(string.split(":")[0]);
    }

    private static int getGasConsumption(String string) {
        return Integer.valueOf(string.split(":")[1]);
    }
//    1, 5, 7, 9, and 11

    public static int CoinDeterminer(int num) {
        if (num < 1) {
            return 0;
        }
        List<Integer> coins = Arrays.asList(11, 9, 7, 5, 1);
        int res = num;
        int index = 0;
        int iterations = 0;
        do {
            if (res - coins.get(index) < 0) {
                index++;
                continue;
            } else {
                res -= coins.get(index);
                index = 0;
            }
            iterations++;
        } while (res > 0);
        return iterations;

    }

    public static String StringScramble(String str1, String str2) {
        final String TRUE = "true";
        final String FALSE = "false";
        if (str1 == null || str2 == null || str1.length() == 0 || str2.length() == 0) {
            return FALSE;
        }
        String[] split = str1.split("");
        String[] split2 = str2.split("");
        Map<String, Integer> map1 = Arrays.asList(split).stream().collect(Collectors.groupingBy(str -> str, Collectors.collectingAndThen(Collectors.toList(), ff -> ff.size())));
        Map<String, Integer> map2 = Arrays.asList(split2).stream().collect(Collectors.groupingBy(str -> str, Collectors.collectingAndThen(Collectors.toList(), ff -> ff.size())));
        if (!map1.keySet().containsAll(map2.keySet())) {
            return FALSE;
        }
        return map2.entrySet().stream().allMatch(entry -> {
            return map1.get(entry.getKey()) >= entry.getValue();
        }) ? TRUE : FALSE;
    }

    public static String Palindrome(String str) {
        if (str == null) {
            return "false";
        }
        String modifiedString = str.replaceAll(" ", "").toLowerCase();
        if (new StringBuilder(modifiedString).reverse().toString().equals(modifiedString)) {
            return "true";
        } else {
            return "false";
        }
    }

    public static String LongestWord(String sen) {

        // code goes here
        /* Note: In Java the return type of a function and the
        parameter types being passed are defined, so this return
        call must match the return type of the function.
        You are free to modify the return type. */
        Optional<String> max = Arrays.asList(sen.split(" ")).stream().max((str1, str2) -> Integer.valueOf(str1.length()).compareTo(str2.length()));

        return max.get();

    }

    public static final String connectionString = "jdbc:oracle:thin:@10.2.72.25:1521:ERZ";
    public static final String user = "pmp_prod";
    public static final String password = "manager";

    private static void testCursors() throws Exception {
        BlockingQueue queue = new LinkedBlockingQueue<MonitorBean>(1);
        ConnectionMonitorDaemon monitorDaemon = new ConnectionMonitorDaemon(queue);
        monitorDaemon.start();
        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before sleep"));
        Thread.sleep(2000);
        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after sleep"));
        System.out.println("testCursors started!");
        final AtomicInteger i = new AtomicInteger(0);
        try {
            queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before connection opened"));
            Connection connection = DriverManager.getConnection(connectionString, user, password);
            Statement statement = null;
            ResultSet rs = null;
            try {
                queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before createStatement"));
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
                queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after createStatement"));
                rs = statement.executeQuery("select * from pmp_medical_case order by id");
                queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after executeQuery"));
                while (rs.next()) {
                    Long id = rs.getLong("ID");
                    System.out.println("id = " + id.toString() + "!");
                    queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "during rs.next()"));
                    Thread.sleep(20);
                    if (i.incrementAndGet() > 100) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                try {
                    if (statement != null) {
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before statement closed"));
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before statement closed"));
                        statement.close();
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after statement closed"));
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after statement closed"));
                    }
                    if (connection != null) {
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before connection closed"));
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "before connection closed"));
                        System.out.println("connection closed!");
                        connection.close();
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after connection closed"));
                        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after connection closed"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int j = 0; j < 20; j++) {
            queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.COUNT, "after all"));
        }
        queue.put(new MonitorBean(MonitorBean.MonitorBeanEnum.CLOSE, ""));
        monitorDaemon.join();
        System.out.println("testCursors finished!");
    }

    private static void bigDecimalTest2(String value) {
        BigDecimal bd1 = new BigDecimal(value);
        BigDecimal bd2 = bd1.setScale(2, RoundingMode.HALF_UP);
        System.out.println(bd1.toString() + " --> " + bd2.toString());
    }

    private static void bigDecimalTest() {
        BigDecimal bd1 = new BigDecimal("5.001");
        System.out.println(bd1);
        BigDecimal bd2 = bd1.setScale(0, RoundingMode.CEILING);
        System.out.println(bd2);
    }

    private void testInvoiceModelHandling() throws Exception {
        List<InvoiceModel> invoiceModelList = Arrays.asList(
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-15 00:00:00"))), new PatientModel("6249810886000173", "Ястребова", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-18 00:00:00"))), new PatientModel("6249810886000173", "Тараканова", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-18 00:00:00"))), new PatientModel("6249810886000173", "Орлова", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-15 00:00:00"))), new PatientModel("444", "Морковкина", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-18 00:00:00"))), new PatientModel("444", "Тыковкина", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-15 00:00:00"))), new PatientModel("222", "Кабачкова", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-18 00:00:00"))), new PatientModel("222", "Ржанова", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-10 00:00:00"))), new PatientModel("7777", "Поросёнок", "Марина", "Александровна")),
                new InvoiceModel(new Invoice(new MedicalCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-12-12 00:00:00"))), new PatientModel("8888", "Козлёнок", "Марина", "Александровна"))
        );
        handleInvoiceModelListToFindLastPatientsModels(invoiceModelList);
        checkData(invoiceModelList);
    }

    private String getPatientKey(InvoiceModel invoiceModel) {
        return invoiceModel.getPatientModel().getInsuranceNumber() + " - " + invoiceModel.getPatientModel().getLastName() + " - " + invoiceModel.getPatientModel().getFirstName() + " - " + invoiceModel.getPatientModel().getMiddleName();
    }

    private String getSimplePatientKey(InvoiceModel invoiceModel) {
        return invoiceModel.getPatientModel().getInsuranceNumber();
    }

    private void handleInvoiceModelListToFindLastPatientsModels(List<InvoiceModel> resultModelList) {
        Map<String, Map<String, InvoiceModel>> mapOfMaps = resultModelList.stream().collect(Collectors.groupingBy(this::getSimplePatientKey, Collectors.groupingBy(this::getPatientKey, Collectors.collectingAndThen(Collectors.toList(), ff -> ff.get(0)))));
        mapOfMaps.entrySet().stream().forEach(rootEntry -> {
            if (rootEntry.getValue().size() > 1) {
                List<InvoiceModel> list = new ArrayList(rootEntry.getValue().values());
                Collections.sort(list, (InvoiceModel obj1, InvoiceModel obj2) -> {
                    int compareTo = obj1.getInvoice().getMedicalCase().getCaseDate().compareTo(obj2.getInvoice().getMedicalCase().getCaseDate());
                    if (compareTo == 0) {
                        return getPatientKey(obj1).compareTo(getPatientKey(obj2));
                    } else {
                        return -compareTo;
                    }
                });
                InvoiceModel mainInvoiceModel = list.get(0);
                for (int i = 1; i < list.size(); i++) {
                    list.get(i).setPatientModel(mainInvoiceModel.getPatientModel());
                }
            }
        });
    }

    private void checkData(List<InvoiceModel> resultModelList) {
        resultModelList.stream().forEach(invoiceModel -> {
            try {
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invoiceModel.getInvoice().getMedicalCase().getCaseDate()) + "    " + new String(getPatientKey(invoiceModel).getBytes("utf-8")));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    class InvoiceModel {

        private final Invoice invoice;
        private PatientModel patientModel;

        public InvoiceModel(Invoice invoice, PatientModel patientModel) {
            this.invoice = invoice;
            this.patientModel = patientModel;
        }

        public Invoice getInvoice() {
            return invoice;
        }

        public PatientModel getPatientModel() {
            return patientModel;
        }

        public void setPatientModel(PatientModel patientModel) {
            this.patientModel = patientModel;
        }

    }

    class Invoice {

        private final MedicalCase medicalCase;

        public Invoice(MedicalCase medicalCase) {
            this.medicalCase = medicalCase;
        }

        public MedicalCase getMedicalCase() {
            return medicalCase;
        }

    }

    class MedicalCase {

        private final Date caseDate;

        public MedicalCase(Date caseDate) {
            this.caseDate = caseDate;
        }

        public Date getCaseDate() {
            return caseDate;
        }

    }

    class PatientModel {

        private final String insuranceNumber;
        private final String lastName;
        private final String firstName;
        private final String middleName;

        public PatientModel(String insuranceNumber, String lastName, String firstName, String middleName) {
            this.insuranceNumber = insuranceNumber;
            this.lastName = lastName;
            this.firstName = firstName;
            this.middleName = middleName;
        }

        public String getInsuranceNumber() {
            return insuranceNumber;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

    }

    private static String cut(String str, int maxLength) {
        if (str != null && str.length() > maxLength) {
            str = str.substring(str.length() - maxLength, str.length());
        }
        return str;
    }

//    private static final Pattern PART_PATTERN = Pattern.compile("^.+?_" + Strings.repeat("(\\d+?)[_-]$", 5), Pattern.DOTALL);
//    private static final Pattern PART_PATTERN = Pattern.compile("^.+?_(\\d+?)[_-](\\d+?)[_-](\\d+?)[_-](\\d+?)[_-](\\d+?)$", Pattern.DOTALL);
    private static Pattern PART_PATTERN = Pattern.compile("^.+?(-.+?)[^\\w^\\d](\\d+?)[^\\w^\\d](\\d\\d\\d\\d-\\d\\d).*$");
//    private static final Pattern PART_PATTERN = Pattern.compile("^.+?_(\\d+?)[_-](\\d+?)[_-](\\d+?)[_-](\\d+?)[_-](\\d+?)$", Pattern.DOTALL);

//    private static final String patternPart = Strings.repeat("(\\\\d+?)[_-]", 5);
//    private static final Pattern PART_PATTERN = Pattern.compile("^.+?_" + patternPart.substring(0, patternPart.lastIndexOf("[_-]")) + "$", Pattern.DOTALL);
    private static void testFilePattern() {
        Matcher matcher = PART_PATTERN.matcher("java8_64/bin/java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/u01/recreateFor119/2018_12_06__16_23_01/ -XX:GCTimeRatio=19 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=30 -Xmx8G -Dlogs_dir=/u01/recreateFor119/2018_12_06__16_23_01/logs -Dpmp.config.path=/u01/recreateFor119/2018_12_06__16_23_01/conf/runtime.properties -jar /u01/recreateFor119/2018_12_06__16_23_01/module-pmp-bill-recreate.jar -m 5169 2018-11");
        if (matcher.find()) {
            String type__ = matcher.group(1);
            type__ = type__.substring(type__.lastIndexOf("-"));
//            String lpuId = matcher.group(2);
//            String year = matcher.group(3);
//            String month = matcher.group(4);
//            String part = matcher.group(5);
            String hello = "";
        }
    }

    private static final String NAME_FIELD = "name=";

    private static String getTableName() {
        String str = "@javax.persistence.Table(name=PMP_SMP_CASE_AUD, schema=, uniqueConstraints=[], indexes=[], catalog=)";
        int index1 = str.indexOf(NAME_FIELD);
        int index2 = str.indexOf(",", index1);
        String substring = str.substring(index1 + NAME_FIELD.length(), index2);
        return substring;
    }

    private static String formatTableName(String tableName) {
        tableName = tableName.toLowerCase();
        while (tableName.contains("_")) {
            int index = tableName.indexOf("_");
            tableName = tableName.substring(0, index) + tableName.substring(index + 1, index + 2).toUpperCase() + tableName.substring(index + 2);
        }
        return tableName;
    }

    private interface CLibrary extends Library {

        CLibrary INSTANCE = (CLibrary) Native.loadLibrary("c", CLibrary.class);

        int getpid();
    }

    private static String getPidForAix(String[] args) {
        try {
            // ps -ef | grep '2082 2018-08' | awk '{print $2}'
            // ps -fp $$ |tail -1 | awk '{print $3}'
//            Process process = new ProcessBuilder(Arrays.asList(args))
            Process process = new ProcessBuilder(Arrays.asList("ps", "-fp", "$$"))
                    .redirectErrorStream(true)
                    .start();
            while (process.isAlive()) {
                process.waitFor();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception ex2) {
            return "Unknown PID";
        }
    }
//    private static String getPidForAix() {
//        try {
//            // ps -ef | grep '2082 2018-08' | awk '{print $2}'
//            // ps -fp $$ |tail -1 | awk '{print $3}'
//            StringBuilder sb = new StringBuilder();
//            Process process = Runtime.getRuntime().exec("ps -ef | grep recreate");
//            process.waitFor();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            System.out.println(sb.toString());
//            return sb.toString();
//        } catch (Exception ex2) {
//            return "Unknown PID";
//        }
//    }

    private static void testMapReduce() {
        String[] array = {"Mohan", "Sohan", "Mahesh"};
        String get = Arrays.stream(array).reduce((x, y) -> x + "," + y).get();

        Map<String, Object> filterValues = new HashMap<>();
        filterValues.put("code", 2);
        filterValues.put("other_code", 4);
        filterValues.put("other_code_2", 8);
        String createFilter = createFilter(filterValues);

//        filterValues.entrySet().stream().reduce("", (x, y, z) -> );
        String hello = "";
    }

    private static String createFilter(Map<String, Object> filterValues) {
        if (filterValues != null && !filterValues.isEmpty()) {
            String and = " and ";
            StringBuilder ret = new StringBuilder();
            for (Map.Entry<String, Object> entry : filterValues.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                ret.append(key + "='" + value.toString() + "'" + and);
            }
            String retValue = ret.toString();
            return retValue.substring(0, retValue.length() - and.length());
        } else {
            return null;
        }
    }

    private static void testSemaphore() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(0);
//        new FutureTask<>();
        new Thread(() -> {
            try {
                Thread.sleep(20 * 1000);
                semaphore.release();
                System.out.println("semaphore was released! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            } catch (InterruptedException ex) {
                Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        System.out.println("Waiting for semaphore! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        semaphore.acquire();
        System.out.println("Waiting for semaphore finished! " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private static void testPattern() {
        Pattern win32ProcessPattern = Pattern.compile("^(.+?)[^\\w^\\d](.+?)[^\\w^\\d](\\d+?)$", Pattern.DOTALL);
        Pattern win32ProcessPatternWithoutCmd = Pattern.compile("^(.+?)[^\\w^\\d](\\d+?)$", Pattern.DOTALL);
        String str = "java.exe java -Xmx40G -Dpmp.config.path=C:\\recreateFor111\\2018_02_22__14_18_15\\conf\\runtime.properties -jar C:\\recreateFor111\\2018_02_22__14_18_15\\module-pmp-bill-recreate.jar -m 1932 2017-12 9640";
        Matcher winMatcher = win32ProcessPattern.matcher(str);
        if (winMatcher.find()) {
            String processName = winMatcher.group(1);
            String processCmd = winMatcher.group(2);
            String processIdStr = winMatcher.group(3);
//                    return new OsProcessBean(processName, Integer.valueOf(processIdStr), processCmd);
            String hello = "";
        } else {
            Matcher winMatcherWithoutCmd = win32ProcessPatternWithoutCmd.matcher(str);
            while (winMatcherWithoutCmd.find()) {
                String processName = winMatcherWithoutCmd.group(1);
                String processIdStr = winMatcherWithoutCmd.group(2);
//                        return new OsProcessBean(processName, Integer.valueOf(processIdStr), null);
                String hello = "";
            }
        }
    }

    private static byte[] encodeToUtf8(byte[] readAllBytes) throws IOException {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(readAllBytes, 0, readAllBytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            return readAllBytes;
        }
        Charset utf8charset = StandardCharsets.UTF_8;
        Charset originalCharset = Charset.forName(encoding);
        String reencoded2 = new String(readAllBytes, originalCharset);
        return reencoded2.getBytes(utf8charset);
    }

    public static String handleHttpResponseString(String obj) {
        Matcher matcher = Pattern.compile("^.+?Body>(.+?)<[^>]+?Body>.+$", Pattern.DOTALL).matcher(obj);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group;
        }
        return null;
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
//        Path path = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate[^\\w^\\d]rc\\test\\resources[^\\w^\\d]oapResponses\\tmp_8193710398\\Insured_25100582.xml").toPath();
        Path path = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate[^\\w^\\d]rc\\test\\resources[^\\w^\\d]oapResponses\\tmp_8193710398\\SQL.txt").toPath();
        File file2 = new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate[^\\w^\\d]rc\\test\\resources[^\\w^\\d]oapResponses\\tmp_8193710398\\Insured_2.xml");
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
                    File file = new File("D:\\tmp[^\\w^\\d]omeTestFile.txt");
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
                        } else //                Files.write(file.toPath(), "the text".getBytes(), StandardOpenOption.APPEND);
                        {
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
        File file = new File("D:\\tmp[^\\w^\\d]omeTestFile.txt");
        try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            final FileChannel fc = raf.getChannel();
            final FileLock fl = fc.tryLock();
            if (fl == null) {
                // Failed to acquire lock
            } else
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
        Set<String> handledNamesSet = new HashSet<>();
        for (String name : nameArray) {
            String[] handledNameArray = handleName(name);
            if (handledNameArray != null) {
                for (String handledName : handledNameArray) {
                    handledNamesSet.add(handledName);
                }
            }
        }
        int i = startIndex;
        handledNamesSet = new TreeSet<>(handledNamesSet);
        List<String> sqlList = new LinkedList<>();
        for (String name : handledNamesSet) {
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
        File file = new File("D:\\tmp[^\\w^\\d]ql.txt");
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
        return new String(Files.readAllBytes(new File("D:\\GIT\\pmp\\pmp\\module-pmp-bill-recreate[^\\w^\\d]rc\\test\\resources\\recreate[^\\w^\\d]ervices_for_2397.sql").toPath()), "utf-8");
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
                if (value != null && value.startsWith("%") && value.substring(value.length() - 3, value.length() - 2).equals("%"))
                    try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(SomeClass.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
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

    private static void createQuery() throws IOException {
        String qq = "3553520822001758\n"
                + "257996\n"
                + "7456\n"
                + "7433\n"
                + "947\n"
                + "F1088626\n"
                + "7748340880003457\n"
                + "047747\n"
                + "897\n"
                + "Б-39346\n"
                + "e2243369276\n"
                + "13924-р18\n"
                + "18/16705\n"
                + "1623474\n"
                + "820625270\n"
                + "ЧЕР 35165/18\n"
                + "37313038\n"
                + "e21381793\n"
                + "7490\n"
                + "7495\n"
                + "808860988\n"
                + "7353\n"
                + "22705-18\n"
                + "7491\n"
                + "29235/18\n"
                + "3555740875001871\n"
                + "41597Д\n"
                + "18/13696-1\n"
                + "89839-18\n"
                + "52070/18\n"
                + "7499\n"
                + "65111/851\n"
                + "310805\n"
                + "60475830274\n"
                + "7371\n"
                + "1051\n"
                + "3488/18\n"
                + "F558313\n"
                + "7053837\n"
                + "ид2534\n"
                + "11/16031\n"
                + "7320\n"
                + "7505\n"
                + "7342\n"
                + "7470\n"
                + "6734.18\n"
                + "5164009\n"
                + "П018730\n"
                + "ХОР 31215/18\n"
                + "13483-18\n"
                + "408813\n"
                + "148491П-18\n"
                + "6551-318\n"
                + "я1131843\n"
                + "В38251\n"
                + "ДД_3547800886001913\n"
                + "6357\n"
                + "6325\n"
                + "П018593\n"
                + "18820584740\n"
                + "18820584342\n"
                + "F918039\n"
                + "191263515922\n"
                + "F1066673\n"
                + "808843090\n"
                + "П140086\n"
                + "000883\n"
                + "e2239289802\n"
                + "02/6384\n"
                + "38212064\n"
                + "38213009\n"
                + "26601-18\n"
                + "22117368\n"
                + "e2238557217\n"
                + "17251101\n"
                + "311622\n"
                + "6497.18\n"
                + "6495.18\n"
                + "858/17\n"
                + "F639850\n"
                + "6358.18\n"
                + "92908д\n"
                + "808723454\n"
                + "41217/18\n"
                + "ОСТ 32390/18\n"
                + "F1121911\n"
                + "45204-08/18\n"
                + "012754/18\n"
                + "012754\n"
                + "e22392832\n"
                + "037667\n"
                + "4364/18А\n"
                + "5176594\n"
                + "31284033\n"
                + "22-80369\n"
                + "F887044\n"
                + "977\n"
                + "e2242394602\n"
                + "1000143228\n"
                + "F968681\n"
                + "15/18110\n"
                + "311185\n"
                + "310836\n"
                + "e31082603\n"
                + "820659540\n"
                + "67324/851\n"
                + "820646565\n"
                + "F1098947\n"
                + "1000204826\n"
                + "П160219\n"
                + "F1041036\n"
                + "19049463420\n"
                + "44012/018\n"
                + "18109733\n"
                + "808827413\n"
                + "1823681\n"
                + "28665\n"
                + "30187\n"
                + "15/7160\n"
                + "32237/18\n"
                + "139835\n"
                + "18109895\n"
                + "2018/11470\n"
                + "312441\n"
                + "F878547\n"
                + "3558800889001692\n"
                + "e2242465249\n"
                + "ЦЗ_3593199774000027\n"
                + "1006\n"
                + "4572\n"
                + "6062.18\n"
                + "1624829\n"
                + "1624258\n"
                + "e2243488219\n"
                + "70752/0-18\n"
                + "131781\n"
                + "191263514899\n"
                + "F1024736\n"
                + "5918 1452\n"
                + "309390\n"
                + "550052\n"
                + "F1101736\n"
                + "311826\n"
                + "8212\\18\n"
                + "221810\n"
                + "41021712212163741\n"
                + "1608720\n"
                + "3857/и-16\n"
                + "105024П-17\n"
                + "06444\n"
                + "808809358\n"
                + "54238/18\n"
                + "261265\n"
                + "808888654\n"
                + "117309539178\n"
                + "e2241679156\n"
                + "O-84071\n"
                + "5185314\n"
                + "F212702\n"
                + "F917638\n"
                + "4291580173266623\n"
                + "O-64915\n"
                + "11477399206\n"
                + "F1071035\n"
                + "1587829\n"
                + "1348\n"
                + "3556210831002198\n"
                + "808864039\n"
                + "О4407/18\n"
                + "1823939\n"
                + "14/24042\n"
                + "229284\n"
                + "17/2428-11\n"
                + "17/2428\n"
                + "e26686223\n"
                + "6629-318\n"
                + "058067\n"
                + "6537/18\n"
                + "9198.18\n"
                + "3161/17\n"
                + "П150445\n"
                + "F1073102\n"
                + "e2245609261\n"
                + "259091\n"
                + "18-18708\n"
                + "311314\n"
                + "1625765\n"
                + "7473\n"
                + "11737\n"
                + "70814/0-18\n"
                + "13711-р18\n"
                + "50175\n"
                + "18/17766\n"
                + "15528/5\n"
                + "2-2658/16\n"
                + "3-8636/17\n"
                + "044303-18\n"
                + "3556010898001855\n"
                + "1311/2016\n"
                + "21116ж/18\n"
                + "18/14716к\n"
                + "18/57478\n"
                + "77721\n"
                + "820710852\n"
                + "18109035\n"
                + "3598399790001711\n"
                + "7114.18\n"
                + "1539944\n"
                + "808649933\n"
                + "8451/С2018\n"
                + "3390/13\n"
                + "5755-18\n"
                + "5753/18\n"
                + "047613\n"
                + "16318\n"
                + "16821654667\n"
                + "F940157\n"
                + "3553440884001911\n"
                + "11898/А-2018\n"
                + "08806-18\n"
                + "1682/18\n"
                + "023329\n"
                + "1247094\n"
                + "77259\n"
                + "21010-5\n"
                + "1719067\n"
                + "2122/17\n"
                + "17/22710\n"
                + "3211-В/18\n"
                + "2018/5936\n"
                + "1245265\n"
                + "5-44-5\n"
                + "2018/10916\n"
                + "2018/10165\n"
                + "54332/2018\n"
                + "1245567\n"
                + "28590.18\n"
                + "1245569\n"
                + "3591489733000214\n"
                + "ПРОФД_3591489733000214\n"
                + "808760389\n"
                + "3555100895000116\n"
                + "3594789787000227\n"
                + "5785/18-У\n"
                + "16783971342\n"
                + "16783971347\n"
                + "П130702\n"
                + "14/21999\n"
                + "868\n"
                + "1059\n"
                + "О105319/18\n"
                + "e2239752170\n"
                + "F773207\n"
                + "3731592803\n"
                + "1003\n"
                + "757010441761\n"
                + "968\n"
                + "13/12470\n"
                + "F1135552\n"
                + "311119\n"
                + "F932244\n"
                + "820611530\n"
                + "808819944\n"
                + "820620654\n"
                + "820625740\n"
                + "e2244625940\n"
                + "F892224\n"
                + "88254д\n"
                + "17703.18\n"
                + "087857/18\n"
                + "087857\n"
                + "1545279\n"
                + "13085-р18\n"
                + "873\n"
                + "12919-р18\n"
                + "53325/18\n"
                + "6346\n"
                + "14113-р18\n"
                + "8354\\18\n"
                + "946\n"
                + "18241-с/2018\n"
                + "808772841\n"
                + "34558/18\n"
                + "806597\n"
                + "741178\n"
                + "252113\n"
                + "6344\n"
                + "Г106708/18\n"
                + "3549710837001979\n"
                + "e2238557062\n"
                + "9900643\n"
                + "я0340888\n"
                + "F86190\n"
                + "6352\n"
                + "808778268\n"
                + "18/1882\n"
                + "54735/18\n"
                + "908\n"
                + "6649.18\n"
                + "7658900846000355\n"
                + "312173\n"
                + "5972/18\n"
                + "16312\n"
                + "4/6011/17ЖК3\n"
                + "F989665\n"
                + "ДД_3556110845000230\n"
                + "Г73410/18\n"
                + "Г96827/18\n"
                + "П151144\n"
                + "П140418\n"
                + "2680/18\n"
                + "2078/18\n"
                + "3537/18\n"
                + "311693\n"
                + "7468\n"
                + "3547300879000220\n"
                + "П016047\n"
                + "6339\n"
                + "30013-18\n"
                + "381608/6\n"
                + "4475/17\n"
                + "285478\n"
                + "П015244\n"
                + "3-3551610872001832\n"
                + "ЦПС-3551610872001832\n"
                + "ц-3551610872001832\n"
                + "48-3551610872001832\n"
                + "ц-355161\n"
                + "F317367\n"
                + "e21596403\n"
                + "18/16991\n"
                + "03475-13\n"
                + "14601-р18\n"
                + "Т41685/18\n"
                + "34819-18\n"
                + "F756858\n"
                + "e2239440822\n"
                + "418558\n"
                + "808721431\n"
                + "54013/18\n"
                + "6651.18\n"
                + "885\n"
                + "808655498\n"
                + "2018-52571\n"
                + "1004\n"
                + "10067-13\n"
                + "16170759\n"
                + "П56438-16\n"
                + "6409\n"
                + "18/16613\n"
                + "6402\n"
                + "6403\n"
                + "6490.18\n"
                + "16326\n"
                + "ор3499\n"
                + "П150972\n"
                + "1800/06\n"
                + "137155\n"
                + "17094\n"
                + "Вх309 от 22.08.2017\n"
                + "F1040120\n"
                + "П140400\n"
                + "6350\n"
                + "18109897\n"
                + "13022-р18\n"
                + "13141653\n"
                + "16767/18\n"
                + "304598\n"
                + "16/11179\n"
                + "15/419\n"
                + "6438\n"
                + "6356\n"
                + "5694/18\n"
                + "П018648\n"
                + "808771294\n"
                + "34282\n"
                + "5169115\n"
                + "6342\n"
                + "887\n"
                + "П130666\n"
                + "10/16722\n"
                + "3137/18-У\n"
                + "1009\n"
                + "6354\n"
                + "1044\n"
                + "e2245403009\n"
                + "72/1/18Г\n"
                + "6362\n"
                + "ЦЗ_3555700824000028\n"
                + "П160481\n"
                + "6430\n"
                + "П015120\n"
                + "13827711453\n"
                + "F920003\n"
                + "ДД_3558110846002092\n"
                + "7478\n"
                + "П160191\n"
                + "31284031\n"
                + "11408-18\n"
                + "6437\n"
                + "2018/5482\n"
                + "П160723\n"
                + "19185/18\n"
                + "e2241204923\n"
                + "F839742\n"
                + "18/8364дк\n"
                + "951\n"
                + "1038\n"
                + "958\n"
                + "38219038\n"
                + "38219092\n"
                + "1013\n"
                + "1017\n"
                + "38736\n"
                + "37037/8\n"
                + "П121068\n"
                + "e2245398373\n"
                + "6372\n"
                + "7299\n"
                + "18/55760\n"
                + "F1143677\n"
                + "3553340827000063\n"
                + "808759128\n"
                + "17284\n"
                + "17/14087\n"
                + "П130648\n"
                + "F979919\n"
                + "820654556\n"
                + "820655120\n"
                + "23192-18\n"
                + "978\n"
                + "6366\n"
                + "5906/218\n"
                + "6364\n"
                + "2018/5369\n"
                + "e2244348814\n"
                + "F994521\n"
                + "1821180\n"
                + "21808133\n"
                + "137301\n"
                + "6427\n"
                + "312083\n"
                + "e2242638190\n"
                + "F901943\n"
                + "311670\n"
                + "6353\n"
                + "5144880\n"
                + "П131220\n"
                + "808767194\n"
                + "1020\n"
                + "16302\n"
                + "1232223/КЦП/ОМС\n"
                + "F950171\n"
                + "40700/18\n"
                + "6413\n"
                + "2018/23607\n"
                + "1026\n"
                + "18/15548\n"
                + "3551230841001696\n"
                + "28867-18\n"
                + "6423\n"
                + "Г108071/18\n"
                + "125114\n"
                + "65938/851\n"
                + "67459/018\n"
                + "О106536/18\n"
                + "820626529\n"
                + "6601.18\n"
                + "55282/18\n"
                + "242472\n"
                + "6377\n"
                + "08583-18\n"
                + "4699/50\n"
                + "4349/218\n"
                + "4545/218\n"
                + "5138747\n"
                + "9682/18\n"
                + "6375\n"
                + "366610\n"
                + "6376\n"
                + "1247908\n"
                + "7488\n"
                + "6389\n"
                + "П015132\n"
                + "6396\n"
                + "961\n"
                + "808723800\n"
                + "Б3-6180/18\n"
                + "5172831\n"
                + "5237904\n"
                + "6360\n"
                + "1029\n"
                + "301348\n"
                + "13012389\n"
                + "13012390\n"
                + "6400\n"
                + "e2240364399\n"
                + "П150994\n"
                + "5576.17\n"
                + "7470.18\n"
                + "17/2092\n"
                + "68501-2018\n"
                + "Л\n"
                + "35242-18\n"
                + "1025\n"
                + "23855-18\n"
                + "e2245801867\n"
                + "вх 616\n"
                + "17/23713\n"
                + "440Д/18\n"
                + "F1146152\n"
                + "e43118836830\n"
                + "312107\n"
                + "023579\n"
                + "09/6742\n"
                + "8166\\18\n"
                + "63560\n"
                + "6422\n"
                + "6429\n"
                + "П338565\n"
                + "П012034\n"
                + "5490\n"
                + "15/18916-2\n"
                + "150-4938\n"
                + "18/17955\n"
                + "12417-р18\n"
                + "6385\n"
                + "047666/18\n"
                + "6363\n"
                + "e2239165318\n"
                + "7755\\18\n"
                + "e31083321\n"
                + "52477574113\n"
                + "Ю15373/18\n"
                + "3557600830000285\n"
                + "6421\n"
                + "2018/5595\n"
                + "135614-р\n"
                + "e2243611168\n"
                + "1221154454559452\n"
                + "F965417\n"
                + "14019-р18\n"
                + "808641940\n"
                + "21379653\n"
                + "993\n"
                + "999910645\n"
                + "6399\n"
                + "49102-2018\n"
                + "18/1900\n"
                + "7448\n"
                + "304760\n"
                + "3591599737002097\n"
                + "311819\n"
                + "6412\n"
                + "5185323\n"
                + "F308979\n"
                + "6425\n"
                + "6411\n"
                + "8123/8\n"
                + "18/1278\n"
                + "0020147\n"
                + "0020147/18\n"
                + "18110264\n"
                + "418311812\n"
                + "291251\n"
                + "6158/18\n"
                + "6424\n"
                + "6368\n"
                + "e2242588194\n"
                + "53859/2018\n"
                + "6415\n"
                + "7435\n"
                + "05486\n"
                + "6393\n"
                + "H2018-5030\n"
                + "312082\n"
                + "П151062\n"
                + "53195\n"
                + "6395\n"
                + "Г18887/18\n"
                + "38673-С8\n"
                + "141184\n"
                + "829-Д/18\n"
                + "9484-16\n"
                + "5843/18\n"
                + "15463091661\n"
                + "18/17249\n"
                + "18/17249-1\n"
                + "67170/851\n"
                + "191263507220\n"
                + "УМО1579/17\n"
                + "6408\n"
                + "П151419\n"
                + "63122/851\n"
                + "e45226624030\n"
                + "34530108710131029531\n"
                + "8156\n"
                + "6340\n"
                + "998\n"
                + "6341\n"
                + "3590789721000121\n"
                + "6072-17\n"
                + "5954/18\n"
                + "6397\n"
                + "6345\n"
                + "6417\n"
                + "293100\n"
                + "ф6/12151\n"
                + "F1100540\n"
                + "6176/18\n"
                + "6414\n"
                + "1563186\n"
                + "П121333\n"
                + "18110309\n"
                + "6394\n"
                + "3547010898001758\n"
                + "F675693\n"
                + "6431\n"
                + "1045\n"
                + "7517\n"
                + "К1445/18\n"
                + "16052\n"
                + "5061410\n"
                + "17314\n"
                + "17/14\n"
                + "3551900823001738\n"
                + "1008\n"
                + "16/11024\n"
                + "956\n"
                + "2817/18-У\n"
                + "244513\n"
                + "209532\n"
                + "e2240971697\n"
                + "П151277\n"
                + "7532\n"
                + "1614135\n"
                + "74912\n"
                + "43903/018\n"
                + "7467\n"
                + "1028\n"
                + "935\n"
                + "955\n"
                + "1056\n"
                + "7418\n"
                + "311952\n"
                + "3551210889001733\n"
                + "6872.18\n"
                + "970\n"
                + "004472\n"
                + "e2239643452\n"
                + "12616-р18\n"
                + "924\n"
                + "560д-18\n"
                + "H2018-5090\n"
                + "2018-29345\n"
                + "7328\n"
                + "808605352\n"
                + "73772М1С18\n"
                + "5133207\n"
                + "7329";
        File file = new File("D:\\tmp\\SQL.txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        Matcher matcher = Pattern.compile("^(.+?)$", Pattern.MULTILINE).matcher(qq);
        while (matcher.find()) {
            String replaceAll = matcher.replaceAll("select '$1' as str_ from dual\nunioun all");
            Files.write(file.toPath(), replaceAll.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println(replaceAll);
        }

    }

}
