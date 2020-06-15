package io.pivotal.rsocketserver;

import io.pivotal.rsocketserver.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class RSocketController {

    static final String SERVER = "Server";
    static final String RESPONSE = "Response";
    static final String STREAM = "Stream";
    static final String CHANNEL = "Channel";

    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    @PreDestroy
    void shutdown() {
        log.info("Detaching all remaining clients...");
        CLIENTS.stream().forEach(requester -> requester.rsocket().dispose());
        log.info("Shutting down.");
    }

    @ConnectMapping("shell-client")
    void connectShellClientAndAskForTelemetry(RSocketRequester requester,
                                              @Payload String client) {

        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    // Add all new clients to a client list
                    log.info("Client: {} CONNECTED.", client);
                    CLIENTS.add(requester);
                })
                .doOnError(error -> {
                    // Warn when channels are closed by clients
                    log.warn("Channel to client {} CLOSED", client);
                })
                .doFinally(consumer -> {
                    // Remove disconnected clients from the client list
                    CLIENTS.remove(requester);
                    log.info("Client {} DISCONNECTED", client);
                })
                .subscribe();

        // Callback to client, confirming connection
        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
                .doOnNext(s -> log.info("Client: {} Free Memory: {}.", client, s))
                .subscribe();
    }

    /**
     * This @MessageMapping is intended to be used "request --> response" style.
     * For each Message received, a new Message is returned with ORIGIN=Server and INTERACTION=Request-Response.
     *
     * @param request
     * @return Message
     */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("request-response")
    Mono<Message> requestResponse(final Message request, @AuthenticationPrincipal UserDetails user) {
        log.info("Received request-response request: {}", request);
        log.info("Request-response initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());
        // create a single Message and return it
        return Mono.just(new Message(SERVER, RESPONSE));
    }

    /**
     * This @MessageMapping is intended to be used "fire --> forget" style.
     * When a new CommandRequest is received, nothing is returned (void)
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("fire-and-forget")
    public Mono<Void> fireAndForget(final Message request, @AuthenticationPrincipal UserDetails user) {
        log.info("Received fire-and-forget request: {}", request);
        log.info("Fire-And-Forget initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());
        return Mono.empty();
    }

    /**
     * This @MessageMapping is intended to be used "subscribe --> stream" style.
     * When a new request command is received, a new stream of events is started and returned to the client.
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("stream")
    Flux<Message> stream(final Message request, @AuthenticationPrincipal UserDetails user) {
        log.info("Received stream request: {}", request);
        log.info("Stream initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());

        return Flux
                // create a new indexed Flux emitting one element every second
                .interval(Duration.ofSeconds(1))
                // create a Flux of new Messages using the indexed Flux
                .map(index -> new Message(SERVER, STREAM, index));
    }

    /**
     * This @MessageMapping is intended to be used "stream <--> stream" style.
     * The incoming stream contains the interval settings (in seconds) for the outgoing stream of messages.
     *
     * @param settings
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("channel")
    Flux<Message> channel(final Flux<Duration> settings, @AuthenticationPrincipal UserDetails user) {
        log.info("Received channel request...");
        log.info("Channel initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());

        return settings
                .doOnNext(setting -> log.info("Channel frequency setting is {} second(s).", setting.getSeconds()))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .switchMap(setting -> Flux.interval(setting)
                        .map(index -> new Message(SERVER, CHANNEL, index)));
    }
}
