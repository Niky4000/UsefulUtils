/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.dto.pdf.ReportData;
import ru.ibs.pmp.smo.services.pdf.IntSmoAktMekSmpGenerator;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class IntSmoAktMekSmpGeneratorTest {

    public static void test() throws Exception {
        ReportData data = createReportData();
        IntSmoAktMekSmpGenerator intSmoAktMekSmpGenerator = new IntSmoAktMekSmpGenerator();
        PdfHelper pdfHelper = new PdfHelper();
        FieldUtil.setField(intSmoAktMekSmpGenerator, PdfReportServiceAbstract.class, pdfHelper, "pdf");
        byte[] createReport = intSmoAktMekSmpGenerator.createReport(data, false);
        File reportFile = new File("/home/me/tmp/reportsPdf/report.pdf");
        if (reportFile.exists()) {
            reportFile.delete();
        }
        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
    }

    private static ReportData createReportData() {
        ReportData reportData = new ReportData();
        Map<String, Object> head = new HashMap<>();
        head.put("NUM", "2000");
        head.put("DATE_CRT", "2020-12-12");
        head.put("PERIOD_STR", "2020-12");
        head.put("MO_NAME", "Какая-то ЛПУ");
        head.put("MO_MCOD", "Код ЛПУ");
        head.put("SMO_NAME", "Имя ЛПУ");
        head.put("SMO_QQ", "M1");
        head.put("CNT_S", "8888");
        head.put("SERVICE_SUM", "57394.82");
        head.put("DEFECT_CNT", "222.42");
        head.put("DEFECT_SUM", "8642.12");
        head.put("UD_SUM", "8812.54");
        head.put("SERVICE_SUM_ALL", "100000");
        head.put("DEFECT_PP", "32.84");
        reportData.setHead(head);
//        reportData.setSmoId(2884L);
        return reportData;
    }
}
