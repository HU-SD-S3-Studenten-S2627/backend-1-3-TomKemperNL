package nl.hu.s3.project;

import nl.hu.s3.project.lobby.presentation.DisabilityDetails;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyAuthentication implements Authentication {

    private String wiedan;
    private String header;

    public MyAuthentication(String wiedan, String header) {
        this.wiedan = wiedan;
        this.header = header;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authorities;
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.header;
    }

    @Override
    public @Nullable Object getDetails() {
        return new DisabilityDetails("Kleurenblindheids");
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        //waarom zou je moeilijk doen
    }

    @Override
    public String getName() {
        return wiedan;
    }
}
