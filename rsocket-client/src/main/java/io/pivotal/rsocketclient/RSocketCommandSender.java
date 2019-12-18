package io.pivotal.rsocketclient;


import io.pivotal.rsocketclient.data.CommandRequest;
import io.pivotal.rsocketclient.data.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@ShellComponent
public class RSocketCommandSender {

    private final RSocketClient rSocketClient;
    private Disposable eventSubscription;

    @Autowired
    public RSocketCommandSender(RSocketClient rSocketClient) {
        this.rSocketClient = rSocketClient;
    }

    @ShellMethod("Send a command message to the RSocket server. Response will be printed.")
    public void sendCommand(@ShellOption(defaultValue = "doSomething") String command) {
        rSocketClient.sendCommand(command).subscribe(cr -> log.info("\nCommand response is: {}", cr));
        return;
    }

    @ShellMethod("Channel a command message to the RSocket server. Many responses will be printed.")
    public void channelCommands(@ShellOption(defaultValue = "One") String command1,
                                @ShellOption(defaultValue = "Two") String command2,
                                @ShellOption(defaultValue = "Three") String command3){
        Flux<String> commands = Flux.fromIterable(Arrays.asList(command1, command2, command3)).delayElements(Duration.ofSeconds(2));
        eventSubscription = rSocketClient.channelCommand(commands).subscribe(er -> log.info("\nEvent Response is {}", er));
        return;
    }
}
