//package ru.ibs.testpumputils;
//
//import java.io.File;
//import java.lang.reflect.Method;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//import ru.ibs.pmp.api.smo.model.ExaminationDocument;
//import ru.ibs.pmp.smo.dto.pdf.ActEkmpPdfData3;
//import ru.ibs.pmp.smo.export.mo.ReportExportContext;
//import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActEkmpReportFileExporter2;
//import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody2;
//import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;
//import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataTable4;
//import ru.ibs.pmp.smo.report.model.ActMeePdfDataTable3;
//import ru.ibs.pmp.smo.services.pdf.ActEkmpReportGenerator2;
//import ru.ibs.pmp.smo.services.pdf.ActEkmpReportGenerator22;
//import ru.ibs.testpumputils.utils.FieldUtil;
//
///**
// *
// * @author me
// */
//public class ActEkmpReportFileExporter2Test {
//
////    static SessionFactory smoSessionFactory;
//    public static void test() throws Exception {
////        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
////        try {
//        ActEkmpReportFileExporter2 actEkmpReportFileExporter2 = new ActEkmpReportFileExporter2() {
//            @Override
//            protected ActEkmpPdfData3 getData(Long docId) {
////		Object[] stringArray = Db.select(smoSessionFactory, session -> (Object[]) session.createSQLQuery("SELECT ACT_ID,ACT_NUMBER,ACT_DATE,EXPERT_FIO_ID,CONTROL_ORGANIZATION,PATIENT_FIO,SN_POL,LPU_OTD_NAME,DOCTOR_FIO,C_I,CURE_START_DATE,CURE_END_DATE,DS,SUM_TO_PAY,WITHDRAWAL_SUM,FINE_SUM,EXPERTISE_END_DATE,PERIOD_START,PERIOD_END,COUNT_SL FROM TABLE (PMP_EXAM_ACT_EKMP.INIT_GET_SERV_DAT(:id))").setParameter("id", docId).uniqueResult());
////		List<Object[]> stringArrayList = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,RN,SN_POL,C_I,START_DATE,END_DATE,DS,PAYED_SUM FROM TABLE (PMP_EXAM_ACT_EKMP.INIT_GET_SERV_TABLE(:id))").setParameter("id", docId).list());
////		List<Object[]> stringArrayList2 = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,RN,FIL,C_I,OSN,WITHDRAWAL_PERCENT,WITHDRAWAL_SUM,FINE_SUM FROM TABLE (PMP_EXAM_ACT_EKMP.INIT_GET_SERV_ERR_TABLE(:id))").setParameter("id", docId).list());
////		List<Object[]> stringArrayList3 = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,ACT_NUM,ACT_DATE,CONTROL_ORGANIZATION,C_I,DOCTOR_FIO,SN_POL,SEX,BIRTH_DATE,LPU_NAME,BILL_ID,BILL_DATE,KD_FACT,CURE_SUM,HDS,EXPERT_FIO_ID,EXPERTISE_END_DATE,IS_EMERGENCY,ISHOD,SURGERY,DS,DS_3,DS_2,DS_PAT,DS3_PAT,DS2_PAT,EXPERT_FIO FROM TABLE (PMP_EXAM_ACT_EKMP.INIT_GET_SERV_ZAK_TABLE(:id))").setParameter("id", docId).list());
//                Object[] stringArray = createTestData();
//                List<Object[]> stringArrayList = Arrays.asList(createTestData(), createTestData());
//                List<Object[]> stringArrayList2 = Arrays.asList(createTestData(), createTestData());
//                List<Object[]> stringArrayList3 = Arrays.asList(createTestData(), createTestData());
//
//                ActEkmpPdfDataBody2 actEkmpPdfDataBody2 = new ActEkmpPdfDataBody2();
////ACT_ID,
////ACT_NUMBER,
//                actEkmpPdfDataBody2.setActNumber(getValue(stringArray, 1));
////ACT_DATE,
//                actEkmpPdfDataBody2.setActDate(getValue(stringArray, 2));
////EXPERT_FIO_ID,
//                actEkmpPdfDataBody2.setExpertFioId(getValue(stringArray, 3));
////CONTROL_ORGANIZATION,
//                actEkmpPdfDataBody2.setControlOrganization(getValue(stringArray, 4));
////PATIENT_FIO,
//                actEkmpPdfDataBody2.setPatientFio(getValue(stringArray, 5));
////SN_POL,
//                actEkmpPdfDataBody2.setSnPol(getValue(stringArray, 6));
////LPU_OTD_NAME,
//                actEkmpPdfDataBody2.setLpuOtdName(getValue(stringArray, 7));
////DOCTOR_FIO,
//                actEkmpPdfDataBody2.setDoctorFio(getValue(stringArray, 8));
////C_I,
//                actEkmpPdfDataBody2.setCi(getValue(stringArray, 9));
////CURE_START_DATE,
//                actEkmpPdfDataBody2.setCureStartDate(getValue(stringArray, 10));
////CURE_END_DATE,
//                actEkmpPdfDataBody2.setCureEndDate(getValue(stringArray, 11));
////DS,
//                actEkmpPdfDataBody2.setDs(getValue(stringArray, 12));
////SUM_TO_PAY,
//                actEkmpPdfDataBody2.setSumToPay(getValue(stringArray, 13));
////WITHDRAWAL_SUM,
//                actEkmpPdfDataBody2.setWithdrawalSum(getValue(stringArray, 14));
////FINE_SUM,
//                actEkmpPdfDataBody2.setFineSum(getValue(stringArray, 15));
////EXPERTISE_END_DATE,
//                actEkmpPdfDataBody2.setExpertiseEndDate(getValue(stringArray, 16));
////PERIOD_START,
//                actEkmpPdfDataBody2.setPeriodStart(getValue(stringArray, 17));
////PERIOD_END,
//                actEkmpPdfDataBody2.setPeriodEnd(getValue(stringArray, 18));
////COUNT_SL
//                actEkmpPdfDataBody2.setCountSl(getValue(stringArray, 19));
//                List<ActMeePdfDataTable3> actMeePdfDataTable3List = stringArrayList.stream().map(obj -> {
//                    ActMeePdfDataTable3 actMeePdfDataTable3 = new ActMeePdfDataTable3();
////RN,
//                    actMeePdfDataTable3.setRn(getValue(obj, 1));
////SN_POL,
//                    actMeePdfDataTable3.setSnPol(getValue(obj, 2));
////C_I,
//                    actMeePdfDataTable3.setCi(getValue(obj, 3));
////START_DATE,
//                    actMeePdfDataTable3.setStartDate(getValue(obj, 4));
////END_DATE,
//                    actMeePdfDataTable3.setEndDate(getValue(obj, 5));
////DS,
//                    actMeePdfDataTable3.setDs(getValue(obj, 6));
////PAYED_SUM
//                    actMeePdfDataTable3.setPayerSum(getValue(obj, 7));
//                    return actMeePdfDataTable3;
//                }).collect(Collectors.toList());
//                List<ActEkmpPdfDataTable4> actEkmpPdfDataTable4List = stringArrayList2.stream().map(obj -> {
//                    ActEkmpPdfDataTable4 actEkmpPdfDataTable4 = new ActEkmpPdfDataTable4();
////RN,
//                    actEkmpPdfDataTable4.setRn(getValue(obj, 1));
////FIL,
//                    actEkmpPdfDataTable4.setFil(getValue(obj, 2));
////C_I,
//                    actEkmpPdfDataTable4.setCi(getValue(obj, 3));
////OSN,
//                    actEkmpPdfDataTable4.setOsn(getValue(obj, 4));
////WITHDRWAUAL_PERCENT,
//                    actEkmpPdfDataTable4.setWithdrawalPercent(getValue(obj, 5));
////WITHDRWAUAL_SUM,
//                    actEkmpPdfDataTable4.setWithdrawalSum(getValue(obj, 6));
////FINE_SUM
//                    actEkmpPdfDataTable4.setFineSum(getValue(obj, 7));
//                    return actEkmpPdfDataTable4;
//                }).collect(Collectors.toList());
//                List<ActEkmpPdfDataBody3> actEkmpPdfDataBody3List = stringArrayList3.stream().map(obj -> {
//                    ActEkmpPdfDataBody3 actEkmpPdfDataBody3 = new ActEkmpPdfDataBody3();
////ACT_ID,
////ACT_NUM,
//                    actEkmpPdfDataBody3.setActNumber(getValue(obj, 1));
////ACT_DATE,
//                    actEkmpPdfDataBody3.setActDate(getValue(obj, 2));
////CONTROL_ORGANIZATION,
//                    actEkmpPdfDataBody3.setControlOrganization(getValue(obj, 3));
////C_I,
//                    actEkmpPdfDataBody3.setCi(getValue(obj, 4));
////DOCTOR_FIO,
//                    actEkmpPdfDataBody3.setDoctorFio(getValue(obj, 5));
////SN_POL,
//                    actEkmpPdfDataBody3.setSnPol(getValue(obj, 6));
////SEX,
//                    actEkmpPdfDataBody3.setSex(getValue(obj, 7));
////BIRTH_DATE,
//                    actEkmpPdfDataBody3.setBirthDay(getValue(obj, 8));
////LPU_NAME,
//                    actEkmpPdfDataBody3.setLpuName(getValue(obj, 9));
////BILL_ID,
//                    actEkmpPdfDataBody3.setBillId(getValue(obj, 10));
////BILL_DATE,
//                    actEkmpPdfDataBody3.setBillDate(getValue(obj, 11));
////KD_FACT,
//                    actEkmpPdfDataBody3.setKdFact(getValue(obj, 12));
////CURE_SUM,
//                    actEkmpPdfDataBody3.setCureSum(getValue(obj, 13));
////HDS,
//                    actEkmpPdfDataBody3.setHds(getValue(obj, 14));
////EXPERT_FIO_ID,
//                    actEkmpPdfDataBody3.setExpertFioId(getValue(obj, 15));
////EXPERTISE_END_DATE,
//                    actEkmpPdfDataBody3.setExpertiseEndDate(getValue(obj, 16));
////EMERGENCY,
//                    actEkmpPdfDataBody3.setEmergency(getValue(obj, 17));
////ISHOD,
//                    actEkmpPdfDataBody3.setIshod(getValue(obj, 18));
////SURGERY,
//                    actEkmpPdfDataBody3.setSurgery(getValue(obj, 19));
////DS,
//                    actEkmpPdfDataBody3.setDs(getValue(obj, 20));
////DS_3,
//                    actEkmpPdfDataBody3.setDs3(getValue(obj, 21));
////DS_2,
//                    actEkmpPdfDataBody3.setDs2(getValue(obj, 22));
////DS_PAT,
//                    actEkmpPdfDataBody3.setDsPat(getValue(obj, 23));
////DS3_PAT,
//                    actEkmpPdfDataBody3.setDs3Pat(getValue(obj, 24));
////DS2_PAT,
//                    actEkmpPdfDataBody3.setDs2Pat(getValue(obj, 25));
////EXPERT_FIO
//                    actEkmpPdfDataBody3.setExpertFio(getValue(obj, 26));
//                    return actEkmpPdfDataBody3;
//                }).collect(Collectors.toList());
//                ActEkmpPdfData3 actEkmpPdfData3 = new ActEkmpPdfData3();
//                actEkmpPdfData3.setB(actEkmpPdfDataBody2);
//                actEkmpPdfData3.setT(actMeePdfDataTable3List);
//                actEkmpPdfData3.setT2(actEkmpPdfDataTable4List);
//                actEkmpPdfData3.setB2(actEkmpPdfDataBody3List);
//                return actEkmpPdfData3;
//            }
//        };
//        ActEkmpReportGenerator2 actEkmpReportGenerator2 = new ActEkmpReportGenerator2() {
//            @Override
//            public String getTemplatePath() {
//                return "/home/me/GIT/pmp/pmp/module-smo/src/main/resources/word_templates/act_ekmp2.docx";
//            }
//        };
//        ActEkmpReportGenerator22 actEkmpReportGenerator22 = new ActEkmpReportGenerator22() {
//            @Override
//            public String getTemplatePath() {
//                return "/home/me/GIT/pmp/pmp/module-smo/src/main/resources/word_templates/act_ekmp2_2.docx";
//            }
//        };
//        FieldUtil.setField(actEkmpReportFileExporter2, ActEkmpReportFileExporter2.class, actEkmpReportGenerator2, "reportGenerator");
//        FieldUtil.setField(actEkmpReportFileExporter2, ActEkmpReportFileExporter2.class, actEkmpReportGenerator22, "reportGenerator2");
////        FieldUtil.setField(actEkmpReportFileExporter2, ActEkmpReportFileExporter2.class, smoSessionFactory, "smoSessionFactory");
//        Method getReportPdfMethod = ActEkmpReportFileExporter2.class.getDeclaredMethod("getReportPdf", ReportExportContext.class);
//        getReportPdfMethod.setAccessible(true);
//        ReportExportContext context = new ReportExportContext(null, null);
//        ExaminationDocument examinationDocument = new ExaminationDocument();
//        examinationDocument.setId(123L);
//        context.setDoc(examinationDocument);
//        byte[] createReport = (byte[]) getReportPdfMethod.invoke(actEkmpReportFileExporter2, context);
//        File reportFile = new File("/home/me/tmp/reportsPdf/report.docx");
//        if (reportFile.exists()) {
//            reportFile.delete();
//        }
//        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
////        } finally {
////            smoSessionFactory.close();
////        }
//    }
//
//    private static Object[] createTestData() {
//        return new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"};
//    }
//
//    private static String getValue(Object[] stringArray, int index) {
//        return (String) stringArray[index] != null ? (String) stringArray[index] : "---";
//    }
//}
