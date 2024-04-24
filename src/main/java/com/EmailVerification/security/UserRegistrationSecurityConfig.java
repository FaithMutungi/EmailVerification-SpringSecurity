package com.EmailVerification.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class UserRegistrationSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }


     @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         return http
                 .csrf(AbstractHttpConfigurer::disable)
                 .authorizeHttpRequests(req ->
                         req.requestMatchers("/register/**")
                                 .permitAll()
                                 .requestMatchers( "/users/**").hasAnyAuthority("USER", "ADMIN")
                                 .anyRequest()
                                 .authenticated())

                 .build();

    }
}
