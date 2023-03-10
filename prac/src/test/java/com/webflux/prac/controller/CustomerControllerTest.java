package com.webflux.prac.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class TestControllerTest {

    @Autowired
    TestController controller;
    @Test
    void fluxTransformDefaultIfEmpty() {
        var fluxUnit  = controller.FluxTransformDefaultIfEmpty(6);

        StepVerifier.create(fluxUnit)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void fluxTransformSwitch() {
        var fluxUnit  = controller.FluxTransformSwitch(7);

        StepVerifier.create(fluxUnit)
                .expectNext("pineapple","grapefruit")
                .verifyComplete();
    }

    @Test
    void fluxConcat() {

        var result = controller.fluxConcat().log();

        StepVerifier.create(result).expectNext("mango","banana","tomato","pineapple").verifyComplete();
    }

    @Test
    void fluxConcatWith() {
        var result = controller.fluxConcatWith().log();

        StepVerifier.create(result).expectNext("mango","banana","tomato","pineapple").verifyComplete();
    }

    @Test
    void zipExample() {
        var result = controller.zipExample().log();

        StepVerifier.create(result).expectNext("mangotomato","orangelemon").verifyComplete();
    }

    @Test
    void zipwithEx() {
        var result = controller.zipwithEx().log();

        StepVerifier.create(result).expectNext("mangotomato","orangelemon").verifyComplete();
    }

    @Test
    void zipwithTupleEx() {
        var result = controller.zipwithTupleEx();

        StepVerifier.create(result).expectNext("mangotomatopotato","orangelemonbeans").verifyComplete();
    }

    @Test
    void zipwithExMono() {
        var result = controller.zipwithExMono().log();

        StepVerifier.create(result).expectNext("mangotomato").verifyComplete();
    }

    @Test
    void doOnExample() {
        var result = controller.doOnExample(5).log();
        StepVerifier.create(result).expectNext("orange","banana").verifyComplete();
    }

    @Test
    void onErrorContinue() {
        var result = controller.onErrorContinue().log();

        StepVerifier.create(result).expectNext("APPLE","ORANGE").verifyComplete();

    }

    @Test
    void onErrorMap() {
        var result = controller.onErrorMap().log();

        StepVerifier.create(result).expectNext("APPLE")
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void doOnError() {
        var result = controller.doOnError().log();

        StepVerifier.create(result).expectNext("APPLE")
                .expectError(RuntimeException.class)
                .verify();
    }
}