package com.webflux.prac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@RequiredArgsConstructor
@Data
public class Customer {
    @Id
    private Long id;
    private final String firstName;
    private final String lastName;

}
