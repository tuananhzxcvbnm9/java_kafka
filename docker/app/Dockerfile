FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=oms-bootstrap/target/oms-bootstrap-0.1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
