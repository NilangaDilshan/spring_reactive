package com.reactivespring.review.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder
public class Review {
    @Id
    private String reviewId;
    @NotNull(message = "{review.movieInfoId.not.null}")
    private Long movieInfoId;
    private String comment;
    @Min(value = 0L, message = "{review.rating.min}")
    private Double rating;
}
