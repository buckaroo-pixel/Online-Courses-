package com.courses.repository;

import com.courses.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

    @Query("""
            SELECT l FROM Lesson l
            JOIN l.section s
            JOIN s.course c
            WHERE c.id = :courseId
            ORDER BY s.orderIndex, l.orderIndex
            """)
    List<Lesson> findAllByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Lesson l JOIN l.section s WHERE s.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
}