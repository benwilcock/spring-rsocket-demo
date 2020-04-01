package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Controller
public class RSocketController {

    private static final String SERVER = "Server";
    private static final String RESPONSE = "Response";
    private static final String STREAM = "Stream";
    private static final String CHANNEL = "Channel";

    /**
     * This @MessageMapping is intended to be used "request --> response" style.
     * For each Message received, a new Message is returned with ORIGIN=Server and INTERACTION=Request-Response.
     * @param request
     * @return Message
     */
    @MessageMapping("request-response")
    Message requestResponse(final Message request) {
        log.info("Received request-response request: {}", request);
        // create a single Message and return it
        return new Message(SERVER, RESPONSE);
    }

    /**
     * This @MessageMapping is intended to be used "fire --> forget" style.
     * When a new CommandRequest is received, nothing is returned (void)
     * @param request
     * @return
     */
    @MessageMapping("fire-and-forget")
    public void fireAndForget(final Message request) {
        log.info("Received fire-and-forget request: {}", request);
    }

    /**
     * This @MessageMapping is intended to be used "subscribe --> stream" style.
     * When a new request command is received, a new stream of events is started and returned to the client.
     * @param request
     * @return
     */
    @MessageMapping("stream")
    Flux<Message> stream(final Message request) {
        log.info("Received stream request: {}", request);
        return Flux
                // create a new indexed Flux emitting one element every second
                .interval(Duration.ofSeconds(1))
                // create a Flux of new Messages using the indexed Flux
                .map(index -> new Message(SERVER, STREAM, index))
                // use the Flux logger to output each flux event
                .log();
    }

    /**
     * This @MessageMapping is intended to be used "stream <--> stream" style.
     * The incoming stream contains the interval settings (in seconds) for the outgoing stream of messages. 
     * @param settings
     * @return
     */
    @MessageMapping("channel")
    Flux<Message> channel(final Flux<Duration> settings) {
        return settings
                    .doOnNext(setting -> log.info("\nFrequency setting is {} second(s).\n", setting.getSeconds()))
                    .switchMap(setting -> Flux.interval(setting)
                                                   .map(index -> new Message(SERVER, CHANNEL, index)))
                                                   .log();
    }
}
