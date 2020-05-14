package io.pivotal.rsocketclient;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class RSocketServerToClientIT {

    private static String clientId = UUID.randomUUID().toString();

    private static AnnotationConfigApplicationContext context;

    private static CloseableChannel server;


    @BeforeAll
    @SuppressWarnings("ConstantConditions")
    public static void setupOnce() {

        context = new AnnotationConfigApplicationContext(ServerConfig.class);
        RSocketMessageHandler messageHandler = context.getBean(RSocketMessageHandler.class);
        SocketAcceptor responder = messageHandler.responder();

        server = RSocketServer.create(responder)
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .bind(TcpServerTransport.create("localhost", 0))
                .block();
    }

    @AfterAll
    public static void tearDownOnce() {
        server.dispose();
    }

    @Test
    public void testServerCallsClientAfterConnection() {
        connectAndRunTest("shell-client");
    }

    private void connectAndRunTest(String connectionRoute) {

        context.getBean(ServerController.class).reset();
        RSocketStrategies strategies = context.getBean(RSocketStrategies.class);
        SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

        RSocketRequester requester = null;
        try {
            requester = RSocketRequester.builder()
                    .setupRoute(connectionRoute)
                    .setupData(clientId)
                    .rsocketStrategies(strategies)
                    .rsocketConnector(connector -> connector.acceptor(responder))
                    .connectTcp("localhost", server.address().getPort())
                    .block();

            context.getBean(ServerController.class).await(Duration.ofSeconds(10));
        } finally {
            if (requester != null) {
                requester.rsocket().dispose();
            }
        }
    }

    @Controller
//    @SuppressWarnings({"unused", "NullableProblems"})
    static class ServerController {

        // Must be initialized by @Test method...
        volatile MonoProcessor<Object> result;


        public void reset() {
            this.result = MonoProcessor.create();
        }

        public void await(Duration duration) {
            this.result.block(duration);
        }

        @ConnectMapping("shell-client")
        void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String client) {

            log.info("************** CONNECTION - Client ID: {}", client);
            assertThat(client).isNotNull();
            assertThat(client).isNotEmpty();
            assertThat(client).isEqualTo(clientId);

            runTest(() -> {
                Flux<String> flux = requester
                        .route("client-status")
                        .data("OPEN")
                        .retrieveFlux(String.class);

                StepVerifier.create(flux)
                        .consumeNextWith(s -> assertThat(Integer.valueOf(s)).isGreaterThan(0))
                        .thenCancel()
                        .verify(Duration.ofSeconds(10));
            });
        }

        private void runTest(Runnable test) {
            Mono.fromRunnable(test)
                    .doOnError(ex -> result.onError(ex))
                    .doOnSuccess(o -> result.onComplete())
                    .subscribeOn(Schedulers.elastic()) // StepVerifier will block
                    .subscribe();
        }
    }

    @TestConfiguration
    static class ServerConfig {

        @Bean
        public ServerController serverController() {
            return new ServerController();
        }

        @Bean
        public RSocketMessageHandler serverMessageHandler(RSocketStrategies strategies) {
            RSocketMessageHandler handler = new RSocketMessageHandler();
            handler.setRSocketStrategies(strategies);
            return handler;
        }

        @Bean
        public RSocketStrategies rsocketStrategies() {
            return RSocketStrategies.create();
        }
    }

}
