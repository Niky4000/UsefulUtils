package ru.ibs.pmp.zzztestapplication;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.Table;
import com.spire.doc.TableRow;
import com.spire.doc.documents.BorderStyle;
import com.spire.doc.documents.DefaultTableStyle;
import com.spire.doc.documents.HorizontalAlignment;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.ParagraphStyle;
import com.spire.doc.documents.TableRowHeightType;
import com.spire.doc.documents.VerticalAlignment;
import com.spire.doc.fields.TextRange;
import java.awt.Color;
import java.io.IOException;

public class WordDocumentTest2 {

	public void createWord() throws IOException {
//Create a Document instance 
		Document document = new Document();

//Add a section 
		Section section = document.addSection();

//Add two paragraphs to the section 
		Paragraph para1 = section.addParagraph();
		para1.appendText("How to create Word document in Java");

		Paragraph para2 = section.addParagraph();
		para2.appendText("This article demonstrate how to create Word document with text, images, lists and tables.");

//Set title style for paragraph 1 
		ParagraphStyle style1 = new ParagraphStyle(document);
		style1.setName("titleStyle");
		style1.getCharacterFormat().setBold(true);
		style1.getCharacterFormat().setTextColor(Color.BLUE);
		style1.getCharacterFormat().setFontName("Arial");
		style1.getCharacterFormat().setFontSize(12f);
		document.getStyles().add(style1);
		para1.applyStyle("titleStyle");

//Set style for paragraph 2 
		ParagraphStyle style2 = new ParagraphStyle(document);
		style2.setName("paraStyle");
		style2.getCharacterFormat().setFontName("Arial");
		style2.getCharacterFormat().setFontSize(11f);
		document.getStyles().add(style2);
		para2.applyStyle("paraStyle");

//Horizontally align paragraph 1 to center 
		para1.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

//Set first-line indent for paragraph 2 
		para2.getFormat().setFirstLineIndent(12f);

//Set spaces after paragraph 1 and 2 
		para1.getFormat().setAfterSpacing(15f);
		para2.getFormat().setAfterSpacing(10f);

//Save the document 
		document.saveToFile("/home/me/tmp/reportDoc/Output.docx", FileFormat.Docx);
	}

	public void createWord2() throws IOException {
//Create a Document instance 
		Document document = new Document();

//Add a section 
		Section section = document.addSection();

//Define data to create table 
		String[] header = {"Name", "Gender"};
		String[][] data
				= {
					new String[]{"Winny", "Female"},
					new String[]{"Lois", "Female"},
					new String[]{"Jois", "Female"},
					new String[]{"Moon", "Male"},
					new String[]{"Vinit", "Female"},};

//Add a table 
		Table table = section.addTable();
		table.resetCells(data.length, header.length);
		table.applyStyle(DefaultTableStyle.Colorful_List);
		table.getTableFormat().getBorders().setBorderType(BorderStyle.Single);

//Set the first row as table header and add data 
//		TableRow row = table.getRows().get(0);
//		row.isHeader(true);
//		row.setHeight(20);
//		row.setHeightType(TableRowHeightType.Exactly);
//		row.getRowFormat().setBackColor(Color.gray);
//		for (int i = 0; i < header.length; i++) {
//			row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
//			Paragraph p = row.getCells().get(i).addParagraph();
//			p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
//			TextRange range1 = p.appendText(header[i]);
//			range1.getCharacterFormat().setFontName("Arial");
//			range1.getCharacterFormat().setFontSize(12f);
//			range1.getCharacterFormat().setBold(true);
//		}

//Add data to the rest of rows 
		for (int r = 0; r < data.length; r++) {
			TableRow dataRow = table.getRows().get(r + 1);
//			dataRow.setHeight(25);
			dataRow.setHeightType(TableRowHeightType.Exactly);
//			dataRow.getRowFormat().setBackColor(Color.white);
			for (int c = 0; c < data[r].length; c++) {
				dataRow.getCells().get(c).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
				TextRange range2 = dataRow.getCells().get(c).addParagraph().appendText(data[r][c]);
				range2.getCharacterFormat().setFontName("Arial");
				range2.getCharacterFormat().setFontSize(10f);
			}
		}

//Set background color for cells 
		for (int j = 1; j < table.getRows().getCount(); j++) {
			if (j % 2 == 0) {
				TableRow row2 = table.getRows().get(j);
				for (int f = 0; f < row2.getCells().getCount(); f++) {
					row2.getCells().get(f).getCellFormat().setBackColor(new Color(173, 216, 230));
				}
			}
		}

//Save the document 
		document.saveToFile("/home/me/tmp/reportDoc/Output.docx", FileFormat.Docx_2013);
	}

