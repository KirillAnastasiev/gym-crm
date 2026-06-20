package com.epam.laboratory.cucumber.client;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;

public interface ApiClient {
    String HTTP_METHOD_GET = "GET";
    String HTTP_METHOD_POST = "POST";
    String HTTP_METHOD_PUT = "PUT";
    String HTTP_METHOD_PATCH = "PATCH";
    String HTTP_METHOD_DELETE = "DELETE";
    String HTTP_METHOD_HEAD = "HEAD";
    String HTTP_METHOD_OPTIONS = "OPTIONS";

    String AUTHORIZATION_HEADER = "Authorization";
    String CONTENT_TYPE_HEADER = "Content-Type";

    HttpClient spec();
    HttpRequest request(String endpoint, String method, Map<String, String> headers, String body);
}
