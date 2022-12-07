package ru.ibs.docxtestproject;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class WordUtils {

	public static void initTable(XWPFTable table, final int rowsCount, final int columnsCount) {
		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(1 * 1440));
		for (int col = 1; col < columnsCount; col++) {
			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1 * 1440));
		}
		for (int col = 0; col < columnsCount; col++) {
			CTTblWidth tblWidth = CTTblWidth.Factory.newInstance();
			tblWidth.setW(BigInteger.valueOf(1 * 1440));
			tblWidth.setType(STTblWidth.DXA);
			for (int row = 0; row < rowsCount; row++) {
				CTTcPr tcPr = table.getRow(row).getCell(col).getCTTc().getTcPr();
				if (tcPr != null) {
					tcPr.setTcW(tblWidth);
				} else {
					tcPr = CTTcPr.Factory.newInstance();
					tcPr.setTcW(tblWidth);
					table.getRow(row).getCell(col).getCTTc().setTcPr(tcPr);
				}
			}
		}
	}

	public static void setWidth(XWPFTable table, double[] widthArray) {
		List<CTTblGridCol> gridColList = table.getCTTbl().getTblGrid().getGridColList();
		if (gridColList.size() != widthArray.length) {
			throw new RuntimeException("Wrong widthArray argument!");
		} else {
			double tableSum = gridColList.stream().map(CTTblGridCol::getW).map(obj -> (Number) obj).map(Number::doubleValue).reduce(0d, (i1, i2) -> i1 + i2);
			double widthSum = Arrays.stream(widthArray).reduce(0d, (i1, i2) -> i1 + i2);
			for (int i = 0; i < widthArray.length; i++) {
//				double columnWidth = gridColList.get(i).getW().doubleValue();
				double columnWidth = ((Number) gridColList.get(i).getW()).doubleValue();
				double newColumnWidth = tableSum * widthArray[i] / widthSum;
				BigInteger newWidth = BigInteger.valueOf(Double.valueOf(newColumnWidth).longValue());
				gridColList.get(i).setW(newWidth);
			}
		}
	}

	public static void changeOrientation(XWPFDocument document, STPageOrientation.Enum orientation) {
		CTDocument1 d_ = document.getDocument();
		CTBody body = d_.getBody();
		if (!body.isSetSectPr()) {
			body.addNewSectPr();
		}
		CTSectPr section = body.getSectPr();
//		CTSectPr section = getSection.apply(body);
		if (!section.isSetPgSz()) {
			section.addNewPgSz();
		}
		CTPageSz pageSize = section.getPgSz();
		if (orientation.equals(STPageOrientation.LANDSCAPE)) {
			pageSize.setOrient(STPageOrientation.LANDSCAPE);
			pageSize.setW(BigInteger.valueOf(15840));
			pageSize.setH(BigInteger.valueOf(12240));
		} else {
			pageSize.setOrient(STPageOrientation.PORTRAIT);
			pageSize.setH(BigInteger.valueOf(842 * 20));
			pageSize.setW(BigInteger.valueOf(595 * 20));
		}
	}

	public static XWPFDocument mergeDocuments(Stream<XWPFDocument> documents) {
		XWPFDocument mergedDocuments = new XWPFDocument();
		CTDocument1 mergedCTDocument = mergedDocuments.getDocument();
		mergedCTDocument.unsetBody();  // to remove blank first page in merged document
		documents.forEach(srcDocument -> {
			CTDocument1 srcCTDocument = srcDocument.getDocument();
			if (srcCTDocument != null) {
				CTBody srcCTBody = srcCTDocument.getBody();
				if (srcCTBody != null) {
					CTBody mergedCTBody = mergedCTDocument.addNewBody();
					mergedCTBody.set(srcCTBody);
				}
			}
		});
		return mergedDocuments;
	}

	public static void mergeCellVertically(XWPFTable table, int col, int fromRow, int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			CTVMerge vmerge = CTVMerge.Factory.newInstance();
			if (rowIndex == fromRow) {
				// The first merged cell is set with RESTART merge value
				vmerge.setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE
				vmerge.setVal(STMerge.CONTINUE);
				// and the content should be removed
				for (int i = cell.getParagraphs().size(); i > 0; i--) {
					cell.removeParagraph(0);
				}
				cell.addParagraph();
			}
			// Try getting the TcPr. Not simply setting an new one every time.
			CTTcPr tcPr = cell.getCTTc().getTcPr();
			if (tcPr == null) {
				tcPr = cell.getCTTc().addNewTcPr();
			}
			tcPr.setVMerge(vmerge);
		}
	}

	public static void mergeCellHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
		XWPFTableCell cell = table.getRow(row).getCell(fromCol);
		// Try getting the TcPr. Not simply setting an new one every time.
		CTTcPr tcPr = cell.getCTTc().getTcPr();
		if (tcPr == null) {
			tcPr = cell.getCTTc().addNewTcPr();
		}
		// The first merged cell has grid span property set
		if (tcPr.isSetGridSpan()) {
			tcPr.getGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
		} else {
			tcPr.addNewGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
		}
		// Cells which join (merge) the first one, must be removed
		for (int colIndex = toCol; colIndex > fromCol; colIndex--) {
			table.getRow(row).removeCell(colIndex); // use only this for apache poi versions greater than 3
//			table.getRow(row).getCtRow().removeTc(colIndex); // use this for apache poi versions up to 3
//			table.getRow(row).removeCell(colIndex);
		}
	}

	public static void createParagraph(XWPFDocument document, String text, ParagraphAlignment align, int size) {
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(align);
		XWPFRun run = paragraph.createRun();
		run.setText(text);
		run.setFontFamily("Times New Roman");
		run.setFontSize(size);
	}

	public static void createParagraph(XWPFParagraph paragraph, String text, ParagraphAlignment align, int size) {
		paragraph.setAlignment(align);
		XWPFRun run = paragraph.createRun();
		run.setText(text);
		run.setFontFamily("Times New Roman");
		run.setFontSize(size);
	}
}
