package com.example.vertxbug;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;

class WebApplicationVerticle extends AbstractVerticle {

    private RouterFactory routerFactory;

    WebApplicationVerticle(RouterFactory routerFactory) {
        this.routerFactory = routerFactory;
    }

    @Override
    public void start(Future<Void> future) {
        httpServer()
                .listen(9999, r -> completeFutureOrFail(r, future));
    }

    private HttpServer httpServer() {
        final HttpServerOptions options = new HttpServerOptions();
        return vertx
                .createHttpServer(options)
                .requestHandler(routerFactory.globalRouter(vertx)::accept);
    }

    private void completeFutureOrFail(AsyncResult<HttpServer> asyncResult, Future<Void> future) {
        if (asyncResult.succeeded()) {
            future.complete();
        } else {
            future.fail(asyncResult.cause());
        }
    }
}
