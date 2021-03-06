package com.it.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKDelete {

    String IP = "192.168.1.123:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("连接创建成功");
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception {
        zooKeeper.close();
    }

    @Test
    public void delete1() throws Exception {
        // arg1: 删除节点的节点路径
        // arg2：数据版本信息 -1表示删除节点时不考虑版本信息
        zooKeeper.delete("/delete/node2",-1);
    }

    @Test
    public void delete2() throws Exception {
        // 异步使用方式
        zooKeeper.delete("/delete/node3", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                // 0 代表删除成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                // 上下文参数对象
                System.out.println(ctx);
            }
        },"I am context");
        Thread.sleep(10000);
        System.out.println("结束");
    }
}
