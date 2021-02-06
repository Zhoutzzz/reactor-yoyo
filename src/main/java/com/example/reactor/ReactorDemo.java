package com.example.reactor;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author zhoutzzz
 */
@Slf4j
public class ReactorDemo {

    public static void main(String[] args) {
        // 创建简单/复杂flux序列
//        complexFluxCreate();
//        simpleFluxCreate();
        // 背压
//        backpressure();

        //mono创建简单序列和延迟方法
//        simpleMonoCreate();
        monoDelayCreate();
        // 通过callable创建mono序列，.doOnNext()方法可以无限调用
//        Mono.fromCallable(ArrayList::new)
//                .doOnNext(x -> x.add("asd"))
//                .doOnNext(x -> x.add("qwe"))
//                .subscribe(System.out::println);

//        new ReactorDemo().go();
    }

    private static void simpleMonoCreate() {
        //与flux的相同，mono与flux中的部分方法是一样的，因为mono与flux顶层接口都是publisher
        Mono.just("hello").subscribe(System.out::println);
        //mono的from相关方法，支持的类型更多
        Mono.from(Mono.just("123")).subscribe(System.out::println);
        //两个方法，只有值不为null或者optional中不为空，才创建对象
        Mono.justOrEmpty(Optional.of("galigeigei")).subscribe(System.out::println);
        //实际上sout方法不会被调用，整个代码正常运行
        Mono.justOrEmpty(null).subscribe(o -> System.out.println(o.toString()));
    }

    private static void simpleFluxCreate() {
        //创建的序列元素类型不定
        Flux.just("asd", "qwe", 123, new Object()).subscribe(System.out::println);
        //创建一个从1-10的序列
        Flux.range(1, 10).subscribe(System.out::println);
        //基于数组、集合、stream创建flux序列
        Flux.fromArray(new Object[]{new Object()}).subscribe(System.out::println);
        Flux.fromStream(Stream.of(1, 2, 3)).subscribe(System.out::println);
        Flux.fromIterable(new ArrayList<>() {{
            add(1);
            add(2);
            add(3);
        }}).subscribe(System.out::println);
        //每隔一秒，从1开始递增无限输出
        Flux.interval(Duration.of(1L, ChronoUnit.SECONDS)).subscribe(System.out::println);
//        Flux.combineLatest();
    }

    private static void monoDelayCreate() {
        Mono.delay(Duration.of(1L, ChronoUnit.SECONDS)).subscribe(System.out::println);
        Mono<String> hello = Mono.defer(() -> Mono.just("hello"));
        hello.subscribe(System.out::println);
        try {
            Thread.sleep(3000L);
        } catch (Exception e) {

        }
        hello.subscribe(System.out::println);
    }

    private static void complexFluxCreate() {
        // flux创建复杂序列的方法
        Flux.generate(sink -> {
            sink.next("Hello");
            sink.next("world");
            sink.complete();
        }).subscribe(System.out::println);

        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);

        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    /**
     * jdk9提供的reactor编程模型类库，模拟背压，内部有阈值
     */
    public static void backpressure() {
        //1.发布者
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        //2. 订阅者
        Flow.Subscriber subscriber = new Flow.Subscriber() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                log.info("onSubscribe");
                //请求数据
                subscription.request(1);
                this.subscription = subscription;
            }

            /**
             * 处理数据
             * @param item
             */
            @Override
            public void onNext(Object item) {
                log.info("item: {}", item);
                log.info("onNext");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.subscription.request(1);
            }

            /**
             * 数据处理异常
             * @param throwable
             */
            @Override
            public void onError(Throwable throwable) {
                log.info("onError");
            }

            /**
             * 数据完成
             */

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        // 3. 建立关系
        publisher.subscribe(subscriber);
        // 4. 生产数据
        for (int i = 0; i < 500; i++) {
            publisher.submit("test" + i);
            log.info("submit:{}", "test" + i);
        }

        // 5 .结束关闭
        publisher.close();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {

        }
    }

    public synchronized void go() {
        try {
            this.wait();
        } catch (Exception e) {

        }
    }
}
