/**
 * This example was written by Bruno Lowagie in answer to the following StackOverflow question:
 * http://stackoverflow.com/questions/29560373/watermark-pdfs-using-text-or-images-in-java
 */
package ru.ibs.pmp.zzztestapplication.others;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransparentWatermark3 {

//    public static final String SRC = "resources/pdfs/pages.pdf";
//    public static final String DEST = "results/stamper/pages_watermarked3.pdf";
//    public static final String IMG = "resources/images/itext.png";
    public static final String SRC = "D:\\tmp\\dbf\\1891\\77004\\20200601\\b0306001\\S_M1060.pdf";
    public static final String DEST = "D:\\tmp\\dbf\\1891\\77004\\20200601\\b0306001\\S_M1060_out3.pdf";
    public static final String IMG = "D:\\tmp\\parcels\\SomeImage.png";

    public void create() throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        manipulatePdf(SRC, DEST);
    }

    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        stamper.setRotateContents(false);
        // text watermark
        Font f = new Font(FontFamily.HELVETICA, 30);
        Phrase p = new Phrase("My watermark (text)", f);
        // image watermark
        Image img = Image.getInstance(IMG);
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
            pagesize = reader.getPageSize(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
            if (i % 2 == 1)
                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
            else
                over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
            over.restoreState();
        }
        stamper.close();
        reader.close();
    }
}
