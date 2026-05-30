package com.fixit.domain.auth.security;

import com.fixit.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Identifier can be phone or email
        return userRepository.findByPhoneNumber(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User not found with identifier: " + identifier)));
    }
}
