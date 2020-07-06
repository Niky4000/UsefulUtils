/**
 * This example was written by Bruno Lowagie in answer to the following StackOverflow question:
 * http://stackoverflow.com/questions/29560373/watermark-pdfs-using-text-or-images-in-java
 */
package ru.ibs.pmp.zzztestapplication.others;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.imageio.ImageIO;

public class TransparentWatermark2 {

//    public static final String SRC = "resources/pdfs/pages.pdf";
//    public static final String DEST = "results/stamper/pages_watermarked.pdf";
//    public static final String IMG = "resources/images/itext.png";
    public static final String SRC = "D:\\tmp\\dbf\\1891\\77004\\20200601\\b0306001\\S_M1060.pdf";
    public static final String DEST = "D:\\tmp\\dbf\\1891\\77004\\20200601\\b0306001\\S_M1060_out2.pdf";
    public static final String IMG = "D:\\tmp\\parcels\\Watermark.png";

    public void create() throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        manipulatePdf(SRC, DEST);
    }

    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        // text watermark
        Font f = new Font(FontFamily.HELVETICA, 30);
        Phrase p = new Phrase("My watermark (text) ПРИВЕТ!", f);
        // image watermark

        Image img = Image.getInstance(createImage2());
//        Image img = Image.getInstance(IMG);
        float w = img.getScaledWidth();
        float h = img.getScaledHeight();
        // transparency
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.5f);
        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        // loop over every page
        for (int i = 1; i <= n; i++) {
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
//            if (i % 2 == 1)
//                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
//            else
            over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
            over.restoreState();
        }
        stamper.close();
        reader.close();
    }

    private byte[] createImage() throws IOException {
        int width = 250;
        int height = 250;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
        g2d.setComposite(composite);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);
        // fill all the image with white
//        g2d.setColor(Color.white);
//        g2d.fillRect(0, 0, width, height);

        // create a circle with black
//        g2d.setColor(Color.black);
//        g2d.fillOval(0, 0, width, height);
        // create a string with yellow
        g2d.setColor(Color.decode("#C0C0C0"));
        g2d.drawString("ПРЕДВАРИТЕЛЬНО", 50, 120);
        g2d.dispose();
        ImageIO.write(bufferedImage, "png", stream);
        return stream.toByteArray();
    }

    public byte[] createImage2() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        java.awt.Font font = new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 30);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-20), 0, 0);
        java.awt.Font rotatedFont = font.deriveFont(affineTransform);
        graphics.setFont(rotatedFont);
        graphics.setColor(Color.RED);
        graphics.drawString("ПРЕДВАРИТЕЛЬНО", 5, 100);
        graphics.dispose();
        ImageIO.write(image, "PNG", stream);
        File file = new File("D:\\tmp\\dbf\\1891\\77004\\20200601\\b0306001\\img.png");
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), stream.toByteArray(), StandardOpenOption.CREATE_NEW);
        return stream.toByteArray();
    }
}
