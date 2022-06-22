package ru.ibs.pmp.smo.services.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import ru.ibs.pmp.api.service.export.msk.pdf.bean.PdfData;

/**
 * User: sokolova
 *
 * @param <T>
 */
public class PmpWordWriter<T extends PdfData> {

	private final String templatePath;

	public PmpWordWriter(String templatePath) {
		this.templatePath = templatePath;
	}

	public byte[] createReport(T data) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		InputStream in = new FileInputStream(getTemplatePath());
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
		IContext context = report.createContext();
		FieldsMetadata metadata = report.createFieldsMetadata();
		fillContext(context, metadata, data);
		report.process(context, stream);
		byte[] toByteArray = stream.toByteArray();
		return toByteArray;
	}

	public boolean fillContext(IContext context, FieldsMetadata metadata, T data) throws XDocReportException {
		if (data != null) {
			metadata.load("data", data.getClass(), true);
			context.put("data", data);
			return true;
		}
		return false;
	}

	public String getTemplatePath() {
		return templatePath;
	}
}
