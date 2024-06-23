package com.reactivespring.review;

import com.reactivespring.review.domain.Review;
import com.reactivespring.review.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Slf4j
class ReviewRouterIntegrationTest extends AbstractMongodbBaseTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ReviewReactiveRepository reviewReactiveRepository;

    private static final String REVIEW_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {
        this.reviewReactiveRepository.saveAll(
                List.of(Review.builder().movieInfoId(1L).comment("Batman Begins").rating(8.2).build(),
                        Review.builder().movieInfoId(2L).comment("The Dark Knight").rating(9.2).build(),
                        Review.builder().movieInfoId(3L).comment("The Dark Knight Rises").rating(8.9).build())
        ).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    void name() {
        //given

        //when
        webTestClient
                .get()
                .uri("/v1/helloworld")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("Hello World");
    }

    @Test
    @Ignore
    void testAddReview() {
        this.reviewReactiveRepository.saveAll(
                List.of(Review.builder().movieInfoId(1L).comment("Batman Begins").rating(8.2).build())
        ).blockLast();
        this.webTestClient.get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    reviews.forEach(review -> log.info("Review: {}", review.getMovieInfoId()));
                    reviews.forEach(review -> log.info("Review: {}", review.getReviewId()));
                    reviews.forEach(review -> log.info("Review: {}", review.getComment()));
                    reviews.forEach(review -> log.info("Review: {}", review.getComment()));
                });
    }

    @Test
    void addReviewTest() {
        //given
        var review = Review.builder()
                .movieInfoId(1L)
                .comment("Batman Begins")
                .rating(8.2)
                .build();

        //when
        var reviewResponse = this.webTestClient.post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(reviewResponse);
        assertNotNull(reviewResponse.getReviewId());
        assertEquals(review.getMovieInfoId(), reviewResponse.getMovieInfoId());
        assertEquals(review.getRating(), reviewResponse.getRating());
        assertEquals(review.getComment(), reviewResponse.getComment());
    }

    @Test
    void getAllReviewsTest() {
        //when
        this.webTestClient.get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertNotNull(reviews);
                    assertFalse(reviews.isEmpty());
                    assertEquals(3, reviews.size());
                    reviews.forEach(review -> log.info("Review: {}", review));
                });
    }

    @Test
    void updateReviewTest() {
        //given
        var review = Review.builder()
                .movieInfoId(1L)
                .comment("Movie Not Updated")
                .rating(8.2)
                .build();
        var reviewId = this.reviewReactiveRepository.save(review).block().getReviewId();
        var reviewToBeUpdated = Review.builder()
                .movieInfoId(1L)
                .comment("Movie Updated")
                .rating(9.2)
                .build();

        //when
        var reviewResponse = this.webTestClient.put()
                .uri(REVIEW_URL + "/{reviewId}", reviewId)
                .bodyValue(reviewToBeUpdated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(reviewResponse);
        assertNotNull(reviewResponse.getReviewId());
        assertEquals(reviewToBeUpdated.getMovieInfoId(), reviewResponse.getMovieInfoId());
        assertEquals(reviewToBeUpdated.getRating(), reviewResponse.getRating());
        assertEquals(reviewToBeUpdated.getComment(), reviewResponse.getComment());
    }

    @Test
    void deleteReviewTest() {
        //given
        var review = Review.builder()
                .movieInfoId(1L)
                .comment("Movie To Be Deleted")
                .rating(8.2)
                .build();
        var reviewId = this.reviewReactiveRepository.save(review).block().getReviewId();

        //when
        this.webTestClient.delete()
                .uri(REVIEW_URL + "/{reviewId}", reviewId)
                .exchange()
                .expectStatus().isOk();

        //then
        this.webTestClient.get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertNotNull(reviews);
                    assertFalse(reviews.isEmpty());
                    assertEquals(3, reviews.size());
                    reviews.forEach(review1 -> assertNotEquals(reviewId, review1.getReviewId()));
                });
    }

    @Test
    void getReviewsByMovieInfoIdTest() {
        //given
        var reviewId = 1L;
        var uri = UriComponentsBuilder.fromPath(REVIEW_URL).queryParam("movieInfoId", reviewId).buildAndExpand().toUri();

        //when
        this.webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertNotNull(reviews);
                    assertFalse(reviews.isEmpty());
                    assertEquals(1, reviews.size());
                    reviews.forEach(review -> assertEquals(reviewId, review.getMovieInfoId()));
                });
    }
}