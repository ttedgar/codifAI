package com.edi.backend.repository;

import com.edi.backend.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    boolean existsByTitle(String title);
}
