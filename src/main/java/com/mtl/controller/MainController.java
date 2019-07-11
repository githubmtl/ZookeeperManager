package com.mtl.controller;

import com.jfoenix.controls.JFXTextField;
import com.mtl.*;
import com.mtl.vo.Node;
import com.mtl.vo.TreeNode;
import com.mtl.zookeeper.ZookeeperConnection;
import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

public class MainController implements Initializable {

    private Logger logger= LoggerFactory.getLogger(MainController.class);

    /**
     * 根节点数据
     */
    public static Node root=null;

    /**
     * 树形节点
     */
    @FXML
    private TreeView<TreeNode> nodeTree;

    @FXML
    private Label name;
    @FXML
    private Label data;
    @FXML
    private Label cZxid;
    @FXML
    private Label mZxid;
    @FXML
    private Label pZxid;
    @FXML
    private Label ctime;
    @FXML
    private Label mtime;
    @FXML
    private Label dataVersion;
    @FXML
    private Label cversion;
    @FXML
    private Label aclversion;
    @FXML
    private Label ephemeralOwner;
    @FXML
    private Label dataLength;
    @FXML
    private Label numChildren;


    public void initialize(URL location, ResourceBundle resources) {
        int initFxmls = FxmlManager.addFromPath("/fxml");
        if (logger.isDebugEnabled()){
            logger.debug("加载了["+initFxmls+"]个初始页面");
            logger.debug(FxmlManager.getAll().toString());
        }
        TreeItem<TreeNode> rootItem=new TreeItem<TreeNode>(new TreeNode(root));
        nodeTree.setRoot(rootItem);
        Node.addChildren(rootItem,root);
        //监听双击事件，把选中的节点的信息展示在右侧
        nodeTree.setOnMouseClicked(new EventHandler<MouseEvent>() {//监听树形结构的点击事件
            public void handle(MouseEvent event) {
                TreeItem<TreeNode> selectedItem = nodeTree.getSelectionModel().getSelectedItem();
                if (selectedItem!=null){
                    Node node = selectedItem.getValue().getNode();
                    Stat stat = node.getStat();
                    String nodename = node.getName();
                    String nodedata = node.getData();
                    name.setText("name:\t\t\t"+(nodename==null?"":nodename));
                    data.setText("data:\t\t\t\t"+(nodedata==null?"":nodedata));
                    cZxid.setText("cZxid:\t\t\t"+String.valueOf(stat.getCzxid()));
                    mZxid.setText("mZxid:\t\t\t"+String.valueOf(stat.getMzxid()));
                    pZxid.setText("pZxid:\t\t\t"+String.valueOf(stat.getPzxid()));
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    ctime.setText("ctime:\t\t\t"+simpleDateFormat.format(new Date(stat.getCtime())));
                    mtime.setText("mtime:\t\t\t"+simpleDateFormat.format(new Date(stat.getMtime())));
                    dataVersion.setText("dataVersin:\t\t"+String.valueOf(stat.getDataLength()));
                    cversion.setText("cversion:\t\t\t"+String.valueOf(stat.getCversion()));
                    aclversion.setText("aclVersion:\t\t"+String.valueOf(stat.getAversion()));
                    ephemeralOwner.setText("ephemeralOwner:\t"+String.valueOf(stat.getEphemeralOwner()));
                    dataLength.setText("dataLength:\t\t"+String.valueOf(stat.getDataLength()));
                    numChildren.setText("numChildren:\t\t"+String.valueOf(stat.getNumChildren()));
                }
            }
        });
        //设置右键菜单
        ContextMenu contextMenu=new ContextMenu();
        MenuItem menuAdd=new MenuItem("添加");
        //点击添加弹出添加页面
        menuAdd.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    TreeItem<TreeNode> selectedItem = nodeTree.getSelectionModel().getSelectedItem();
                    if (selectedItem==null){
                        throw new Exception("请选中一个节点再进行添加！");
                    }
                    Stage stage= StageManager.newStage(FxmlManager.get("init_add.fxml"),false,Modality.APPLICATION_MODAL,nodeTree);
                    stage.setOnShown(new EventHandler<WindowEvent>() {//设置显示监听事件
                        public void handle(WindowEvent event) {
                            //初始化页面的值
                            TreeItem<TreeNode> item = nodeTree.getSelectionModel().getSelectedItem();
                            TreeNode value = item.getValue();
                            AddController addController = (AddController) ControllerManager.get(AddController.class.getName());
                            if (value.getNode()!=Node.getRoot()){
                                addController.getAddqzlbelname().setText(value.getNode().getName()+"/");
                            }else{
                                addController.getAddqzlbelname().setText("/");
                            }
                            addController.getAddname().setText("");
                            addController.getAddValue().setText("");
                        }
                    });
                    stage.showAndWait();
                }catch (Exception e){
                    AlertMessage.error(e.getMessage(),e);
                }
            }
        });

        MenuItem menuDelete=new MenuItem("删除");
        //点击删除按钮，删除对应的节点
        menuDelete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                final TreeItem<TreeNode> selectedItem = nodeTree.getSelectionModel().getSelectedItem();
                if (selectedItem==null){
                    AlertMessage.error("请选择一个节点！",new Exception());
                    return ;
                }
                Task<TreeItem> task=new Task<TreeItem>() {
                    @Override
                    protected TreeItem call() throws Exception {
                        String path = selectedItem.getValue().getNode().getName();
                        ZooKeeper zookeeper = ZookeeperConnection.getZookeeper();
                        zookeeper.delete(path,selectedItem.getValue().getNode().getStat().getVersion());
                        return selectedItem;
                    }
                };
                ProgressTask<TreeItem> progressTask=new ProgressTask<TreeItem>(task, StageManager.getStage(nodeTree), new AsynCallBack() {
                    public void hand(ProgressTask.Result result) {
                        if (result.getStatus()== ProgressTask.TaskStatus.SUCCESS){
                            try {
                                TreeItem<TreeNode> selectItem = (TreeItem<TreeNode>) result.getValue();
                                boolean remove = selectItem.getParent().getChildren().remove(selectedItem);
                                if (remove){
                                    AlertMessage.info(null,"删除成功！");
                                }else{
                                    AlertMessage.error("删除失败！",new Exception());
                                }
                            }catch (Exception e){
                                AlertMessage.error(e.getMessage(),e);
                            }
                        }else{
                            AlertMessage.error("处理失败！"+result.getThrowable().getMessage(),result.getThrowable());
                        }
                    }
                });
                AlertMessage.comfire(progressTask,"确认要删除["+selectedItem.getValue().getNode().getName()+"]节点吗？");
            }
        });
        MenuItem menuSetData=new MenuItem("重新设值");
        //点击重新设置按钮事件
        menuSetData.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                final TreeItem<TreeNode> selectedItem = nodeTree.getSelectionModel().getSelectedItem();
                if (selectedItem==null){
                    AlertMessage.error("请选择一个节点！",new Exception());
                    return;
                }
                Stage stage= StageManager.newStage(FxmlManager.get("init_updateValue.fxml"),false,Modality.APPLICATION_MODAL,selectedItem);
                stage.setOnShown(new EventHandler<WindowEvent>() {//设置显示监听事件
                    public void handle(WindowEvent event) {
                        //初始化页面的值
                        UpdateValueController updateValueController = (UpdateValueController) ControllerManager.get(UpdateValueController.class.getName());
                        updateValueController.getNodeValue().setText("");
                        updateValueController.getNodeName().setText(selectedItem.getValue().getNode().getName());
                    }
                });
                stage.showAndWait();
            }
        });
        MenuItem menuUpdate=new MenuItem("刷新");
        //点击刷新按钮，刷新所有节点
        menuUpdate.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //从zookeeper服务器获取最新的所有节点信息
                Task<Node> task=new Task<Node>() {
                    @Override
                    protected Node call() throws Exception {
                        Node root = Node.reload(ZookeeperConnection.getZookeeper());
                        return root;
                    }
                };
                ProgressTask<Node> progressTask=new ProgressTask<Node>(task, StageManager.getStage(nodeTree), new AsynCallBack() {
                    public void hand(ProgressTask.Result result) {
                        if (result.getStatus()== ProgressTask.TaskStatus.SUCCESS){
                            try {
                                Node root = (Node) result.getValue();
                                Node.reload(nodeTree);
                                AlertMessage.info(null,"刷新成功！");
                            }catch (Exception e){
                                AlertMessage.error("处理失败!"+e.getMessage(),e);
                            }
                        }else{
                            AlertMessage.error("处理失败！"+result.getThrowable().getMessage(),result.getThrowable());
                        }
                    }
                });
                AlertMessage.comfire(progressTask,"确定需要重新从服务器获取节点信息吗？");
            }
        });
        contextMenu.getItems().addAll(menuAdd,menuDelete,menuSetData,menuUpdate);
        nodeTree.setContextMenu(contextMenu);
        ControllerManager.set(MainController.class.getName(),this);
    }


    /**
     * 各属性点击事件
     * @param actionEvent
     */
    @FXML
    public void labelOnclick(MouseEvent actionEvent){
        Label label = (Label) actionEvent.getSource();
        String text = label.getText();
        int i = text.indexOf(":");
        String msg=null;
        if (i==text.length()){
            msg="";
        }else {
            msg=text.substring(i+1);
        }
        AlertMessage.info(text.substring(0,i),msg.trim());
    }

    /**
     * 点击关于按钮的事件
     */
    public void aboutOnClick(){
        Stage stage = StageManager.newStage(FxmlManager.get("init_about.fxml"), false, Modality.APPLICATION_MODAL, null);
        stage.showAndWait();
    }

    /**
     * 点击连接信息按钮事件
     */
    public void btnConnectionOnClick(){
        Stage stage = StageManager.newStage(FxmlManager.get("init_connection.fxml"), false, Modality.APPLICATION_MODAL, null);
        stage.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ConnectionInfoController connectionInfoController = (ConnectionInfoController) ControllerManager.get(ConnectionInfoController.class.getName());
                JFXTextField ip = connectionInfoController.getIp();
                JFXTextField port = connectionInfoController.getPort();
                String url = ZookeeperConnection.getUrl();
                String[] split = url.split(":");
                ip.setText(split[0]);
                port.setText(split[1]);
                connectionInfoController.getIp().setEditable(false);
                connectionInfoController.getPort().setEditable(false);
                connectionInfoController.getBtnmodify().setDisable(false);
            }
        });
        stage.showAndWait();
    }

    public TreeView<TreeNode> getNodeTree() {
        return nodeTree;
    }

    public Label getName() {
        return name;
    }

    public Label getData() {
        return data;
    }

    public Label getcZxid() {
        return cZxid;
    }

    public Label getmZxid() {
        return mZxid;
    }

    public Label getpZxid() {
        return pZxid;
    }

    public Label getCtime() {
        return ctime;
    }

    public Label getMtime() {
        return mtime;
    }

    public Label getDataVersion() {
        return dataVersion;
    }

    public Label getCversion() {
        return cversion;
    }

    public Label getAclversion() {
        return aclversion;
    }

    public Label getEphemeralOwner() {
        return ephemeralOwner;
    }

    public Label getDataLength() {
        return dataLength;
    }

    public Label getNumChildren() {
        return numChildren;
    }

}
