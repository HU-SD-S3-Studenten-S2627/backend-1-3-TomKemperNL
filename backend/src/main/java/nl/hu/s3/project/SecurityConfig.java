package nl.hu.s3.project;

import ch.qos.logback.classic.selector.servlet.LoggerContextFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
public class SecurityConfig {

    public static final  String SECRET = "Dit is een of andere random string die in principe groot genoeg moet zijn om als key te dienen";

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http){
        return http
                .csrf(csrf -> csrf.disable())
                .headers(c -> {
                    c.frameOptions(frame -> frame.disable());
                })
                .authorizeHttpRequests(a -> a.requestMatchers(HttpMethod.POST, "/players/login").permitAll())
                .authorizeHttpRequests(a -> a.requestMatchers("/players").hasRole("ADMIN"))
                .authorizeHttpRequests(a -> a.requestMatchers("/h2-console/**").permitAll())
                .authorizeHttpRequests(a -> a
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()) //Anders vertaalt Spring soms intern 403 -> 401
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .addFilterAfter(new JwtFilter(), LogoutFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(401); //zorgt er voor dat een missende Auth header nog steeds een 401 teruggeeft
                }))
                .build();
    }
}
