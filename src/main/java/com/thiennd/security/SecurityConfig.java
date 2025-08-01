package com.thiennd.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        manager.setUsersByUsernameQuery(
                "SELECT username, pwd, enabled FROM users WHERE username = ?"
        );
        manager.setAuthoritiesByUsernameQuery(
                "SELECT username, authority FROM authorities WHERE username = ?"
        );
        manager.setCreateUserSql(
                "INSERT INTO users (username, pwd, enabled) VALUES (?,?,?)"
        );
        return manager;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll()
                                .requestMatchers("/seat-type/create").hasRole("ADMIN")
                                .requestMatchers("/seat-type/edit/*").hasRole("ADMIN")
                                .requestMatchers("/seat-type/delete/*").hasRole("ADMIN")
                                .requestMatchers("/seat-type/duplicate/*").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login")
                        )
                        .accessDeniedHandler((req, res, accessDeniedEx) -> res.sendRedirect("/seat-type/booking"))
                )
                .logout(logout -> logout.permitAll()
                )
                .csrf(csrf -> csrf.disable())   ;
        return http.build();
    }

}
