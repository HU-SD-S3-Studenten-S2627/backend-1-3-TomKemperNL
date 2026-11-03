package nl.hu.s3.project;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class S3Authentication implements Authentication {
    public enum Role {
        Admin;
    }

    private final String name;
    private final String header;
    private boolean authenticated;
    private List<GrantedAuthority> authorities =  new ArrayList<>();

    public S3Authentication(String name, String header) {
        this.name = name;
        this.header = header;
        this.authenticated = true;
    }

    public void addRole(Role role){
        switch (role){
            case Admin:
                this.authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.header;
    }

    @Override
    public @Nullable Object getDetails() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
