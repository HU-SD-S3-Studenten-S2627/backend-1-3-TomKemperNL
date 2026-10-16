package nl.hu.s3.project;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OnsAuthenticatieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Mijn header is: " + request.getHeader("Authorization"));
        String volleHeader = request.getHeader("Authorization");
        String alleenDeWaarde = volleHeader.substring(8);

        System.out.println("De ingelogde gebruiker is " + alleenDeWaarde);
        SecurityContextHolder.getContext().setAuthentication(new MyAuthentication(alleenDeWaarde, volleHeader));
        filterChain.doFilter(request, response);
    }
}
