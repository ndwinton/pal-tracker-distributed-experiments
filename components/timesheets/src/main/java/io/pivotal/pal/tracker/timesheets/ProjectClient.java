package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    public static final String CACHE_NAME = "timesheets-project";
    private final RestOperations restOperations;
    private final String endpoint;
    private final CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint, CacheManager cacheManager) {
        this.restOperations= restOperations;
        this.endpoint = registrationServerEndpoint;
        this.cacheManager = cacheManager;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    @CachePut(cacheNames = CACHE_NAME)
    public ProjectInfo getProject(long projectId) {
        logger.info("Retrieving info for project {}", projectId);
        return restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Falling back to cached data for project {}", projectId);
        return cacheManager.getCache(CACHE_NAME).get(projectId, ProjectInfo.class);
    }
}