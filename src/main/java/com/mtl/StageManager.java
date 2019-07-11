package com.mtl;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.SynchronousQueue;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/07 18:50
 */
public class StageManager {
    /**
     * 缓存窗口
     */
    public static Map<Parent, Stage> stages=new HashMap<Parent, Stage>();


    /**
     * 创建一个舞台
     * @param parent
     * @param isResizable
     * @param modality
     * @param o 需要向下一个舞台传的值
     * @return
     */
    public static Stage newStage(Parent parent,boolean isResizable,Modality modality,Object o){
        Stage stage = stages.get(parent);
        if (stage==null){
            stage=new Stage();
            stage.getIcons().add(new Image(StageManager.class.getClassLoader().getResourceAsStream("zk.jpg")));
            stage.setTitle("Zookeeper Manager");
            stage.setResizable(isResizable);
            if (modality!=null){
                stage.initModality(modality);
                stage.setOnHidden(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent event) {
                        ((Stage) event.getSource()).setUserData(null);
                    }
                });
            }
            Scene scene=new Scene(parent);
            stage.setScene(scene);
            stages.put(parent,stage);
        }
        stage.setUserData(o);
        return stage;
    }

    /**
     * 获取一个loading界面
     * @param parent
     * @param primaryStage
     * @return
     */
    public static Stage newLoadingStage(Parent parent,Stage primaryStage){
        Stage stage = stages.get(parent);
        if (stage==null){
            stage=new Stage();
            Scene scene=new Scene(parent);
            stage.setScene(scene);
            scene.setFill(null);
            stage.setScene(scene);
            stage.initOwner(primaryStage);
            stage.initStyle(StageStyle.UNDECORATED);//无窗体
            stage.initStyle(StageStyle.TRANSPARENT);//透明
            stage.initModality(Modality.APPLICATION_MODAL);
            stages.put(parent,stage);
        }
        return stage;
    }

    /**
     * 获取某个组件的舞台
     * @param control
     * @return
     */
    public static Stage getStage(Control control){
        Stage window = (Stage) control.getScene().getWindow();
        return window;
    }

}
