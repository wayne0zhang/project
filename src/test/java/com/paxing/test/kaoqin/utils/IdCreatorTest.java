package com.paxing.test.kaoqin.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/28
 */
public class IdCreatorTest {

    @Test
    public void testId() {
        long ss = 0L;
        for (int j = 0; j < 10; j++) {
            List<Callable<Long>> partitions = new ArrayList<>();
            IdCreator idCreator = IdCreator.get();

            for (int i = 0; i < 1000000; i++) {
                partitions.add(new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return idCreator.nextId();
                    }
                });
            }

            ExecutorService executorService;
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            try {
                long s = System.currentTimeMillis();
                executorService.invokeAll(partitions, 10000L, TimeUnit.SECONDS);
                long l = System.currentTimeMillis() - s;
                ss += l;
                System.out.println("耗时：" + l / 1000.000);
                executorService.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("avg耗时：" + ss / 1000/10.0);
    }
}