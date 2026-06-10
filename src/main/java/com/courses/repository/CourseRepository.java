package com.courses.repository;

import com.courses.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByPublishedTrue(Pageable pageable);

    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);

    @EntityGraph(value = "Course.withSectionsAndLessons")
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdWithContent(@Param("id") Long id);

    @Query("""
            SELECT c.id, c.title, COUNT(e.id)
            FROM Course c
            LEFT JOIN CourseEnrollment e ON e.course.id = c.id
            WHERE c.published = true
            GROUP BY c.id, c.title
            ORDER BY COUNT(e.id) DESC
            """)
    List<Object[]> findPopularCourses(Pageable pageable);
}