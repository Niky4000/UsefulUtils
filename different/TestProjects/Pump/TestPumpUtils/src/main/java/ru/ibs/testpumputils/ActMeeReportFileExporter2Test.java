package ru.ibs.testpumputils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.smo.model.ExaminationDocument;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.pmp.smo.dto.pdf.ActMeePdfData2;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActMeeReportFileExporter2;
import ru.ibs.pmp.smo.report.model.ActMeePdfDataBody2;
import ru.ibs.pmp.smo.report.model.ActMeePdfDataTable3;
import ru.ibs.pmp.smo.services.pdf.ActMeeReportGenerator2;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class ActMeeReportFileExporter2Test {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            ActMeeReportFileExporter2 actMeeReportFileExporter2 = new ActMeeReportFileExporter2() {
                @Override
                protected ActMeePdfData2 getData(Long docId) {
                    Object[] stringArray = Db.select(smoSessionFactory, session -> (Object[]) session.createSQLQuery("SELECT ACT_ID,ACT_NUMBER,ACT_DATE,EXPERTISE_DATE,EXPERT_FIO,CONTROL_ORGANIZATION,LPU_NAME,BILL_ID,SN_POL,C_I,SEX,BIRTH_DATE,DS,DS_2,CURE_START_DATE,CURE_END_DATE,CURE_SUM,KD_FACT,DOCTOR_FIO,DS_0,NAPR_ORG_PROF,HOSP_DATE,WITHDRAWAL_SUM,FINE_SUM,SUM_TO_PAY,PERIOD_START,PERIOD_END,EXPERTISE_START_DATE,EXPERTISE_END_DATE,BILL_DATE,COUNT_SL FROM TABLE (PMP_SMO1.PMP_EXAM_ACT_MEE.INIT_GET_SERV_DAT(:id))").setParameter("id", docId).uniqueResult());
                    List<Object[]> stringArrayList = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,RN,SN_POL,C_I,START_DATE,END_DATE,DS,PAYED_SUM FROM TABLE (PMP_SMO1.PMP_EXAM_ACT_MEE.INIT_GET_SERV_TABLE(123))").setParameter("id", docId).list());
//ACT_ID,
//ACT_NUMBER,
                    String actNumber = (String) stringArray[1];
//ACT_DATE,
                    String actDate = (String) stringArray[2];
//EXPERTISE_DATE,
                    String expertiseDate = (String) stringArray[3];
//EXPERT_FIO,
                    String expertFio = (String) stringArray[4];
//CONTROL_ORGANIZATION,
                    String controlOrganization = (String) stringArray[5];
//LPU_NAME,
                    String lpuName = (String) stringArray[6];
//BILL_ID,
                    String billId = (String) stringArray[7];
//SN_POL,
                    String snPol = (String) stringArray[8];
//C_I,
                    String ci = (String) stringArray[9];
//SEX,
                    String sex = (String) stringArray[10];
//BIRTH_DATE,
                    String birthDate = (String) stringArray[11];
//DS,
                    String ds = (String) stringArray[12];
//DS_2,
                    String ds2 = (String) stringArray[13];
//CURE_START_DATE,
                    String cureStartDate = (String) stringArray[14];
//CURE_END_DATE,
                    String cureEndDate = (String) stringArray[15];
////CURE_SUM,
                    String cureSum = (String) stringArray[16];
//KD_FACT,
                    String kdFact = (String) stringArray[17];
//DOCTOR_FIO,
                    String doctorFio = (String) stringArray[18];
//DS_0,
                    String ds0 = (String) stringArray[19];
//NAPR_ORG_PROF,
                    String naprOrgProf = (String) stringArray[20];
//HOSP_DATE,
                    String hospDate = (String) stringArray[21];
//WITHDRAWAL_SUM,
                    String withdrawalSum = (String) stringArray[22];
//FINE_SUM,
                    String fineSum = (String) stringArray[23];
//SUM_TO_PAY,
                    String sumToPay = (String) stringArray[24];
//PERIOD_START,
                    String periodStart = (String) stringArray[25];
//PERIOD_END,
                    String periodEnd = (String) stringArray[26];
//EXPERTISE_START_DATE,
                    String expertiseStartDate = (String) stringArray[27];
//EXPERTISE_END_DATE,
                    String expertiseEndDate = (String) stringArray[28];
//BILL_DATE,
                    String billDate = (String) stringArray[29];
//COUNT_SL     
                    String countSl = (String) stringArray[30];

                    List<ActMeePdfDataTable3> dataList = stringArrayList.stream().map(obj -> {
                        ActMeePdfDataTable3 actMeePdfDataTable3 = new ActMeePdfDataTable3();
//ACT_ID,
                        String actId_ = (String) stringArray[0];
//RN,
                        String rn_ = (String) stringArray[1];
//SN_POL,
                        String snPol_ = (String) stringArray[2];
//C_I,
                        String ci_ = (String) stringArray[3];
//START_DATE,
                        String startDate_ = (String) stringArray[4];
//END_DATE,
                        String endDate_ = (String) stringArray[5];
//DS,
                        String ds_ = (String) stringArray[6];
//PAYED_SUM
                        String payerSum_ = (String) stringArray[7];
                        actMeePdfDataTable3.setActId(actId_);
                        actMeePdfDataTable3.setCi(ci_);
                        actMeePdfDataTable3.setDs(ds_);
                        actMeePdfDataTable3.setEndDate(endDate_);
                        actMeePdfDataTable3.setPayerSum(payerSum_);
                        actMeePdfDataTable3.setRn(rn_);
                        actMeePdfDataTable3.setSnPol(snPol_);
                        actMeePdfDataTable3.setStartDate(startDate_);
                        return actMeePdfDataTable3;
                    }).collect(Collectors.toList());
                    ActMeePdfData2 actMeePdfData = new ActMeePdfData2();
                    actMeePdfData.setT(dataList);
                    ActMeePdfDataBody2 body = new ActMeePdfDataBody2();
                    body.setActDate(actDate);
                    body.setActNumber(actNumber);
                    body.setBillDate(billDate);
                    body.setBillId(billId);
                    body.setBirthDay(birthDate);
                    body.setCi(ci);
                    body.setControlOrganization(controlOrganization);
                    body.setCountSl(countSl);
                    body.setCureEndDate(cureEndDate);
                    body.setCureStartDate(cureStartDate);
                    body.setCureSum(cureSum);
                    body.setDoctorFio(doctorFio);
                    body.setDs(ds);
                    body.setDs0(ds0);
                    body.setDs2(ds2);
                    body.setExpertFio(expertFio);
                    body.setExpertiseDate(expertiseDate);
                    body.setExpertiseEndDate(expertiseEndDate);
                    body.setExpertiseStartDate(expertiseStartDate);
                    body.setFineSum(fineSum);
                    body.setHospDate(hospDate);
                    body.setKdFact(kdFact);
                    body.setLpuName(lpuName);
                    body.setNaprOrgProf(naprOrgProf);
                    body.setPeriodEnd(periodEnd);
                    body.setPeriodStart(periodStart);
                    body.setSex(sex);
                    body.setSnPol(snPol);
                    body.setSumToPay(sumToPay);
                    body.setWithdrawalSum(withdrawalSum);
                    actMeePdfData.setB(body);
                    return actMeePdfData;
                }
            };
            ActMeeReportGenerator2 actMeeReportGenerator2 = new ActMeeReportGenerator2() {
                @Override
                public String getTemplatePath() {
                    return "/home/me/Downloads/act_mee2.docx";
                }
            };
            FieldUtil.setField(actMeeReportFileExporter2, ActMeeReportFileExporter2.class, actMeeReportGenerator2, "reportGenerator");
            Method getReportPdfMethod = ActMeeReportFileExporter2.class.getDeclaredMethod("getReportPdf", ReportExportContext.class);
            getReportPdfMethod.setAccessible(true);
            ReportExportContext context = new ReportExportContext(null, null);
            ExaminationDocument examinationDocument = new ExaminationDocument();
            examinationDocument.setId(123L);
            context.setDoc(examinationDocument);
            byte[] createReport = (byte[]) getReportPdfMethod.invoke(actMeeReportFileExporter2, context);
            File reportFile = new File("/home/me/tmp/reportsPdf/report.docx");
            if (reportFile.exists()) {
                reportFile.delete();
            }
            Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
        } finally {
            smoSessionFactory.close();
        }
    }
}
