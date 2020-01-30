package io.pivotal.rsocketclient;


import io.pivotal.rsocketclient.data.CommandRequest;
import io.pivotal.rsocketclient.data.EventResponse;
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

    @Autowired
    public RSocketClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }

    @ShellMethod("Request-Response. Send one request. One response will be printed.")
    public void requestResponse(@ShellOption(defaultValue = "request") String command) {
        log.info("\nRequest-Response. Sending one request. Waiting for one response...");
        this.rsocketRequester
                .route("command")
                .data(new CommandRequest(command))
                .retrieveMono(EventResponse.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }

    @ShellMethod("Fire-And-Forget. Send one request. No response will be returned.")
    public void fireAndForget(@ShellOption(defaultValue = "fire-and-forget") String command) {
        log.info("\nFire-And-Forget. Sending one request. Expect no response (check server)...");
        this.rsocketRequester
                .route("notify")
                .data(new CommandRequest(command))
                .send()
                .subscribe()
                .dispose();
    }

    @ShellMethod("Request-Stream. Send one request. Many responses (stream) will be printed.")
    public void requestStream(@ShellOption(defaultValue = "stream") String command) {
        log.info("\nRequest-Stream. Sending one request. Waiting for unlimited responses (Stop process to quit)...");
        this.rsocketRequester
                .route("events")
                .data(new CommandRequest(command))
                .retrieveFlux(EventResponse.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }

    @ShellMethod("Channel. Stream ten requests. Ten responses (stream) will be printed.")
    public void channel(@ShellOption(defaultValue = "channel") String command){
        log.info("\nChannel. Sending ten requests. Waiting for ten responses...");
        this.rsocketRequester
                .route("channel")
                .data(Flux.range(0,10).map(cr -> new CommandRequest(command)), CommandRequest.class)
                .retrieveFlux(EventResponse.class)
                .subscribe(er -> log.info("Response received: {}", er));
    }
}
