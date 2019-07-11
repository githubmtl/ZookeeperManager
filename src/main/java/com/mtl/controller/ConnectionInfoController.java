package com.mtl.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mtl.*;
import com.mtl.vo.Node;
import com.mtl.vo.TreeNode;
import com.mtl.zookeeper.ZookeeperConnection;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.apache.zookeeper.ZooKeeper;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/11 19:49
 */
public class ConnectionInfoController implements Initializable {
    @FXML
    private JFXButton btnmodify;

    @FXML
    private JFXTextField port;

    @FXML
    private JFXTextField ip;

    @FXML
    private JFXButton btnyes;
    public void initialize(URL location, ResourceBundle resources) {
        ControllerManager.set(ConnectionInfoController.class.getName(),this);
    }

    /**
     * 点击修改的事件
     */
    public void btnUpdateOnClick(){
        btnmodify.setDisable(true);
        port.setEditable(true);
        ip.setEditable(true);
    }

    /**
     * 点击确认时候的事件
     */
    public void btnYesOnClick(){
        if (ip.getText()==null||"".equals(ip.getText())){
            AlertMessage.error("地址不能为空！",new Exception());
            return ;
        }
        if (port.getText()==null||"".equals(port.getText())){
            AlertMessage.error("端口不能为空！！",new Exception());
            return ;
        }
        if (!ip.getText().matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")){
            AlertMessage.error("地址不合法！",new Exception());
            return ;
        }
        if (!port.getText().matches("[0-9]+")){
            AlertMessage.error("端口不合法！",new Exception());
            return ;
        }
        String[] split = ZookeeperConnection.getUrl().split(":");
        if (ip.getText().equals(split[0])&&port.getText().equals(split[1])){
            AlertMessage.error("没有修改任何信息！",new Exception());
            return ;
        }
        Task<Node> task=new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                String oldUrl=ZookeeperConnection.getUrl();
                Node root=null;
                try {
                    ZookeeperConnection.close();
                    ZookeeperConnection.setUrl(ip.getText()+":"+port.getText());
                    ZooKeeper zookeeper = ZookeeperConnection.getZookeeper();
                    root = Node.reload(zookeeper);
                }catch (Exception e){
                    ZookeeperConnection.setUrl(oldUrl);
                    ZooKeeper zookeeper = ZookeeperConnection.getZookeeper();
                    root = Node.reload(zookeeper);
                    throw new NoneException(root,e.getMessage(),e);
                }
                return root;
            }
        };
        ProgressTask<Node> progressTask=new ProgressTask<Node>(task, StageManager.getStage(btnmodify), new AsynCallBack() {
            @Override
            public void hand(ProgressTask.Result result) {
                MainController mainController = (MainController) ControllerManager.get(MainController.class.getName());
                TreeView<TreeNode> nodeTree = mainController.getNodeTree();
                if (result.getStatus()== ProgressTask.TaskStatus.SUCCESS){
                    Node root = (Node) result.getValue();
                    Node.reload(nodeTree);
                    AlertMessage.info(null,"修改连接信息成功！",StageManager.getStage(btnyes));
                }else{
                    Node.reload(nodeTree);
                    AlertMessage.error(result.getThrowable().getMessage(),result.getThrowable());
                }
            }
        });
        AlertMessage.comfire(progressTask,"你确认修改连接信息吗？点击确认修改后将断开当前连接，重新连接修改后的Zookeeper服务器！");
    }

    public JFXButton getBtnmodify() {
        return btnmodify;
    }

    public JFXTextField getPort() {
        return port;
    }

    public JFXTextField getIp() {
        return ip;
    }

    public JFXButton getBtnyes() {
        return btnyes;
    }
}
