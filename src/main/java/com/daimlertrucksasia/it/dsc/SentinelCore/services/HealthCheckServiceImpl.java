package com.daimlertrucksasia.it.dsc.SentinelCore.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link HealthCheckService} interface.
 * <p>
 * This service aggregates and evaluates the health of registered {@link HealthCheck} components.
 * It executes each check conditionally, collects their results, and derives an overall system health status.
 * </p>
 *
 * <p>Checks can be selectively skipped using {@link HealthCheck#isPerformCheck(Map)} based on prior results.</p>
 *
 * <p>Logs are emitted for skipped, failed, and exception-throwing health checks for observability.</p>
 */
@Slf4j
@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    /**
     * List of all registered health checks to be evaluated.
     */
    private final List<HealthCheck> checks;

    /**
     * Constructs a {@code HealthCheckServiceImpl} with the specified list of health checks.
     *
     * @param checks the health check implementations to be evaluated
     */
    public HealthCheckServiceImpl(List<HealthCheck> checks) {
        this.checks = checks;
    }

    /**
     * Executes the system health checks using the provided parameters.
     *
     * <p>Each {@link HealthCheck} is invoked conditionally based on {@link HealthCheck#isPerformCheck(Map)}.
     * If any check returns {@code false}, the system is marked {@link Status#UNHEALTHY}.
     * If any exception occurs during evaluation, the system is marked {@link Status#FAILED}.</p>
     *
     * @param parameters a map of request parameters that may influence health check behavior (e.g. quick=true)
     * @return the aggregated {@link Status} representing system health
     */
    @Override
    public Status checkHealth(Map<String, ?> parameters) {
        Status[] ret = {Status.HEALTHY};

        // Holds the outcome of each individual health check
        Map<HealthCheck, Boolean> statuses = new IdentityHashMap<>();

        // Prevents checks from modifying each other's results
        Map<HealthCheck, Boolean> readOnly = Collections.unmodifiableMap(statuses);

        checks.stream()
                .filter(check -> {
                    boolean shouldRun = check.isPerformCheck(readOnly);
                    if (!shouldRun) {
                        log.info("Skipping health check: {}", check.getClass().getSimpleName());
                    }
                    return shouldRun;
                })
                .forEach(check -> {
                    try {
                        boolean healthy = check.isHealthy(parameters);
                        statuses.put(check, healthy);
                        if (!healthy) {
                            log.warn("Health check FAILED: {}", check.getClass().getSimpleName());
                            ret[0] = Status.UNHEALTHY;
                        }
                    } catch (Exception ex) {
                        log.error("Exception during health check: {}", check.getClass().getSimpleName(), ex);
                        statuses.put(check, false);
                        ret[0] = Status.FAILED;
                    }
                });

        return ret[0];
    }
}
