package com.reactivespring.review.domain;

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
    private Long movieInfoId;
    private String comment;
    private Double rating;
}
