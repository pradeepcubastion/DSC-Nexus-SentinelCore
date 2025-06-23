package com.daimlertrucksasia.it.dsc.SentinelCore.services;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Service interface responsible for aggregating and evaluating the health of various system components.
 * <p>
 * Implementations typically run a series of {@link HealthCheck} instances to assess the overall system state.
 * This is useful for readiness, liveness, and custom health endpoints.
 * </p>
 */
@Component
public interface HealthCheckService {

    /**
     * Enum representing the possible outcomes of a health check.
     */
    enum Status {
        /**
         * The health check process failed to execute completely, often due to exceptions or timeouts.
         * This should be interpreted as a critical failure.
         */
        FAILED,

        /**
         * All health checks passed successfully.
         */
        HEALTHY,

        /**
         * At least one health check failed, indicating that the system is in a degraded or unavailable state.
         */
        UNHEALTHY
    }

    /**
     * Executes all registered health checks and returns the resulting system status.
     * <p>
     * This may include checks for database connectivity, cache availability, external service responses, etc.
     * </p>
     *
     * @param parameters a {@link Map} of optional request parameters (e.g., {@code quick=true}) to influence the check behavior.
     *                   These parameters may be used by individual checks to modify execution logic (e.g., fast vs. full checks).
     * @return the aggregated {@link Status} of the system based on individual component health.
     */
    Status checkHealth(Map<String, ?> parameters);
}
