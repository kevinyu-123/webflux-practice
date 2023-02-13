package com.webflux.prac.service;

import com.webflux.prac.domain.Book;
import com.webflux.prac.domain.Review;
import com.webflux.prac.handler.exception.BookException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookInfoService bookInfoService;

    private final ReviewService reviewService;


    public Flux<Book> getBooks(){
        var allBooks = bookInfoService.getBooks();
        return allBooks
                .flatMap(bookInfo -> {
                    Mono<List<Review>> reviews = reviewService.getReviews(bookInfo.getBookId()).collectList();
                    return reviews
                            .map(review -> new Book(bookInfo,review));
                })
                .onErrorMap(throwable -> {
                    log.error("execption is : " + throwable);
                    return new BookException("exception occured while fetching Books");
                })
                .log();
    }


    public Mono<Book> getBookById(long bookId){
        var book = bookInfoService.getBookById(bookId); //mono 타입
        var review = reviewService.getReviews(bookId).collectList(); //flux 타입

        return book.zipWith(review,(b,r) -> new Book(b,r));
    }


}
