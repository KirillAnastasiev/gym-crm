package com.epam.laboratory.app.rest;

import com.epam.laboratory.app.util.RequestIdHolder;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.function.Supplier;

public interface Controller {
    String REQUEST_ID_HEADER = "X-Request-ID";

    default <T> ResponseEntity<T> performRequest(String requestId,
                                                 Object[] arguments,
                                                 Supplier<T> action,
                                                 HttpStatus successStatus,
                                                 Logger log) {
        log.debug("Request {}, Request Body: {}", requestId, Arrays.toString(arguments));
        RequestIdHolder.setRequestId(requestId);
        var responseBody = action.get();
        var headers = new HttpHeaders();
        headers.add(REQUEST_ID_HEADER, requestId);
        log.debug("Response for request {} - Status Code: {}. Response body: {}", requestId, HttpStatus.OK, responseBody);
        return new ResponseEntity<>(responseBody, headers, successStatus);
    }
}
