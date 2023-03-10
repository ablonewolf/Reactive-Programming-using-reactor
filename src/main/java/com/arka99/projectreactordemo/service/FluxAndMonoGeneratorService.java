package com.arka99.projectreactordemo.service;

import com.arka99.projectreactordemo.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class FluxAndMonoGeneratorService {

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
//        subscribing to the name flux
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(System.out::println);
        System.out.println();
//        subscribing to the name mono
        fluxAndMonoGeneratorService.nameMono()
                .subscribe(System.out::println);

    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Arka", "Farhan", "Akif", "Nipa", "Zareen", "Mosfikur")).log();
    }

    public Mono<String> nameMono() {
        return Mono.just("Arka Bhuiyan").log();
    }

    public Mono<String> monoMap() {
        return Mono
                .just("Arka Bhuiyan")
                .map(String::toUpperCase)
                .log();
    }

    public Mono<String> monoMapWithFilter(Integer stringLength) {
        return Mono
                .just("Arka")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .defaultIfEmpty("default String")
                .log();
    }

    public Flux<String> nameFlux_map() {
        return Flux
                .fromIterable(List.of("Arka", "Farhan", "Akif", "Nipa", "Zareen", "Mosfikur"))
                .filter(name -> name.length() > 4)
                .map(String::toUpperCase)
                .doOnNext(System.out::println)
                .doOnSubscribe(System.out::println)
                .doOnComplete(() -> System.out.println("Inside the complete callback."))
                .doFinally(System.out::println)
                .log();
    }

    public Flux<String> nameFluxFlatMap(Integer stringLength) {
        Function<String, Flux<String>> splitName = name -> Flux.fromArray(name.split(""));
        return Flux
                .fromIterable(List.of("Arka", "Farhan", "Akif", "Nipa", "Zareen", "Mosfikur"))
                .filter(name -> name.length() > stringLength)
                .flatMap(splitName)
                .defaultIfEmpty("default String")
                .log();
    }

    public Flux<String> nameFluxTransform(Integer stringLength) {
        Function<String, Flux<String>> splitName = name -> Flux.fromArray(name.split(""));
        Function<Flux<String>, Flux<String>>
                transformFlux = name ->
                name.map(String::toUpperCase)
                        .filter(string -> string.length() > stringLength)
                        .flatMap(splitName);

        var defaultFlux = Flux
                .just("defaultString")
                .transform(transformFlux);

        return Flux
                .fromIterable(List.of("Arka", "Farhan", "Akif", "Nipa", "Zareen", "Mosfikur"))
                .transform(transformFlux)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> nameFluxFlatMapAsync(Integer stringLength) {
        return Flux
                .fromIterable(List.of("Arka", "Farhan", "Akif", "Nipa", "Zareen", "Mosfikur"))
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> nameFluxConcatMap(Integer stringLength) {
        return Flux
                .fromIterable(List.of("Arka", "Farhan", "Akif", "Nipa", "Zareen", "Mosfikur"))
                .filter(name -> name.length() > stringLength)
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    private Flux<String> splitStringWithDelay(String name) {
        Random random = new Random();
        return Flux.fromArray(name.split(""))
                .delayElements(Duration.ofMillis(random.nextInt(500)));
    }

    public Mono<List<String>> nameMonoFlatMap() {
        Function<String, Mono<List<String>>> splitName = name -> Mono.just(List.of(name.toUpperCase().split("")));

        return Mono
                .just("Arka Bhuiyan")
                .flatMap(splitName)
                .log();
    }

    public Mono<List<String>> nameMonoTransformWithFilter(Integer stringLength) {
        Function<String, Mono<List<String>>> splitName = name -> Mono.just(List.of(name.toUpperCase().split("")));
        Function<Mono<String>, Mono<List<String>>>
                transformMono = name ->
                name.map(String::toUpperCase)
                        .filter(string -> string.length() > stringLength)
                        .flatMap(splitName);

        var defaultMono = Mono
                .just("default")
                .transform(transformMono);

        return Mono
                .just("Arka")
                .transform(transformMono)
                .switchIfEmpty(defaultMono)
                .log();
    }

    public Flux<String> nameMonoFlatMapMany() {
        Function<String, Flux<String>> splitName = name -> Flux.fromArray(name.toUpperCase().split(""));
        return Mono
                .just("ArkaBhuiyan")
                .flatMapMany(splitName)
                .log();
    }

    public Flux<String> fluxConcat() {
        return Flux
                .concat(Flux.just("A", "B", "C")
                        , Flux.just("D", "E", "F")).log();
    }

    public Flux<String> fluxConcatWith() {
        return Flux
                .just("Arka", "Nipa")
                .concatWith(
                        Flux.
                                just("Farhan", "Akif")
                ).log();

    }

    public Flux<String> monoConcatWith() {
        return Mono
                .just("Arka")
                .concatWith(
                        Mono.just("Bhuiyan")
                ).log();
    }

    public Flux<String> fluxMerge() {
        var firstFlux = Flux.
                fromIterable(List.of(
                        "Arka",
                        "Farhan",
                        "Faiaz"
                )).
                delayElements(
                        Duration.ofMillis(150)
                );
        var secondFlux = Flux.
                fromIterable(List.of(
                        "Mosfik",
                        "Zareen",
                        "Ifti"
                )).
                delayElements(
                        Duration.ofMillis(200)
                );

        return Flux.merge(firstFlux, secondFlux).log();
    }

    public Flux<String> monoMerge() {
        var firstMono = Mono.just("Arka Bhuiyan")
                .delayElement(Duration.ofMillis(200));
        var secondMono = Mono.just("Farhan Zaman")
                .delayElement(Duration.ofMillis(500));

        return Flux.merge(firstMono, secondMono).log();
    }

    public Flux<String> monoMergeWith() {
        var firstMono = Mono.just("Arka Bhuiyan")
                .delayElement(Duration.ofMillis(200));
        var secondMono = Mono.just("Farhan Zaman")
                .delayElement(Duration.ofMillis(500));

        return firstMono.mergeWith(secondMono).log();
    }

    public Mono<String> monoZip() {
        var firstName = Mono.just("Arka");
        var secondName = Mono.just("Bhuiyan");

        return Mono.zip(firstName, secondName, (first, second) -> first + " " + second).log();

    }

    public Flux<String> fluxZip() {
        var firstNames = Flux.fromIterable(
                List.of(
                        "Arka",
                        "Farhan",
                        "Akif"
                )
        );
        var lastNames = Flux.fromIterable(
                List.of(
                        "Bhuiyan",
                        "Zaman",
                        "Azwad"
                )
        );
        var employeeIDs = Flux.fromIterable(
                List.of(
                        11512,
                        11514,
                        11507
                )
        );

        return Flux.zip(firstNames, lastNames, employeeIDs)
                .map(t4 -> t4.getT1() + " " + t4.getT2() + ", ID : " + t4.getT3())
                .log();
    }

    public Mono<String> monoZipWith() {
        var firstName = Mono.just("Arka");
        var lastName = Mono.just("Bhuiyan");

        return firstName.zipWith(lastName)
                .map(t2 -> t2.getT1() + " " + t2.getT2())
                .log();
    }

    public Flux<String> fluxZipWith() {
        var firstNames = Flux.fromIterable(
                List.of(
                        "Arka",
                        "Farhan",
                        "Akif"
                )
        );
        var lastNames = Flux.fromIterable(
                List.of(
                        "Bhuiyan",
                        "Zaman",
                        "Azwad"
                )
        );

        return firstNames.zipWith(lastNames)
                .map(t2 -> t2.getT1() + " " + t2.getT2())
                .log();

    }

    public Flux<String> exceptionFlux() {
        return Flux
                .just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("An Exception Occurred.")))
                .concatWith(Flux.just("D"))
                .log();
    }

    public Flux<String> exploreOnErrorReturn() {
        return Flux
                .just("a", "b", "c")
                .concatWith(Flux.error(new IllegalStateException("An Error Occurred.")))
                .onErrorReturn("d")
                .log();
    }

    public Flux<String> exploreOnErrorResume(Exception exception) {
        Function<Throwable, Flux<String>> errorResumeFunction = ex -> Flux.just(ex.getMessage());

        return Flux
                .just("a", "b", "c")
                .concatWith(Flux.error(exception))
                .onErrorResume(errorResumeFunction)
                .log();
    }

    public Flux<String> exploreOnErrorContinue() {
        Function<String, String> toUpperCase = name -> {
            if (name.equals("Nusaiba"))
                throw new IllegalStateException("Cannot change it to upper case");
            return name.toUpperCase();
        };
        BiConsumer<Throwable, Object> errorContinueFunction = (ex, message) -> System.out.println(ex + ". \nThe name is " + message);
        return Flux
                .just("Arka", "Nusaiba", "Farhan", "Zareen")
                .map(toUpperCase)
                .onErrorContinue(errorContinueFunction)
                .log();
    }

    public Mono<String> exploreMonoOnErrorContinue(String name) {
        Function<String, String> toUpperCase = string -> {
            if (string.equals("abc")) {
                throw new RuntimeException("An error occurred. abc cannot be accepted.");
            }
            return string.toUpperCase();
        };
        BiConsumer<Throwable, Object> errorContinueFunction = (ex, string) -> {
            log.error("Exception message is : {}", ex.getMessage());
            log.error("The String is : {}", string);
        };
        return Mono
                .just(name)
                .map(toUpperCase)
                .onErrorContinue(errorContinueFunction)
                .log();
    }

    public Flux<String> exploreOnErrorMap() {
        Function<String, String> toUpperCase = name -> {
            if (name.equals("Nusaiba"))
                throw new IllegalStateException("Cannot change it to upper case");
            return name.toUpperCase();
        };

        Function<Throwable, Throwable> convertException = (ex) ->
        {
            log.error("Exception is ", ex);
            return new ReactorException(ex, ex.getMessage());
        };
        return Flux
                .just("Arka", "Nusaiba", "Farhan", "Zareen")
                .map(toUpperCase)
                .onErrorMap(convertException)
                .log();
    }

    public Flux<String> exploreDoOnError() {
        Function<String, String> toUpperCase = name -> {
            if (name.equals("Nusaiba"))
                throw new IllegalStateException("Cannot change it to upper case");
            return name.toUpperCase();
        };
        Consumer<Throwable> printException = ex -> log.error(ex.getMessage());
        return Flux
                .just("Arka", "Nusaiba", "Farhan", "Zareen")
                .map(toUpperCase)
                .doOnError(printException)
                .log();
    }


}
