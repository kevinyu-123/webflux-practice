package com.webflux.prac.controller;

import com.webflux.prac.domain.Customer;
import com.webflux.prac.repository.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;


@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customer")
    public Flux<Customer> findAllCustomer(){
        return customerRepository.findAll().log();
    }

    @GetMapping("/test")
    public Flux<Integer> test(){
        return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/teststream",produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> teststream(){
        return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable Long id){
        return customerRepository.findById(id).log();
    }

    @GetMapping(value = "/customer/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> findAllSSE(){
        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }
}
