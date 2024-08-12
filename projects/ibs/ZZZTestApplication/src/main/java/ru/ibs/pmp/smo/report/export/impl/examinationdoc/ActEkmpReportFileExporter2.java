package ru.ibs.pmp.smo.report.export.impl.examinationdoc;

import com.itextpdf.text.DocumentException;
import fr.opensagres.xdocreport.core.XDocReportException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlOptions;
import org.hibernate.SessionFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.pmp.smo.dto.pdf.ActEkmpPdfData3;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody2;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataTable4;
import ru.ibs.pmp.smo.report.model.ActMeePdfDataTable3;
import ru.ibs.pmp.smo.services.pdf.PmpWordWriter;

/**
 *
 * @author me
 */
public class ActEkmpReportFileExporter2 {

    @Autowired
    @Qualifier("smoSessionFactory")
    SessionFactory smoSessionFactory;
    @Autowired
    @Qualifier("actEkmpReportGenerator2")
    private PmpWordWriter<ActEkmpPdfData3> reportGenerator;
    @Autowired
    @Qualifier("actEkmpReportGenerator22")
    private PmpWordWriter<ActEkmpPdfDataBody3> reportGenerator2;

//    @Override
    protected byte[] getReportPdf(Long docId) {
        ActEkmpPdfData3 reportPdfData = getData(docId);
        ByteArrayInputStream byteArrayInputStream;
        try {
            byteArrayInputStream = new ByteArrayInputStream(reportGenerator.createReport(reportPdfData));
        } catch (DocumentException e) {
            throw new RuntimeException(this.getClass() + " pdf error", e);
        } catch (IOException e) {
            throw new RuntimeException(this.getClass() + " io exception", e);
        } catch (XDocReportException e) {
            throw new RuntimeException(this.getClass() + " xDoc exception", e);
        } catch (Exception e) {
            throw new RuntimeException(this.getClass() + " Exception", e);
        }
        List<ActEkmpPdfDataBody3> b2 = reportPdfData.getB2();
        List<InputStream> byteArrayInputStreamList = new ArrayList<>(b2.size());
        for (ActEkmpPdfDataBody3 actEkmpPdfDataBody3 : b2) {
            try {
                byteArrayInputStreamList.add(new ByteArrayInputStream(reportGenerator2.createReport(actEkmpPdfDataBody3)));
            } catch (DocumentException e) {
                throw new RuntimeException(this.getClass() + " pdf error", e);
            } catch (IOException e) {
                throw new RuntimeException(this.getClass() + " io exception", e);
            } catch (XDocReportException e) {
                throw new RuntimeException(this.getClass() + " xDoc exception", e);
            } catch (Exception e) {
                throw new RuntimeException(this.getClass() + " Exception", e);
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            merge(byteArrayInputStream, byteArrayInputStreamList, byteArrayOutputStream);
        } catch (Exception ex) {
            throw new RuntimeException(this.getClass() + " Merge Exception", ex);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static void merge(InputStream src1, List<InputStream> src2List, OutputStream dest) throws Exception {
        OPCPackage src1Package = OPCPackage.open(src1);
        XWPFDocument src1Document = new XWPFDocument(src1Package);
        CTBody src1Body = src1Document.getDocument().getBody();
        for (InputStream src2 : src2List) {
            OPCPackage src2Package = OPCPackage.open(src2);
            XWPFDocument src2Document = new XWPFDocument(src2Package);
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

    protected ActEkmpPdfData3 getData(Long docId) {
//        Object[] stringArray = Db.select(smoSessionFactory, session -> (Object[]) session.createSQLQuery("SELECT ACT_ID,ACT_NUMBER,ACT_DATE,EXPERT_FIO_ID,CONTROL_ORGANIZATION,PATIENT_FIO,SN_POL,LPU_OTD_NAME,DOCTOR_FIO,C_I,CURE_START_DATE,CURE_END_DATE,DS,SUM_TO_PAY,WITHDRAWAL_SUM,FINE_SUM,EXPERTISE_END_DATE,PERIOD_START,PERIOD_END,COUNT_SL FROM TABLE (PMP_SMO.PMP_EXAM_ACT_EKMP.INIT_GET_SERV_DAT(:id))").setParameter("id", docId).uniqueResult());
//        List<Object[]> stringArrayList = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,RN,SN_POL,C_I,START_DATE,END_DATE,DS,PAYED_SUM FROM TABLE (PMP_SMO.PMP_EXAM_ACT_EKMP.INIT_GET_SERV_TABLE(:id))").setParameter("id", docId).list());
//        List<Object[]> stringArrayList2 = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,RN,FIL,C_I,OSN,WITHDRAUAL_PERCENT,WITHDRAUAL_SUM,FINE_SUM FROM TABLE (PMP_SMO.PMP_EXAM_ACT_EKMP.INIT_GET_SERV_ERR_TABLE(:id))").setParameter("id", docId).list());
//        List<Object[]> stringArrayList3 = Db.select(smoSessionFactory, session -> (List<Object[]>) session.createSQLQuery("SELECT ACT_ID,ACT_NUM,ACT_DATE,CONTROL_ORGANIZATION,C_I,DOCTOR_FIO,SN_POL,SEX,BIRTH_DATE,LPU_NAME,BILL_ID,BILL_DATE,KD_FACT,CURE_SUM,HDS,EXPERT_FIO_ID,EXPERTISE_END_DATE,IS_EMERGENCY,ISHOD,SURGERY,DS,DS_3,DS_2,DS_PAT,DS3_PAT,DS2_PAT,EXPERT_FIO FROM TABLE (PMP_SMO.PMP_EXAM_ACT_EKMP.INIT_GET_SERV_ZAK_TABLE(:id))").setParameter("id", docId).list());

        Object[] stringArray = new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"};
        List<Object[]> stringArrayList = Arrays.asList(new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"}, new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"});
        List<Object[]> stringArrayList2 = Arrays.asList(new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"}, new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"});
        List<Object[]> stringArrayList3 = Arrays.asList(new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"}, new Object[]{"test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test"});

        ActEkmpPdfDataBody2 actEkmpPdfDataBody2 = new ActEkmpPdfDataBody2();
//ACT_ID,
//ACT_NUMBER,
        actEkmpPdfDataBody2.setActNumber((String) stringArray[1]);
//ACT_DATE,
        actEkmpPdfDataBody2.setActDate((String) stringArray[2]);
//EXPERT_FIO_ID,
        actEkmpPdfDataBody2.setExpertFioId((String) stringArray[3]);
//CONTROL_ORGANIZATION,
        actEkmpPdfDataBody2.setControlOrganization((String) stringArray[4]);
//PATIENT_FIO,
        actEkmpPdfDataBody2.setPatientFio((String) stringArray[5]);
//SN_POL,
        actEkmpPdfDataBody2.setSnPol((String) stringArray[6]);
//LPU_OTD_NAME,
        actEkmpPdfDataBody2.setLpuOtdName((String) stringArray[7]);
//DOCTOR_FIO,
        actEkmpPdfDataBody2.setDoctorFio((String) stringArray[8]);
//C_I,
        actEkmpPdfDataBody2.setCi((String) stringArray[9]);
//CURE_START_DATE,
        actEkmpPdfDataBody2.setCureStartDate((String) stringArray[10]);
//CURE_END_DATE,
        actEkmpPdfDataBody2.setCureEndDate((String) stringArray[11]);
//DS,
        actEkmpPdfDataBody2.setDs((String) stringArray[12]);
//SUM_TO_PAY,
        actEkmpPdfDataBody2.setSumToPay((String) stringArray[13]);
//WITHDRAWAL_SUM,
        actEkmpPdfDataBody2.setWithdrawalSum((String) stringArray[14]);
//FINE_SUM,
        actEkmpPdfDataBody2.setFineSum((String) stringArray[15]);
//EXPERTISE_END_DATE,
        actEkmpPdfDataBody2.setExpertiseEndDate((String) stringArray[16]);
//PERIOD_START,
        actEkmpPdfDataBody2.setPeriodStart((String) stringArray[17]);
//PERIOD_END,
        actEkmpPdfDataBody2.setPeriodEnd((String) stringArray[18]);
//COUNT_SL
        actEkmpPdfDataBody2.setCountSl((String) stringArray[19]);

        List<ActMeePdfDataTable3> actMeePdfDataTable3List = stringArrayList.stream().map(obj -> {
            ActMeePdfDataTable3 actMeePdfDataTable3 = new ActMeePdfDataTable3();
//RN,
            actMeePdfDataTable3.setRn((String) stringArray[1]);
//SN_POL,
            actMeePdfDataTable3.setSnPol((String) stringArray[2]);
//C_I,
            actMeePdfDataTable3.setCi((String) stringArray[3]);
//START_DATE,
            actMeePdfDataTable3.setStartDate((String) stringArray[4]);
//END_DATE,
            actMeePdfDataTable3.setEndDate((String) stringArray[5]);
//DS,
            actMeePdfDataTable3.setDs((String) stringArray[6]);
//PAYED_SUM
            actMeePdfDataTable3.setPayerSum((String) stringArray[7]);
            return actMeePdfDataTable3;
        }).collect(Collectors.toList());

        List<ActEkmpPdfDataTable4> actEkmpPdfDataTable4List = stringArrayList2.stream().map(obj -> {
            ActEkmpPdfDataTable4 actEkmpPdfDataTable4 = new ActEkmpPdfDataTable4();
//RN,
            actEkmpPdfDataTable4.setRn((String) stringArray[1]);
//FIL,
            actEkmpPdfDataTable4.setFil((String) stringArray[2]);
//C_I,
            actEkmpPdfDataTable4.setCi((String) stringArray[3]);
//OSN,
            actEkmpPdfDataTable4.setOsn((String) stringArray[4]);
//WITHDRWAUAL_PERCENT,
            actEkmpPdfDataTable4.setWithdrawalPercent((String) stringArray[5]);
//WITHDRWAUAL_SUM,
            actEkmpPdfDataTable4.setWithdrawalSum((String) stringArray[6]);
//FINE_SUM
            actEkmpPdfDataTable4.setFineSum((String) stringArray[7]);
            return actEkmpPdfDataTable4;
        }).collect(Collectors.toList());

        List<ActEkmpPdfDataBody3> actEkmpPdfDataBody3List = stringArrayList3.stream().map(obj -> {
            ActEkmpPdfDataBody3 actEkmpPdfDataBody3 = new ActEkmpPdfDataBody3();
//ACT_ID,
//ACT_NUM,
            actEkmpPdfDataBody3.setActNumber((String) stringArray[1]);
//ACT_DATE,
            actEkmpPdfDataBody3.setActDate((String) stringArray[1]);
//CONTROL_ORGANIZATION,
            actEkmpPdfDataBody3.setControlOrganization((String) stringArray[1]);
//C_I,
            actEkmpPdfDataBody3.setCi((String) stringArray[1]);
//DOCTOR_FIO,
            actEkmpPdfDataBody3.setDoctorFio((String) stringArray[1]);
//SN_POL,
            actEkmpPdfDataBody3.setSnPol((String) stringArray[1]);
//SEX,
            actEkmpPdfDataBody3.setSex((String) stringArray[1]);
//BIRTH_DATE,
            actEkmpPdfDataBody3.setBirthDay((String) stringArray[1]);
//LPU_NAME,
            actEkmpPdfDataBody3.setLpuName((String) stringArray[1]);
//BILL_ID,
            actEkmpPdfDataBody3.setBillId((String) stringArray[1]);
//BILL_DATE,
            actEkmpPdfDataBody3.setBillDate((String) stringArray[1]);
//KD_FACT,
            actEkmpPdfDataBody3.setKdFact((String) stringArray[1]);
//CURE_SUM,
            actEkmpPdfDataBody3.setCureSum((String) stringArray[1]);
//HDS,
            actEkmpPdfDataBody3.setHds((String) stringArray[1]);
//EXPERT_FIO_ID,
            actEkmpPdfDataBody3.setExpertFioId((String) stringArray[1]);
//EXPERTISE_END_DATE,
            actEkmpPdfDataBody3.setExpertiseEndDate((String) stringArray[1]);
//EMERGENCY,
            actEkmpPdfDataBody3.setEmergency((String) stringArray[1]);
//ISHOD,
            actEkmpPdfDataBody3.setIshod((String) stringArray[1]);
//SURGERY,
            actEkmpPdfDataBody3.setSurgery((String) stringArray[1]);
//DS,
            actEkmpPdfDataBody3.setDs((String) stringArray[1]);
//DS_3,
            actEkmpPdfDataBody3.setDs3((String) stringArray[1]);
//DS_2,
            actEkmpPdfDataBody3.setDs2((String) stringArray[1]);
//DS_PAT,
            actEkmpPdfDataBody3.setDsPat((String) stringArray[1]);
//DS3_PAT,
            actEkmpPdfDataBody3.setDs3Pat((String) stringArray[1]);
//DS2_PAT,
            actEkmpPdfDataBody3.setDs2Pat((String) stringArray[1]);
//EXPERT_FIO
            actEkmpPdfDataBody3.setExpertFio((String) stringArray[1]);
            return actEkmpPdfDataBody3;
        }).collect(Collectors.toList());

        ActEkmpPdfData3 actEkmpPdfData3 = new ActEkmpPdfData3();
        actEkmpPdfData3.setB(actEkmpPdfDataBody2);
        actEkmpPdfData3.setT(actMeePdfDataTable3List);
        actEkmpPdfData3.setT2(actEkmpPdfDataTable4List);
        actEkmpPdfData3.setB2(actEkmpPdfDataBody3List);
        return actEkmpPdfData3;
    }
}
