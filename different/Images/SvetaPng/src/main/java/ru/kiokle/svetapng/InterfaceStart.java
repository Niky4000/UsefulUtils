/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import ru.kiokle.marykaylib.Keys;
import ru.kiokle.marykaylib.MailHandler;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import static ru.kiokle.marykaylib.CommonUtil.getPathToSaveFolder;

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
            new Keys(getPathToSaveFolder(InterfaceStart.class)).generateKeyPair();
        } else if (argList.contains("-load")) {
//            KeyPair keyPair = Keys.loadKeys();
////            byte[] sign = Keys.sign("Hello!".getBytes(), keyPair.getPrivate());
////            boolean verify = Keys.verify("Hello!".getBytes(), sign, keyPair.getPublic());
//            String sign = Keys.sign("Hello", keyPair.getPrivate());
//            boolean verify = Keys.verify("Hello", sign, keyPair.getPublic());
//            KeyPair keyPair = Keys.generateKeyPair2();
            Keys keys = new Keys(getPathToSaveFolder(InterfaceStart.class));
            String cpuId = CpuClass.getCPUId();
            KeyPair keyPair = keys.loadKeys4();
            String signature = keys.sign("foobar", keyPair.getPrivate());
            keys.saveSignature(signature);
            String loadedSignature = keys.loadSignature();
            //Let's check the signature
            boolean verify = keys.verify("foobar", loadedSignature, keyPair.getPublic());
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
    public void start(Stage primaryStage) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        Keys keys = new Keys(getPathToSaveFolder(InterfaceStart.class));
        String loadedSignature = keys.loadSignature();
        if (loadedSignature != null) {
            //Let's check the signature
            PublicKey publicKey = keys.loadPublicKey(InterfaceStart.class);
            String cpuId = CpuClass.getCPUId();
            boolean verify = keys.verify(cpuId, loadedSignature, publicKey);
            if (verify) {
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
            } else {
                sendMail();
            }
        } else {
            sendMail();
        }
    }

    private void sendMail() {
        MailHandler mailHandler = new MailHandler();
        Properties properties = new Properties();
        Properties properties2 = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("mailConfig.conf"));
            properties2.load(getClass().getClassLoader().getResourceAsStream("mailConfig2.conf"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String mail = (String) properties2.get("mail.send.box");
        String user = (String) properties2.get("mail.send.user");
        String password = (String) properties2.get("mail.send.password");
        String sendMail = (String) properties2.get("mail.box.for.sending");
        mailHandler.sendMail2(properties, mail, user, password, sendMail);
        System.out.println("Hello!");
    }

    @Override
    public void stop() {
//		DeadCatUtils.serializeObject(ROOT_DIMENSIONS_BEAN_NAME, new RootDimensionsBean(root.getHeight(), root.getWidth(), primaryStage.getX(), primaryStage.getY()));
        System.out.println("Stage is closing");
    }
}
