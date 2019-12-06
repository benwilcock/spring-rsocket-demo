package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.CommandRequest;
import io.pivotal.rsocketserver.data.CommandResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
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
    CommandResponse runCommand(CommandRequest request) {
        log.info("Received a Command: {} at {}", request.getCommand(), Instant.now());
        return new CommandResponse(request.getCommand());
    }

    /**
     * This @MessageMapping is intended to be used "sunscribe --> stream" style.
     * When a new request command is received, a new stream of events is started and returned to the client.
     * @param request
     * @return
     */
    @MessageMapping("events")
    Flux<CommandResponse> events(CommandRequest request) {
        log.info("Received Command: {} at {}", request.getCommand(), Instant.now());
        log.info("Starting a Stream of Strings");
        return Flux
                .fromStream(Stream.generate(() -> new CommandResponse("subscription")))
                .delayElements(Duration.ofSeconds(1))
                .log();
    }
}
