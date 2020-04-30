package io.pivotal.rsocketserver;

import io.rsocket.core.RSocketServer;
import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("resumption")
@Component
public class RSocketServerResumptionConfig implements RSocketServerCustomizer {

    /**
     * Make the socket capable of resumption.
     * By default, the Resume Session will have a duration of 120s, a timeout of
     * 10s, and use the In Memory (volatile, non-persistent) session store.
     *
     * @param rSocketServer
     * @return
     */

    @Override
    public void customize(RSocketServer rSocketServer) {
        rSocketServer.resume(new Resume());
    }
}
