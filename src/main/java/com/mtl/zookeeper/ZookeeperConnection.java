package com.mtl.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 说明：
 *
 * @author 莫天龙
 * @email 92317919@qq.com
 * @dateTime 2019/07/04 21:19
 */
public class ZookeeperConnection {
    private static Logger logger= LoggerFactory.getLogger(ZookeeperConnection.class);
    private static ZooKeeper zooKeeper;
    private static String url="127.0.0.1:2181";
    public static synchronized ZooKeeper getZookeeper(){
        if (zooKeeper==null||zooKeeper.getState()!= ZooKeeper.States.CONNECTED){
            final CountDownLatch countDownLatch=new CountDownLatch(1);
            try {
                zooKeeper=new ZooKeeper(url, 15000, new Watcher() {
                    public void process(WatchedEvent event) {
                        if (event.getType()== Event.EventType.None&&event.getState()== Event.KeeperState.SyncConnected){
                            countDownLatch.countDown();
                            if (logger.isDebugEnabled()){
                                logger.debug("zookeeper连接成功！");
                            }
                        }
                    }
                });
                countDownLatch.await(20,TimeUnit.SECONDS);
            }catch (IOException e){
                logger.error("连接zookeeper服务失败 ！url:"+url,e);
                throw new RuntimeException("连接Zookeeper服务失败！URL="+url,e);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        if (zooKeeper.getState()!= ZooKeeper.States.CONNECTED){
            throw new RuntimeException("连接Zookeeper服务失败！URL="+url);
        }
        return zooKeeper;
    }

    public static void setUrl(String connectionString){
        url=connectionString;
    }

    public static String getUrl(){
        return url;
    }

    public static void close(){
        if (zooKeeper!=null&&zooKeeper.getState()== ZooKeeper.States.CONNECTED){
            try {
                zooKeeper.close();
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    }

}
