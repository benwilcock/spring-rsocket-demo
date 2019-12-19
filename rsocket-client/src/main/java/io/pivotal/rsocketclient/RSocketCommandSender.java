package io.pivotal.rsocketclient;


import lombok.extern.slf4j.Slf4j;
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

    @ShellMethod("Send a command message to the RSocket server. Response will be printed.")
    public void sendCommand(@ShellOption(defaultValue = "doSomething") String command) {
        rSocketClient.sendCommand(command).subscribe(cr -> log.info("\nCommand response is: {}", cr));
        return;
    }

    @ShellMethod("Channel a command message to the RSocket server. Many responses will be printed.")
    public void channelCommands(@ShellOption(defaultValue = "CommandOne") String command1,
                                @ShellOption(defaultValue = "CommandTwo") String command2,
                                @ShellOption(defaultValue = "CommandThree") String command3){
        log.info("\nSending 3 commands");
        Flux<String> commands = Flux.fromIterable(Arrays.asList(command1, command2, command3)).delayElements(Duration.ofSeconds(2));
        rSocketClient.channelCommand(commands).subscribe(er -> log.info("\nEvent Response is {}", er));
        return;
    }
}
