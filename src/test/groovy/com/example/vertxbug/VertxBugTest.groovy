package com.example.vertxbug

import io.vertx.core.Vertx
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static io.restassured.RestAssured.given

class VertxBugTest extends Specification {

    Vertx vertx

    void setup() {
        vertx = Vertx.vertx()
        def deploymentLatch = new CountDownLatch(1)
        vertx.deployVerticle(
                new WebApplicationVerticle(new RouterFactory()),
                { deploymentLatch.countDown() }
        )
        deploymentLatch.await(30, TimeUnit.SECONDS)
    }

    void cleanup() {
        def latch = new CountDownLatch(1)
        vertx.close({ latch.countDown() })
        latch.await(1, TimeUnit.MINUTES)
    }

    def "should return proper body under load"() {
        given: 'significant load in background'
        def threadPool = Executors.newFixedThreadPool(10)
        (1..1000).each {
            threadPool.submit {
                given().get('http://localhost:9999/v1/foo').andReturn()
            }
        }
        threadPool.shutdown()

        expect: 'response to contain valid json body'
        (1..100).each {
            def response = given().get('http://localhost:9999/v1/bar').then()

            def jsonBody = response.extract().body().asString()
            assert jsonBody == '{"name": "bar"}'
        }

        threadPool.awaitTermination(3, TimeUnit.MINUTES)
    }
}
