package com.reactivespring.movies.domain;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    private String reviewId;
    @NotNull(message = "{review.movieInfoId.not.null}")
    private Long movieInfoId;
    private String comment;
    @Min(value = 0L, message = "{review.rating.min}")
    private Double rating;
}