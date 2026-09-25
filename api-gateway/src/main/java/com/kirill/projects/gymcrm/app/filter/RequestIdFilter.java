package com.kirill.projects.gymcrm.app.filter;

import com.kirill.projects.gymcrm.app.util.RestLoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class RequestIdFilter implements GlobalFilter, Ordered {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        var mutableExchange = exchange.mutate()
                                      .request(request -> request.header(REQUEST_ID_HEADER, requestId))
                                      .build();
        RestLoggingUtil.logRestRequest(mutableExchange);
        return chain.filter(mutableExchange)
                .then(Mono.fromRunnable(() -> RestLoggingUtil.logRestResponse(exchange)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
