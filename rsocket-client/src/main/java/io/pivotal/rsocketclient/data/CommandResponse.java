package io.pivotal.rsocketclient.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponse {

    private String command = "none";
    private Instant received = Instant.now();

    public CommandResponse(String command) {
        this.command = command;
    }

}