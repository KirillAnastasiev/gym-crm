# Stage 1: build 'logging' and 'main-app' modules
FROM cleanstart/maven:latest AS build
WORKDIR /build

RUN mkdir -p /build/logging
COPY logging/pom.xml /build/logging/
COPY logging/src /build/logging/src

RUN mkdir -p /build/main-app
COPY main-app/pom.xml /build/main-app/
COPY main-app/src /build/main-app/src

RUN cd /build/logging && mvn clean install -e -DskipTests && \
    cd /build/main-app && mvn clean package -e -DskipTests

# Stage 2: run the application
FROM sourcemation/jdk-21:latest
ENV PROFILE=docker
WORKDIR /app
COPY --from=build /build/main-app/target/main-app-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]
