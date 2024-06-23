package com.reactivespring.movies.client;

import com.reactivespring.movies.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

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
                .bodyToFlux(Review.class)
                .log();
    }
}
