package com.reactivespring.reactor.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
class FluxAndMonoGeneratorServiceTest {
    private FluxAndMonoGeneratorService fluxAndMonoGeneratorService;

    @BeforeEach
    void setUp() {
        fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("Adam")
                .expectNext("Anna")
                .expectNext("Jack")
                .expectNext("Jenny")
                .verifyComplete();
        StepVerifier.create(namesFlux)
                .expectNextCount(4)
                .verifyComplete();
        StepVerifier.create(namesFlux)
                .expectNext("Adam")
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void namesFluxMapTest() {
        //given
        var expectedNames = List.of("ADAM", "ANNA", "JACK", "JENNY");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxMap();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void namesFluxImmutabilityTest() {
        //given
        var expectedNames = List.of("Adam", "Anna", "Jack", "Jenny");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxImmutability();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void namesFluxFilterTest() {
        //given
        int stringLength = 4;

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxFilter(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    //add test method for namesFluxFlatMap() method
    @Test
    void namesFluxFlatMapTest() {
        //given
        var expectedNames = List.of("A", "d", "a", "m", "A", "n", "n", "a", "J", "a", "c", "k", "J", "e", "n", "n", "y");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxFlatMap();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        //given
        //var expectedNames = List.of("A", "d", "a", "m", "A", "n", "n", "a", "J", "a", "c", "k", "J", "e", "n", "n", "y");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxFlatMapAsync();

        //then
        StepVerifier.create(namesFlux)
                //.expectNextSequence(expectedNames)
                .expectNextCount(17)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {
        //given
        var expectedNames = List.of("A", "d", "a", "m", "A", "n", "n", "a", "J", "a", "c", "k", "J", "e", "n", "n", "y");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxConcatMap();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void namesFluxTransformTest() {
        //given
        int stringLength = 4;
        var expectedNames = List.of("JENNY");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void namesFluxDefaultIfEmptyTest() {
        //given
        int stringLength = 6;
        var expectedNames = List.of("default");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxDefaultIfEmpty(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void namesFluxSwitchIfEmptyTest() {
        //given
        int stringLength = 6;
        var expectedNames = List.of("DEFAULT");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.namesFluxSwitchIfEmpty(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreConcatTest() {
        //given
        var expectedNames = List.of("Adam", "Anna", "Jack", "Jenny");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreConcat();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreConcatWithFluxTest() {
        //given
        var expectedNames = List.of("Adam", "Anna", "Jack", "Jenny");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreConcatWithFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMonoTest() {
        //given
        var expectedNames = List.of("Adam", "Anna");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreConcatWithMono();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreMergeTest() {
        //given
        var expectedNames = List.of("Adam", "Jack", "Anna", "Jenny", "Ava", "Jerry");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreMerge();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreMergeWithTest() {
        //given
        var expectedNames = List.of("Adam", "Jack", "Anna", "Jenny", "Ava", "Jerry");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreMergeWith();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMonoTest() {
        //given
        var expectedNames = List.of("Adam", "Jack");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreMergeWithMono();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreMergeSequentialTest() {
        //given
        var expectedNames = List.of("Adam", "Anna", "Ava", "Jack", "Jenny", "Jerry");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreMergeSequential();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreZipTest() {
        //given
        var expectedNames = List.of("Adam Jack", "Anna Jenny", "Ava Jerry");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreZip();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreZipMapTest() {
        //given
        var expectedNames = List.of("Adam Jack Slayer", "Anna Jenny Metallica", "Ava Jerry Megadeth");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreZipMap();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreZipWithTest() {
        //given
        var expectedNames = List.of("Adam Jack", "Anna Jenny", "Ava Jerry");

        //when
        var namesFlux = this.fluxAndMonoGeneratorService.exploreZipWith();

        //then
        StepVerifier.create(namesFlux)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void nameMono() {
    }

    //create test method for nameMonoFlatMap() method
    @Test
    void nameMonoFlatMap() {
        //given
        var stringLength = 4;
        var expectedNames = List.of("S", "L", "A", "Y", "E", "R");

        //when
        var namesMono = this.fluxAndMonoGeneratorService.nameMonoFlatMap(stringLength);

        //then
        StepVerifier.create(namesMono)
                .expectNext(expectedNames)
                .verifyComplete();
    }

    @Test
    public void test_converts_slayer_to_uppercase() {
        // Given
        int stringLength = 4;
        var expectedNames = List.of("S", "L", "A", "Y", "E", "R");
        // When
        var result = this.fluxAndMonoGeneratorService.nameMonoFlatMapMany(stringLength);

        // Then
        StepVerifier.create(result)
                .expectNextSequence(expectedNames)
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilterTest() {
        //given
        var stringLength = 4;
        var expectedNames = "SLAYER";

        //when
        var namesMono = this.fluxAndMonoGeneratorService.nameMonoMapFilter(stringLength);

        //then
        StepVerifier.create(namesMono)
                .expectNext(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreZipMapMonoTest() {
        //given
        var expectedNames = "Adam Jack Slayer";

        //when
        var namesMono = this.fluxAndMonoGeneratorService.exploreZipMapMono();

        //then
        StepVerifier.create(namesMono)
                .expectNext(expectedNames)
                .verifyComplete();
    }

    @Test
    void exploreZipWithMonoTest() {
        //given
        var expectedNames = "Adam Jack";

        //when
        var namesMono = this.fluxAndMonoGeneratorService.exploreZipWithMono();

        //then
        StepVerifier.create(namesMono)
                .expectNext(expectedNames)
                .verifyComplete();
    }
}