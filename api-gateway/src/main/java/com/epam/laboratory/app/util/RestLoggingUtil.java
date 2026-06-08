package com.epam.laboratory.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class RestLoggingUtil {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    public static void logRestRequest(ServerWebExchange  exchange) {
        var request = exchange.getRequest();
        var httpEndpoint = request.getURI();
        var httpMethod = request.getMethod();
        var requestBody = request.getBody();
        var requestId = Optional.ofNullable(request.getHeaders().get(REQUEST_ID_HEADER))
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse("N/A");
        log.trace("REST Call {} - Endpoint: {}, HTTP Method: {}, RequestBody: {}",
                requestId, httpEndpoint, httpMethod, requestBody);
    }

    public static void logRestResponse(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        var statusCode = response.getStatusCode();
        var requestId = Optional.ofNullable(exchange.getRequest().getHeaders().get(REQUEST_ID_HEADER))
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse("N/A");
        log.trace("REST Call {} Response - Status Code: {}", requestId, statusCode);
    }
}
