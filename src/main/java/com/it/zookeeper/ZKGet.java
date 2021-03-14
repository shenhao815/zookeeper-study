package com.it.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKGet {

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
    public void get1() throws Exception {
        Stat stat = new Stat();
        // arg1：节点的路径
        // arg3：读取节点属性的对象
        byte[] bys = zooKeeper.getData("/get/node1", false, stat);
        // 打印数据
        System.out.println(new String(bys));
        // 打印版本信息
        System.out.println(stat.getVersion());
    }

    @Test
    public void get2() throws Exception {
        // 异步方式
        zooKeeper.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] bytes, Stat stat) {
                // 0 代表成功
                System.out.println(rc);
                // 节点路径
                System.out.println(path);
                // 上下文参数
                System.out.println(ctx);
                // 数据
                System.out.println(new String(bytes));
                // 属性对象
                System.out.println(stat.getVersion());
            }
        }, "I am context");
        Thread.sleep(10000);
        System.out.println("结束");
    }
}
