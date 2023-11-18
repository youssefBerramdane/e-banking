package com.example.springcode.Security;

import com.example.springcode.Entities.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class userdetails implements UserDetails {
    private Profile profile;


    public userdetails(Profile profile) {
        this.profile = profile;
    }
    public Long getId(){
        return profile.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(profile.getRole().split(",")).map(auth->new SimpleGrantedAuthority(auth)).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return profile.getPasssword();
    }

    @Override
    public String getUsername() {
        return profile.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
