package com.matching.mentoree.config.security;

import com.matching.mentoree.domain.RefreshToken;
import com.matching.mentoree.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void removeRefreshToken(Authentication auth) {
        String email = (String) auth.getPrincipal();
        RefreshToken refreshToken = tokenRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        tokenRepository.delete(refreshToken);
    }

}
