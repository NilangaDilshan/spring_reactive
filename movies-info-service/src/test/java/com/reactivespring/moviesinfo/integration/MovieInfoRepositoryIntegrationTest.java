package com.reactivespring.moviesinfo.integration;

import com.reactivespring.moviesinfo.domain.MovieInfo;
import com.reactivespring.moviesinfo.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@DataMongoTest
@ActiveProfiles("test")
@Slf4j
class MovieInfoRepositoryIntegrationTest extends AbstractMongodbBaseTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUpBeforeEach() {
        log.info("setUpBeforeEach");
    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
        this.movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAllTest() {
        //given
        this.movieInfoRepository.saveAll(
                List.of(
                        new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"), LocalDate.of(2005, 6, 15)),
                        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"), LocalDate.of(2008, 7, 18)),
                        new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy", "Anne Hathaway"), LocalDate.of(2012, 7, 20))
                )
        ).blockLast();

        //when
        //var moviesInfoFlux = movieInfoRepository.findAll().log().collectList().block();
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
       /* assertNotNull(moviesInfoFlux);
        assertEquals(3, moviesInfoFlux.size());*/
        StepVerifier.create(moviesInfoFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findByIdTest() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        var savedMovieInfo = movieInfoRepository.save(movieInfo).block();

        //when
        var movieInfoMono = movieInfoRepository.findById(savedMovieInfo.getMovieInfoId()).log();

        //then
        StepVerifier.create(movieInfoMono)
                .expectSubscription()
                /* .expectNextMatches(movieInfo1 -> movieInfo1.getName().equals("Batman Begins"))*/
                .assertNext(movieInfo1 -> {
                    Assertions.assertEquals("Batman Begins", movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveTest() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));

        //when
        var savedMovieInfo = movieInfoRepository.save(movieInfo).log();

        //then
        StepVerifier.create(savedMovieInfo)
                .expectSubscription()
                .assertNext(movieInfo1 -> {
                    Assertions.assertNotEquals(null, movieInfo1.getMovieInfoId());
                    Assertions.assertEquals("Batman Begins", movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateTest() {
        //given
        var updatedMovieName = "Batman Begins Updated";
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        var savedMovieInfo = movieInfoRepository.save(movieInfo).block();

        //when
        savedMovieInfo.setName(updatedMovieName);
        var updatedMovieInfo = movieInfoRepository.save(savedMovieInfo).log();

        //then
        StepVerifier.create(updatedMovieInfo)
                .expectSubscription()
                .assertNext(movieInfo1 -> {
                    Assertions.assertEquals(savedMovieInfo.getMovieInfoId(), movieInfo1.getMovieInfoId());
                    Assertions.assertEquals(updatedMovieName, movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void deleteTest() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"),
                LocalDate.of(2005, 6, 15));
        var savedMovieInfo = movieInfoRepository.save(movieInfo).block();

        //when
        movieInfoRepository.deleteById(savedMovieInfo.getMovieInfoId()).block();

        //then
        var movieInfoMono = movieInfoRepository.findById(savedMovieInfo.getMovieInfoId()).log();
        StepVerifier.create(movieInfoMono)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByYearTest() {
        //given
        this.movieInfoRepository.saveAll(
                List.of(
                        new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"), LocalDate.of(2005, 6, 15)),
                        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"), LocalDate.of(2008, 7, 18)),
                        new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy", "Anne Hathaway"), LocalDate.of(2012, 7, 20))
                )
        ).blockLast();

        //when
        var moviesInfoFlux = movieInfoRepository.findByYear(2008).log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findMovieByNameTest() {
        //given
        this.movieInfoRepository.saveAll(
                List.of(
                        new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Caine", "Liam Neeson"), LocalDate.of(2005, 6, 15)),
                        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger", "Aaron Eckhart"), LocalDate.of(2008, 7, 18)),
                        new MovieInfo(null, "The Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy", "Anne Hathaway"), LocalDate.of(2012, 7, 20))
                )
        ).blockLast();

        //when
        var movieInfoMono = movieInfoRepository.findMovieByName("The Dark Knight").log();

        //then
        StepVerifier.create(movieInfoMono)
                .expectSubscription()
                .assertNext(movieInfo1 -> {
                    Assertions.assertEquals("The Dark Knight", movieInfo1.getName());
                })
                .verifyComplete();
    }
}