package com.example.vertxbug;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class BarHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        ctx.vertx().setTimer(10, id -> {
            ctx.response().setStatusCode(200);
            ctx.response().write("{\"name\": \"bar\"}");
            ctx.next();
        });
    }
}
