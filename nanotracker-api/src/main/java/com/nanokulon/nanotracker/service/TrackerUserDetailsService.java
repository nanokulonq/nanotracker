package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.entity.TrackerUser;
import com.nanokulon.nanotracker.repository.TrackerUserRepository;
import com.nanokulon.nanotracker.util.TrackerUserDetailsFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackerUserDetailsService implements UserDetailsService {
    private final TrackerUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TrackerUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));

        return TrackerUserDetailsFactory.create(user);
    }
}
