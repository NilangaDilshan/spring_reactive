package com.reactivespring.review;

import com.reactivespring.review.domain.Review;
import com.reactivespring.review.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
@Slf4j
public class ReviewReactiveRepositoryTest extends AbstractMongodbBaseTest {
    @Autowired
    private ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUpBeforeEach() {
        log.info("setUpBeforeEach");
        this.reviewReactiveRepository.saveAll(
                List.of(Review.builder().movieInfoId(1L).comment("Batman Begins").rating(8.2).build(),
                        Review.builder().movieInfoId(2L).comment("The Dark Knight").rating(9.2).build(),
                        Review.builder().movieInfoId(3L).comment("The Dark Knight Rises").rating(8.9).build())
        ).blockLast();
    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
        this.reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void findAllTest() {
        var reviews = this.reviewReactiveRepository.findAll().log();

        //then
        StepVerifier.create(reviews)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findByMovieInfoIdTest() {
        var reviews = this.reviewReactiveRepository.findByMovieInfoId(1L).log();

        //then
        StepVerifier.create(reviews)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

}
