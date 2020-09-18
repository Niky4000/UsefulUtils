/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import ru.kiokle.marykaylib.Keys;
import ru.kiokle.marykaylib.MailHandlerImpl;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import static ru.kiokle.marykaylib.CommonUtil.getPathToSaveFolder;
import static ru.kiokle.marykaylib.MailHandlerImpl.QUEUE_SIZE;
import ru.kiokle.marykaylib.MailHandlerWebImpl;
import ru.kiokle.marykaylib.bean.MailBean;
import static ru.kiokle.marykaylib.bean.MailBean.ACCEPTED;
import static ru.kiokle.marykaylib.bean.MailBean.CPU_ID;
import static ru.kiokle.marykaylib.bean.MailBean.REASON;
import static ru.kiokle.marykaylib.bean.MailBean.USER_NAME;
import ru.kiokle.marykaylib.threads.MailReaderThread;
import ru.kiokle.marykaylib.threads.MailSenderThread;

/**
 *
 * @author me
 */
public class InterfaceStart extends Application {

    protected static final int TIME_TO_WAIT = 20000;
    Stage primaryStage;
    TabPane root;

    public static void main(String[] args) throws Exception {
//        MailHandlerWebImpl.sendPost("http://kiokle.ru/index2.php?name=Babuvin&oiooi=DFGGG");
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
                startApplication(primaryStage);
            } else {
                sendMail();
            }
        } else {
            sendMail();
        }
    }

    public void startApplication(Stage primaryStage1) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("form.fxml"));
        root = (TabPane) loader.load();
        ImageCreateController imageCreateController = loader.getController();
        primaryStage1.setOnHidden(e -> imageCreateController.shutdown());
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        primaryStage1.setTitle("Программа создания наклеек для тестеров Mary Kay");
        primaryStage1.setScene(scene);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
