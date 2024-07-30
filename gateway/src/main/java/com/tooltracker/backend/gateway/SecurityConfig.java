package com.tooltracker.backend.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/*
 * We had initially planned to build our custom authorization server or even use
 * a self-hosted authorization server. However, due to time limitations, we have
 * decided to present the authorization process through Spring Cloud Gateway.
 * Therefore, we are opting to utilize a third-party identity provider, namely
 * Cognito.
 * 
 * We don't have enough time to set up the client-side authentication in the
 * frontend. Therefore, for demonstration purposes, we have decided to secure
 * all patch requests on the 'tool' endpoint and leave other endpoints open for
 * presentation purposes. This will showcase how the API Gateway will function
 * as a resource server and how the authorization will work.
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(csrf -> csrf
                        .disable()
                        .authorizeExchange(exchange -> exchange
                                .pathMatchers(HttpMethod.GET, "/v*/tool")
                                .permitAll()
                                .pathMatchers(HttpMethod.GET, "/v*/tool/**")
                                .permitAll()
                                .pathMatchers(HttpMethod.DELETE, "v*/tool/**")
                                .authenticated()
                                .pathMatchers(HttpMethod.POST, "/v*/tool/**")
                                .permitAll()
                                .pathMatchers(HttpMethod.POST, "/v*/tool")
                                .permitAll()
                                .pathMatchers("/eureka/**")
                                .permitAll()
                                .pathMatchers("/v*/movement")
                                .permitAll()
                                .pathMatchers("/v*/movement/**")
                                .permitAll()
                                .anyExchange()
                                .authenticated())
                        .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt));
        return serverHttpSecurity.build();
    }

}
