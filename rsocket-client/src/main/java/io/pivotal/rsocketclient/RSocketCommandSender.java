package io.pivotal.rsocketclient;


import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@ShellComponent
public class RSocketCommandSender {

    private final RSocketClient rSocketClient;

    @Autowired
    public RSocketCommandSender(RSocketClient rSocketClient) {
        this.rSocketClient = rSocketClient;
    }

    @ShellMethod("Send one request to the RSocket server. No response will be returned.")
    public void fireAndForget(@ShellOption(defaultValue = "fire-and-forget") String command) {
        log.info("\nSending fire and forget request...");
        rSocketClient.notifyCommand(command).subscribe(new Subscriber<Void>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("\nDone");
                return;
            }

            @Override
            public void onNext(Void aVoid) {
                return;
            }

            @Override
            public void onError(Throwable t) {
                return;
            }

            @Override
            public void onComplete() {
                return;
            }
        });
        return;
    }

    @ShellMethod("Send one request to the RSocket server. One response will be printed.")
    public void requestResponse(@ShellOption(defaultValue = "request") String command) {
        log.info("\nSending one request. Waiting for one response...");
        rSocketClient.requestResponse(command).subscribe(cr -> log.info("\nEvent response is: {}", cr));
        return;
    }

    @ShellMethod("Send three requests to the RSocket server. Three responses (stream) will be printed.")
    public void channel(@ShellOption(defaultValue = "requestOne") String command1,
                        @ShellOption(defaultValue = "requestTwo") String command2,
                        @ShellOption(defaultValue = "requestThree") String command3){
        log.info("\nSending three requests. Waiting for three responses...");
        Flux<String> commands = Flux.fromIterable(Arrays.asList(command1, command2, command3)).delayElements(Duration.ofSeconds(2));
        rSocketClient.channelCommand(commands).subscribe(er -> log.info("\nEvent Response is {}", er));
        return;
    }

    @ShellMethod("Send one request to the RSocket server. Many responses (stream) will be printed.")
    public void stream(@ShellOption(defaultValue = "stream") String command) {
        log.info("\nSending one request. Waiting for many responses...");
        rSocketClient.streamCommand(command).subscribe(er -> log.info("\nEvent response is: {}", er));
        return;
    }
}
