package com.paxing.test.kaoqin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author wtzhang
 * @date 2019-02-18
 */
@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
public class KaoqinApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaoqinApplication.class, args);
    }

}
