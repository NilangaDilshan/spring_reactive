package com.reactivespring.moviesinfo.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;

@Slf4j
public class SinksTest {

    @Test
    void sinkTest() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        //when
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_1: {}", integer));
        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_2: {}", integer));

        replaySink.tryEmitNext(3);

        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_3: {}", integer));
    }

    @Test
    void sinkMultiCastTest() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().multicast().onBackpressureBuffer();

        //when
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_1: {}", integer));
        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_2: {}", integer));


        replaySink.tryEmitNext(3);

        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_3: {}", integer));

        replaySink.tryEmitNext(4);
    }

    @Test
    void sinkUniCastTest() {
        //given
        Sinks.Many<Integer> replaySink = Sinks.many().unicast().onBackpressureBuffer();

        //when
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then
        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_1: {}", integer));
        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_2: {}", integer));


        replaySink.tryEmitNext(3);

        replaySink.asFlux()
                .subscribe(integer -> log.info("Received_IN_Channel_3: {}", integer));

        replaySink.tryEmitNext(4);
    }
}
