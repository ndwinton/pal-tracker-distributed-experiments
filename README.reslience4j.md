## Circuit breaker with Resilience4j

This is a basic reimplementation of the PAL circuit breaker lab using
[Resilience4j](https://resilience4j.readme.io) instead of Hystrix.
In this case metrics will be exposed via actuator and, additionally,
will be configured to generate data in a form that can be pulled
into [Prometheus](https://prometheus.io).

The steps are very similar to those for using Hystrix, namely:
* Add a dependency for each component (allocations, backlog, timesheets):
  * `io.github.resilience4j:resilience4j-spring-boot2`
* Add dependencies for the applications `server.gradle`:
  * `org.springframework.boot:spring-boot-starter-actuator`
  * `org.springframework.boot:spring-boot-starter-aop`
  * `io.micrometer:micrometer-registry-prometheus` - ensuring that this
  is a version compatible with the Micrometer core libraries brought in
  by the Actuator starter. This is required only if you want to
  expose data in Prometheus format.
* Apply a `@CircuitBreaker` annotation.
* Expose the management endpoints (done here by setting
`management.endpoints.web.exposure.include=*` in `application.properties`)

It is also possible (supposedly) to hook the data into PCF Metrics.
See [the documentation](https://docs.pivotal.io/pivotalcf/2-6/metric-registrar/using.html)
for more details.

### To Do

Resilience4j is highly tunable and also supports retry, bulkhead and
rate-limiting patterns. It would be good to add these features.

