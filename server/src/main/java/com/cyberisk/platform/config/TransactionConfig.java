package com.cyberisk.platform.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;

@Configuration
@PropertySource({"classpath:transaction-ds.properties"})
@EnableJpaRepositories(
        basePackages = "com.mobility.demo.model",
        entityManagerFactoryRef = "entityManager",
        transactionManagerRef = "transactionManager"
)
public class TransactionConfig {

    private final String SCHEMA = "classpath:schema.sql";
    private final String FUNCTION = "classpath:function_notify_event.sql";

    private final Environment env;
    private final ApplicationContext context;

    public TransactionConfig(Environment env, ApplicationContext context) {
        this.env = env;
        this.context = context;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.mobility.demo.model");
        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        properties.put("hibernate.temp.use_jdbc_metadata_defaults",
                env.getProperty("hibernate.temp.use_jdbc_metadata_defaults"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "transactionDS")
    public DataSource dataSource() {

        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));
        Resource schema = context.getResource(SCHEMA);
        Resource function = context.getResource(FUNCTION);
        try {
            ScriptUtils.executeSqlScript(dataSource.getConnection(), schema);
            ScriptUtils.executeSqlScript(dataSource.getConnection(), function);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManager().getObject());
        return transactionManager;
    }


}
