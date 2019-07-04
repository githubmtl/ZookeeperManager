package com.mtl.controller;

import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button b1;
    @FXML
    private TreeView tv;

    public void onChangeClick(ActionEvent actionEvent){
        Object selectedItem = tv.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem);
    }

    public void tvclick(ActionEvent actionEvent){
        System.out.println(actionEvent);
        System.out.println(actionEvent.getSource());
        System.out.println(actionEvent.getEventType());
        System.out.println(actionEvent.getTarget());
        System.out.println("----------------------------");
    }

    public void initialize(URL location, ResourceBundle resources) {
        /*TreeItem root = new TreeItem("/");
        TreeItem treeItem = new TreeItem("/zookeeper");
        root.getChildren().add(treeItem);
        root.getChildren().add(new TreeItem("service"));
        TreeItem test = new TreeItem("test");
        treeItem.getChildren().add(test);
        tv.setRoot(root);
        root.addEventHandler(EventType.ROOT, new EventHandler() {
            public void handle(Event event) {
                System.out.println(event.getTarget()+"\t"+event.getEventType());
                System.out.println("-----------------------------------");
            }
        });
        tv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println(oldValue);
                System.out.println(newValue);
                System.out.println(observable);
                System.out.println("-----------------------");
            }
        });*/
    }

    public void onConnectionBtClick(ActionEvent actionEvent){
        ObservableList<Stage> stages = FXRobotHelper.getStages();
        for (Stage stage:stages){
            System.out.println(stage);
            System.out.println("----------------------");
        }
    }
}
