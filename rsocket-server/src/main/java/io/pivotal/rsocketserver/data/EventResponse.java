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
    private long sent = Instant.now().getEpochSecond();

    public EventResponse(String event) {
        this.event = event;
    }

    public EventResponse(String event, int number) {
        this.event = event;
    }
}