package com.courses.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_courses_teacher_id", columnList = "teacher_id"),
        @Index(name = "idx_courses_published", columnList = "published")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
        name = "Course.withSectionsAndLessons",
        attributeNodes = {
                @NamedAttributeNode("teacher"),
                @NamedAttributeNode(value = "sections", subgraph = "section-lessons")
        },
        subgraphs = @NamedSubgraph(name = "section-lessons", attributeNodes = @NamedAttributeNode("lessons"))
)
public class Course extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = false;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Section> sections = new ArrayList<>();
}