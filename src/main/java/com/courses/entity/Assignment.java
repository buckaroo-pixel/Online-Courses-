package com.courses.entity;

import com.courses.entity.enums.AssignmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignments_lesson_id", columnList = "lesson_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
        name = "Assignment.withQuestions",
        attributeNodes = @NamedAttributeNode(value = "questions", subgraph = "question-options"),
        subgraphs = @NamedSubgraph(name = "question-options", attributeNodes = @NamedAttributeNode("options"))
)
public class Assignment extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentType type;

    @Column(name = "max_score", nullable = false)
    @Builder.Default
    private int maxScore = 100;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
}