package io.pivotal.rsocketclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShutdownConfig {
    @Bean
    public ExitBean getTerminateBean(RSocketShellClient client) {
        return new ExitBean(client);
    }
}
