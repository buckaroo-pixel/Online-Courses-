package com.courses.repository;

import com.courses.entity.Submission;
import com.courses.entity.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserId(Long userId);

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStatus(SubmissionStatus status);

    Optional<Submission> findTopByUserIdAndAssignmentIdOrderBySubmittedAtDesc(Long userId, Long assignmentId);

    @Query("""
            SELECT COALESCE(SUM(s.score), 0) FROM Submission s
            JOIN s.assignment a
            JOIN a.lesson l
            JOIN l.section sec
            WHERE s.user.id = :userId AND sec.course.id = :courseId
              AND s.status IN ('GRADED', 'AUTO_GRADED')
            """)
    int sumScoresByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("""
            SELECT s FROM Submission s
            JOIN FETCH s.user u
            JOIN FETCH s.assignment a
            WHERE s.status = 'PENDING' AND a.type = 'PRACTICAL'
            ORDER BY s.submittedAt ASC
            """)
    List<Submission> findPendingPracticalSubmissions();
}