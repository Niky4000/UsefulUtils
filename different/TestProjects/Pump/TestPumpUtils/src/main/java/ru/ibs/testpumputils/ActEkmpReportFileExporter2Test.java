package ru.ibs.testpumputils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.smo.model.ExaminationDocument;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActEkmpReportFileExporter2;
import ru.ibs.pmp.smo.services.pdf.ActEkmpReportGenerator2;
import ru.ibs.pmp.smo.services.pdf.ActEkmpReportGenerator22;
import ru.ibs.pmp.smo.services.pdf.ActMeeReportGenerator2;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class ActEkmpReportFileExporter2Test {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            ActEkmpReportFileExporter2 actEkmpReportFileExporter2 = new ActEkmpReportFileExporter2();
            ActEkmpReportGenerator2 actEkmpReportGenerator2 = new ActEkmpReportGenerator2() {
                @Override
                public String getTemplatePath() {
                    return "/home/me/Downloads/act_ekmp2_.docx";
                }
            };
            ActEkmpReportGenerator22 actEkmpReportGenerator22 = new ActEkmpReportGenerator22() {
                @Override
                public String getTemplatePath() {
                    return "/home/me/GIT/pmp/pmp/module-smo/src/main/resources/word_templates/act_ekmp2_2.docx";
                }
            };
            FieldUtil.setField(actEkmpReportFileExporter2, ActEkmpReportFileExporter2.class, actEkmpReportGenerator2, "reportGenerator");
            FieldUtil.setField(actEkmpReportFileExporter2, ActEkmpReportFileExporter2.class, actEkmpReportGenerator22, "reportGenerator2");
            FieldUtil.setField(actEkmpReportFileExporter2, ActEkmpReportFileExporter2.class, smoSessionFactory, "smoSessionFactory");
            Method getReportPdfMethod = ActEkmpReportFileExporter2.class.getDeclaredMethod("getReportPdf", ReportExportContext.class);
            getReportPdfMethod.setAccessible(true);
            ReportExportContext context = new ReportExportContext(null, null);
            ExaminationDocument examinationDocument = new ExaminationDocument();
            examinationDocument.setId(123L);
            context.setDoc(examinationDocument);
            byte[] createReport = (byte[]) getReportPdfMethod.invoke(actEkmpReportFileExporter2, context);
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
