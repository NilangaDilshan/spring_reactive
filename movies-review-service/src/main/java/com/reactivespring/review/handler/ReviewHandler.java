package com.reactivespring.review.handler;

import com.reactivespring.review.domain.Review;
import com.reactivespring.review.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewHandler {
    
    private final ReviewReactiveRepository reviewReactiveRepository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(review -> {
                    log.info("Review request to be added: {}", review);
                    return this.reviewReactiveRepository.save(review);
                })
                .flatMap(review -> ServerResponse.ok().bodyValue(review));
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {
        var reviews = this.reviewReactiveRepository.findAll();
        return ServerResponse.ok()
                .body(reviews, Review.class);
    }
}
