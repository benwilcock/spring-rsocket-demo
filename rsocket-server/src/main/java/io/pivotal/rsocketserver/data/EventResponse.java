package io.pivotal.rsocketserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private String event = "";
    private long created = Instant.now().getEpochSecond();

    public EventResponse(String event) {
        this.event = "Response for '" + event + "'";
    }   
}