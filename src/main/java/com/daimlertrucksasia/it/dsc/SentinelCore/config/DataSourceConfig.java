package com.daimlertrucksasia.it.dsc.SentinelCore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the application's database connection pool using HikariCP.
 * <p>
 * This class configures a {@link HikariDataSource} bean using properties provided through
 * the Spring Boot application configuration (e.g., `application.yml` or `application.properties`).
 * </p>
 * <p>
 * The configured data source can be used by Spring's JDBC, JPA, or other database integrations.
 * </p>
 */
@Configuration
public class DataSourceConfig {

    /**
     * Creates and configures a {@link HikariDataSource} bean.
     * <p>
     * HikariCP is a fast and reliable JDBC connection pool implementation recommended by Spring Boot.
     * This bean will be used by Spring for database connectivity.
     * </p>
     *
     * @param url             the JDBC URL to connect to the database
     * @param username        the database username
     * @param password        the database password
     * @param driverClassName the fully qualified class name of the JDBC driver
     * @return a configured {@link HikariDataSource} instance ready for use
     */
    @Bean
    public HikariDataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverClassName
    ) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        return new HikariDataSource(config);
    }
}
