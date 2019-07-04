//package ru.ibs.pmp.zzztestapplication;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfDocument;
//import com.itextpdf.text.pdf.PdfImportedPage;
//import com.itextpdf.text.pdf.PdfPage;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfWriter;
//import java.awt.Color;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//
///**
// * @author NAnishhenko
// */
//public class PdfTest {
//
//    public void createSomePdf() throws Exception {
//        Document document = new Document();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
////This is your new pdf doc
//        PdfWriter writer = PdfWriter.getInstance(document, stream);
//
//        document.open();
//        document.newPage();
//
////Get the file of you template, you should use try catch and then close it
////I simply to just show sistem
//        FileInputStream template = new FileInputStream(new File("D:\\tmp\\dbf119\\2290\\00000\\100087919\\S_IN.pdf"));
//
////Load it into a reader
//        PdfReader reader = new PdfReader(template);
//
////Get the page of the template you like
//        PdfImportedPage page = writer.getImportedPage(reader, 1);
//
////Now you can add it to you report
//        PdfContentByte cb = writer.getDirectContent();
//        cb.addTemplate(page, 0, 0);
//
//        document.add(new Paragraph("Hello World 2!"));
//
//        document.close();
//        byte[] toByteArray = stream.toByteArray();
//        File file = new File("D:\\tmp\\SomePdf2.pdf");
//        if (file.exists()) {
//            file.delete();
//        }
//        Files.write(file.toPath(), toByteArray, StandardOpenOption.CREATE_NEW);
//    }
//
//    public void createSomeSimplePdfFromStratch() throws Exception {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        Document document = new Document(PageSize.A4, 25, 25, 30, 25);
//        PdfWriter.getInstance(document, stream);
//        document.open();
//        document.add(new Paragraph("Hello World!"));
//        document.close();
//        byte[] toByteArray = stream.toByteArray();
//        File file = new File("D:\\tmp\\SomePdf.pdf");
//        if (file.exists()) {
//            file.delete();
//        }
//        Files.write(file.toPath(), toByteArray, StandardOpenOption.CREATE_NEW);
//    }
//
//    public void changePdfOrientation() throws Exception {
//        InputStream src=null;
//        OutputStream dest=null;
//        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
//        float margin = 72;
//        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
//            PdfPage page = pdfDoc.getPage(i);
//            // change page size
//            Rectangle mediaBox = page.getMediaBox();
//            Rectangle newMediaBox = new Rectangle(
//                    mediaBox.getLeft() - margin, mediaBox.getBottom() - margin,
//                    mediaBox.getWidth() + margin * 2, mediaBox.getHeight() + margin * 2);
//            page.setMediaBox(newMediaBox);
//            // add border
//            PdfCanvas over = new PdfCanvas(page);
//            over.setStrokeColor(Color.GRAY);
//            over.rectangle(mediaBox.getLeft(), mediaBox.getBottom(),
//                    mediaBox.getWidth(), mediaBox.getHeight());
//            over.stroke();
//            // change rotation of the even pages
//            if (i % 2 == 0) {
//                page.setRotation(180);
//            }
//        }
//        pdfDoc.close();
//    }
//}
