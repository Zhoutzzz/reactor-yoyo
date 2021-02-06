package com.example.reactor;

import com.example.reactor.entity.TestDO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhoutzzz
 */
@Component
public class TestService {

    /**
     * 使用webflux的编程方式，请求到来后路由对应的方法必须接收一个 {@link ServerRequest} 对象。
     */
    public Mono<ServerResponse> get(ServerRequest request) {
        List<String> collect = request.queryParams().values()
                .parallelStream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
        Mono<TestDO> tag = Flux.fromIterable(collect)
                .doFirst(() -> System.out.println("第一步操作"))
                .reduceWith(() -> new TestDO(0), (x, y) -> {
                    x.setCount(x.getCount() + Integer.parseInt(y));
                    return x;
                });
        return ServerResponse.accepted().body(tag, TestDO.class);
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        request.queryParams().forEach((s, strings) -> System.out.println(s + ":" + strings));
        Mono<String> tag = Mono.just("yeah")
                .doFirst(() -> {
                    System.out.println("第一步操作");
                })
                .then(Mono.just("post请求"));
        return ServerResponse.accepted().body(tag, String.class);
    }

    public Mono<ServerResponse> put(ServerRequest request) {
        request.queryParams().forEach((s, strings) -> System.out.println(s + ":" + strings));
        Mono<String> tag = Mono.just("yeah")
                .doFirst(() -> {
                    System.out.println("第一步操作");
                })
                .then(Mono.just("put请求"));
        return ServerResponse.accepted().body(tag, String.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        request.queryParams().forEach((s, strings) -> System.out.println(s + ":" + strings));
        Mono<String> tag = Mono.just("yeah")
                .doFirst(() -> {
                    System.out.println("第一步操作");
                })
                .then(Mono.just("del请求"));
        return ServerResponse.accepted().body(tag, String.class);
    }

    public static void main(String[] args) {
        Mono<String> tag = Mono.just("yeah")
                .doFirst(() -> System.out.println("第一步操作"))
                .then(Mono.just("asdad"));
        tag.subscribe(System.out::println);
    }
}
