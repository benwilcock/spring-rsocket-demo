package io.pivotal.rsocketserver;

import io.rsocket.RSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.server.ServerRSocketFactoryProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("resumption")
@Component
public class RSocketServerSetup implements ServerRSocketFactoryProcessor {

    /**
     * In this method we can configure the ServerRSocketFactory. In this case, we are
     * switching on the 'resumption' feature with 'resume()'. By default, the Resume Session will have a
     * duration of 120s, a timeout of 10s, and use the In Memory (volatile, non-persistent) session store.
     * @param factory
     * @return
     */
    @Override
    public RSocketFactory.ServerRSocketFactory process(RSocketFactory.ServerRSocketFactory factory) {
        log.info("Adding RSocket Server 'Resumption' Feature.");
        return factory.resume(); // By default duration=120s and store=InMemory and timeout=10s
    }
}
