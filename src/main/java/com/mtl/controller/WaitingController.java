package com.mtl.controller;

import com.mtl.AlertMessage;
import com.mtl.AsynCallBack;
import com.mtl.AsynRunner;
import com.mtl.ProgressTask;
import com.mtl.vo.Node;
import com.mtl.zookeeper.ZookeeperConnection;
import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * 说明:
 *
 * @作者 莫天龙
 * @时间 2019/07/04 12:19
 */
public class WaitingController {
    private static Logger logger= LoggerFactory.getLogger(WaitingController.class);
    @FXML
    private ProgressBar progressbar;
    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private Button connectionbt;
    @FXML
    private Pane loginPane;


    public void connection(ActionEvent actionEvent){
        connectionbt.setDisable(true);
        progressbar.setVisible(true);
        ip.setEditable(false);
        port.setEditable(false);
        try {
            if (ip.getText()==null||"".equals(ip.getText())){
                throw new Exception("地址不能为空！");
            }
            if (port.getText()==null||"".equals(port.getText())){
                throw new Exception("端口不能为空！");
            }
            if (!ip.getText().matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")){
                throw new Exception("地址不合法！");
            }
            if (!port.getText().matches("[0-9]+")){
                throw new Exception("端口不合法！");
            }
            Task<Node> task=new Task<Node>() {
                @Override
                protected Node call() throws Exception {
                    ZookeeperConnection.setUrl(ip.getText()+":"+port.getText());
                    ZooKeeper zookeeper = ZookeeperConnection.getZookeeper();
                    Node node=Node.init(zookeeper);
                    return node;
                }
            };
            AsynRunner<Node> asynRunner=new AsynRunner<Node>(task, new AsynCallBack<Node>() {
                public void hand(ProgressTask.Result<Node> result) {
                    if (result.getStatus()== ProgressTask.TaskStatus.FAIL){
                        AlertMessage.error(result.getThrowable().getMessage(),result.getThrowable());
                        progressbar.setVisible(false);
                        connectionbt.setDisable(false);
                        ip.setEditable(true);
                        port.setEditable(true);
                    }else{
                        Node node = result.getValue();
                        MainController.root=node;
                        Stage stage=new Stage();
                        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("zk.jpg")));
                        stage.setTitle("Zookeeper Manager");
                        try {
                            Parent root = FXMLLoader.load(Main.class.getResource("/fxml/sample.fxml"));
                            stage.setScene(new Scene(root, 600, 400));
                        }catch (IOException e){
                            logger.error("初始化失败！",e);
                            AlertMessage.error("初始化失败！",e);
                        }
                        stage.setTitle("Zookeeper Manager");
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            public void handle(WindowEvent event) {
                                //关闭zookeeper
                                ZookeeperConnection.close();
                                System.exit(0);
                            }
                        });
                        stage.show();
                        //关闭其他场景
                        ObservableList<Stage> stages = FXRobotHelper.getStages();
                        stages.get(0).close();
                    }
                }
            });
            asynRunner.execute();
        }catch (Throwable throwable){
            logger.error("登录失败！",throwable);
            AlertMessage.error(throwable.getMessage(),throwable);
            progressbar.setVisible(false);
            connectionbt.setDisable(false);
            ip.setEditable(true);
            port.setEditable(true);
        }
    }

}
