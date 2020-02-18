package io.pivotal.rsocketclient;


import io.pivotal.rsocketclient.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@Slf4j
@ShellComponent
public class RSocketShellClient {

    private final RSocketRequester rsocketRequester;
    private static final String CLIENT = "Client";
    private static final String REQUEST = "Request";
    private static final String FIRE_AND_FORGET = "Fire-And-Forget";
    private static final String STREAM = "Stream";
    private static final String CHANNEL = "Channel";

    @Autowired
    public RSocketShellClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }

    @ShellMethod("Send one request. One response will be printed.")
    public void requestResponse() throws InterruptedException {
        log.info("\nRequest-Response. Sending one request. Waiting for one response...");
        this.rsocketRequester
                .route("request-response")
                .data(new Message(CLIENT, REQUEST))
                .retrieveMono(Message.class)
                .subscribe(message -> log.info("\nServer says message received. \nResponse was: {}", message));
        TimeUnit.SECONDS.sleep(2);
    }

    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
        log.info("\nFire-And-Forget. Sending one request. Expect no response (check server console log)...");
        this.rsocketRequester
                .route("fire-and-forget")
                .data(new Message(CLIENT, FIRE_AND_FORGET))
                .send()
                .subscribe()
                .dispose();
        TimeUnit.SECONDS.sleep(2);
    }

    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream() {
        log.info("\nRequest-Stream. Sending one request. Waiting for unlimited responses (Stop process to quit)...");
        this.rsocketRequester
                .route("stream")
                .data(new Message(CLIENT, STREAM))
                .retrieveFlux(Message.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }

    @ShellMethod("Stream ten requests. Ten responses (stream) will be printed.")
    public void channel(){
        log.info("\nChannel. Sending ten requests. Waiting for ten responses...");
        this.rsocketRequester
                .route("channel")
                .data(Flux.range(0,10).map(integer -> new Message(CLIENT, CHANNEL, integer)), Message.class)
                .retrieveFlux(Message.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }
}
