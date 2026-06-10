package com.courses.repository;

import com.courses.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    List<CourseEnrollment> findByUserId(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    long countByCourseId(Long courseId);

    @Query("""
            SELECT e.user.id, e.user.firstName, e.user.lastName,
                   AVG(e.progressPercent), SUM(e.totalScore), COUNT(e.id)
            FROM CourseEnrollment e
            WHERE e.course.id = :courseId
            GROUP BY e.user.id, e.user.firstName, e.user.lastName
            ORDER BY AVG(e.progressPercent) DESC
            """)
    List<Object[]> findStudentStatsByCourse(@Param("courseId") Long courseId);
}