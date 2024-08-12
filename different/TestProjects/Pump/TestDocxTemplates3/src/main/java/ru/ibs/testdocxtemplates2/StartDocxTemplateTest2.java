package ru.ibs.testdocxtemplates2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import ru.ibs.pmp.smo.dto.pdf.ActEkmpPdfData3;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActEkmpReportFileExporter2;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;
import ru.ibs.pmp.smo.services.pdf.PmpWordWriter;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class StartDocxTemplateTest2 {

	public static void main(String[] args) throws Exception {
		ActEkmpReportFileExporter2 actEkmpReportFileExporter2 = new ActEkmpReportFileExporter2();
		PmpWordWriter<ActEkmpPdfData3> reportGenerator = new PmpWordWriter<>("/home/me/VMwareShared/act_ekmp2.docx");
		PmpWordWriter<ActEkmpPdfDataBody3> reportGenerator2 = new PmpWordWriter<>("/home/me/VMwareShared/act_ekmp2_2.docx");
		FieldUtil.setField(actEkmpReportFileExporter2, reportGenerator, "reportGenerator");
		FieldUtil.setField(actEkmpReportFileExporter2, reportGenerator2, "reportGenerator2");
		byte[] createReport = actEkmpReportFileExporter2.getReportPdf();
        File reportFile = new File("/home/me/tmp/reportsPdf/report.docx");
        if (reportFile.exists()) {
            reportFile.delete();
        }
        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
//		ActEkmpReportFileExporter2Test.test();
//Files.write(new File("/home/me/tmp/byteArrayInputStream").toPath(), createReport, StandardOpenOption.CREATE_NEW);
//		byte[] readAllBytes = Files.readAllBytes(new File("/home/me/tmp/byteArrayInputStream").toPath());
//		byte[] readAllBytes2 = Files.readAllBytes(new File("/home/me/tmp/byteArrayInputStreamList").toPath());
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(readAllBytes);
//		ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(readAllBytes2);
//		merge(byteArrayInputStream, Arrays.asList(byteArrayInputStream2), byteArrayOutputStream);
	}

//	private static void merge(InputStream src1, List<InputStream> src2List, OutputStream dest) throws Exception {
//		OPCPackage src1Package = OPCPackage.open(src1);
//		XWPFDocument src1Document = new XWPFDocument(src1Package);
//		if (!src2List.isEmpty()) {
//			XWPFParagraph paragraph_ = src1Document.createParagraph();
//			paragraph_.setPageBreak(true);
//		}
//		CTBody src1Body = src1Document.getDocument().getBody();
//		for (int i = 0; i < src2List.size(); i++) {
//			InputStream src2 = src2List.get(i);
//			OPCPackage src2Package = OPCPackage.open(src2);
//			XWPFDocument src2Document = new XWPFDocument(src2Package);
//			if (i < src2List.size() - 1) {
//				XWPFParagraph paragraph = src2Document.createParagraph();
//				paragraph.setPageBreak(true);
//			}
//			CTBody src2Body = src2Document.getDocument().getBody();
//			appendBody(src1Body, src2Body);
//		}
//		src1Document.write(dest);
//	}
//
//	private static void appendBody(CTBody src, CTBody append) throws Exception {
//		XmlOptions optionsOuter = new XmlOptions();
//		optionsOuter.setSaveOuter();
//		String srcString = src.xmlText();
//		String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
//		String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
//		String sufix = srcString.substring(srcString.lastIndexOf("<"));
//		String appendString = append.xmlText(optionsOuter);
//		String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
//		CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
//		src.set(makeBody);
//	}
}
