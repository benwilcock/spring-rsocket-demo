package io.pivotal.rsocketclient;


import io.pivotal.rsocketclient.data.CommandRequest;
import io.pivotal.rsocketclient.data.CommandResponse;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RSocketClient {

    private final RSocketRequester rsocketRequester;

    public RSocketClient(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }

    public Mono<CommandResponse> sendCommand(String name) {
        return this.rsocketRequester.route("command").data(new CommandRequest(name))
                .retrieveMono(CommandResponse.class);
    }
}
