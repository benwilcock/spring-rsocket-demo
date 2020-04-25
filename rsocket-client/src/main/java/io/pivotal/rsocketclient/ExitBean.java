package io.pivotal.rsocketclient;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

@Slf4j
public class ExitBean {

    RSocketShellClient client;

    public ExitBean(RSocketShellClient client) {
        this.client = client;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        client.kill();
        log.info("Shutting down the client.");
    }
}
