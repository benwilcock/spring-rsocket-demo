package io.pivotal.rsocketclient;


import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class ClientHandler {

    @MessageMapping("status")
    public Mono<String> statusUpdate(String status) {
        log.info(status);
        return Mono.just("CONNECTED").delayElement(Duration.ofSeconds(1));
    }
}
