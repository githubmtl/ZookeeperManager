package com.mtl.controller;

import com.mtl.ControllerManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/11 19:53
 */
public class AboutController implements Initializable {
    @FXML
    private Hyperlink link;
    public void initialize(URL location, ResourceBundle resources) {
        ControllerManager.set(AboutController.class.getName(),this);
    }

    /**
     * 点击链接，打开我的github
     * @param actionEvent
     */
    public void linkOnClick(ActionEvent actionEvent){
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("https://github.com/githubmtl"));
        }catch (Exception e){}
    }
}
