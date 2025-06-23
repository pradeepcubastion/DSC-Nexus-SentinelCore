package com.daimlertrucksasia.it.dsc.SentinelCore.services;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Contract for health check components used in the system.
 * <p>
 * Implementations of this interface define specific logic to determine the health status
 * of various subsystems such as databases, caches, APIs, or internal services.
 * </p>
 * <p>
 * These health checks can be composed by a central {@link HealthCheckService} to evaluate
 * overall system readiness or liveness.
 * </p>
 */
@Component
public interface HealthCheck {

    /**
     * Checks whether this component is healthy based on the given input parameters.
     *
     * @param parameters a {@link Map} of input flags and options, typically provided via HTTP request query parameters.
     *                   The map may include {@link #PARAMETER_QUICK} to signal a fast check.
     * @return {@code true} if the component is healthy, {@code false} if unhealthy.
     */
    boolean isHealthy(Map<String, ?> parameters);

    /**
     * Name of the request parameter used to indicate a "quick" health check.
     * <p>
     * This is typically used to avoid long-running validations during regular uptime pings.
     * </p>
     */
    String PARAMETER_QUICK = "quick";

    /**
     * Utility method to extract the {@link #PARAMETER_QUICK} flag from a parameter map.
     * <p>
     * If the value is an {@link Iterable}, the method attempts to use the first element.
     * </p>
     *
     * @param parameters a {@link Map} of parameters, often extracted from request query strings
     * @return {@code true} if a quick check was requested, {@code false} otherwise
     */
    static boolean isQuick(Map<String, ?> parameters) {
        Object quick = parameters.get(PARAMETER_QUICK);
        if (quick == null) return false;

        if (quick instanceof Iterable<?> iterable) {
            quick = iterable.iterator().hasNext() ? iterable.iterator().next() : null;
        }

        return Boolean.parseBoolean(String.valueOf(quick));
    }

    /**
     * Determines whether this health check should be executed, based on the outcome of prior checks.
     * <p>
     * This allows certain health checks to be skipped if dependent systems are already unhealthy.
     * By default, all checks are always performed.
     * </p>
     *
     * @param priorStatuses an unmodifiable {@link Map} containing the health status of previously evaluated checks.
     * @return {@code true} to run the check, {@code false} to skip.
     */
    default boolean isPerformCheck(Map<HealthCheck, Boolean> priorStatuses) {
        return true;
    }
}
