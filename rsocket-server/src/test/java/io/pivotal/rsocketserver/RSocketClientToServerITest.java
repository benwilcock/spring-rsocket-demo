package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RSocketClientToServerITest {

    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketStrategies strategies, @Value("${spring.rsocket.server.port}") Integer port) {
        requester = RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .connectTcp("localhost", port)
                .block();
    }

    @Test
    public void testRequestGetsResponse() {
        // Send a request message
        Mono<Message> result = requester
                .route("request-response")
                .data(new Message("TEST", "Request"))
                .retrieveMono(Message.class);

        // Verify that the response message contains the expected data
        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.RESPONSE);
                    assertThat(message.getIndex()).isEqualTo(0);
                })
                .verifyComplete();
    }

    @Test
    public void TestFireAndForget() {
        // Send a fire-and-forget message
        Mono<Message> result = requester
                .route("fire-and-forget")
                .data(new Message("TEST", "Fire-And-Forget"))
                .retrieveMono(Message.class);

        // Assert that the result is a completed Mono.
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    public void testRequestGetsStream() {
        // Send a request message
        Flux<Message> result = requester
                .route("stream")
                .data(new Message("TEST", "Stream"))
                .retrieveFlux(Message.class);

        // Verify that the response messages contain the expected data
        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.STREAM);
                    assertThat(message.getIndex()).isEqualTo(0L);
                })
                .expectNextCount(3)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.STREAM);
                    assertThat(message.getIndex()).isEqualTo(4L);
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void testStreamGetsStream() {
        Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(4)).delayElement(Duration.ofSeconds(0));
        Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(1)).delayElement(Duration.ofSeconds(5));
        Flux<Duration> settings = Flux.concat(setting1, setting2);

        // Send a stream of request messages
        Flux<Message> result = requester
                .route("channel")
                .data(settings)
                .retrieveFlux(Message.class);

        // Verify that the response messages contain the expected data
        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.CHANNEL);
                    assertThat(message.getIndex()).isEqualTo(0L);
                })
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo(RSocketController.CHANNEL);
                    assertThat(message.getIndex()).isEqualTo(0L);
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void testNoMatchingRouteGetsException() {
        // Send a request with bad route and data
        Mono<String> result = requester
                .route("invalid")
                .data("anything")
                .retrieveMono(String.class);

        // Verify that an error is generated
        StepVerifier.create(result)
                .expectErrorMessage("No handler for destination 'invalid'")
                .verify(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownOnce() {
        requester.rsocket().dispose();
    }
}