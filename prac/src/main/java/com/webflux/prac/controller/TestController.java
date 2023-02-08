package com.webflux.prac.controller;

import com.webflux.prac.domain.Customer;
import com.webflux.prac.repository.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;


@RestController
public class TestController {

    private final CustomerRepository customerRepository;

    private final Sinks.Many<Customer> sink;

    /*
      sink : A 요청 : a stream
             B 요청 : b stream
             Flux.merge -> sink ( stream을 합쳐줌)
     */


    public TestController(CustomerRepository customerRepository){
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
        return customerRepository.save(new Customer("kevin","yu")).doOnNext(sink::tryEmitNext);
    }

    @GetMapping("/filter")
    public Flux<Integer> filterTest(){
        return Flux.fromIterable(List.of(1,2,3,4,5,6,7)).filter( s -> s > 3).log();
    }

    @GetMapping("/filter-map")
    public Flux<String> filterWithMapTest(){
        return Flux.fromIterable(List.of("abc","be","casdfadsf","dweee"))
                .filter( s -> s.length() > 2)
                .map(String::toUpperCase)
                .log();
    }

    @GetMapping("/flatmap")
    public Flux<String> fruitsFluxFlatmap(){
        return Flux.fromIterable(List.of("orange","mango","apple"))
                .flatMap(s -> Flux.just(s.split(" ")))
                .log();
    }

    @GetMapping("/to-flux")
    public Flux<String> FlatmapMany(){  // mono to flux
        return Mono.just("Apple")
                .flatMapMany(s -> Flux.just(s.split("")))
                .log();
    }

    @GetMapping("/transform")
    public Flux<String> FluxTransform(@RequestParam Integer num){
        Function<Flux<String>,Flux<String>> filterData = data -> data.filter(s -> s.length() > num);

        return Flux.fromIterable(List.of("apple","orange","mango"))
                .transform(filterData)
                .log();
    }

    @GetMapping("/transform-default")
    public Flux<String> FluxTransformDefaultIfEmpty(@RequestParam Integer num){
        Function<Flux<String>,Flux<String>> filterData = data -> data.filter(s -> s.length() > num);

        return Flux.fromIterable(List.of("apple","orange","mango"))
                .transform(filterData)
                .defaultIfEmpty("default") // when data is empty, pass default value
                .log();
    }

    @GetMapping("/transform-switch")
    public Flux<String> FluxTransformSwitch(@RequestParam Integer num){
        Function<Flux<String>,Flux<String>> filterData = data -> data.filter(s -> s.length() > num);

        return Flux.fromIterable(List.of("apple","orange","mango"))
                .transform(filterData)
                .switchIfEmpty(Flux.just("pineapple","grapefruit")) // switch to new data set when values are empty
                    .transform(filterData)
                .log();
    }

    @GetMapping("/concat")
    public Flux<String> fluxConcat(){
        var f = Flux.just("mango","banana");
        var a = Flux.just("tomato","pineapple");
        return Flux.concat(f,a);
    }

    @GetMapping("/concatwith")
    public Flux<String> fluxConcatWith(){
        var f = Flux.just("mango","banana");
        var a = Flux.just("tomato","pineapple");
        return f.concatWith(a);
    }

    @GetMapping("/zip")
    public Flux<String> zipExample(){
        var fruits = Flux.just("mango","orange");
        var veggis = Flux.just("tomato", "lemon");

        return Flux.zip(fruits,veggis,(first, second) -> first + second).log();

    }

    @GetMapping("/zipwith")
    public  Flux<String> zipwithEx(){
        var fruits = Flux.just("mango","orange");
        var veggis = Flux.just("tomato", "lemon");

        return fruits.zipWith(veggis, (f,s)-> f+s).log();
    }












}
