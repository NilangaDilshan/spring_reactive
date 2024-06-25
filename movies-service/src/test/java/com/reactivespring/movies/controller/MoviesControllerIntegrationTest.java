package com.reactivespring.movies.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.movies.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084) //spin up a httpserver on port 8084
@TestPropertySource(properties =
        {"restClient.reviewsInfoUrl=http://localhost:8084/v1/reviews",
                "restClient.moviesInfoUrl=http://localhost:8084/v1/movies-info"})
@Slf4j
class MoviesControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }

    @Test
    void retrieveMovieById() {
        //given
        var movieId = "1";
        stubFor(get(urlEqualTo("/v1/movies-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                //.expectBody(Movie.class)
                .expectBody()
                .jsonPath("$.movieInfo.movieInfoId").isEqualTo("1")
                .jsonPath("$.movieInfo.name").isEqualTo("Batman Begins")
                .jsonPath("$.reviews.length()").isEqualTo(2)
                .jsonPath("$.reviews[0].reviewId").isEqualTo("1")
                .jsonPath("$.reviews[0].movieInfoId").isEqualTo("1")
                .jsonPath("$.reviews[0].comment").isEqualTo("Awesome Movie")
                .jsonPath("$.reviews[0].rating").isEqualTo("9.0")
                .jsonPath("$.reviews[1].reviewId").isEqualTo("2")
                .jsonPath("$.reviews[1].movieInfoId").isEqualTo("1")
                .jsonPath("$.reviews[1].comment").isEqualTo("Excellent Movie")
                .jsonPath("$.reviews[1].rating").isEqualTo("8.0");

    }

    @Test
    void retrieveMovieById_4XX() {
        //given
        var movieId = "1";
        var retryCount = 1;
        stubFor(get(urlEqualTo("/v1/movies-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Movie not found for movie ID: %s".formatted(movieId));

        WireMock.verify(retryCount, getRequestedFor(urlEqualTo("/v1/movies-info" + "/" + movieId)));
    }

    @Test
    void retrieveReviewById_4XX() {
        //given
        var movieId = "1";

        stubFor(get(urlEqualTo("/v1/movies-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(response -> {
                    var movie = response.getResponseBody();
                    assertNotNull(movie);
                    assertEquals("1", movie.getMovieInfo().getMovieInfoId());
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertTrue(movie.getReviews().isEmpty());
                });
    }

    @Test
    void retrieveMovieById_5XX() {
        //given
        var movieId = "1";
        var retryCount = 4;
        var serverErrorMessage = "Please try again later";
        stubFor(get(urlEqualTo("/v1/movies-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(serverErrorMessage)
                ));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in MoviesInfoService: %s".formatted(serverErrorMessage));

        WireMock.verify(retryCount, getRequestedFor(urlEqualTo("/v1/movies-info" + "/" + movieId)));
    }

    @Test
    void retrieveMovieByIdReviews_5XX() {
        //given
        var movieId = "1";
        var retryCount = 4;
        var reviewServerErrorMessage = "Review Service Not Available. Please try again later.";

        stubFor(get(urlEqualTo("/v1/movies-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody(reviewServerErrorMessage)));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in ReviewsService: %s".formatted(reviewServerErrorMessage));

        WireMock.verify(retryCount, getRequestedFor(urlPathMatching("/v1/reviews*")));
    }

}