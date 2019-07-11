package com.mtl;

import java.util.concurrent.Future;

/**
 * 说明:执行器执行完了以后的回调
 *
 * @作者 莫天龙
 * @时间 2019/07/05 17:34
 */
public interface AsynCallBack<T> {
    /**
     * 回调
     * @param result
     */
    void hand(ProgressTask.Result<T> result);
}
