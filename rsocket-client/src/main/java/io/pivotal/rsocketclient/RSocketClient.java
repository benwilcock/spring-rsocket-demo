package io.pivotal.rsocketclient;


import io.pivotal.rsocketclient.data.CommandRequest;
import io.pivotal.rsocketclient.data.EventResponse;
import org.reactivestreams.Publisher;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RSocketClient {

    private final RSocketRequester rsocketRequester;

    public RSocketClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }

    public Mono<EventResponse> requestResponse(String command) {
        return this.rsocketRequester
                .route("command")
                .data(new CommandRequest(command))
                .retrieveMono(EventResponse.class);
    }

    public Flux<EventResponse> channelCommand(Flux<String> commands) {
        return this.rsocketRequester
                .route("channel")
                .data(Flux.from(commands).map(CommandRequest::new), CommandRequest.class)
                .retrieveFlux(EventResponse.class);
    }

    public Publisher<Void> notifyCommand(String command) {
        return rsocketRequester
                .route("notify")
                .data(new CommandRequest(command))
                .send();
    }

    public Flux<EventResponse> streamCommand(String command) {
        return rsocketRequester
                .route("events")
                .data(new CommandRequest(command))
                .retrieveFlux(EventResponse.class);
    }
}
