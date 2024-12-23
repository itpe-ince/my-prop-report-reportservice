package com.dnc.mprs.reportservice;

import com.dnc.mprs.reportservice.config.AsyncSyncConfiguration;
import com.dnc.mprs.reportservice.config.EmbeddedElasticsearch;
import com.dnc.mprs.reportservice.config.EmbeddedKafka;
import com.dnc.mprs.reportservice.config.EmbeddedSQL;
import com.dnc.mprs.reportservice.config.JacksonConfiguration;
import com.dnc.mprs.reportservice.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { ReportserviceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
    // 5s is Spring's default https://github.com/spring-projects/spring-framework/blob/main/spring-test/src/main/java/org/springframework/test/web/reactive/server/DefaultWebTestClient.java#L106
    String DEFAULT_TIMEOUT = "PT5S";

    String DEFAULT_ENTITY_TIMEOUT = "PT5S";
}
