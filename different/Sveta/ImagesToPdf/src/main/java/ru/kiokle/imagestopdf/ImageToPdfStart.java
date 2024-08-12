package ru.kiokle.imagestopdf;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.itextpdf.text.DocumentException;
import static com.itextpdf.text.Element.ALIGN_LEFT;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageToPdfStart {

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

	public static void main(String[] args) throws Exception {
		createPdf(new File("/home/me/Светик Картинки/GIRLS PAINTING"), new File("/home/me/tmp/Girls.pdf"));
		createPdf(new File("/home/me/Светик Картинки/ANIMALS PAINTING/"), new File("/home/me/tmp/Animals.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/АБСТРАКЦИЯ"), new File("/home/me/tmp/Abstraction.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/Ван Гог"), new File("/home/me/tmp/VanGog.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ГОРЫ"), new File("/home/me/tmp/Mountains.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ДИПТИХ ИЛИ ТРИПТИХ"), new File("/home/me/tmp/Deep.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ДЛЯ ДЕТЕЙ"), new File("/home/me/tmp/Children.pdf"));
//		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ДРУГОЕ"), new File("/home/me/tmp/Another.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ЖЕНЩИНЫ"), new File("/home/me/tmp/Women.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ЗАКАТЫ И РАССВЕТЫ"), new File("/home/me/tmp/SunRise.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/КОТИКИ"), new File("/home/me/tmp/Cats.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ЛЮБОВЬ"), new File("/home/me/tmp/Love.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/МОРЕ"), new File("/home/me/tmp/See.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ПРИРОДА"), new File("/home/me/tmp/Nature.pdf"));
		createPdf(new File("/home/me/Светик Картинки/РЕФЕРЕНСЫ \"ИНТЕРЬЕРНАЯ ЖИВОПИСЬ\"/ЦВЕТЫ"), new File("/home/me/tmp/Flowers.pdf"));
//		createPdf(new File("/home/me/Светик Картинки/ZZZZZZZ"), new File("/home/me/tmp/Animals.pdf"));
	}

	private static void createPdf(File dir, File outputPdf) throws Exception {
		OutputStream stream = new FileOutputStream(outputPdf);
		Document document = new Document(PageSize.A4, 15, 15, 30, 25);
		PdfWriter.getInstance(document, stream);
		try {
			document.open();
			document.addTitle("My first PDF");
			document.add(new Chunk(""));
//			document.add(new Paragraph("Title of the document", catFont));
			PdfPTable table = new PdfPTable(ROWS);
			table.setWidths(new float[]{1, 1, 1});
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(ALIGN_LEFT);
			table.setSpacingBefore(5);
			File[] listFiles = dir.listFiles();
			int k = 1;
			for (File file : listFiles) {
				BufferedImage scaledImage = scaleImage(file);
				byte[] toByteArray = toByteArray(scaledImage);
				Image image = Image.getInstance(toByteArray);
				table.addCell(scaleCell(document, image));
//				System.out.println(k + " " + image.getWidth() + " x " + image.getHeight());
				k++;
			}
			for (int i = 0; i < listFiles.length % ROWS; i++) {
				table.addCell(new PdfPCell(new Phrase(new Chunk(""))));
			}
			document.add(table);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	private static PdfPCell scaleCell(Document doc, Image img) {
		float docWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();
		float docHeight = doc.getPageSize().getHeight() - doc.topMargin() - doc.bottomMargin();
		float docWidthDiv2 = docWidth / 2 - 10;
		float docHeightDiv2 = docHeight / 2 - 10;
		PdfPCell cell = null;
		//img.scaleAbsolute(100, 50);
		if (img.getWidth() < docWidthDiv2 && img.getHeight() < docHeightDiv2) {
			cell = new PdfPCell(img, false);
		} else {
			cell = new PdfPCell(img, true);
		}
		cell.setFixedHeight(CELL_HEIGHT); //"setCalculatedHeight" doesn't work
		return cell;
	}
	private static final int CELL_HEIGHT = 250;

	private static final int ROWS = 3;
	private static final int maxSize = 50;

	private static BufferedImage scaleImage(File file) {
		try {
			BufferedImage before = ImageIO.read(file);
////			File f = new File("/home/me/tmp/MyFile.png");
//			int w = before.getWidth();
//			int h = before.getHeight();
//			int minDimension = Math.max(w, h);
//			Double k = (double) maxSize / (double) minDimension;
//			if (k < 1) {
//				BufferedImage after = new BufferedImage((int) ((double) w * k), (int) ((double) h * k), BufferedImage.TYPE_INT_ARGB);
//				AffineTransform at = new AffineTransform();
//				at.scale(k, k);
//				AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//				after = scaleOp.filter(before, after);
//				return after;
//			} else {
//				return before;
//			}
			return before;
//			ImageIO.write(after, "PNG", f);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static byte[] toByteArray(BufferedImage bi) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "gif", baos);
		baos.flush();
		byte[] bytes = baos.toByteArray();
		baos.close();
		return bytes;
	}

	private static void manipulateImage(String src, String dest) throws IOException, DocumentException {
		BufferedImage before = ImageIO.read(new File(src));
//		BufferedImage before = getBufferedImage(encoded);
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(2.0, 2.0);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		ImageIO.write(after, "jpg", new File(dest));
	}

	private static void main2() {
		try {
			java.awt.Image in = Toolkit.getDefaultToolkit().createImage("/home/me/Светик Картинки/GIRLS PAINTING/1629c35b9b93cd30b7514139a2a70dbd.jpg");
			BufferedImage out = new BufferedImage(in.getWidth(null), in.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = out.createGraphics();
			g2d.drawImage(in, 0, 0, null);
			g2d.dispose();
			ImageIO.write(out, "jpg", new File("/home/me/tmp/Test01.jpg"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
