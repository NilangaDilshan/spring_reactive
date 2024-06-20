package com.reactivespring.review.router;

import com.reactivespring.review.domain.Review;
import com.reactivespring.review.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/*@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Slf4j*/
//--------------------------------
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
//--------------------------------
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
}