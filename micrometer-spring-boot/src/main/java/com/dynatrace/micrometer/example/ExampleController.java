package com.dynatrace.micrometer.example;

import io.micrometer.core.instrument.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ExampleController {
    private final MeterRegistry registry;
    private final Tags tags = Tags.of("controller_class", ExampleController.class.getName());

    private final Map<String, Meter> meters = new HashMap<>();
    // this can be changed, and in turn changes the gauge value
    private final GaugeFunction gaugeFunction = new GaugeFunction(5.234);

    public ExampleController(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/api/timer")
    public String timer(@RequestBody String name,
                        @RequestHeader(name = "Nonce", defaultValue = "NO_NONCE") String nonce) {
        String timerName = String.format("timer_%s", name);
        if (!meters.containsKey(timerName)) {
            Tags concatenated = Tags.concat(tags, "Nonce", nonce);
            Timer timer = registry.timer(timerName, concatenated);
            // put the timer in the map, so a strong reference is kept and the object is not garbage collected.
            meters.put(timerName, timer);
        }

        Timer timer = registry.find(timerName).timer();
        if (timer == null) {
            return String.format("failed to create timer with name %s.", timerName);
        }

        int timerValue = 234;
        timer.record(Duration.ofMillis(timerValue));

        return String.format("recorded a timer value with name %s (%dms).", timerName, timerValue);
    }

    private static class GaugeFunction {
        private double value;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public GaugeFunction(double value) {
            this.value = value;
        }
    }

    private static double getGaugeFunctionValue(GaugeFunction gaugeFunction) {
        return gaugeFunction.getValue();
    }

    @PostMapping("/api/gauge")
    public String gauge(@RequestBody String name,
                        @RequestHeader(name = "Nonce", defaultValue = "NO_NONCE") String nonce) {
        String gaugeName = String.format("gauge_%s", name);
        if (!meters.containsKey(gaugeName)) {
            Tags concatenated = Tags.concat(tags, "Nonce", nonce);
            registry.gauge(gaugeName, concatenated, gaugeFunction, ExampleController::getGaugeFunctionValue);
            Gauge gauge = registry.find(gaugeName).gauge();
            meters.put(gaugeName, gauge);
        }

        return String.format("the gauge '%s' currently shows a value of %.3f", gaugeName, gaugeFunction.getValue());
    }

    @RequestMapping("/**")
    public String index(final HttpServletRequest request) {
        return "<h1>Spring Boot and Micrometer test app is up and running.</h1>" +
                "<p>Try the /api/timer and /api/gauge POST endpoints. " +
                "Set the name of the instrument as request body.</p>";
    }
}
