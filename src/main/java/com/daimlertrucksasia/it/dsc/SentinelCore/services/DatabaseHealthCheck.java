package com.daimlertrucksasia.it.dsc.SentinelCore.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Service responsible for checking the health status of the application's database.
 * <p>
 * This component is intended to be used by health endpoints or monitoring tools to determine
 * if the database is reachable and functioning correctly.
 * </p>
 * <p>
 * The health check attempts to acquire a connection from the configured {@link DataSource}
 * and verifies its validity within a timeout window.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseHealthCheck {

    /**
     * The configured {@link DataSource} used to obtain connections for health checks.
     * Typically provided by Spring Boot through a connection pool like HikariCP.
     */
    private final DataSource dataSource;

    /**
     * Performs a health check by attempting to get a valid connection from the database.
     *
     * @param quick if {@code true}, attempt to return as quickly as possible (e.g., use low timeout);
     *              for now, this flag is present for compatibility but not actively alters behavior.
     * @return {@code true} if the connection is valid and reachable within the timeout; {@code false} otherwise.
     */
    public boolean isHealthy(boolean quick) {
        try (Connection conn = dataSource.getConnection()) {
            // Only wait up to 1 second to determine if the connection is valid
            return conn.isValid(1);
        } catch (Exception e) {
            log.error("DB health check failed", e);
            return false;
        }
    }
}
