package com.webflux.prac.service;

import com.webflux.prac.domain.Book;
import com.webflux.prac.domain.Review;
import com.webflux.prac.handler.exception.BookException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
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

    public Flux<Book> getBooksRetry(){
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
        //        .retry() //exception 발생시 다시 시도
                .retry(3) // 3번 시도 (첫번째 시도 후 3번을 다시 시도함  *총 4번 시도)
                .log();
    }

    public Flux<Book> getBooksRetryWhen(){
    //    var retrySpec = getRetryBackoffSpec();

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
                .retryWhen(getRetryBackoffSpec())
                .log();
    }

    private static RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.backoff(3, Duration.ofMillis(1000))
                .filter(throwable -> throwable instanceof BookException) // 특정 exception 발생시를 위한 filter
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));
    }


    public Mono<Book> getBookById(long bookId){
        var book = bookInfoService.getBookById(bookId); //mono 타입
        var review = reviewService.getReviews(bookId).collectList(); //flux 타입

        return book.zipWith(review,(b,r) -> new Book(b,r));
    }


}
