package com.edi.backend.repository;

import com.edi.backend.entity.Submission;
import com.edi.backend.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByUserIdAndChallengeIdAndStatus(Long userId, Long challengeId, SubmissionStatus status);

    List<Long> findChallengeIdsByUserIdAndStatus(Long userId, SubmissionStatus status);
}
