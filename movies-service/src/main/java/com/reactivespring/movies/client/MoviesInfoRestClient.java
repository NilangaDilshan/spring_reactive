package com.reactivespring.movies.client;

import com.reactivespring.movies.domain.MovieInfo;
import com.reactivespring.movies.exception.MoviesInfoClientException;
import com.reactivespring.movies.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    private final WebClient webClient;
    private final String movieInfoUrl;

    public MoviesInfoRestClient(WebClient webClient, @Value("${restClient.moviesInfoUrl}") String movieInfoUrl) {
        this.webClient = webClient;
        this.movieInfoUrl = movieInfoUrl;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        return webClient.get()
                .uri(movieInfoUrl.concat("/{id}"), movieId)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> {
                            log.error("Error Status Code: {}", clientResponse.statusCode().value());
                            var statusCode = clientResponse.statusCode();
                            if (statusCode.equals(HttpStatus.NOT_FOUND)) {
                                return Mono.error(new MoviesInfoClientException(String.format("Movie not found for movie ID: %s", movieId),
                                        statusCode.value()));
                            }
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error: {}", errorBody);
                                        return Mono.error(new MoviesInfoClientException(errorBody, statusCode.value()));
                                    });
                        })
                .onStatus(httpStatus -> httpStatus.is5xxServerError(),
                        clientResponse -> {
                            log.error("Error Status Code: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error: {}", errorBody);
                                        return Mono.error(
                                                new MoviesInfoServerException(String.format("Server Exception in MoviesInfoService: %s", errorBody)));
                                    });
                        })
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
