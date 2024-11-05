package org.example.whatsdownbackend.repository;

import org.example.whatsdownbackend.entity.RefreshToken;
import org.example.whatsdownbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}
