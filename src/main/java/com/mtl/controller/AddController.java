package com.mtl.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mtl.*;
import com.mtl.vo.Node;
import com.mtl.vo.TreeNode;
import com.mtl.zookeeper.ZookeeperConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/07 16:48
 */
public class AddController implements Initializable{

    @FXML
    private JFXButton addyes;

    @FXML
    private TextArea addValue;

    @FXML
    private JFXButton addno;

    @FXML
    private JFXTextField addname;

    @FXML
    private Label addqzlbelname;

    @FXML
    private CheckBox checkp;
    @FXML
    private CheckBox checke;
    @FXML
    private CheckBox checks;

    public void initialize(URL location, ResourceBundle resources) {
        ControllerManager.set(AddController.class.getName(),this);
        //默认持久和临时只能选择一个
        checkp.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue==true){
                    checke.setSelected(false);
                }else{
                    checke.setSelected(true);
                }
            }
        });
        checke.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue==true){
                    checkp.setSelected(false);
                }else{
                    checkp.setSelected(true);
                }
            }
        });
    }

    public void addyesOnClick(final ActionEvent actionEvent) {
        if (addname.getText()==null||addname.getText().equals("")){
            AlertMessage.error("请输入节点名称！节点名称不能为空！",new RuntimeException("添加参数不能为空！"));
            return ;
        }
        if (addValue.getText()==null||addValue.getText().equals("")){
            AlertMessage.error("请输入值！节点值不能为空！",new RuntimeException("添加参数不能为空！"));
            return ;
        }
        CreateMode createMode=CreateMode.PERSISTENT;//默认为持久节点
        //判断节点类型
        if (checks.isSelected()){
            if (checkp.isSelected()){
                createMode=CreateMode.PERSISTENT_SEQUENTIAL;
            }else{
                createMode=CreateMode.EPHEMERAL_SEQUENTIAL;
            }
        }else{
            if (checkp.isSelected()){
                createMode=CreateMode.PERSISTENT;
            }else{
                createMode=CreateMode.EPHEMERAL;
            }
        }
        final CreateMode c=createMode;
        //执行创建任务
        Task<Node> createTask=new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                //发送添加请求
                String s = ZookeeperConnection.getZookeeper().create(
                        addqzlbelname.getText() + addname.getText(), addValue.getText().getBytes("UTF-8"),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, c);
                MainController mainController = (MainController) ControllerManager.get(MainController.class.getName());
                //刷新新的树节点
                Node node = Node.reload(ZookeeperConnection.getZookeeper());
                return node;
            }
        };
        ProgressTask<Node> progressTask=new ProgressTask<Node>(createTask,StageManager.getStage(addqzlbelname),new AsynCallBack() {
            public void hand(ProgressTask.Result result) {
                if (result.getStatus()== ProgressTask.TaskStatus.SUCCESS){
                    Node root = (Node) result.getValue();
                    MainController mainController = (MainController) ControllerManager.get(MainController.class.getName());
                    Node.reload(mainController.getNodeTree());
                    Alert alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("添加成功！");
                    alert.setResultConverter(new Callback<ButtonType, ButtonType>() {//关闭添加页面
                        public ButtonType call(ButtonType param) {
                            Stage stage = StageManager.getStage(addyes);
                            stage.close();
                            return param;
                        }
                    });
                    alert.show();
                }else{
                    AlertMessage.error("添加失败！"+result.getThrowable().getMessage(),result.getThrowable());
                }
            }
        });
        AlertMessage.comfire(progressTask,"确认添加["+addqzlbelname.getText()+addname.getText()+"]节点吗？\n节点类型："+c+"\n节点数据为："+addValue.getText());
    }

    /**
     * 关闭舞台
     * @param actionEvent
     */
    public void addnoOnClick(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();
        Stage window = ((Stage) button.getScene().getWindow());
        window.close();
    }

    public JFXButton getAddyes() {
        return addyes;
    }

    public TextArea getAddValue() {
        return addValue;
    }

    public JFXButton getAddno() {
        return addno;
    }

    public JFXTextField getAddname() {
        return addname;
    }

    public Label getAddqzlbelname() {
        return addqzlbelname;
    }

    public CheckBox getCheckp() {
        return checkp;
    }

    public CheckBox getChecke() {
        return checke;
    }

    public CheckBox getChecks() {
        return checks;
    }
}
