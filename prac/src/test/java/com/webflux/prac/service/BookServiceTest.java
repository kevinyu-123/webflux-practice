package com.webflux.prac.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private final BookInfoService bookInfoService = new BookInfoService();

    private final ReviewService reviewService = new ReviewService();

    private final BookService bookService = new BookService(bookInfoService,reviewService);


    /**
     * bookInfo service를 통하여 3개의 목업데이터를 넣어둠.
     * 3개다 체크
     */
    @Test
    void getBooks() {
        var books = bookService.getBooks();
        StepVerifier.create(books)
                .assertNext(book -> {
                    assertEquals("book 1",book.getBookInfo().getTitle());
                    assertEquals(2,book.getReviews().size());
                })
                .assertNext( book -> {
                    assertEquals("book 2",book.getBookInfo().getTitle());
                    assertEquals(2,book.getReviews().size());
                })
                .assertNext( book -> {
                    assertEquals("book 3",book.getBookInfo().getTitle());
                    assertEquals(2,book.getReviews().size());
                })
                .verifyComplete();
    }

    @Test
    void getBookById() {
        var book = bookService.getBookById(1).log();
        StepVerifier.create(book)
                .assertNext(book1 -> {
                    assertEquals("book 1",book1.getBookInfo().getTitle());
                    assertEquals(2,book1.getReviews().size());
                }).verifyComplete();
    }
}