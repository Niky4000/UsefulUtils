package ru.ibs.pmp.smo.report.export.impl.examinationdoc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import ru.ibs.pmp.smo.dto.pdf.ActEkmpPdfData3;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;
import ru.ibs.pmp.smo.services.pdf.PmpWordWriter;

/**
 *
 * @author me
 */
public class ActEkmpReportFileExporter2 {

	private PmpWordWriter<ActEkmpPdfData3> reportGenerator;
	private PmpWordWriter<ActEkmpPdfDataBody3> reportGenerator2;
	
	private static final int BUFFER_SIZE=1024*1024;

	private ObjectInput getInputStream(File serfile, boolean gzip) throws FileNotFoundException, IOException {
		ObjectInput input;
		if (gzip) {
			try {
				InputStream file = new FileInputStream(serfile);
				InputStream zip = new GZIPInputStream(file, BUFFER_SIZE); // Use zip!
				input = new ObjectInputStream(zip); // Use zip!
			} catch (IOException ioe) {
				InputStream file = new FileInputStream(serfile);
				InputStream buffer = new BufferedInputStream(file, BUFFER_SIZE); // Do not use zip!
				input = new ObjectInputStream(buffer); // Do not use zip!
			}
		} else {
			InputStream file = new FileInputStream(serfile);
			InputStream buffer = new BufferedInputStream(file, BUFFER_SIZE); // Do not use zip!
			input = new ObjectInputStream(buffer); // Do not use zip!
		}
		return input;
	}

	private <T> T deSerializeObject(File serfile) {
		if (!serfile.exists()) {
			return null;
		}
		try (ObjectInput input = getInputStream(serfile, false)) {
			T obj = (T) input.readObject();
			return obj;
		} catch (ClassNotFoundException | IOException e) {
//			callBackBeanForDeSerialization.apply(e);
			e.printStackTrace();
//			if (serfile.exists()) {
//				serfile.delete();
//			}
		}
		return null;
	}

	private ObjectOutput getOutputStream(File serfile, boolean gzip) throws FileNotFoundException, IOException {
		ObjectOutput output;
		OutputStream file = new FileOutputStream(serfile);
		if (gzip) {
			OutputStream zip = new GZIPOutputStream(file, BUFFER_SIZE); // Use zip!
			output = new ObjectOutputStream(zip); // Use zip!
		} else {
			OutputStream buffer = new BufferedOutputStream(file, BUFFER_SIZE); // Do not use zip!
			output = new ObjectOutputStream(buffer); // Do not use zip!
		}
		return output;
	}

	private void serializeObject(File serfile, Object obj) {
		if (!serfile.getParentFile().exists()) {
			serfile.getParentFile().mkdirs();
		}
		if (serfile.exists()) {
			serfile.delete();
		}
		try (ObjectOutput output = getOutputStream(serfile, false)) {
			output.writeObject(obj);
		} catch (IOException ioe) {
//			callBackBeanForSerialization.apply(ioe);
			ioe.printStackTrace();
		}
	}

	public byte[] getReportPdf() throws Exception {
//		ActEkmpPdfData3 reportPdfData = getData(context.getDoc().getId());
		ActEkmpPdfData3 reportPdfData = (ActEkmpPdfData3) deSerializeObject(new File("/home/me/Downloads/14847/reportPdfData"));
		ByteArrayInputStream byteArrayInputStream;
		try {
			byte[] createReport = reportGenerator.createReport(reportPdfData);
//			Files.write(new File("/home/me/tmp/byteArrayInputStream").toPath(), createReport, StandardOpenOption.CREATE_NEW);
			byteArrayInputStream = new ByteArrayInputStream(createReport);
		} catch (Exception e) {
			throw new RuntimeException(this.getClass() + " Exception", e);
		}
		List<ActEkmpPdfDataBody3> b2 = reportPdfData.getB2();
		List<InputStream> byteArrayInputStreamList = new ArrayList<>(b2.size());
		for (ActEkmpPdfDataBody3 actEkmpPdfDataBody3 : b2) {
			try {
				byte[] createReport = reportGenerator2.createReport(actEkmpPdfDataBody3);
//				Files.write(new File("/home/me/tmp/byteArrayInputStreamList").toPath(), createReport, StandardOpenOption.CREATE_NEW);
				byteArrayInputStreamList.add(new ByteArrayInputStream(createReport));
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
		if (!src2List.isEmpty()) {
			XWPFParagraph paragraph_ = src1Document.createParagraph();
			paragraph_.setPageBreak(true);
		}
		CTBody src1Body = src1Document.getDocument().getBody();
		for (int i = 0; i < src2List.size(); i++) {
			InputStream src2 = src2List.get(i);
			OPCPackage src2Package = OPCPackage.open(src2);
			XWPFDocument src2Document = new XWPFDocument(src2Package);
			if (i < src2List.size() - 1) {
				XWPFParagraph paragraph = src2Document.createParagraph();
				paragraph.setPageBreak(true);
			}
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

	private static String getValue(Object[] stringArray, int index) {
		return (String) stringArray[index] != null ? (String) stringArray[index] : "---";
	}
}
