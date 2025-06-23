package com.daimlertrucksasia.it.dsc.SentinelCore.application.controller;

import com.daimlertrucksasia.it.dsc.SentinelCore.services.DatabaseHealthCheck;
import com.daimlertrucksasia.it.dsc.SentinelCore.services.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller responsible for exposing custom health check endpoints for the application.
 * <p>
 * This controller supports checking:
 * <ul>
 *     <li>Basic liveness of the application</li>
 *     <li>Database connectivity</li>
 *     <li>Overall system health based on registered {@link HealthCheckService} checks</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthCheckController {

    /**
     * Service used to check the health of the database connection.
     */
    private final DatabaseHealthCheck dbHealthCheck;

    /**
     * Service responsible for aggregating and executing multiple {@link com.daimlertrucksasia.it.dsc.SentinelCore.services.HealthCheck}
     * implementations across various subsystems like database, cache, external APIs, etc.
     */
    private final HealthCheckService svc;

    /**
     * Basic system liveness endpoint.
     * <p>
     * Used by uptime monitors or load balancers to confirm the application is responsive.
     * This endpoint does not perform any deeper health checks.
     *
     * @return HTTP 200 OK to indicate the application is alive.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.status(200).build();
    }

    /**
     * Endpoint to verify the health of the database connection.
     * <p>
     * The `quick` parameter can be used to request a lightweight/fast check.
     *
     * @param quick if {@code true}, performs a minimal health check (e.g. skip long waits).
     * @return HTTP 200 OK if database is reachable, 503 Service Unavailable if unreachable.
     */
    @GetMapping("/db")
    public ResponseEntity<String> checkDbHealth(@RequestParam(defaultValue = "false") boolean quick) {
        return dbHealthCheck.isHealthy(quick)
                ? ResponseEntity.ok("Database is healthy")
                : ResponseEntity.status(503).body("Database is unhealthy");
    }

    /**
     * Comprehensive system health status endpoint.
     * <p>
     * Executes all registered {@link com.daimlertrucksasia.it.dsc.SentinelCore.services.HealthCheck} checks
     * and determines an overall status.
     *
     * @param queryParams key-value parameters to pass to each health check implementation.
     * @return <ul>
     *     <li>200 OK if all checks return healthy</li>
     *     <li>503 Service Unavailable if any check returns unhealthy</li>
     *     <li>500 Internal Server Error for unexpected failures</li>
     * </ul>
     */
    @GetMapping("/status")
    public ResponseEntity<Void> check(@RequestParam Map<String, String> queryParams) {
        HealthCheckService.Status status = svc.checkHealth(queryParams);
        status = status != null ? status : HealthCheckService.Status.FAILED;

        return ResponseEntity.status(switch (status) {
            case HEALTHY -> 200;
            case UNHEALTHY -> 503;
            default -> 500;
        }).build();
    }
}
