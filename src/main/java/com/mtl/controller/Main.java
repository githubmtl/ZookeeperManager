package com.mtl.controller;

import com.mtl.FxmlManager;
import com.mtl.zookeeper.ZookeeperConnection;
import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jdk.nashorn.internal.ir.IfNode;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage)  throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/waiting.fxml"));
        primaryStage.setScene(new Scene(root, 400, 269));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Zookeeper Manager");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                //关闭zookeeper
                ZookeeperConnection.close();
                System.exit(0);
            }
        });
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("zk.jpg")));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
