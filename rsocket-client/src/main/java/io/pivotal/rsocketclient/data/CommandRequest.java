package io.pivotal.rsocketclient.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandRequest {
    private String command = "command";
    private long created = Instant.now().getEpochSecond();

    public CommandRequest(String command) {
        this.command = command;
    }
}