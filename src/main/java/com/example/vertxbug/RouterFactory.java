package com.example.vertxbug;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;


class RouterFactory {

    Router globalRouter(Vertx vertx) {
        final Router mainRouter = Router.router(vertx);

        setDefaultContentType(mainRouter);
        setChunkEncoding(mainRouter);
        endResponseAsFinalStep(mainRouter);
        registerRoutes(mainRouter, vertx);

        return mainRouter;
    }

    private void setDefaultContentType(Router router) {
        final String APPLICATION_JSON = "application/json";
        router.route().consumes(APPLICATION_JSON);
        router.route().produces(APPLICATION_JSON);
        router.route().handler(context -> {
            context.response().headers().add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
            context.next();
        });
    }

    private void setChunkEncoding(Router router) {
        router.route().handler(context -> {
            context.response().setChunked(true);
            context.next();
        });
    }

    private void endResponseAsFinalStep(Router router) {
        router.route().last().handler(c -> c.response().end());
    }

    private void registerRoutes(Router router, Vertx vertx) {
        final Router subRouter = Router.router(vertx);

        subRouter.get("/foo").handler(new FooHandler());
        subRouter.get("/bar").handler(new BarHandler());

        router.mountSubRouter("/v1", subRouter);
    }
}
