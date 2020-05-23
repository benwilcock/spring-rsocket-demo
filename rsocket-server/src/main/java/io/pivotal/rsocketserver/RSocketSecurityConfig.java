package io.pivotal.rsocketserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Profile("secured")
@Configuration
@EnableRSocketSecurity
public class RSocketSecurityConfig {

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        //This is NOT intended for production use (it is intended for getting started experience only)
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("pass")
                .roles("ROLE")
                .build();

        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
        rsocket.authorizePayload(authorize ->
                authorize
                        .anyRequest().authenticated() // blocks all other requests
                        .anyExchange().authenticated() // blocks connections
        ).simpleAuthentication(Customizer.withDefaults());
        return rsocket.build();
    }
}
