package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.CommandRequest;
import io.pivotal.rsocketserver.data.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Controller
public class CommandRSocketController {

    /**
     * This @MessageMapping is intended to be used "request --> response" style.
     * For each command received, a simple response is generated showing the command sent.
     * @param request
     * @return
     */
    @MessageMapping("command")
    Mono<EventResponse> requestResponse(CommandRequest request) {
        log.info("Received request-response request: {}", request);
        // create a Mono containing a single EventResponse and return it
        return Mono.just(new EventResponse(request.getCommand()));
    }

    /**
     * This @MessageMapping is intended to be used "subscribe --> stream" style.
     * When a new request command is received, a new stream of events is started and returned to the client.
     * @param request
     * @return
     */
    @MessageMapping("events")
    Flux<EventResponse> stream(CommandRequest request) {
        log.info("Received stream request: {}", request);
        return Flux
                // create a new Flux emitting an element every 1 second
                .interval(Duration.ofSeconds(1))
                // index the Flux
                .index()
                // create a Flux of new EventResponses using the indexed Flux
                .map(objects -> new EventResponse(request.getCommand() + " " + objects.getT1()))
                // use the Flux logger to output each flux event
                .log();
    }

    /**
     * This @MessageMapping is intended to be used "stream --> stream" style.
     * When a new stream of CommandRequests is received, a new stream of EventResponses is started and returned to the client.
     * @param requests
     * @return
     */
    @MessageMapping("channel")
    Flux<EventResponse> channel(Flux<CommandRequest> requests) {
        log.info("Received channel request (Flux) at {}", Instant.now());
        return requests
                // Create an indexed flux which gives each element a number
                .index()
                // then every 1 second
                .delayElements(Duration.ofSeconds(1))
                // create a new Flux with one EventResponse for each CommandRequest (numbered)
                .map(objects -> new EventResponse(objects.getT2().getCommand() + " " + objects.getT1()))
                // use the Flux logger to output each flux event
                .log();
    }

    /**
     * This @MessageMapping is intended to be used "fire --> forget" style.
     * When a new CommandRequest is received, a new mono is returned which is empty.
     * @param request
     * @return
     */
    @MessageMapping("notify")
    public Mono<Void> fireAndForget(CommandRequest request) {
        log.info("Received fire-and-forget request: {}", request);
        // create an empty (Void) Mono and return it
        return Mono.empty();
    }
}
