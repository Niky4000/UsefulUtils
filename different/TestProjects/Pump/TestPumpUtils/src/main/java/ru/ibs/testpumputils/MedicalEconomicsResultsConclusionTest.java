package ru.ibs.testpumputils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.report.export.impl.MedicalEconomicsResultsConclusion;
import ru.ibs.testpumputils.utils.FieldUtil;

public class MedicalEconomicsResultsConclusionTest {

    public static void test() throws Exception {
        PdfHelper pdf = new PdfHelper();
        MedicalEconomicsResultsConclusion medicalEconomicsResultsConclusion = new MedicalEconomicsResultsConclusion();
        FieldUtil.setField(medicalEconomicsResultsConclusion, pdf, "pdf");
        byte[] createReport = medicalEconomicsResultsConclusion.createReport(false, null);
        File file = new File("/home/me/tmp/zztmp/report.pdf");
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), createReport, StandardOpenOption.CREATE_NEW);
    }
}