//		if (rootDimensionsBean != null) {
//			primaryStage.setX(rootDimensionsBean.getX());
//			primaryStage.setY(rootDimensionsBean.getY());
//		}
        primaryStage1.show();
        this.primaryStage = primaryStage1;
    }

    private void sendMail() {
        Properties properties = new Properties();
        Properties properties2 = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("mailConfig.conf"));
            properties2.load(getClass().getClassLoader().getResourceAsStream("mailConfig2.conf"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String imapServer = properties2.getProperty("mail.read.imap.server");
        String fromMail = (String) properties2.get("mail.read.box");
        String user = (String) properties2.get("mail.read.user");
        String password = (String) properties2.get("mail.read.password");
        String mailForSending = (String) properties2.get("mail.send.box");
        String userForSending = (String) properties2.get("mail.send.user");
        String passwordForSending = (String) properties2.get("mail.send.password");
        String toMail = (String) properties2.get("mail.box.for.sending");

        TextInputDialog dialog = new TextInputDialog("сюда необходимо ввести регистрационные данные");
        dialog.setTitle("Регистрационные данные. Необходимо подключение к Internet!");
        dialog.setHeaderText("В данное поле необходимо ввести регистрационные данные");
        dialog.setContentText("Поле:");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            BlockingQueue<String> displayQueue = new LinkedBlockingQueue<String>();
            displayFileUploadForm(displayQueue);
            final String cpuId = CpuClass.getCPUId();
            String userName = result.get();
            String stringToSign = cpuId + "_" + userName;
            ArrayBlockingQueue<MailBean> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            MailReaderThread mailReaderThread = new MailReaderThread(user, password, imapServer, mailBean -> mailBean != null && mailBean.getSubject() != null && mailBean.getSubject().contains(stringToSign), queue);
            boolean exception = false;
            try {
                mailReaderThread.readMail();
            } catch (Exception ex) {
                Logger.getLogger(InterfaceStart.class.getName()).log(Level.SEVERE, null, ex);
                exception = true;
            }
            displayQueue.add("Начата активация программы.");
            analizeResponse(queue, queue_ -> queue_.size() == 1, exception);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Properties propertiesForSending = new Properties();
            propertiesForSending.setProperty(USER_NAME, userName);
            propertiesForSending.setProperty(CPU_ID, cpuId);
            try {
                propertiesForSending.store(byteArrayOutputStream, "propertiesForSending");
                String sign = new String(byteArrayOutputStream.toByteArray());
                MailBean mailBean = new MailBean(null, Arrays.asList(mailForSending), stringToSign, sign);
                ArrayBlockingQueue<MailBean> queueForSendBack = new ArrayBlockingQueue<>(QUEUE_SIZE);
                MailSenderThread mailSenderThread = new MailSenderThread(userForSending, passwordForSending, properties, fromMail, toMail, queueForSendBack);
                mailSenderThread.sendMail(mailBean);
                displayQueue.add("Послан запрос на активацию программы.");
                mailReaderThread.start();
                displayQueue.add("Ожидание ответа сервера активации.");
                analizeResponse(queue, queue_ -> true, exception);
            } catch (IOException ex) {
                Logger.getLogger(InterfaceStart.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            activationErrorDialog();
        }
//        mailHandler.sendMail2(properties, mail, user, password, sendMail);
        System.out.println("Hello!");
    }

    private void analizeResponse(ArrayBlockingQueue<MailBean> queue, Predicate<ArrayBlockingQueue<MailBean>> condition, boolean exception) {
        if (condition.test(queue)) {
            try {
                MailBean mailBean = queue.take();
                if (mailBean.isNull()) {
                    exception = true;
                } else {
                    Properties data = new Properties();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mailBean.getText().getBytes());
                    try {
                        data.load(byteArrayInputStream);
                        String accepted = (String) data.get(ACCEPTED);
                        if (accepted != null && accepted.equals("true")) {
                            startApplication(primaryStage);
                        } else {
                            String reason = (String) data.get(REASON) != null ? (String) data.get(REASON) : "По неизвестной технической причине!";
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Произвести активацию не удалось!");
                            alert.setHeaderText("Произвести активацию не удалось!");
                            alert.setContentText(reason);
                            alert.setResizable(true);
                            alert.showAndWait();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(InterfaceStart.class.getName()).log(Level.SEVERE, null, ex);
                        exception = true;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(InterfaceStart.class.getName()).log(Level.SEVERE, null, ex);
                exception = true;
            }
        }
        if (exception) {
            activationErrorDialog();
        }
    }

    private void activationErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Произвести активацию не удалось!");
        alert.setHeaderText("Произвести активацию не удалось!");
        alert.setContentText("Произошла неизвестная техническая ошибка!");
        alert.setResizable(true);
        alert.showAndWait();
    }

    private static String DEAD_PILL = "DEAD_PILL";
    private static final double DEFAULT_POSITION = 100d;
    private static final int TIME_TO_WAIT_BEFORE_CLOSE_UPDATE_WINDOW = 10 * 1000;

    private void displayFileUploadForm(BlockingQueue<String> queue) {
        try {
            GridPane root = FXMLLoader.load(getClass().getClassLoader().getResource("activation_form.fxml"));
            TextArea textArea = (TextArea) root.getChildrenUnmodifiable().get(0);
            textArea.textProperty().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue,
                        Object newValue) {
                    textArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Активация...");
            stage.setX(DEFAULT_POSITION);
            stage.setY(DEFAULT_POSITION);
            stage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            stage.show();
            Thread formThread = new Thread(() -> {
                try {
                    String take;
                    do {
                        take = queue.take();
                        if (!take.equals(DEAD_PILL)) {
                            textArea.appendText(take + "\n");
                        }
                    } while (!take.equals(DEAD_PILL));
                    Platform.runLater(() -> {
                        try {
                            Thread.sleep(TIME_TO_WAIT_BEFORE_CLOSE_UPDATE_WINDOW);
                            ((Stage) textArea.getScene().getWindow()).close();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InterfaceStart.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(InterfaceStart.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            formThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
//		DeadCatUtils.serializeObject(ROOT_DIMENSIONS_BEAN_NAME, new RootDimensionsBean(root.getHeight(), root.getWidth(), primaryStage.getX(), primaryStage.getY()));
        System.out.println("Stage is closing");
    }
}
