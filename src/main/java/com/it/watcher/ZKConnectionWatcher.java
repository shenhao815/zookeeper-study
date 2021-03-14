package com.it.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKConnectionWatcher implements Watcher {

    // 计数器对象
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    // 连接对象
    static ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent event) {
        try {
            // 事件类型
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("断开连接");
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时");
                    // 会话超时，说明当前连接对象已经不可用了
                    // 这时我们可以重新创建连接对象
                    zooKeeper = new ZooKeeper("192.168.1.123:2181", 5000, new ZKConnectionWatcher());
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            zooKeeper = new ZooKeeper("192.168.1.123:2181", 5000, new ZKConnectionWatcher());
            // 阻塞线程等待连接的创建
            countDownLatch.await();
            // 会话id
            System.out.println(zooKeeper.getSessionId());


            zooKeeper.addAuthInfo("digest1","itcast1:1234561".getBytes());
            byte[] data = zooKeeper.getData("/node1", false, null);
            System.out.println(new String(data));
            Thread.sleep(50000);
            zooKeeper.close();
            System.out.println("结束");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


