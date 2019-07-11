package com.mtl;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 说明：提示信息
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/06 18:31
 */
public class AlertMessage {
    private static Logger logger= LoggerFactory.getLogger(AlertMessage.class);

    /**
     * 错误信息提示
     * @param errorMsg
     * @param throwable
     */
    public static void error(String errorMsg,Throwable throwable){
        logger.error(errorMsg,throwable);
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(errorMsg);
        alert.show();
    }

    /**
     * 提示信息
     * @param headerText
     * @param msg
     */
    public static void info(String headerText,String msg){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(msg);
        alert.show();
    }

    /**
     * 提示信息,确认后，关闭stage
     * @param headerText
     * @param msg
     */
    public static void info(String headerText, String msg, final Stage stage){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(msg);
        alert.setResultConverter(new Callback<ButtonType, ButtonType>() {
            public ButtonType call(ButtonType param) {
                stage.close();
                return param;
            }
        });
        alert.show();
    }

    /**
     * 异步执行确认框，确认后执行任务
     * @param asynRunner
     * @param msg
     */
    public static void comfire(final AsynRunner asynRunner, String msg){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.setResultConverter(new Callback<ButtonType, ButtonType>() {
            public ButtonType call(ButtonType param) {
                if (param==ButtonType.OK){
                    asynRunner.execute();
                }
                return param;
            }
        });
        alert.show();
    }

    /**
     * 带等待界面的确认框，确认后执行任务
     * @param task
     * @param msg
     */
    public static void comfire(final ProgressTask task, String msg){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.setResultConverter(new Callback<ButtonType, ButtonType>() {
            public ButtonType call(ButtonType param) {
                if (param==ButtonType.OK){
                    task.call();
                }
                return param;
            }
        });
        alert.show();
    }

}
