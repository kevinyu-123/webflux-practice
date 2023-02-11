package com.webflux.prac.service;

import com.webflux.prac.domain.Review;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ReviewService {

    public Flux<Review> getReviews(long bookId){
        var reviews = List.of(
                new Review(1,bookId,4.5,"good"),
                new Review(2,bookId,9.9,"very good")
        );
        return Flux.fromIterable(reviews);
    }





}
