package com.paxing.test.kaoqin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 引用xml注入方式的配置
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/1
 */
@Configuration
@ImportResource(locations = "classpath:spring/spring.xml")
public class LoadXmlConfig {
}
