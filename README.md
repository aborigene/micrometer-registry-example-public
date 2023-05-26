# Micrometer Registry Examples

This repository contains examples for setting up Micrometer as a [standalone metrics collector](#micrometer-standalone-deployment) or [as part of a Spring Boot app](#micrometer-in-spring-boot).

## Micrometer in Spring Boot

### On your machine

```shell
cd micrometer-spring-boot

# see above for a different way to use environment variables.
DT_METRICS_INGEST_URL=https://your-env.dev.dynatracelabs.com/api/v2/metrics/ingest \
DT_METRICS_INGEST_API_TOKEN=your.metrics.ingest.token \
./gradlew bootRun
```

### In a Docker container

#### This step requires operator in CloudNativeFullStack running on K8S

```shell
cd micrometer-spring-boot

# build the docker image
docker build -t micrometer-spring-boot .

# run the docker container. You can check if the app is up on localhost:8080
# Update the tenant and token to your token and tenant in order to see the metrics in Dynatrace.
docker run \
    --env DT_METRICS_INGEST_URL=https://your-environment.dev.dynatracelabs.com/api/v2/metrics/ingest \
    --env DT_METRICS_INGEST_API_TOKEN=your.metrics.ingest.token \
    -p 8080:8080 \
    micrometer-spring-boot
```

### In a K8S

```shell
cd micrometer-spring-boot

# build the docker image
docker build -t "<your_repo>/micrometer-spring-boot:<some_version>" .

# push to your repo
docker push "<your_repo>/micrometer-spring-boot:<some_version>"

# edit the file deploy.yaml to point to your image from the previous steps
# deply the file
kubectl create ns micrometer-example
kubectl apply -f deploy.yaml -n micrometer-example

# you can then get the logs from the created pod and you will see that the endpoint is configured automatically by the Dynatrace Operator
```

## Micrometer Standalone Deployment

### On your machine

```shell
cd micrometer-standalone

# this way, the environment variables are not added to the global environment, but only passed to the gradle command
DT_METRICS_INGEST_URL=https://your-env.dev.dynatracelabs.com/api/v2/metrics/ingest \
DT_METRICS_INGEST_API_TOKEN=your.metrics.ingest.token \
./gradlew run
```

Alternatively, you can add the environment variables to the current shell and export them, then run gradle:

```shell
export DT_METRICS_INGEST_URL=https://your-env.dev.dynatracelabs.com/api/v2/metrics/ingest
export DT_METRICS_INGEST_API_TOKEN=your.metrics.ingest.token

# then you can run gradle and it will be able to read the environment variables automatically:
./gradlew run
```

### In a Docker container

```shell
cd micrometer-standalone

# build the docker image
docker build -t micrometer-standalone .

# and run it. Update the tenant and token to your token and tenant in order to see the metrics in Dynatrace.
docker run \
    --env DT_METRICS_INGEST_URL=https://your-environment.dev.dynatracelabs.com/api/v2/metrics/ingest \
    --env DT_METRICS_INGEST_API_TOKEN=your.metrics.ingest.token \
    micrometer-standalone
```