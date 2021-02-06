package com.example.reactor;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

@SpringBootApplication
@RestController
@RequestMapping("/api")
@Slf4j
public class ReactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactorApplication.class);
    }

    /**
     * 传统的mvc+reactor
     */

    @GetMapping
    public String mono() {
        return "123";
    }


    @GetMapping("/flux")
    public Flux<String> flux() {
        return Flux.just("Hello", "world", "qiekenao");
    }

    @GetMapping("/unlimited")
    public Flux<ServerSentEvent<Integer>> unlimited() {
        return Flux.interval(Duration.of(1, ChronoUnit.SECONDS)).map(data ->
                ServerSentEvent.<Integer>builder()
                        .event("random")
                        .id(String.valueOf(data))
                        .data(data.intValue())
                        .build());
    }
}
