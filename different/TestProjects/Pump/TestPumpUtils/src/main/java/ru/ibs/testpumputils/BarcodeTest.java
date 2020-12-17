///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.testpumputils;
//
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//import org.apache.avalon.framework.configuration.Configuration;
//import org.apache.avalon.framework.configuration.DefaultConfiguration;
//import org.krysalis.barcode4j.BarcodeGenerator;
//import org.krysalis.barcode4j.BarcodeUtil;
//import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
//
///**
// *
// * @author me
// */
//public class BarcodeTest {
//
//    public static void test() throws Exception {
//        byte[] generate = generate("Hello World!!!");
//        File file = new File("/home/me/tmp/barcode.png");
//        if (file.exists()) {
//            file.delete();
//        }
//        Files.write(file.toPath(), generate, StandardOpenOption.CREATE_NEW);
//    }
//    
//	private static Configuration buildCfg(String type) {
//		DefaultConfiguration cfg = new DefaultConfiguration("barcode");
//		DefaultConfiguration child = new DefaultConfiguration(type);
//		cfg.addChild(child);
//		DefaultConfiguration attr = new DefaultConfiguration("human-readable");
//		DefaultConfiguration subAttr = new DefaultConfiguration("placement");
//		subAttr.setValue("none");
//		attr.addChild(subAttr);
//		child.addChild(attr);
//		return cfg;
//	}
//
//	public static byte[] generate(String text) {
//		try {
//			BarcodeGenerator barcodeGenerator = BarcodeUtil.getInstance().createBarcodeGenerator(buildCfg("code128"));
//			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//			int resolution = 600;
//			BitmapCanvasProvider canvas = new BitmapCanvasProvider(byteArrayOutputStream, "image/jpeg", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
//			barcodeGenerator.generateBarcode(canvas, text);
//			canvas.finish();
//			return byteArrayOutputStream.toByteArray();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//}
