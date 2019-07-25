package com.niezhiliang.luck.draw;

import com.niezhiliang.luck.draw.utils.HttpUtil;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author NieZhiLiang
 * @Email nzlsgg@163.com
 * @GitHub https://github.com/niezhiliang
 * @Date 2019/07/24 14:51
 */
public class TestIncre {

    /**
     * 多线程测试redis的累加是否会出想并发问题
     * @throws InterruptedException
     */
    @Test
    public void testIncre() throws InterruptedException {

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, 300 , 2000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200));
        for (int i = 0; i < 200; i++) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        System.out.println(HttpUtil.getResult("http://127.0.0.1:8080/prize/test"));

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        while(true) {
            Thread.sleep(100);
        }

    }


    /**
     * 抽奖初始化容器内的奖品
     */
    @Test
    public void init() {
        System.out.println(HttpUtil.getResult("http://127.0.0.1:8080/prize/init"));
    }

    /**
     * 测试多线程模拟600用户同时抽奖
     * @throws InterruptedException
     */
    @Test
    public void testLuck() throws InterruptedException {

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(600, 700 , 2000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(600));
        for (int i = 0; i < 600; i++) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String name = UUID.randomUUID().toString();
                        System.out.println(HttpUtil.getResult("http://127.0.0.1:8080/prize/do?name="+name));

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        while(true) {
            Thread.sleep(100);
        }

    }

}
