package io.pivotal.rsocketclient.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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