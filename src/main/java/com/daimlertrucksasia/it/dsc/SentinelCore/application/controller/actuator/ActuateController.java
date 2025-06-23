package com.daimlertrucksasia.it.dsc.SentinelCore.application.controller.actuator;

import com.daimlertrucksasia.it.dsc.SentinelCore.services.DatabaseHealthCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Actuator health indicator for checking the status of the database connection.
 *
 * <p>This component integrates with Spring Boot Actuator and allows the database health status
 * to be exposed via the {@code /actuator/health} endpoint.
 *
 * <p>It delegates the health check logic to {@link DatabaseHealthCheck}.
 */
@Component
@RequiredArgsConstructor
public class ActuateController implements HealthIndicator {

    /**
     * Service that performs the actual database health check.
     */
    private final DatabaseHealthCheck dbHealthCheck;

    /**
     * Provides detailed health status of the database.
     *
     * @param includeDetails whether to include additional health details (unused in this implementation)
     * @return {@link Health#up()} if the database is reachable and healthy, otherwise {@link Health#down()}.
     */
    @Override
    public Health getHealth(boolean includeDetails) {
        if (dbHealthCheck.isHealthy(false)) {
            return Health.up().withDetail("Database", "Reachable").build();
        }
        return Health.down().withDetail("Database", "Unreachable").build();
    }

    /**
     * Default health status used by Spring Boot Actuator.
     *
     * @return the health of the database.
     */
    @Override
    public Health health() {
        return getHealth(true); // Delegate to getHealth() with details
    }
}
