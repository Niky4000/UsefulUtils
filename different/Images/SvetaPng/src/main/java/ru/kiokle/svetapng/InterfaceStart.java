/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 *
 * @author me
 */
public class InterfaceStart extends Application {

    protected static final int TIME_TO_WAIT = 20000;
    Stage primaryStage;
    TabPane root;

    public static void main(String[] args) throws Exception {
        List<String> argList = Arrays.asList(args).stream().collect(Collectors.toList());
        if (argList.contains("-keys")) {
            Keys.generateKeyPair();
        } else if (argList.contains("-load")) {
//            KeyPair keyPair = Keys.loadKeys();
////            byte[] sign = Keys.sign("Hello!".getBytes(), keyPair.getPrivate());
////            boolean verify = Keys.verify("Hello!".getBytes(), sign, keyPair.getPublic());
//            String sign = Keys.sign("Hello", keyPair.getPrivate());
//            boolean verify = Keys.verify("Hello", sign, keyPair.getPublic());
//            KeyPair keyPair = Keys.generateKeyPair2();
            String cpuId = CpuClass.getCPUId();
            KeyPair keyPair = Keys.loadKeys4();
            String signature = Keys.sign("foobar", keyPair.getPrivate());
            //Let's check the signature
            boolean verify = Keys.verify("foobar", signature, keyPair.getPublic());
            if (verify) {
                System.out.print("Verified! " + cpuId + "!");
            } else {
                System.out.print("Not verified! " + cpuId + "!");
            }
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("form.fxml"));
        root = (TabPane) loader.load();
        ImageCreateController imageCreateController = loader.getController();
        primaryStage.setOnHidden(e -> imageCreateController.shutdown());

        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        primaryStage.setTitle("Программа создания наклеек для тестеров Mary Kay");
        primaryStage.setScene(scene);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
//		if (rootDimensionsBean != null) {
//			primaryStage.setX(rootDimensionsBean.getX());
//			primaryStage.setY(rootDimensionsBean.getY());
//		}
        primaryStage.show();
        this.primaryStage = primaryStage;
    }

    @Override
    public void stop() {
//		DeadCatUtils.serializeObject(ROOT_DIMENSIONS_BEAN_NAME, new RootDimensionsBean(root.getHeight(), root.getWidth(), primaryStage.getX(), primaryStage.getY()));
        System.out.println("Stage is closing");
    }
}
