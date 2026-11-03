package nl.hu.s3.project;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static nl.hu.s3.project.SecurityConfig.SECRET;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtParser parser;

    public  JwtFilter() {
        this.parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header =  request.getHeader("Authorization");
        if(header != null){
            String jwt =  header.replace("Bearer ", "");
            var parsedJwt = parser.parseSignedClaims(jwt);
            String user = parsedJwt.getPayload().getSubject();
            S3Authentication myAuth = new S3Authentication(user, header);
            if(parsedJwt.getPayload().containsKey("role") && parsedJwt.getPayload().get("role").equals("ADMIN")){
                myAuth.addRole(S3Authentication.Role.Admin);
            }
            SecurityContextHolder.getContext().setAuthentication(myAuth);
        }

        filterChain.doFilter(request, response);
    }
}
