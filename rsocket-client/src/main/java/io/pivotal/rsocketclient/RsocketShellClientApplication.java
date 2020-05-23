package io.pivotal.rsocketclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class, SecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class, RSocketSecurityAutoConfiguration.class})
public class RsocketShellClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RsocketShellClientApplication.class, args);
    }
}
