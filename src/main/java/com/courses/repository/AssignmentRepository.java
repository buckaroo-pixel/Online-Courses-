package com.courses.repository;

import com.courses.entity.Assignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByLessonId(Long lessonId);

    @EntityGraph(value = "Assignment.withQuestions")
    @Query("SELECT a FROM Assignment a WHERE a.id = :id")
    Optional<Assignment> findByIdWithQuestions(@Param("id") Long id);
}