package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.Message;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class RSocketControllerTest {

    private static AnnotationConfigApplicationContext context;

    private static CloseableChannel server;

    private static RSocketRequester requester;

    @BeforeAll
    @SuppressWarnings("ConstantConditions")
    public static void setupOnce() {

        MimeType metadataMimeType = MimeTypeUtils.parseMimeType(
                WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.getString());

        context = new AnnotationConfigApplicationContext(ServerConfig.class);
        RSocketMessageHandler messageHandler = context.getBean(RSocketMessageHandler.class);
        SocketAcceptor responder = messageHandler.responder();

        server = RSocketServer.create(responder)
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .bind(TcpServerTransport.create("localhost", 7001))
                .block();

        requester = RSocketRequester.builder()
                .metadataMimeType(metadataMimeType)
                .rsocketStrategies(context.getBean(RSocketStrategies.class))
                .connectTcp("localhost", 7001)
                .block();
    }

    @Test
    public void voidReturnValue() {
        Mono<Message> result = requester
                .route("request-response")
                .data(new Message("TEST","REQUEST"))
                .retrieveMono(Message.class);

        StepVerifier
                .create(result)
                .consumeNextWith(message -> {
                    assertThat(message.getOrigin()).isEqualTo(RSocketController.SERVER);
                    assertThat(message.getInteraction()).isEqualTo("Response");
                    assertThat(message.getIndex()).isEqualTo(0);
                })
                .verifyComplete();
    }

    @Test
    public void noMatchingRoute() {
        Mono<String> result = requester.route("invalid").data("anything").retrieveMono(String.class);
        StepVerifier.create(result)
                .expectErrorMessage("No handler for destination 'invalid'")
                .verify(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownOnce() {
        requester.rsocket().dispose();
        server.dispose();
    }

    @Configuration
    static class ServerConfig {

        @Bean
        public RSocketController controller() {
            return new RSocketController();
        }

        @Bean
        public RSocketMessageHandler messageHandler() {
            RSocketMessageHandler handler = new RSocketMessageHandler();
            handler.setRSocketStrategies(rsocketStrategies());
            return handler;
        }

        @Bean
        public RSocketStrategies rsocketStrategies() {
            return RSocketStrategies.builder().encoder(new Jackson2CborEncoder()).decoder(new Jackson2CborDecoder()).build();
        }
    }
}
