# Stage 1: build 'logging' and 'statistics-app' modules
FROM cleanstart/maven:latest AS build
WORKDIR /build

RUN mkdir -p /build/logging
COPY logging/pom.xml /build/logging/
COPY logging/src /build/logging/src

RUN mkdir -p /build/statistics-app
COPY statistics-app/pom.xml /build/statistics-app/
COPY statistics-app/src /build/statistics-app/src

RUN cd /build/logging && mvn clean install -e -DskipTests && \
    cd /build/statistics-app && mvn clean package -e -DskipTests

# Stage 2: run the application
FROM sourcemation/jdk-21:latest
ENV PROFILE=docker
WORKDIR /app
COPY --from=build /build/statistics-app/target/statistics-app*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${PROFILE}"]

