### OAuth2 auto-configuration changes at Spring Boot 2.1

The use of the following dependency causes a problem with Spring Boot 2.1 and above:

```groovy
compile "org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:$springSecurityOAuthAutoconfigVersion"
```

The following error is seen at startup:

```text
The bean 'oauth2ClientContext', defined in class path resource
[org/springframework/boot/autoconfigure/security/oauth2/client/OAuth2RestOperationsConfiguration$SingletonScopedConfiguration.class],
could not be registered. A bean with that name has already been defined in BeanDefinition defined in
org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ClientConfiguration$OAuth2ClientContextConfiguration
and overriding is disabled.

Action:

Consider renaming one of the beans or enabling overriding by setting
spring.main.allow-bean-definition-overriding=true
```

A solution, as the message suggests, is to set the property in all
main and test `application.properties` files, which is what has been done.
However, this is not ideal and the conflict arises from the use of the
autoconfigure library which is now in maintenance mode.

In theory, it should be possible to do everything with the Spring Security
features now integrated into Spring Boot, but I have not been able to
get that to work.
In particular, the inclusion of Eureka causes messages about
`eureka.client.oauth2.clientid` not having been set.

Note also that the `io.pivotal.spring.cloud:spring-cloud-sso-connector`
dependency in the labs is supposedly not supported under Spring Boot 2.1+
and has been replaced by the `io.pivotal.cfenv:java-cfenv-boot-pivotal-sso`
library.
However ... it still seems to work, and introducing the suggested
dependencies causes a number of other issues (requiring the removal of
Spring Cloud Connector classes, which cause compilation failures, for
example).

It looks like there is an amount of work to be done to figure out a
minimum set of Spring Boot 2.1+ dependencies to support all of the
included services!
