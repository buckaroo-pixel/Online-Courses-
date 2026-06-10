package com.courses.repository;

import com.courses.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    List<LessonProgress> findByUserIdAndLessonIdIn(Long userId, List<Long> lessonIds);

    @Query("""
            SELECT COUNT(lp) FROM LessonProgress lp
            JOIN lp.lesson l
            JOIN l.section s
            WHERE lp.user.id = :userId AND s.course.id = :courseId AND lp.completed = true
            """)
    long countCompletedByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    long countByUserIdAndCompletedTrue(Long userId);
}