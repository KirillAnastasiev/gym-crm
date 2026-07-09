# Stage 1: build 'registry-server' module
FROM cleanstart/maven:latest AS build
WORKDIR /build
COPY registry-server/pom.xml .
COPY registry-server/src ./src
RUN mvn clean package -DskipTests

# Stage 2: run the application
FROM sourcemation/jdk-21:latest
ENV PROFILE=docker
WORKDIR /app
COPY --from=build /build/target/registry-server*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]

