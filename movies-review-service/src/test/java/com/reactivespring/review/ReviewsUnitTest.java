package com.reactivespring.review;

import com.reactivespring.review.domain.Review;
import com.reactivespring.review.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.review.handler.ReviewHandler;
import com.reactivespring.review.repository.ReviewReactiveRepository;
import com.reactivespring.review.router.ReviewRouter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@AutoConfigureWebTestClient
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@Slf4j
class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReviewTest() {
        //given
        var review = Review.builder().movieInfoId(1L).comment("Batman Begins").rating(8.2).build();
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins")
                        .rating(8.2).build()));

        //when
        var response = webTestClient.post()
                .uri("/v1/reviews/add")
                .body(Mono.just(review), Review.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotNull(response);
        assertEquals("abc", response.getReviewId());
        assertEquals(review.getMovieInfoId(), response.getMovieInfoId());
        assertEquals(review.getComment(), response.getComment());
        assertEquals(review.getRating(), response.getRating());
    }

    @Test
    void getAllReviewsTest() {
        //given
        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.just(Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins")
                                .rating(8.2).build(),
                        Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins")
                                .rating(8.2).build(),
                        Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins")
                                .rating(8.2).build()));

        //when
        var response = webTestClient.get()
                .uri("/v1/reviews/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotNull(response);
        assertEquals(3, response.size());
    }

    @Test
    void updateReviewTest() {
        //given
        var review = Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins Updated").rating(8.2).build();
        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins").rating(8.2).build()));
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));

        //when
        var response = webTestClient.put()
                .uri("/v1/reviews/abc")
                .body(Mono.just(review), Review.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotNull(response);
        assertEquals(review.getReviewId(), response.getReviewId());
        assertEquals(review.getMovieInfoId(), response.getMovieInfoId());
        assertEquals(review.getComment(), response.getComment());
        assertEquals(review.getRating(), response.getRating());
    }

    @Test
    void deleteReviewTest() {
        //given
        when(reviewReactiveRepository.deleteById(isA(String.class)))
                .thenReturn(Mono.empty());

        //when
        webTestClient.delete()
                .uri("/v1/reviews/abc")
                .exchange()
                .expectStatus().isOk();

        //then
    }

    @Test
    void addReviewValidationTest() {
        //given
        var review = Review.builder().movieInfoId(null).comment("Batman Begins").rating(-9.0).build();
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins")
                        .rating(8.2).build()));

        //when
        webTestClient.post()
                .uri("/v1/reviews/add")
                .body(Mono.just(review), Review.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.movieInfoId : must not be null, review.rating : must be greater than or equal to 1");
    }

    @Test
    void updateReviewNotFoundExceptionTest() {
        //given
        var review = Review.builder().reviewId("abc").movieInfoId(1L).comment("Batman Begins Updated").rating(8.2).build();
        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.empty());

        //when
        webTestClient.put()
                .uri("/v1/reviews/abc")
                .body(Mono.just(review), Review.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Review not found for given review id: abc");
    }
}