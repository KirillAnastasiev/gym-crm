package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.util.RequestIdHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public interface Controller {
    String REQUEST_ID_HEADER = "X-Request-ID";

    default <T> ResponseEntity<T> performRequest(String requestId, Supplier<T> action, HttpStatus successStatus) {
        RequestIdHolder.setRequestId(requestId);
        var responseBody = action.get();
        var headers = new HttpHeaders();
        headers.add(REQUEST_ID_HEADER, requestId);
        return new ResponseEntity<>(responseBody, headers, successStatus);
    }
}
