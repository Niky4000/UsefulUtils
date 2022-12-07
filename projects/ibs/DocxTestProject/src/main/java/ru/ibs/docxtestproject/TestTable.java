package ru.ibs.docxtestproject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import static ru.ibs.docxtestproject.WordUtils.changeOrientation;
import static ru.ibs.docxtestproject.WordUtils.initTable;
import static ru.ibs.docxtestproject.WordUtils.mergeDocuments;
import static ru.ibs.docxtestproject.WordUtils.setWidth;

public class TestTable {

	public void create() throws IOException {
		String path = "/home/me/tmp/reportDoc/";
		try (FileOutputStream out = new FileOutputStream(path + "Output.docx");
				XWPFDocument document = new XWPFDocument();
				XWPFDocument document2 = new XWPFDocument();) {

			changeOrientation(document, STPageOrientation.LANDSCAPE);
			createTable(document);
			XWPFParagraph pageBreak = document.createParagraph();
			pageBreak.setPageBreak(true);

			changeOrientation(document2, STPageOrientation.PORTRAIT);
			XWPFParagraph pageBreak2 = document2.createParagraph();
			pageBreak2.setPageBreak(true);
			XWPFParagraph pageBreak3 = document2.createParagraph();
			pageBreak3.setPageBreak(true);
			createTable(document2);

			XWPFDocument mergeDocuments = mergeDocuments(Stream.of(document, document2));

			mergeDocuments.write(out);
		}
	}


	private void createTable(final XWPFDocument document) {
		//XWPFDocument doc = new XWPFDocument();
		XWPFTable table = document.createTable(1, 2);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, 1, 2);
		setWidth(table, new double[]{8d, 1d});
//			table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(8000));
//			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
		table.getRow(0).getCell(0).setText("1A");
		table.getRow(0).getCell(1).setText("1B");
		XWPFTableRow newrow = table.createRow();
		newrow.getCell(0).setText("2A");
		newrow.getCell(1).setText("2B");
	}
}
