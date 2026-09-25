package com.kirill.projects.gymcrm.cucumber.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Getter
public class ApiClientImpl implements ApiClient {

    @Value("${api.base-url}")
    private String baseUrl;

    public HttpClient spec() {
        return HttpClient.newBuilder()
                         .version(HttpClient.Version.HTTP_1_1)
                         .build();
    }

    @Override
    public HttpRequest request(String endpoint, String method, Map<String, String> headers, String body) {
        return HttpRequest.newBuilder()
                          .uri(URI.create(baseUrl + endpoint))
                          .headers(headersToArray(headers))
                          .method(method, bodyResolver(body))
                          .build();
    }

    private String[] headersToArray(Map<String, String> headers) {
        return Objects.isNull(headers)
                ? new String[0]
                : headers.entrySet()
                         .stream()
                         .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                         .toArray(String[]::new);
    }

    private HttpRequest.BodyPublisher bodyResolver(String body) {
        return Objects.nonNull(body) && !body.isEmpty()
                ? HttpRequest.BodyPublishers.ofString(body)
                : HttpRequest.BodyPublishers.noBody();
    }

}
