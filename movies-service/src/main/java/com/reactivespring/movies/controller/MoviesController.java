package com.reactivespring.movies.controller;

import com.reactivespring.movies.client.MoviesInfoRestClient;
import com.reactivespring.movies.client.ReviewsRestClient;
import com.reactivespring.movies.domain.Movie;
import com.reactivespring.movies.domain.MovieInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@Slf4j
@RequiredArgsConstructor
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewsRestClient reviewsRestClient;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
        log.info("retrieveMovieById: {}", movieId);
        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsListMono = this.reviewsRestClient.retrieveReviews(movieId).collectList();
                    return reviewsListMono.map(reviews -> new Movie(movieInfo, reviews));
                }).log();
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> retrieveMovieInfos() {
        log.info("retrieveMovieInfos");
        return this.moviesInfoRestClient.retrieveMoviesStream();
    }

}
