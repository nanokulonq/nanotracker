package com.nanokulon.nanotracker.util;

import com.nanokulon.nanotracker.entity.Authority;
import com.nanokulon.nanotracker.entity.TrackerUser;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class TrackerUserDetailsFactory {

    public TrackerUserDetails create(TrackerUser user) {
        return new TrackerUserDetails(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(new ArrayList<>(user.getAuthorities()))
        );
    }

    private List<GrantedAuthority> mapToGrantedAuthorities(final List<Authority> authorities) {
        return authorities.stream()
                .map(Authority::getAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
