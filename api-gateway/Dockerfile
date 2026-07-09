# Stage 1: build 'api-gateway' module
FROM cleanstart/maven:latest AS build
WORKDIR /build
COPY api-gateway/pom.xml .
COPY api-gateway/src ./src
RUN mvn clean package -DskipTests

# Stage 2: run the application
FROM sourcemation/jdk-21:latest
ENV PROFILE=docker
WORKDIR /app
COPY --from=build /build/target/api-gateway*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]
