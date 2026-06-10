package com.courses.config;

import com.courses.entity.*;
import com.courses.entity.enums.*;
import com.courses.repository.CourseRepository;
import com.courses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = createUser("admin@courses.ru", "София", "Алексеева", Set.of(Role.ADMIN, Role.TEACHER));
        User teacher = createUser("teacher@courses.ru", "Елена", "Морозова", Set.of(Role.TEACHER));
        User student = createUser("student@courses.ru", "Анна", "Сидорова", Set.of(Role.STUDENT));

        Course course = Course.builder()
                .title("Профессиональная Java-разработка")
                .description("Пошаговый курс для тех, кто хочет уверенно писать на Java и создавать современные приложения на Spring Boot. Теория, практика и реальные задачи — всё в одном месте.")
                .teacher(teacher)
                .published(true)
                .build();

        Section section1 = Section.builder()
                .course(course)
                .title("Знакомство с Java")
                .orderIndex(1)
                .build();

        Section section2 = Section.builder()
                .course(course)
                .title("Spring Boot на практике")
                .orderIndex(2)
                .build();

        course.getSections().add(section1);
        course.getSections().add(section2);

        Lesson lesson1 = Lesson.builder()
                .section(section1)
                .title("Почему Java — отличный выбор?")
                .content("Java — зрелый и востребованный язык с сильной экосистемой. Он используется в банках, стартапах и крупных IT-компаниях. На этом курсе вы шаг за шагом освоите фундамент, который пригодится в любой backend-разработке.")
                .type(LessonType.TEXT)
                .orderIndex(1)
                .durationMinutes(18)
                .build();

        Lesson lesson2 = Lesson.builder()
                .section(section1)
                .title("Настройка рабочего окружения")
                .content("Разберём установку JDK, выбор IDE и настройку проекта так, чтобы можно было сразу приступить к практике без лишней рутины.")
                .type(LessonType.VIDEO)
                .mediaUrl("https://www.youtube.com/embed/mGkZ72V0Blg")
                .orderIndex(2)
                .durationMinutes(22)
                .build();

        Lesson lesson3 = Lesson.builder()
                .section(section2)
                .title("Первое приложение на Spring Boot")
                .content("Spring Boot берёт на себя рутину: автоконфигурацию, встроенный сервер и готовые стартеры. В этом уроке вы создадите свой первый проект и поймёте, из чего состоит типичное веб-приложение.")
                .type(LessonType.TEXT)
                .orderIndex(1)
                .durationMinutes(28)
                .build();

        section1.getLessons().add(lesson1);
        section1.getLessons().add(lesson2);
        section2.getLessons().add(lesson3);

        Assignment quiz = Assignment.builder()
                .lesson(lesson1)
                .title("Мини-тест: основы Java")
                .description("Небольшая проверка, чтобы закрепить базовые понятия после первого урока")
                .type(AssignmentType.QUIZ)
                .maxScore(100)
                .build();

        Assignment practical = Assignment.builder()
                .lesson(lesson3)
                .title("Практикум: первый REST-сервис")
                .description("Создайте простой Spring Boot проект с REST-контроллером и опишите, как вы его запускали")
                .type(AssignmentType.PRACTICAL)
                .maxScore(100)
                .build();

        lesson1.getAssignments().add(quiz);
        lesson3.getAssignments().add(practical);

        Question q1 = Question.builder().assignment(quiz).text("Какой тип в Java хранит целые числа?").build();
        Question q2 = Question.builder().assignment(quiz).text("Какой модификатор скрывает поле внутри класса?").build();
        quiz.getQuestions().add(q1);
        quiz.getQuestions().add(q2);

        q1.getOptions().add(AnswerOption.builder().question(q1).text("int").correct(true).build());
        q1.getOptions().add(AnswerOption.builder().question(q1).text("String").correct(false).build());
        q1.getOptions().add(AnswerOption.builder().question(q1).text("boolean").correct(false).build());

        q2.getOptions().add(AnswerOption.builder().question(q2).text("private").correct(true).build());
        q2.getOptions().add(AnswerOption.builder().question(q2).text("public").correct(false).build());
        q2.getOptions().add(AnswerOption.builder().question(q2).text("protected").correct(false).build());

        userRepository.save(admin);
        userRepository.save(teacher);
        userRepository.save(student);
        courseRepository.save(course);
    }

    private User createUser(String email, String firstName, String lastName, Set<Role> roles) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode("password"))
                .firstName(firstName)
                .lastName(lastName)
                .roles(roles)
                .enabled(true)
                .build();
    }
}