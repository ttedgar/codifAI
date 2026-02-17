package com.edi.backend.repository;

import com.edi.backend.entity.Submission;
import com.edi.backend.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByUserIdAndChallengeIdAndStatus(Long userId, Long challengeId, SubmissionStatus status);

    @Query("SELECT DISTINCT s.challengeId FROM Submission s WHERE s.userId = :userId AND s.status = :status")
    List<Long> findChallengeIdsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") SubmissionStatus status);
}
