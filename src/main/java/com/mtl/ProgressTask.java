package com.mtl;

import com.mtl.controller.LoadingController;
import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.*;

/**
 * 说明:一个带等待框的任务
 *
 * @作者 莫天龙
 * @时间 2019/07/05 15:29
 */
public class ProgressTask<T> {
    private Task<T> task;
    private Result<T> result=new Result<T>();
    private AsynCallBack callBack;
    private String msg;
    private Stage primaryStage;//所属的父舞台
    private static ExecutorService executorService=Executors.newSingleThreadExecutor();

    public ProgressTask(Task<T> task,Stage primaryStage, AsynCallBack callBack) {
        this.task = task;
        this.callBack = callBack;
        this.primaryStage=primaryStage;
    }


    public void call(){
        // 窗口父子关系
        final Stage stage=StageManager.newLoadingStage(FxmlManager.get("init_loading.fxml"),primaryStage);
        stage.setX(primaryStage.getX()+primaryStage.getWidth()/5*2);
        stage.setY(primaryStage.getY()+primaryStage.getHeight()/5*2);
        stage.show();
        //监听字段变化
        task.valueProperty().addListener(new ChangeListener<T>() {
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                result.setValue(newValue);
            }
        });
        //监听成功回调
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                stage.close();
                result.setStatus(TaskStatus.SUCCESS);
                callBack.hand(result);
            }
        });
        //监听失败回调
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                stage.close();
                result.setStatus(TaskStatus.FAIL);
                callBack.hand(result);
            }
        });
        task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                stage.close();
            }
        });
        //监听异常
        task.exceptionProperty().addListener(new ChangeListener<Throwable>() {
            public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable newValue) {
                result.setThrowable(newValue);
            }
        });
        executorService.submit(task);
    }


    /**
     * 结果对象
     * @param <T>
     */
    public static class Result<T>{
        private T value;//方法返回值
        private TaskStatus status;//状态
        private String msg;
        private Throwable throwable;

        public Result() {
        }

        public Result(T value, TaskStatus status) {
            this.value = value;
            this.status = status;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "value=" + value +
                    ", status=" + status +
                    ", msg='" + msg + '\'' +
                    '}';
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static enum TaskStatus{
        SUCCESS,FAIL;
    }
}
