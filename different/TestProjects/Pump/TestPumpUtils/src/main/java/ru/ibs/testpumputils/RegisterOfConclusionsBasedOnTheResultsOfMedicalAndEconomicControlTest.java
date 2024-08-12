package ru.ibs.testpumputils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.report.export.impl.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControl;
import ru.ibs.testpumputils.utils.FieldUtil;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlTest {

    public static void test() throws Exception {
        PdfHelper pdf = new PdfHelper();
        RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControl registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControl = new RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControl();
        FieldUtil.setField(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControl, pdf, "pdf");
        byte[] createReport = registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControl.createReport(false, null);
        File file = new File("/home/me/tmp/zztmp/report2.pdf");
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), createReport, StandardOpenOption.CREATE_NEW);
    }
}
