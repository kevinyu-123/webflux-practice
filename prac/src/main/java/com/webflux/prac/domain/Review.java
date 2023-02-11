package com.webflux.prac.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    private long reviewId;

    private long bookId;

    private Double rate;

    private String comments;
}
