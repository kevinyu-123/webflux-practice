package com.webflux.prac.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CustomerControllerTest {

    @Autowired
    CustomerController controller;
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
}