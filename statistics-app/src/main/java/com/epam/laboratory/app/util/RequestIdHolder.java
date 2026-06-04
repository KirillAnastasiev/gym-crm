package com.epam.laboratory.app.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RequestIdHolder {
    private static final ThreadLocal<String> REQUEST_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static String getRequestId() {
        return REQUEST_ID_THREAD_LOCAL.get();
    }

    public static void setRequestId(String requestId) {
        REQUEST_ID_THREAD_LOCAL.set(requestId);
    }

    public static void clearRequestId() {
        REQUEST_ID_THREAD_LOCAL.remove();
    }

}
