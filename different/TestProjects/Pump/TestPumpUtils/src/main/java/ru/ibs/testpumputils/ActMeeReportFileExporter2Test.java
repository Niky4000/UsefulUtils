package ru.ibs.testpumputils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.smo.model.ExaminationDocument;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActMeeReportFileExporter2;
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
            ActMeeReportFileExporter2 actMeeReportFileExporter2 = new ActMeeReportFileExporter2();
            ActMeeReportGenerator2 actMeeReportGenerator2 = new ActMeeReportGenerator2() {
                @Override
                public String getTemplatePath() {
                    return "/home/me/Downloads/act_mee2.docx";
                }
            };
            FieldUtil.setField(actMeeReportFileExporter2, ActMeeReportFileExporter2.class, actMeeReportGenerator2, "reportGenerator");
            FieldUtil.setField(actMeeReportFileExporter2, ActMeeReportFileExporter2.class, smoSessionFactory, "smoSessionFactory");
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
