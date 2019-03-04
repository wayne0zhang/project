package com.paxing.test.kaoqin.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * datasource 配置
 * <p>
 * 多数据源可通过名称来区分
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/1
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    /**
     * 连接信息
     *
     * @return
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.common")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 配置数据源
     *
     * @param dataSourceProperties
     * @return
     */
    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * 配置事务管理器
     *
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 配置mybatis sessionFactory
     * <p>
     * 在这里配置 xml 文件位置
     *
     * @param dataSource
     * @return
     */
    @Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactoryBean sessionFactoryBean(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setConfigLocation(resolver.getResource("classpath:global/mybatis-config.xml"));

        List<Resource> mapperLocations = new ArrayList<>();
        mapperLocations.addAll(Arrays.asList(resolver.getResources("classpath*:mybatis/*.xml")));
        factoryBean.setMapperLocations(mapperLocations.toArray(new Resource[mapperLocations.size()]));

        return factoryBean;
    }

    /**
     * 配置mybatis sqlSessionTemplate
     *
     * @param sqlSessionFactoryBean
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionTemplate sessionTemplate(@Qualifier("sqlSessionFactoryBean") SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryBean.getObject());
    }

    /**
     * 配置mybatis dao 文件位置
     *
     * @return
     */
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.paxing.test.kaoqin.dao");
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactoryName");
        return configurer;

    }

    @Profile({"development", "test", "off"})
    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource HikariDataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(HikariDataSource);
        jdbcTemplate.query(HikariDataSource.getConnectionTestQuery(), rs -> {
            int id = rs.getInt(1);
            log.debug("非正式环境验证db连接可用性：{}", id == 1);
        });
        return jdbcTemplate;
    }

}
