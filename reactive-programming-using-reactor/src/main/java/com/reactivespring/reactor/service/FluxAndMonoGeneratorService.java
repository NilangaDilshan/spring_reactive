package com.reactivespring.reactor.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny")).log();
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFluxImmutability() {
        var namesflux = Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"));
        namesflux.map(String::toUpperCase);
        return namesflux;
    }

    public Flux<String> namesFluxFilter(int stringLength) {
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .filter(name -> name.length() == stringLength)
                .log();
    }

    public Flux<String> namesFluxFlatMap() {
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .flatMap(this::splitString)
                .log();
    }

    private Flux<String> splitString(String name) {
        return Flux.fromArray(name.split(""));
    }

    public Flux<String> namesFluxFlatMapAsync() {
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    private Flux<String> splitStringWithDelay(String name) {
        //var delay = new Random().nextInt(1000);
        var delay = 1000;
        return Flux.fromArray(name.split(""))
                .delayElements(java.time.Duration.ofMillis(delay));
    }

    public Flux<String> namesFluxConcatMap() {
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> function =
                nameFlux -> nameFlux.filter(name -> name.length() > stringLength).map(String::toUpperCase);
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .transform(function)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxDefaultIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> function =
                nameFlux -> nameFlux.filter(name -> name.length() > stringLength).map(String::toUpperCase);
        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .transform(function)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxSwitchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> function =
                nameFlux -> nameFlux.filter(name -> name.length() > stringLength)
                        .map(String::toUpperCase);

        var defaultFlux = Flux.just("default").transform(function).log();

        return Flux.fromIterable(List.of("Adam", "Anna", "Jack", "Jenny"))
                .transform(function)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> exploreConcat() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna"));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny"));
        return Flux.concat(namesFlux1, namesFlux2).log();
    }

    public Flux<String> exploreConcatWithFlux() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna"));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny"));
        return namesFlux1.concatWith(namesFlux2).log();
    }

    public Flux<String> exploreConcatWithMono() {
        var namesMono1 = Mono.just("Adam");
        var namesMono2 = Mono.just("Anna");
        return namesMono1.concatWith(namesMono2).log();
    }

    public Flux<String> exploreMerge() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna", "Ava"))
                .delayElements(java.time.Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny", "Jerry"))
                .delayElements(java.time.Duration.ofMillis(125));
        return Flux.merge(namesFlux1, namesFlux2).log();
    }

    public Flux<String> exploreMergeWith() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna", "Ava"))
                .delayElements(java.time.Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny", "Jerry"))
                .delayElements(java.time.Duration.ofMillis(125));
        return namesFlux1.mergeWith(namesFlux2).log();
    }

    public Flux<String> exploreMergeWithMono() {
        var namesMono_1 = Mono.just("Adam");
        var namesMono_2 = Mono.just("Jack");
        return namesMono_1.mergeWith(namesMono_2).log();
    }

    public Flux<String> exploreMergeSequential() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna", "Ava"))
                .delayElements(java.time.Duration.ofMillis(100));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny", "Jerry"))
                .delayElements(java.time.Duration.ofMillis(125));
        return Flux.mergeSequential(namesFlux1, namesFlux2).log();
    }

    public Flux<String> exploreZip() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna", "Ava"));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny", "Jerry"));
        return Flux.zip(namesFlux1, namesFlux2, (name1, name2) -> name1 + " " + name2).log();
    }

    public Flux<String> exploreZipMap() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna", "Ava"));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny", "Jerry"));
        var namesFlux3 = Flux.fromIterable(List.of("Slayer", "Metallica", "Megadeth"));
        return Flux.zip(namesFlux1, namesFlux2, namesFlux3)
                .map(tuple -> tuple.getT1() + " " + tuple.getT2() + " " + tuple.getT3())
                .log();
    }

    public Flux<String> exploreZipWith() {
        var namesFlux1 = Flux.fromIterable(List.of("Adam", "Anna", "Ava"));
        var namesFlux2 = Flux.fromIterable(List.of("Jack", "Jenny", "Jerry"));
        return namesFlux1.zipWith(namesFlux2, (name1, name2) -> name1 + " " + name2).log();
    }

    public Mono<String> nameMono() {
        return Mono.just("Slayer").log();
    }

    public Mono<String> nameMonoMapFilter(int stringLength) {
        return Mono.just("Slayer")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }

    public Mono<List<String>> nameMonoFlatMap(int stringLength) {
        return Mono.just("Slayer")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var splitString = s.split("");
        return Mono.just(List.of(splitString));
    }

    public Flux<String> nameMonoFlatMapMany(int stringLength) {
        return Mono.just("Slayer")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    public Mono<String> exploreZipMapMono() {
        var namesMono1 = Mono.just("Adam");
        var namesMono2 = Mono.just("Jack");
        var namesMono3 = Mono.just("Slayer");
        return Mono.zip(namesMono1, namesMono2, namesMono3)
                .map(tuple -> tuple.getT1() + " " + tuple.getT2() + " " + tuple.getT3())
                .log();
    }

    public Mono<String> exploreZipWithMono() {
        var namesMono1 = Mono.just("Adam");
        var namesMono2 = Mono.just("Jack");
        return namesMono1.zipWith(namesMono2, (name1, name2) -> name1 + " " + name2).log();
    }


    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> log.info("Flux Name is: {}", name));
        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> log.info("Mono Name is: {}", name));
    }
}
