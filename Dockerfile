#FROM alpine/java:21-jre
FROM bellsoft/liberica-openjre-alpine-musl
COPY ./target/application-boot.jar /app/application-boot.jar

ENTRYPOINT ["java"]
CMD ["-XX:+UseContainerSupport", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:MaxRAMPercentage=80.0", \
    "-jar", "/app/application-boot.jar"]
