package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.Message;
import io.rsocket.metadata.WellKnownMimeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
public class RSocketClientDeniedConnectionToSecuredServerITest {

    private static RSocketRequester.Builder reqbuilder;
    private static RSocketRequester requester;
    private static UsernamePasswordMetadata credentials;
    private static MimeType mimeType;
    private static Integer theport;


    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder,
                                 @LocalRSocketServerPort Integer port,
                                 @Autowired RSocketStrategies strategies) {

        mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
        reqbuilder = builder;
        theport = port;

        // *******  The user 'fake' is NOT in the user list! **********
        credentials = new UsernamePasswordMetadata("fake", "pass");


    }

    @Test
    public void testConnectionIsRefused() {
        requester = reqbuilder
                .setupRoute("shell-client")
                .setupData(UUID.randomUUID().toString())
                .setupMetadata(credentials, mimeType)
                .rsocketStrategies(b ->
                        b.encoder(new SimpleAuthenticationEncoder()))
                .connectTcp("localhost", theport)
                .block();

        Mono<Void> result = requester
                .route("fire-and-forget")
                .data(new Message("TEST", "Fire-And-Forget"))
                .retrieveMono(Void.class);

        StepVerifier
                .create(result)
                .verifyErrorMessage("Invalid Credentials");
    }

    @AfterAll
    public static void tearDownOnce() {
        requester.rsocket().dispose();
    }

}