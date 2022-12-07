package ru.ibs.pmp.zzztestapplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class WordDocumentTest {

	//Create Word
	public void createWord() throws IOException {
		String path = "/home/me/tmp/reportDoc/";
		//Check the generated path. If it is not there, create it.
		if (!Paths.get(path).toFile().exists()) {
			Files.createDirectories(Paths.get(path));
		}
		//Create Word docs.
		//Blank Document
		XWPFDocument document = new XWPFDocument();
		//Write the Document in file system
		FileOutputStream out = new FileOutputStream(path + "createdWord.docx");
		XWPFParagraph paragraph = createParagraph(document, "Some text!\n");

		XWPFTable table = document.createTable();
		table.setWidth("100%");
		table.setTableAlignment(TableRowAlign.CENTER);

//		CTSectPr sectPr = document.getDocument().getBody().getSectPr();
//		CTPageSz pageSize = sectPr.getPgSz();
//		double pageWidth = pageSize.getW().doubleValue();
//
//		CTPageMar pageMargin = sectPr.getPgMar();
//		double pageMarginLeft = pageMargin.getLeft().doubleValue();
//		double pageMarginRight = pageMargin.getRight().doubleValue();
//		double effectivePageWidth = pageWidth - pageMarginLeft - pageMarginRight;
//
//		double widthInPercent = 0.6;
//		int size = (int) (effectivePageWidth * widthInPercent);
//
//		CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
//		width.setType(STTblWidth.DXA);
//		width.setW(new BigInteger(size + ""));

//		table.setWidthType(TableWidthType.PCT);
//		table.setWidth(9000);
		//create first row
		XWPFTableRow tableRowOne = table.getRow(0);
		tableRowOne.getCell(0).setText("col one, row one");
		tableRowOne.addNewTableCell().setText("col two, row one");
		tableRowOne.addNewTableCell().setText("col three, row one");

		//create second row
		XWPFTableRow tableRowTwo = table.createRow();
		tableRowTwo.getCell(0).setText("col one, row two");
		tableRowTwo.getCell(1).setText("col two, row two");
		tableRowTwo.getCell(2).setText("col three, row two");

		//create third row
		XWPFTableRow tableRowThree = table.createRow();
		tableRowThree.getCell(0).setText("col one, row three");
		tableRowThree.getCell(1).setText("col two, row three");
		tableRowThree.getCell(2).setText("col three, row three");

		document.write(out);
		//Close document
		out.close();
	}

	public void createWord2() throws IOException {
		String path = "/home/me/tmp/reportDoc/";
		FileOutputStream out = new FileOutputStream(path + "createdWord.docx");
		XWPFDocument document = new XWPFDocument();
		XWPFTable table = document.createTable(1, 2);
		table.setWidth("100%");
		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2000));
		table.getRow(0).getCell(0).setText("1A");
		table.getRow(0).getCell(1).setText("1B");
		XWPFTableRow newrow = table.createRow();
		newrow.getCell(0).setText("2A");
		newrow.getCell(1).setText("2B");
		document.write(out);
		out.close();
	}

	private XWPFParagraph createParagraph(XWPFDocument document, String text) {
		//create Paragraph
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.setText(text);
		return paragraph;
	}
}
