package com.example.reactor;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author zhoutzzz
 */
@Slf4j
public class ReactorOperation {
    public static void main(String[] args) {
//        flatmapOperator();
//        fluxOperator();
//        mergeOperator();
        combineLatestOperator();
//        concatMapOperator();
        new ReactorOperation().justWait();
    }

    public static void concatMapOperator() {
        Stream<Integer> stream1 = Stream.of(1, 2, 3, 4, 5, 6);
        Flux.fromStream(stream1)
                .concatMap(x -> Mono.just(x).subscribeOn(Schedulers.parallel()))
                .doOnNext(x -> System.out.println("Received " + x + " on thread: " + Thread.currentThread().getName()))
                .concatMap(x -> {
                    System.out.println("Received in flatMap: " + x + " on thread: " + Thread.currentThread().getName());
                    x++;
                    return Mono.just(x).subscribeOn(Schedulers.boundedElastic());
                })
                .subscribe(x -> System.out.println("Received (in the subscriber): "
                        + x
                        + " on thread: "
                        + Thread.currentThread().getName()));
    }

    public static void fluxOperator() {
        Flux.just(1, 2, 3)
                .filter(x -> x < 2) //过滤
                .log() //打印日志
                .map(x -> x++) //当前值自增
                .delayElements(Duration.of(1L, ChronoUnit.SECONDS)) //延迟1秒传输
                .subscribe(System.out::println); //订阅
    }

    public static void mergeOperator() {
        Stream<Integer> stream1 = Stream.of(1, 2, 3);
        Stream<Integer> stream2 = Stream.of(4, 5, 6);
        Flux.fromStream(stream1)
                .mergeWith(Flux.fromStream(stream2))
                .subscribe(System.out::println);
    }

    public static void combineLatestOperator() {

        Stream<Integer> stream1 = Stream.of(1, 2, 3);
        Stream<Integer> stream2 = Stream.of(4, 5, 6);
        Flux.combineLatest(Arrays::toString,
                Flux.fromStream(stream1).delayElements(Duration.of(1L, ChronoUnit.SECONDS)),
                Flux.fromStream(stream2).delayElements(Duration.of(500L, ChronoUnit.MILLIS)))
                .subscribe(System.out::println);
    }

    /*
    map与flatmap区别在于，map操作具体的值，flatmap操作的是mono或者flux对象，且map是个同步操作。
    flatmap支持异步操作，flux序列里面的每一个元素，都会被重新单独创建一个流出来，最终进行合并，且合并并不保证顺序。
    https://medium.com/swlh/understanding-reactors-flatmap-operator-a6a7e62d3e95
     */
    public static void flatmapOperator() {
        Flux<Integer> f = Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5));
        f.flatMap(a -> Mono.just(a).subscribeOn(Schedulers.parallel())) //使用新线程调度并行执行
                .doOnNext( //传输到下一步
                        a ->
                                System.out.println(
                                        "Received: " + a + " on thread: " + Thread.currentThread().getName()))
                .flatMap(
                        a -> {
                            System.out.println(
                                    "Received in flatMap: " + a + " on thread: " + Thread.currentThread().getName());
                            a++;
                            //boundedElastic也是使用新线程异步执行，与parallel区别parallel是单个线程的线程池，elastic是多线程数
                            //的线程池，并且有60秒限制，到时可能会关闭，默认最大线程数为cpu核数*10
                            return Mono.just(a).subscribeOn(Schedulers.boundedElastic());
                        })
                .subscribe(
                        a ->
                                System.out.println(
                                        "Received (in the subscriber): "
                                                + a
                                                + " on thread: "
                                                + Thread.currentThread().getName()));

    }

    private synchronized void justWait() {
        try {
            this.wait();
        } catch (Exception e) {

        }
    }
}
