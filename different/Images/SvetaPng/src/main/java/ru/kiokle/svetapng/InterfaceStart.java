/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import java.io.IOException;
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
        launch(args);
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
