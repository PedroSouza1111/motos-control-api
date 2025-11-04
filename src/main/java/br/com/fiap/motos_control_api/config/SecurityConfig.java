package br.com.fiap.motos_control_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private CustomGrantedAuthoritiesMapper customGrantedAuthoritiesMapper;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/login", "/h2-console/**", "/swagger-ui/**",
                                                                "/v3/api-docs/**", "/css/**")
                                                .permitAll()
                                                .requestMatchers("/motos/delete/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .oauth2Login(login -> login
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/motos", true)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userAuthoritiesMapper(
                                                                                customGrantedAuthoritiesMapper::mapAuthorities)))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login"));

                http.csrf(csrf -> csrf
                                .ignoringRequestMatchers("/login", "/h2-console/**", "/swagger-ui/**",
                                                "/v3/api-docs/**"));
                http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }
}