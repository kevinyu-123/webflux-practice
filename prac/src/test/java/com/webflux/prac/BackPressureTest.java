package com.webflux.prac;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class BackPressureTest {

    @Test
    public void testBackPressure(){
        var numbers = Flux.range(1,10).log();
//        numbers.subscribe(s -> {
//            System.out.println("num : "+numbers);
        numbers.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(3);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("value : " + value);
                if(value == 3 ){
                    cancel();
                }

            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                super.hookOnCancel();
            }
        });

    }

    @Test
    public void testBackPressureDrop(){
        var numbers = Flux.range(1,10).log();
//        numbers.subscribe(s -> {
//            System.out.println("num : "+numbers);
        numbers
                .onBackpressureDrop(integer -> {
                    System.out.println("Dropped value : "+ integer);
                })
                .subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(3);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("value : " + value);
                if(value == 3 ){
                    hookOnCancel();
                }

            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                super.hookOnCancel();
            }
        });

    }

    @Test
    public void testBackPressureBuffer(){
        var numbers = Flux.range(1,10).log();
//        numbers.subscribe(s -> {
//            System.out.println("num : "+numbers);
        numbers
                .onBackpressureBuffer(10, integer -> System.out.println("buffered value : " + integer))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(3);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("value : " + value);
                        if(value == 3 ){
                            hookOnCancel();
                        }

                    }

                    @Override
                    protected void hookOnComplete() {
                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        super.hookOnCancel();
                    }
                });

    }
}
