package com.paxing.test.kaoqin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * jackson 配置
 * <p>
 * 主要用来解决服务启动时，JacksonHttpMessageConvertersConfiguration异常，objectMapper 参数无法注入的问题
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @see org.springframework.boot.autoconfigure.web.JacksonHttpMessageConvertersConfiguration.MappingJackson2HttpMessageConverterConfiguration#mappingJackson2HttpMessageConverter
 * @since 2019/3/1
 */
@Configuration
public class JacksonConfig {

    ObjectMapper objectMapper;

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        objectMapper = builder.createXmlMapper(false).build();
        return objectMapper;
    }

    @Bean
    @Primary
    @ConditionalOnBean(ObjectMapper.class)
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
