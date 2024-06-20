package com.reactivespring.moviesinfo.service;

import com.reactivespring.moviesinfo.domain.MovieInfo;
import com.reactivespring.moviesinfo.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviesInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovie(MovieInfo movieInfo) {
        log.info("Adding movie information {}", movieInfo.toString());
        return this.movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovies() {
        log.info("Fetching all movies information");
        return this.movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String movieId) {
        log.info("Fetching movie information for movieId: {}", movieId);
        return this.movieInfoRepository.findById(movieId);
    }

    public Mono<MovieInfo> updateMovieInfo(String movieId, MovieInfo movieInfo) {
        log.info("Updating movie information for movieId: {}", movieId);
        return this.movieInfoRepository.findById(movieId)
                .flatMap(movieInfoToUpdate -> {
                    movieInfoToUpdate.setName(movieInfo.getName());
                    movieInfoToUpdate.setYear(movieInfo.getYear());
                    movieInfoToUpdate.setCast(movieInfo.getCast());
                    movieInfoToUpdate.setRelease_date(movieInfo.getRelease_date());
                    return this.movieInfoRepository.save(movieInfoToUpdate);
                });
    }

    public Mono<Void> deleteMovieInfo(String movieId) {
        log.info("Deleting movie information for movieId: {}", movieId);
        return this.movieInfoRepository.deleteById(movieId);
    }

    public Flux<MovieInfo> getMovieInfoByYear(Integer year) {
        log.info("Fetching movie information for year: {}", year);
        return this.movieInfoRepository.findByYear(year);
    }

    public Mono<MovieInfo> getMovieInfoByName(String name) {
        log.info("Fetching movie information for name: {}", name);
        return this.movieInfoRepository.findMovieByName(name);
    }
}
