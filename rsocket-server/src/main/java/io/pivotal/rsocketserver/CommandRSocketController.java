package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.CommandRequest;
import io.pivotal.rsocketserver.data.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Stream;

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
                .fromStream(Stream.generate(() -> new EventResponse(request.getCommand())))
                .delayElements(Duration.ofSeconds(1));
    }

    @MessageMapping("channel")
    Flux<EventResponse> channel(Flux<CommandRequest> requests) {
        log.info("Received channel request (Flux).");
        return Flux.from(requests)
                .map(commandRequest -> new EventResponse(commandRequest.getCommand()));
    }

    @MessageMapping("notify")
    public Mono<Void> fireAndForget(CommandRequest request) {
        log.info("Received fire-and-forget request: {}", request);
        return Mono.empty();
    }
}
