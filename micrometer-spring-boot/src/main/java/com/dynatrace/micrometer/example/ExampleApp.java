package com.dynatrace.micrometer.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = ExampleController.class)
@EnableScheduling
public class ExampleApp {
    public static void main(String[] args) {
        // The profiles step will read the src/main/resources/dynatrace.yml and configure the app
        new SpringApplicationBuilder(ExampleApp.class).profiles("dynatrace").run(args);
    }
}
