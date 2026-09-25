package com.kirill.projects.gymcrm.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestIdHolder {
    private static final ThreadLocal<String> REQUEST_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void setRequestId(String requestId) {
        REQUEST_ID_THREAD_LOCAL.set(requestId);
    }

    public static String getRequestId() {
        return REQUEST_ID_THREAD_LOCAL.get();
    }

    public static void clearRequestId() {
        REQUEST_ID_THREAD_LOCAL.remove();
    }
}
