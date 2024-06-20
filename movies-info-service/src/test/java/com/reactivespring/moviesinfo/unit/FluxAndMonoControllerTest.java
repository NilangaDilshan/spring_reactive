package com.reactivespring.moviesinfo.unit;

import com.reactivespring.moviesinfo.controller.FluxAndMonoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
//@ContextConfiguration(classes = FluxAndMonoController.class)
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void fluxApproach2() {
        //given
        //when
        var flux = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();
            /*    .expectBodyList(Integer.class)
                .hasSize(4)
                .contains(1, 2, 3, 4);*/

        //then
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void fluxApproach3() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> assertEquals(4, response.getResponseBody().size()));
    }

    @Test
    void mono() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                //.isEqualTo("Hello World!");
                .consumeWith(response -> assertEquals("Hello World!", response.getResponseBody()));
    }

    @Test
    void stream() {
        var longFlux = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFlux)
                .expectNext(0L, 1L, 2L)
                /*  .expectNext(1L)
                  .expectNext(2L)*/
                .thenCancel()
                .verify();
    }
}