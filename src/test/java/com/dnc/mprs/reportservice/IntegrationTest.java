package com.dnc.mprs.reportservice;

import com.dnc.mprs.reportservice.config.AsyncSyncConfiguration;
import com.dnc.mprs.reportservice.config.EmbeddedElasticsearch;
import com.dnc.mprs.reportservice.config.EmbeddedKafka;
import com.dnc.mprs.reportservice.config.EmbeddedRedis;
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
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
