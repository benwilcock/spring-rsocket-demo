package io.pivotal.rsocketclient;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

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
}
