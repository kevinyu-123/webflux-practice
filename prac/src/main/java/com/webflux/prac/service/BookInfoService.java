package com.webflux.prac.service;

import com.webflux.prac.domain.BookInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BookInfoService {

    public Flux<BookInfo> getBooks(){
        var books = List.of(
                new BookInfo(1L,"book 1","author 1","123123123"),
                new BookInfo(2L,"book 2","author 2","32553123"),
                new BookInfo(3L,"book 3","author 3","9223123")
        );
        return Flux.fromIterable(books);
    }

    public Mono<BookInfo> getBookById(long id){
        var book = new BookInfo(id,"book 1","author 1","123123123");

        return Mono.just(book);
    }

}