	public void createWord3() throws IOException {
//Create a Document instance 
		Document document = new Document();

//Add a section 
		Section section = document.addSection();

//Add two paragraphs to the section 
		Paragraph para1 = section.addParagraph();
		para1.appendText("How to create Word document in Java");

		Paragraph para2 = section.addParagraph();
		para2.appendText("This article demonstrate how to create Word document with text, images, lists and tables.");

//Set title style for paragraph 1 
		ParagraphStyle style1 = new ParagraphStyle(document);
		style1.setName("titleStyle");
		style1.getCharacterFormat().setBold(true);
		style1.getCharacterFormat().setTextColor(Color.BLUE);
		style1.getCharacterFormat().setFontName("Arial");
		style1.getCharacterFormat().setFontSize(12f);
		document.getStyles().add(style1);
		para1.applyStyle("titleStyle");

//Set style for paragraph 2 
		ParagraphStyle style2 = new ParagraphStyle(document);
		style2.setName("paraStyle");
		style2.getCharacterFormat().setFontName("Arial");
		style2.getCharacterFormat().setFontSize(11f);
		document.getStyles().add(style2);
		para2.applyStyle("paraStyle");

//Horizontally align paragraph 1 to center 
		para1.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

//Set first-line indent for paragraph 2 
		para2.getFormat().setFirstLineIndent(12f);

//Set spaces after paragraph 1 and 2 
		para1.getFormat().setAfterSpacing(15f);
		para2.getFormat().setAfterSpacing(10f);

//Define data to create table 
		String[] header = {"Name", "Gender"};
		String[][] data = {
			new String[]{"Winny", "Female"},
			new String[]{"Lois", "Female"},
			new String[]{"Jois", "Female"},
			new String[]{"Moon", "Male"},
			new String[]{"Vinit", "Female"},};

//Add a table 
		Table table = section.addTable();
		table.resetCells(data.length + 1, header.length);
		table.applyStyle(DefaultTableStyle.Colorful_List);
		table.getTableFormat().getBorders().setBorderType(BorderStyle.Single);

//Set the first row as table header and add data 
		TableRow row = table.getRows().get(0);
		row.isHeader(true);
		row.setHeight(20);
		row.setHeightType(TableRowHeightType.Exactly);
		row.getRowFormat().setBackColor(Color.gray);
		for (int i = 0; i < header.length; i++) {
			row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
			Paragraph p = row.getCells().get(i).addParagraph();
			p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
			TextRange range1 = p.appendText(header[i]);
			range1.getCharacterFormat().setFontName("Arial");
			range1.getCharacterFormat().setFontSize(12f);
			range1.getCharacterFormat().setBold(true);
		}

//Add data to the rest of rows 
		for (int r = 0; r < data.length; r++) {
			TableRow dataRow = table.getRows().get(r + 1);
			dataRow.setHeight(25);
			dataRow.setHeightType(TableRowHeightType.Exactly);
			dataRow.getRowFormat().setBackColor(Color.white);
			for (int c = 0; c < data[r].length; c++) {
				dataRow.getCells().get(c).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
				TextRange range2 = dataRow.getCells().get(c).addParagraph().appendText(data[r][c]);
				range2.getCharacterFormat().setFontName("Arial");
				range2.getCharacterFormat().setFontSize(10f);
			}
		}

//Set background color for cells 
		for (int j = 1; j < table.getRows().getCount(); j++) {
			if (j % 2 == 0) {
				TableRow row2 = table.getRows().get(j);
				for (int f = 0; f < row2.getCells().getCount(); f++) {
					row2.getCells().get(f).getCellFormat().setBackColor(new Color(173, 216, 230));
				}
			}
		}

//Save the document 
		document.saveToFile("/home/me/tmp/reportDoc/Output.docx", FileFormat.Docx_2013);
	}
}
