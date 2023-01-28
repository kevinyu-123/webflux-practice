package com.webflux.prac.controller;

import com.webflux.prac.domain.Customer;
import com.webflux.prac.repository.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;


@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;

    private final Sinks.Many<Customer> sink;

    /*
      sink : A 요청 : a stream
             B 요청 : b stream
             Flux.merge -> sink ( stream을 합쳐줌)
     */

    public CustomerController(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
        sink = Sinks.many().multicast().onBackpressureBuffer();
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

    @GetMapping(value = "/customer/ssesink")
    public Flux<ServerSentEvent<Customer>> findAllSSESink(){ // serverSentEvent : 자동으로 produces = MediaType.TEXT_EVENT_STREAM_VALUE 생성
        return sink.asFlux().map( c -> ServerSentEvent.builder(c).build()).doOnCancel(()->{
            sink.asFlux().blockLast(); // blockLast() : 중간에 요청을 멈춰도 다시 실행될 수 있도록 해줌
        });
    }

    @PostMapping("/customer")
    public Mono<Customer> saveTestData(){  //db에 테스트 데이터 넣기
        return customerRepository.save(new Customer("kevin","yu")).doOnNext( c -> {
           sink.tryEmitNext(c);
        });
    }

}
