package com.mobility.demo.config;

import com.impossibl.postgres.jdbc.PGDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource({ "classpath:notification-ds.properties" })
public class NotificationConfig {

    private final Environment env;

    @Autowired
    public NotificationConfig(Environment env) {
        this.env = env;
    }

    @Bean(name = "notificationDS")
    public PGDataSource dataSource() {

        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost("localhost");
        dataSource.setPort(5432);
        dataSource.setDatabase("postgres");
        dataSource.setUser(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));

        return dataSource;
    }
}
