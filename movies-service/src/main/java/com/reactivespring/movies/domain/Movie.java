package com.reactivespring.movies.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Movie {
    private MovieInfo movieInfo;
    private List<Review> review;
}
