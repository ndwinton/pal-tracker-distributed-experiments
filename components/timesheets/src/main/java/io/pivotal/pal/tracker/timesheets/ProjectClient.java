package io.pivotal.pal.tracker.timesheets;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private Map<Long,ProjectInfo> cache = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @CircuitBreaker(name = "timesheets", fallbackMethod = "getProjectFromCache")
    @RateLimiter(name = "timesheets", fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        logger.info("Getting project info for " + projectId);
        ProjectInfo info = restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        cache.put(projectId, info);
        return info;
    }

    ProjectInfo getProjectFromCache(long projectId, Throwable throwable) throws Throwable {
        logger.warn("Using cached result for " + projectId + " because: " + throwable.toString());
        if (cache.containsKey(projectId)) {
            return cache.get(projectId);
        } else {
            throw throwable;
        }
    }
}
