package com.ordersphere.gatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTraceFilter.class);

    FilterUtility filterUtility;

    public RequestTraceFilter(FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        if(isCorrelationIdPresent(requestHeaders)){
            LOGGER.debug("order-sphere-correlation-id found in request trace filter: " + filterUtility.getCorrelationId(
                    requestHeaders
            ));
        }else {
            String correlationId = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange,correlationId);
            LOGGER.debug("\"order-sphere-correlation-id found in request trace filter: " + correlationId);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders httpHeaders){
        if(filterUtility.getCorrelationId(httpHeaders) != null){
            return true;
        }else{
            return false;
        }
    }

    private String generateCorrelationId(){
        return UUID.randomUUID().toString();
    }
}
