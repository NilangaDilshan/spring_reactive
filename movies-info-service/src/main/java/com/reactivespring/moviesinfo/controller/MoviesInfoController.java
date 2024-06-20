package com.reactivespring.moviesinfo.controller;

import com.reactivespring.moviesinfo.domain.MovieInfo;
import com.reactivespring.moviesinfo.exception.MovieInfoNotfoundException;
import com.reactivespring.moviesinfo.service.MoviesInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies-info")
@Slf4j
@RequiredArgsConstructor
public class MoviesInfoController {

    private final MoviesInfoService movieInfoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        //throw new MovieInfoNotfoundException("Movie not found");
        log.info("Adding movie information {}", movieInfo.toString());
        return this.movieInfoService.addMovie(movieInfo).log();
    }

    @GetMapping("/{movieId}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String movieId) {
        log.info("Fetching movie information for movieId: {}", movieId);
        return this.movieInfoService.getMovieInfoById(movieId)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @GetMapping("/all")
    public Flux<MovieInfo> getAllMoviesInfo(@RequestParam(required = false) Integer year) {
        log.info("Fetching all movies information. Year: {}", year);
        if (year != null) {
            return this.movieInfoService.getMovieInfoByYear(year).log();
        }
        return this.movieInfoService.getAllMovies().log();
    }

    @PutMapping("/{movieId}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String movieId, @RequestBody MovieInfo movieInfo) {
        log.info("Updating movie information for movieId: {}", movieId);
        return this.movieInfoService.updateMovieInfo(movieId, movieInfo)
                .map(ResponseEntity.ok()::body)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                //.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String movieId) {
        log.info("Deleting movie information for movieId: {}", movieId);
        return this.movieInfoService.deleteMovieInfo(movieId).log();
    }

    @GetMapping("/year/{year}")
    public Flux<MovieInfo> getMovieInfoByYear(@PathVariable Integer year) {
        log.info("Fetching movie information for year: {}", year);
        return this.movieInfoService.getMovieInfoByYear(year).log();
    }

    @GetMapping("/name/{name}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoByName(@PathVariable String name) {
        log.info("Fetching movie information for name: {}", name);
        return this.movieInfoService.getMovieInfoByName(name)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }
}
