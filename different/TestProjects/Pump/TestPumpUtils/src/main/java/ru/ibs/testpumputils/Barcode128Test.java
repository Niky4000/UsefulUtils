///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.testpumputils;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.pdf.PdfWriter;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import org.krysalis.barcode4j.impl.code128.Code128Bean;
//import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
//import com.itextpdf.text.Rectangle;
//import org.apache.avalon.framework.configuration.Configuration;
//import org.apache.avalon.framework.configuration.DefaultConfiguration;
//import org.krysalis.barcode4j.BarcodeGenerator;
//import org.krysalis.barcode4j.BarcodeUtil;
//
///**
// *
// * @author User
// */
//public class Barcode128Test {
//
//    public static void test() throws Exception {
////// Select a barcode type to create a Java barcode object 
////        Code128 barcode = new Code128();
////// Set barcode data text to encode
////        barcode.setData("Barcode-in-Java");
////// Set barcode data text to encode
////        barcode.setX(2);
////// Generate barcode & encode into GIF format
////        barcode.drawBarcode("C://tmp//barcode-code128.gif");
////// Generate barcode & encode into JPG format
////        barcode.drawBarcode("C://tmp//barcode-code128.jpg");
////// Generate barcode & encode into PNG format
////        barcode.drawBarcode("C://tmp//barcode-code128.png");
////// Generate barcode & encode into EPS format
////        barcode.drawBarcode2EPS("C://tmp//barcode-code128.eps");
////// Generate barcode & print into Graphics2D object
////        barcode.drawBarcode("Java Graphics2D object");
//
//        Code128Bean code128 = new Code128Bean();
//        code128.setHeight(15f);
//        code128.setModuleWidth(0.3);
//        code128.setQuietZone(10);
//        code128.doQuietZone(true);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
//        code128.generateBarcode(canvas, "Hello1234567890");
//        canvas.finish();
//
////write to png file
//        FileOutputStream fos = new FileOutputStream("C:\\tmp\\barcode.png");
//        fos.write(baos.toByteArray());
//        fos.flush();
//        fos.close();
//
////write to pdf
////        Image png = Image.getInstance(baos.toByteArray());
//        Image png = Image.getInstance(generate("0000077777"));
//        png.setAbsolutePosition(400, 685);
//        png.scalePercent(25);
//
//        Document document = new Document(new Rectangle(595, 842));
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\tmp\\barcodes.pdf"));
//        document.open();
//        document.add(png);
//        document.close();
//
//        writer.close();
//
//    }
//
//    private static Configuration buildCfg(String type) {
//        DefaultConfiguration cfg = new DefaultConfiguration("barcode");
//        DefaultConfiguration child = new DefaultConfiguration(type);
//        cfg.addChild(child);
//        DefaultConfiguration attr = new DefaultConfiguration("human-readable");
//        DefaultConfiguration subAttr = new DefaultConfiguration("placement");
//        subAttr.setValue("none");
//        attr.addChild(subAttr);
//        child.addChild(attr);
//        return cfg;
//    }
//
//    public static byte[] generate(String text) {
//        try {
//            BarcodeGenerator barcodeGenerator = BarcodeUtil.getInstance().createBarcodeGenerator(buildCfg("code128"));
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            int resolution = 600;
//            BitmapCanvasProvider canvas = new BitmapCanvasProvider(byteArrayOutputStream, "image/jpeg", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
//            barcodeGenerator.generateBarcode(canvas, text);
//            canvas.finish();
//            return byteArrayOutputStream.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
