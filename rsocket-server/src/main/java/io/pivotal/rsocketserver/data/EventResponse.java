package io.pivotal.rsocketserver.data;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private String event = "none";
    private Instant sent = Instant.now();

    public EventResponse(String event) {
        this.event = event;
    }
}