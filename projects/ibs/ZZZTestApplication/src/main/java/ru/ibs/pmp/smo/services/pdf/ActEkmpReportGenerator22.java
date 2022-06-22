package ru.ibs.pmp.smo.services.pdf;

import java.io.File;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.ibs.pmp.smo.dto.pdf.ActEkmpPdfData3;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;

/**
 *
 * @author Sakalova
 */
@Service
public class ActEkmpReportGenerator22 extends PmpWordWriter<ActEkmpPdfDataBody3> {

	private String templateDir = "word_templates";
	private static final String templateName = "act_ecmp2.docx";

	@Override
	public String getTemplatePath() {
		try {
			File folder = new ClassPathResource(templateDir).getFile();
			String pathToConfigs = folder.getPath();
			return pathToConfigs + File.separator + templateName;
		} catch (IOException e) {
			return null;
		}
	}
}
