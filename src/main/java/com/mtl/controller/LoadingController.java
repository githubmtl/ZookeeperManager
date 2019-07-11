package com.mtl.controller;

import com.mtl.ControllerManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/09 20:33
 */
public class LoadingController implements Initializable {
    @FXML
    private VBox loadingvbox;

    public void initialize(URL location, ResourceBundle resources) {
        loadingvbox.setBackground(Background.EMPTY);
        ControllerManager.set(LoadingController.class.getName(),this);
    }
}
