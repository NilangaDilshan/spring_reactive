package com.reactivespring.review.handler;

import com.reactivespring.review.domain.Review;
import com.reactivespring.review.exception.ReviewDataException;
import com.reactivespring.review.exception.ReviewNotFoundException;
import com.reactivespring.review.repository.ReviewReactiveRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewHandler {

    private final ReviewReactiveRepository reviewReactiveRepository;
    private final Validator validator;
    Sinks.Many<Review> reviewSink = Sinks.many().replay().latest();

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validateReview)
                .flatMap(review -> {
                    log.info("Review request to be added: {}", review);
                    return this.reviewReactiveRepository.save(review);
                })
                .doOnNext(review -> {
                    reviewSink.tryEmitNext(review);
                })
                .flatMap(review -> ServerResponse.ok().bodyValue(review));
    }

    private void validateReview(Review review) {
        var constraintViolations = this.validator.validate(review);
        log.info("Constraint violations: {}", constraintViolations);
        if (constraintViolations.size() > 0) {
            var errorMessage = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        if (request.queryParam("movieInfoId").isPresent()) {
            return ServerResponse.ok()
                    .body(this.reviewReactiveRepository.findByMovieInfoId(Long.valueOf(request.queryParam("movieInfoId").get())),
                            Review.class);
        }
        return ServerResponse.ok()
                .body(this.reviewReactiveRepository.findAll(), Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var id = request.pathVariable("id");
        var existingReview = this.reviewReactiveRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException(String.format("Review not found for given review id: %s", id))));
        return existingReview.flatMap(review -> request.bodyToMono(Review.class)
                .map(newReview -> {
                    review.setComment(newReview.getComment());
                    review.setRating(newReview.getRating());
                    return review;
                })
                .flatMap(this.reviewReactiveRepository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        return this.reviewReactiveRepository.deleteById(request.pathVariable("id"))
                .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSink.asFlux(), Review.class)
                .log();

    }
}
