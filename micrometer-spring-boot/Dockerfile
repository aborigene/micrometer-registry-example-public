# syntax=docker/dockerfile:1
FROM gradle:7.1.1-jdk11-hotspot
COPY . $HOME
RUN gradle build --no-daemon
CMD ["gradle", "bootRun", "--no-daemon"]
