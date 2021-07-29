package com.dynatrace.micrometer.example;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.dynatrace.DynatraceApiVersion;
import io.micrometer.dynatrace.DynatraceConfig;
import io.micrometer.dynatrace.DynatraceMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final int stepTimeSeconds = 10;
    private static final int runTimeoutSeconds = 70;

    public static void main(String[] args) {
        DynatraceConfig config = new DynatraceConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String apiToken() {
                String token = System.getenv("DT_METRICS_INGEST_API_TOKEN");
                return token != null ? token : "";
            }

            @Override
            public String uri() {
                String endpoint = System.getenv("DT_METRICS_INGEST_URL");
                return endpoint != null ? endpoint : DynatraceConfig.super.uri();
            }

            @Override
            public DynatraceApiVersion apiVersion() {
                return DynatraceApiVersion.V2;
            }

            @Override
            public String metricKeyPrefix() {
                return "micrometer.example.standalone";
            }

            @Override
            public Map<String, String> defaultDimensions() {
                Map<String, String> dims = new HashMap<>();
                dims.put("dimension-key-1", "value1");
                dims.put("dimension-key-2", "value2");

                return dims;
            }

            @Override
            public boolean enrichWithDynatraceMetadata() {
                return true;
            }

            @Override
            public Duration step() {
                // Set the export interval to 10 seconds
                return Duration.ofSeconds(stepTimeSeconds);
            }
        };

        DynatraceMeterRegistry registry = new DynatraceMeterRegistry(config, Clock.SYSTEM);
        Metrics.addRegistry(registry);

        Counter counter = registry.counter("counter");
        int i = 0;
        // run for 60 seconds, then exit.
        while (i < runTimeoutSeconds) {
            counter.increment(5.23);
            registry.gauge("gauge", 1.23);

            try {
                Thread.sleep(3 * 1000);
                i += 3;
            } catch (InterruptedException ignored) {
            }
        }

        logger.debug("shutting down...");
    }
}
