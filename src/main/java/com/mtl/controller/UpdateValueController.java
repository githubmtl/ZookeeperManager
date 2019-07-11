package com.mtl.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.mtl.*;
import com.mtl.vo.Node;
import com.mtl.vo.TreeNode;
import com.mtl.zookeeper.ZookeeperConnection;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.apache.zookeeper.data.Stat;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 说明:
 *
 * @作者 莫天龙
 * @时间 2019/07/10 12:28
 */
public class UpdateValueController implements Initializable {
    @FXML
    private JFXTextField nodeName;

    @FXML
    private JFXTextArea nodeValue;

    @FXML
    private JFXButton btnno;

    @FXML
    private JFXButton btnyes;

    public void initialize(URL location, ResourceBundle resources) {
        ControllerManager.set(UpdateValueController.class.getName(),this);
    }


    /**
     * 确认按钮事件
     * @param actionEvent
     */
    public void btnyesOnClick(ActionEvent actionEvent){
        if (nodeValue.getText()==null||nodeValue.getText().equals("")){
            AlertMessage.error("请输入新的值！",new Exception());
            return ;
        }
        final TreeItem<TreeNode> selectedItem = (TreeItem<TreeNode>) StageManager.getStage(btnyes).getUserData();
        Task<Integer> task=new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int version = selectedItem.getValue().getNode().getStat().getVersion();
                Stat stat = ZookeeperConnection.getZookeeper().setData(nodeName.getText(), nodeValue.getText().getBytes("UTF-8"), version);
                selectedItem.getValue().getNode().setData(nodeValue.getText());
                selectedItem.getValue().getNode().setStat(stat);
                return 1;
            }
        };
        ProgressTask<Integer> progressTask=new ProgressTask<Integer>(task, StageManager.getStage(btnyes), new AsynCallBack() {
            public void hand(ProgressTask.Result result) {
                if (result.getStatus()== ProgressTask.TaskStatus.SUCCESS){
                    MainController mainController = (MainController) ControllerManager.get(MainController.class.getName());
                    //界面显示重新设值
                    mainController.getData().setText("data:\t\t\t\t"+nodeValue.getText());
                    AlertMessage.info(null,"处理成功！",StageManager.getStage(btnyes));
                }else{
                    AlertMessage.error("处理失败!"+result.getThrowable().getMessage(),result.getThrowable());
                }
            }
        });
        Node node = selectedItem.getValue().getNode();
        AlertMessage.comfire(progressTask,"你确定为节点["+node.getName()+"]设置新值吗？\n节点原来的值为:"+node.getData()+"\n节点新的值为:"+nodeValue.getText());
    }

    /**
     * 取消按钮事件
     * @param actionEvent
     */
    public void btnnoOnClick(ActionEvent actionEvent){
        Stage stage = StageManager.getStage(btnno);
        stage.close();
    }

    public JFXTextField getNodeName() {
        return nodeName;
    }

    public void setNodeName(JFXTextField nodeName) {
        this.nodeName = nodeName;
    }

    public JFXTextArea getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(JFXTextArea nodeValue) {
        this.nodeValue = nodeValue;
    }

    public JFXButton getBtnno() {
        return btnno;
    }

    public void setBtnno(JFXButton btnno) {
        this.btnno = btnno;
    }

    public JFXButton getBtnyes() {
        return btnyes;
    }

    public void setBtnyes(JFXButton btnyes) {
        this.btnyes = btnyes;
    }
}
