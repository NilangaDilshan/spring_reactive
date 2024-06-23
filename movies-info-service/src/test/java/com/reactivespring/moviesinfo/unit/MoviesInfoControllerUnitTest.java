package com.reactivespring.moviesinfo.unit;

import com.reactivespring.moviesinfo.controller.MoviesInfoController;
import com.reactivespring.moviesinfo.domain.MovieInfo;
import com.reactivespring.moviesinfo.service.MoviesInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
//@ContextConfiguration(classes = MoviesInfoController.class)
@Slf4j
public class MoviesInfoControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private MoviesInfoService moviesInfoService;

    private static final String MOVIES_INFO_URL = "/v1/movies-info";

    @Test
    void getAllMoviesTest() {
        //given
        when(moviesInfoService.getAllMovies()).thenReturn(Flux.just(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"), LocalDate.of(2005, 6, 15)),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"), LocalDate.of(2008, 7, 18)),
                new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy", "Anne Hathaway"), LocalDate.of(2012, 7, 20))
        ));

        //when
        var response = webTestClient.get()
                .uri(MOVIES_INFO_URL + "/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotEquals(null, response);
        assertEquals(3, response.size());
    }

    @Test
    void getMovieInfoByIdTest() {
        //given
        var movieId = "1";
        when(moviesInfoService.getMovieInfoById(movieId)).thenReturn(Mono.just(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"), LocalDate.of(2005, 6, 15))
        ));

        //when
        var response = webTestClient.get()
                .uri(MOVIES_INFO_URL + "/" + movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .returnResult()
                .getResponseBody();

        //then
        assertNotEquals(null, response);
        assertEquals(1, response.size());
        assertEquals("Batman Begins", response.get(0).getName());
        assertEquals(2005, response.get(0).getYear());
        assertEquals(List.of("Christian Bale", "Michael Caine", "Liam Neeson"), response.get(0).getCast());
        assertEquals(LocalDate.of(2005, 6, 15), response.get(0).getRelease_date());
    }

    @Test
    void addMovieInfoTest() {
        //given
        String id = "1";
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        when(moviesInfoService.addMovie(isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo(id, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                        LocalDate.of(2005, 6, 15))
        ));

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
        assert movieInfoResponse != null;
        assert movieInfoResponse.getMovieInfoId().equals(id);
        assert movieInfoResponse.getName().equals(movieInfo.getName());
        assert movieInfoResponse.getYear().equals(movieInfo.getYear());
        assert movieInfoResponse.getCast().equals(movieInfo.getCast());
        assert movieInfoResponse.getRelease_date().equals(movieInfo.getRelease_date());
    }

    @Test
    void addMovieInfoValidationTest() {
        //given
        String id = "1";
        /*var movieInfo = new MovieInfo(null, "", -2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));*/
        var movieInfo = new MovieInfo(null, "", -2005, List.of(),
                LocalDate.of(2005, 6, 15));
        when(moviesInfoService.addMovie(isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo(id, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                        LocalDate.of(2005, 6, 15))
        ));

        //when
        webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    log.info("Response: {}", response.getResponseBody());
                    var expectedErrorMessages = "movieInfo.cast : must be present,movieInfo.name : must be present,movieInfo.year : must be positive";
                    assertNotNull(response.getResponseBody());
                    assertEquals(expectedErrorMessages, response.getResponseBody());
                });
    }

    @Test
    void updateMovieInfoTest() {
        //given
        String id = "1";
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        when(moviesInfoService.updateMovieInfo(isA(String.class), isA(MovieInfo.class))).thenReturn(Mono.just(
                new MovieInfo(id, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                        LocalDate.of(2005, 6, 15))
        ));

        //when
        var movieInfoResponse = webTestClient.put()
                .uri(MOVIES_INFO_URL + "/" + id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void deleteMovieInfoTest() {
        //given
        String id = "1";
        when(moviesInfoService.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        //when
        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
