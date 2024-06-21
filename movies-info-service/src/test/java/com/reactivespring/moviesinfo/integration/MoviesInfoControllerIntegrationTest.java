package com.reactivespring.moviesinfo.integration;

import com.reactivespring.moviesinfo.domain.MovieInfo;
import com.reactivespring.moviesinfo.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Slf4j
class MoviesInfoControllerIntegrationTest extends AbstractMongodbBaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    private static final String MOVIES_INFO_URL = "/v1/movies-info";

    @BeforeEach
    void setUpBeforeEach() {
        log.info("setUpBeforeEach");
        this.movieInfoRepository.saveAll(
                List.of(
                        new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"), LocalDate.of(2005, 6, 15)),
                        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"), LocalDate.of(2008, 7, 18)),
                        new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy", "Anne Hathaway"), LocalDate.of(2012, 7, 20))
                )
        ).blockLast();
    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
        this.movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfoTest() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));

        //when
        var movieInfoResponse = webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotNull(movieInfoResponse);
        assertNotNull(movieInfoResponse.getMovieInfoId());
        assertEquals(movieInfo.getName(), movieInfoResponse.getName());
        assertEquals(movieInfo.getYear(), movieInfoResponse.getYear());
        assertEquals(movieInfo.getCast(), movieInfoResponse.getCast());
        assertEquals(movieInfo.getRelease_date(), movieInfoResponse.getRelease_date());
    }

    @Test
    void getAllMoviesInfoTest() {
        //when
        var moviesInfoResponse = webTestClient.get()
                .uri(MOVIES_INFO_URL + "/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotNull(moviesInfoResponse);
        assertEquals(3, moviesInfoResponse.size());
    }

    @Test
    void getAllMoviesInfoByYearTest() {
        //given
        var year = 2005;
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL + "/all").queryParam("year", year).buildAndExpand().toUri();
        //when
        var moviesInfoResponse = webTestClient.get()
                //.uri(MOVIES_INFO_URL + "/year/{year}", year)
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotNull(moviesInfoResponse);
        assertEquals(1, moviesInfoResponse.size());
        assertEquals(year, moviesInfoResponse.get(0).getYear());
        assertEquals("Batman Begins", moviesInfoResponse.get(0).getName());

    }

    @Test
    void getMovieInfoByIdTest() {
        //given
        this.movieInfoRepository.deleteAll().block();
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        var savedMovieInfo = movieInfoRepository.save(movieInfo).block();

        //when
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{movieId}", savedMovieInfo.getMovieInfoId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo(savedMovieInfo.getMovieInfoId())
                .jsonPath("$.name").isEqualTo(savedMovieInfo.getName())
                .jsonPath("$.year").isEqualTo(savedMovieInfo.getYear())
                .jsonPath("$.cast").isArray()
                .jsonPath("$.cast[0]").isEqualTo("Christian Bale")
                .jsonPath("$.cast[1]").isEqualTo("Michael Caine")
                .jsonPath("$.cast[2]").isEqualTo("Liam Neeson")
                .jsonPath("$.release_date").isEqualTo(savedMovieInfo.getRelease_date().toString());
        //then
    }

    @Test
    void getMovieInfoByIdNotFoundTest() {
        //given
        this.movieInfoRepository.deleteAll().block();
        var id = "test";

        //when
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{movieId}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateMovieInfoTest() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        var savedMovieInfo = movieInfoRepository.save(movieInfo).block();

        var updatedMovieInfo = new MovieInfo(null, "Batman Begins Updated", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));

        //when
        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{movieId}", savedMovieInfo.getMovieInfoId())
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(savedMovieInfo.getMovieInfoId(), responseBody.getMovieInfoId());
                    assertEquals(updatedMovieInfo.getName(), responseBody.getName());
                    assertEquals(updatedMovieInfo.getYear(), responseBody.getYear());
                    assertEquals(updatedMovieInfo.getCast(), responseBody.getCast());
                    assertEquals(updatedMovieInfo.getRelease_date(), responseBody.getRelease_date());
                });

        //then
    }

    @Test
    void updateMovieInfoNotFoundTest() {
        //given
        var updatedMovieInfo = new MovieInfo(null, "Batman Begins Updated", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));

        //when
        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{movieId}", "test")
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus().isNotFound();

        //then
    }

    @Test
    void deleteMovieInfoTest() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        var savedMovieInfo = movieInfoRepository.save(movieInfo).block();

        //when
        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{movieId}", savedMovieInfo.getMovieInfoId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        //then
        var movieInfoMono = movieInfoRepository.findById(savedMovieInfo.getMovieInfoId());
        StepVerifier.create(movieInfoMono)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void getMovieInfoByNameTest() {
        //given
        var name = "Batman Begins";
        //when
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/name/{name}", name)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(name)
                .jsonPath("$.year").isEqualTo(2005)
                .jsonPath("$.cast").isArray()
                .jsonPath("$.cast[0]").isEqualTo("Christian Bale")
                .jsonPath("$.cast[1]").isEqualTo("Michael Caine")
                .jsonPath("$.cast[2]").isEqualTo("Liam Neeson")
                .jsonPath("$.release_date").isEqualTo("2005-06-15");
        //then
    }
}