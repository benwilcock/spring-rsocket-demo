package io.pivotal.rsocketclient;


import io.pivotal.rsocketclient.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Flux;

@Slf4j
@ShellComponent
public class RSocketClient {

    private final RSocketRequester rsocketRequester;
    private static final String ORIGIN = "Client";
    private static final String RR = "Request-Response";
    private static final String FAF = "Fire-And-Forget";
    private static final String STREAM = "Stream";
    private static final String CHANNEL = "Channel";

    @Autowired
    public RSocketClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }

    @ShellMethod("Send one request. One response will be printed.")
    public void requestResponse() {
        log.info("\nRequest-Response. Sending one request. Waiting for one response...");
        this.rsocketRequester
                .route("command")
                .data(new Message(ORIGIN, RR))
                .retrieveMono(Message.class)
                .subscribe(message -> log.info("Response received: {}", message));
    }

    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() {
        log.info("\nFire-And-Forget. Sending one request. Expect no response (check server)...");
        this.rsocketRequester
                .route("notify")
                .data(new Message(ORIGIN, FAF))
                .send()
                .subscribe()
                .dispose();
    }

    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream() {
        log.info("\nRequest-Stream. Sending one request. Waiting for unlimited responses (Stop process to quit)...");
        this.rsocketRequester
                .route("stream")
                .data(new Message(ORIGIN, STREAM))
                .retrieveFlux(Message.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }

    @ShellMethod("Stream ten requests. Ten responses (stream) will be printed.")
    public void channel(){
        log.info("\nChannel. Sending ten requests. Waiting for ten responses...");
        this.rsocketRequester
                .route("channel")
                .data(Flux.range(0,10).map(integer -> new Message(ORIGIN, CHANNEL, integer)), Message.class)
                .retrieveFlux(Message.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }
}
