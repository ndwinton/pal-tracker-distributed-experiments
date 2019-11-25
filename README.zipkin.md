### Zipkin tracing

This demonstrates the use of Spring Cloud Sleuth along
with [OpenZipkin](https://zipkin.io/) to obtain and display
end-to-end performance data.

Run Zipkin with Docker:

```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

or use one of the other options available via the
[Zipkin Quickstart](https://zipkin.io/pages/quickstart.html)
page.
You could also create your own server and add it to your
platform services as described in
[this article](https://spring.io/blog/2016/02/15/distributed-tracing-with-spring-cloud-sleuth-and-spring-cloud-zipkin).

Start all of the applications:

```bash
./gradlew bootRun --parallel
```

Generate a mix of GET/POST traffic:

```bash
siege -c 5 -r 200 --content-type='application/json' -f siege-urls.txt
```

Navigate to the Zipkin console at http://localhost:9411.

#### application.properties

Note that tracing is controlled with a number of properties.
The most important is `spring.zipking.baseUrl` which specifies
the location of the Zipkin server end-point.

```properties
# Uncomment to see trace/span IDs in the logs
# logging.level.org.springframework.web.servlet.DispatcherServlet=DEBUG
spring.zipkin.baseUrl=http://localhost:9411
```