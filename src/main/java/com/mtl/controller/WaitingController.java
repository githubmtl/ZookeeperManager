package com.mtl.controller;

import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 说明:
 *
 * @作者 莫天龙
 * @时间 2019/07/04 12:19
 */
public class WaitingController implements Initializable {

    @FXML
    private ProgressBar progressbar;


    public void setpro(ActionEvent actionEvent){
        progressbar.setProgress(progressbar.getProgress()+0.1);
    }

    public void initialize(URL location, ResourceBundle resources) {
        progressbar.progressProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.doubleValue()>=1){
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sample.fxml"));
                        ObservableList<Stage> stages = FXRobotHelper.getStages();
                        Stage stage = stages.get(0);
                        stage.hide();
                        Stage mainState = new Stage();
                        mainState.setScene(new Scene(root,600,400));
                        mainState.setTitle("Zookeeper Manager");
                        mainState.show();
                    }catch (Exception e){
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
        });
    }
}
