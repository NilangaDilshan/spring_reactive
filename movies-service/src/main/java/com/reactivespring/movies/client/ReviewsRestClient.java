package com.reactivespring.movies.client;

import com.reactivespring.movies.domain.Review;
import com.reactivespring.movies.exception.ReviewsClientException;
import com.reactivespring.movies.exception.ReviewsServerException;
import com.reactivespring.movies.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {
    private final WebClient webClient;
    private final String reviewsUrl;

    public ReviewsRestClient(WebClient webClient, @Value("${restClient.reviewsInfoUrl}") String reviewsUrl) {
        this.webClient = webClient;
        this.reviewsUrl = reviewsUrl;
    }

    //movieInfoId
    public Flux<Review> retrieveReviews(String movieInfoId) {
        var uriComponentBuilder = UriComponentsBuilder.fromHttpUrl(reviewsUrl).queryParam("movieInfoId", movieInfoId);
        return webClient.get()
                .uri(uriComponentBuilder.build().toUri())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> {
                            log.error("Error Status Code: {}", clientResponse.statusCode().value());
                            var statusCode = clientResponse.statusCode();
                            if (statusCode.equals(HttpStatus.NOT_FOUND)) {
                                return Mono.empty();
                            }
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error: {}", errorBody);
                                        return Mono.error(new ReviewsClientException(errorBody));
                                    });
                        })
                .onStatus(httpStatus -> httpStatus.is5xxServerError(),
                        clientResponse -> {
                            log.error("Error Status Code: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error: {}", errorBody);
                                        return Mono.error(
                                                new ReviewsServerException(String.format("Server Exception in ReviewsService: %s", errorBody)));
                                    });
                        })
                .bodyToFlux(Review.class)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }
}
