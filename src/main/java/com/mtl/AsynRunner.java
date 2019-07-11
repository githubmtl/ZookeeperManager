package com.mtl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 说明：异步执行器
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/06 19:07
 */
public class AsynRunner<T> {
    private Task<T> task;
    private ProgressTask.Result<T> result=new ProgressTask.Result<T>();
    private AsynCallBack<T> callBack;
    private static  ExecutorService executorService= Executors.newCachedThreadPool();

    public AsynRunner(Task<T> task, AsynCallBack<T> callBack) {
        this.task = task;
        this.callBack = callBack;
    }

    public void execute(){
        task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                result.setStatus(ProgressTask.TaskStatus.FAIL);
                callBack.hand(result);
            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                result.setStatus(ProgressTask.TaskStatus.FAIL);
                callBack.hand(result);
            }
        });
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                result.setStatus(ProgressTask.TaskStatus.SUCCESS);
                callBack.hand(result);
            }
        });
        task.valueProperty().addListener(new ChangeListener<T>() {
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                result.setValue(newValue);
            }
        });
        task.exceptionProperty().addListener(new ChangeListener<Throwable>() {
            public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable newValue) {
                result.setThrowable(newValue);
                result.setStatus(ProgressTask.TaskStatus.FAIL);
            }
        });
        executorService.submit(task);
    }
}
