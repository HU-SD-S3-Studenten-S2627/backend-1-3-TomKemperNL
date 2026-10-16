package nl.hu.s3.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
public class SecurityConfig {

    public SecurityConfig(){
        System.out.println("LALALALA");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/players/login").permitAll())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/admin").hasRole("ADMIN"))
                .authorizeHttpRequests(auth -> {
                        auth.anyRequest().authenticated();
        }).addFilterBefore(new OnsAuthenticatieFilter(), LogoutFilter.class)
                .build();
    }
}
