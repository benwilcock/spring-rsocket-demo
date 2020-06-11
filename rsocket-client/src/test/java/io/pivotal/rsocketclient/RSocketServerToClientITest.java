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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
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
public class RSocketServerToClientITest {

    private static String clientId;

    private static AnnotationConfigApplicationContext context;

    private static CloseableChannel server;


    @BeforeAll
    public static void setupOnce() {
        // create a client identity spring for this test suite
        clientId = UUID.randomUUID().toString();

        // create a Spring context for this test suite and obtain some beans
        context = new AnnotationConfigApplicationContext(ServerConfig.class);

        // Create an RSocket server for use in testing
        RSocketMessageHandler messageHandler = context.getBean(RSocketMessageHandler.class);
        server = RSocketServer.create(messageHandler.responder())
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .bind(TcpServerTransport.create("localhost", 0))
                .block();
    }

    @AfterAll
    public static void tearDownOnce() {
        server.dispose();
    }

    /**
     * Test that our client-side 'ClientHandler' class responds to server sent messages correctly.
     */
    @Test
    public void testServerCallsClientAfterConnection() {
        connectAndRunTest("shell-client");
    }

    /**
     * This private method is used to establish a connection to our fake RSocket server.
     * It also controls the state of our test controller. This method is reusable by many tests.
     *
     * @param connectionRoute
     */
    private void connectAndRunTest(String connectionRoute) {

        ServerController controller = context.getBean(ServerController.class);
        RSocketStrategies strategies = context.getBean(RSocketStrategies.class);
        RSocketRequester requester = null;

        try {
            controller.reset();

            // Add our ClientHandler as a responder
            SocketAcceptor responder = RSocketMessageHandler.responder(strategies, new ClientHandler());

            // Create an RSocket requester that includes our responder
            requester = RSocketRequester.builder()
                    .setupRoute(connectionRoute)
                    .setupData(clientId)
                    .rsocketStrategies(strategies)
                    .rsocketConnector(connector -> connector.acceptor(responder))
                    .connectTcp("localhost", server.address().getPort())
                    .block();

            // Give the test time to run, wait for the server's call.
            controller.await(Duration.ofSeconds(10));
        } finally {
            if (requester != null) {
                requester.rsocket().dispose();
            }
        }
    }

    /**
     * Fake Spring @Controller class which is a stand-in 'test rig' for our real server.
     * It contains a custom @ConnectMapping that tests if our ClientHandler is responding to
     * server-side calls for telemetry data.
     */
    @Controller
    static class ServerController {

        // volatile guarantees visibility across threads.
        // MonoProcessor implements stateful semantics for a mono
        volatile MonoProcessor<Object> result;

        // Reset the stateful Mono
        public void reset() {
            this.result = MonoProcessor.create();
        }

        // Allow some time for the test to execute
        public void await(Duration duration) {
            this.result.block(duration);
        }

        /**
         * Test method. When a client connects to this server, ask the client for its telemetry data
         * and test that the telemetry received is within a good range.
         *
         * @param requester
         * @param client
         */
        @ConnectMapping("shell-client")
        void verifyConnectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String client) {

            // test the client's message payload contains the expected client ID
            assertThat(client).isNotNull();
            assertThat(client).isNotEmpty();
            assertThat(client).isEqualTo(clientId);
            log.info("************** CONNECTION - Client ID: {}", client);

            runTest(() -> {
                Flux<String> flux = requester
                        .route("client-status") // Test the 'client-status' message handler mapping
                        .data("OPEN") // confirm to the client th connection is open
                        .retrieveFlux(String.class); // ask the client for its telemetry

                StepVerifier.create(flux)
                        .consumeNextWith(s -> {
                            // assert the memory reading is in the 'good' range
                            assertThat(s).isNotNull();
                            assertThat(s).isNotEmpty();
                            assertThat(Integer.valueOf(s)).isPositive();
                            assertThat(Integer.valueOf(s)).isGreaterThan(0);
                        })
                        .thenCancel()
                        .verify(Duration.ofSeconds(10));
            });
        }

        /**
         * Run the provided test, collecting the results into a stateful Mono.
         *
         * @param test
         */
        private void runTest(Runnable test) {
            // Run the test provided
            Mono.fromRunnable(test)
                    .doOnError(ex -> result.onError(ex)) // test result was an error
                    .doOnSuccess(o -> result.onComplete()) // test result was success
                    .subscribeOn(Schedulers.elastic()) // StepVerifier will block
                    .subscribe();
        }
    }

    /**
     * This test-specific configuration allows Spring to help configure our test environment.
     * These beans will be placed into the Spring context and can be accessed when required.
     */
    @TestConfiguration
    static class ServerConfig {

        @Bean
        public ServerController serverController() {
            return new ServerController();
        }

        @Bean
        public RSocketMessageHandler serverMessageHandler(@Qualifier("testStrategies") RSocketStrategies strategies) {
            RSocketMessageHandler handler = new RSocketMessageHandler();
            handler.setRSocketStrategies(strategies);
            return handler;
        }

        @Bean("testStrategies")
        public RSocketStrategies rsocketStrategies() {
            return RSocketStrategies.create();
        }
    }

}
