package com.mtl.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/waiting.fxml"));
        primaryStage.setScene(new Scene(root, 400, 269));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Zookeeper Manager");
        System.out.println(primaryStage.getStyle());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        System.out.println(primaryStage);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
