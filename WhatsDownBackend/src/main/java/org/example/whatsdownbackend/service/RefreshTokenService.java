package org.example.whatsdownbackend.service;

import org.example.whatsdownbackend.entity.RefreshToken;
import org.example.whatsdownbackend.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUser(User user);
}
