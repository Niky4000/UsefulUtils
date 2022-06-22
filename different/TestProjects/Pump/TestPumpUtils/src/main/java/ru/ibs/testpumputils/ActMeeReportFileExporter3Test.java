package ru.ibs.testpumputils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import ru.ibs.pmp.api.smo.model.ExaminationAct;
import ru.ibs.pmp.api.smo.model.ExaminationDocument;
import ru.ibs.pmp.smo.dao.ExaminationActRepository;
import ru.ibs.pmp.smo.dto.pdf.ActMeePdfData;
import ru.ibs.pmp.smo.dto.pdf.ActMeePdfData2;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActEkmpReportFileExporter;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActEkmpReportFileExporter2Abstract;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActMeeReportFileExporter2;
import ru.ibs.pmp.smo.report.export.impl.examinationdoc.ActMeeReportFileExporter3;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;
import ru.ibs.pmp.smo.services.pdf.ActMeeReportGenerator2;
import ru.ibs.pmp.smo.services.pdf.ActMeeReportGenerator22;
import ru.ibs.pmp.smo.services.pdf.PmpWordWriter;
import ru.ibs.pmp.smo.utils.SerializationDebugUtils;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class ActMeeReportFileExporter3Test {

	public static void test() throws Exception {
		ActMeeReportFileExporter3 actMeeReportFileExporter3 = new ActMeeReportFileExporter3() {
			@Override
			protected List<ActEkmpPdfDataBody3> getReport2Data(ReportExportContext context, ActMeePdfData reportPdfData) {
				try {
					Object obj = SerializationDebugUtils.deSerializeObject(new File("/home/me/VMwareShared/actEkmpPdfDataBody3"));
					return Arrays.asList((ActEkmpPdfDataBody3) obj);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

		};
		ExaminationActRepository actDao = (ExaminationActRepository) Proxy.newProxyInstance(ActMeeReportFileExporter3Test.class.getClassLoader(), new Class[]{ExaminationActRepository.class}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getName().equals("findFirstByDocument")) {
					return new ExaminationAct();
				} else {
					return null;
				}
			}
		});

		ActMeeReportFileExporter2 actMeeReportFileExporter2 = new ActMeeReportFileExporter2() {
			@Override
			protected ActMeePdfData2 getData(Long docId, Date date) {
				try {
					Object obj = SerializationDebugUtils.deSerializeObject(new File("/home/me/VMwareShared/reportPdfData2"));
					return (ActMeePdfData2) obj;
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
//				return super.getData(docId, date); //To change body of generated methods, choose Tools | Templates.
			}

		};

		PmpWordWriter<ActEkmpPdfDataBody3> reportGenerator2 = new ActMeeReportGenerator22() {
			@Override
			public String getTemplatePath() {
				return "/home/me/GIT/pmp/pmp/module-smo/src/main/resources/word_templates/act_mee2_2.docx"; //To change body of generated methods, choose Tools | Templates.
			}
		};
		PmpWordWriter<ActMeePdfData2> reportGenerator = new ActMeeReportGenerator2() {
			@Override
			public String getTemplatePath() {
				return "/home/me/GIT/pmp/pmp/module-smo/src/main/resources/word_templates/act_mee2.docx"; //To change body of generated methods, choose Tools | Templates.
			}
		};

		FieldUtil.setField(actMeeReportFileExporter2, ActMeeReportFileExporter2.class, reportGenerator, "reportGenerator");
		FieldUtil.setField(actMeeReportFileExporter3, ActEkmpReportFileExporter.class, actDao, "actDao");
		FieldUtil.setField(actMeeReportFileExporter3, ActMeeReportFileExporter3.class, actMeeReportFileExporter2, "actMeeReportFileExporter2");
		FieldUtil.setField(actMeeReportFileExporter3, ActMeeReportFileExporter3.class, reportGenerator2, "reportGenerator2");

		ExaminationDocument examinationDocument = new ExaminationDocument();
		examinationDocument.setId(7L);
		ExaminationAct examAct = new ExaminationAct();
		examAct.setActDate(new Date());
		ReportExportContext context = new ReportExportContext(null, null, examinationDocument);
		context.setExamAct(examAct);
		Method getReportPdfMethod = ActEkmpReportFileExporter2Abstract.class.getDeclaredMethod("getReportPdf", ReportExportContext.class);
		getReportPdfMethod.setAccessible(true);
		getReportPdfMethod.invoke(actMeeReportFileExporter3, context);
	}
}
