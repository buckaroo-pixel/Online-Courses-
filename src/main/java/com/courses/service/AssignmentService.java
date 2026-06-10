package com.courses.service;

import com.courses.dto.AssignmentDto;
import com.courses.entity.*;
import com.courses.entity.enums.AssignmentType;
import com.courses.entity.enums.SubmissionStatus;
import com.courses.exception.BusinessException;
import com.courses.exception.ResourceNotFoundException;
import com.courses.mapper.AssignmentMapper;
import com.courses.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;
    private final AssignmentMapper assignmentMapper;
    private final ProgressService progressService;

    @Transactional(readOnly = true)
    public AssignmentDto getAssignment(Long id, Long userId) {
        Assignment assignment = assignmentRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задание не найден"));
        AssignmentDto dto = assignmentMapper.toDto(assignment);
        if (userId != null) {
            submissionRepository.findTopByUserIdAndAssignmentIdOrderBySubmittedAtDesc(userId, id)
                    .ifPresent(s -> {
                        dto.setLastSubmissionStatus(s.getStatus());
                        dto.setLastScore(s.getScore());
                    });
        }
        return dto;
    }

    @Transactional
    public Assignment createAssignment(Long lessonId, String title, String description,
                                       AssignmentType type, int maxScore, Long teacherId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        if (!lesson.getSection().getCourse().getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("Нет доступа");
        }
        Assignment assignment = Assignment.builder()
                .lesson(lesson)
                .title(title)
                .description(description)
                .type(type)
                .maxScore(maxScore)
                .build();
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void addQuestion(Long assignmentId, String text, List<String> options, int correctIndex, Long teacherId) {
        Assignment assignment = getAssignmentForTeacher(assignmentId, teacherId);
        Question question = Question.builder()
                .assignment(assignment)
                .text(text)
                .build();
        question = questionRepository.save(question);

        for (int i = 0; i < options.size(); i++) {
            AnswerOption option = AnswerOption.builder()
                    .question(question)
                    .text(options.get(i))
                    .correct(i == correctIndex)
                    .build();
            question.getOptions().add(option);
        }
        questionRepository.save(question);
    }

    @Transactional
    public Submission submitQuiz(User user, Long assignmentId, Map<Long, Long> answers) {
        Assignment assignment = assignmentRepository.findByIdWithQuestions(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Задание не найден"));

        if (assignment.getType() != AssignmentType.QUIZ) {
            throw new BusinessException("Это задание не является тестом");
        }

        Submission submission = Submission.builder()
                .user(user)
                .assignment(assignment)
                .status(SubmissionStatus.AUTO_GRADED)
                .submittedAt(LocalDateTime.now())
                .build();

        int correctCount = 0;
        int totalQuestions = assignment.getQuestions().size();

        for (Question question : assignment.getQuestions()) {
            Long selectedOptionId = answers.get(question.getId());
            boolean isCorrect = false;
            AnswerOption selected = null;

            if (selectedOptionId != null) {
                selected = answerOptionRepository.findById(selectedOptionId).orElse(null);
                if (selected != null && selected.isCorrect()) {
                    isCorrect = true;
                    correctCount++;
                }
            }

            SubmissionAnswer answer = SubmissionAnswer.builder()
                    .submission(submission)
                    .question(question)
                    .selectedOption(selected)
                    .correct(isCorrect)
                    .build();
            submission.getAnswers().add(answer);
        }

        int score = totalQuestions > 0
                ? (correctCount * assignment.getMaxScore()) / totalQuestions
                : 0;
        submission.setScore(score);
        submission = submissionRepository.save(submission);

        Long courseId = assignment.getLesson().getSection().getCourse().getId();
        progressService.recalculateCourseProgress(user.getId(), courseId);
        return submission;
    }

    @Transactional
    public Submission submitPractical(User user, Long assignmentId, String answerText) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Задание не найден"));

        if (assignment.getType() != AssignmentType.PRACTICAL) {
            throw new BusinessException("Это задание не является практической работой");
        }

        Submission submission = Submission.builder()
                .user(user)
                .assignment(assignment)
                .status(SubmissionStatus.PENDING)
                .answerText(answerText)
                .submittedAt(LocalDateTime.now())
                .build();
        return submissionRepository.save(submission);
    }

    @Transactional
    public void gradeSubmission(Long submissionId, int score, String feedback, Long teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Работа не найдена"));

        Long courseTeacherId = submission.getAssignment().getLesson().getSection().getCourse().getTeacher().getId();
        if (!courseTeacherId.equals(teacherId)) {
            throw new BusinessException("Нет прав для проверки");
        }

        submission.setScore(Math.min(score, submission.getAssignment().getMaxScore()));
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        submissionRepository.save(submission);

        Long courseId = submission.getAssignment().getLesson().getSection().getCourse().getId();
        progressService.recalculateCourseProgress(submission.getUser().getId(), courseId);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> getAssignmentsByLesson(Long lessonId, Long userId) {
        return assignmentRepository.findByLessonId(lessonId).stream()
                .map(a -> {
                    AssignmentDto dto = assignmentMapper.toDto(a);
                    if (userId != null) {
                        submissionRepository.findTopByUserIdAndAssignmentIdOrderBySubmittedAtDesc(userId, a.getId())
                                .ifPresent(s -> {
                                    dto.setLastSubmissionStatus(s.getStatus());
                                    dto.setLastScore(s.getScore());
                                });
                    }
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Submission> getPendingSubmissions() {
        return submissionRepository.findPendingPracticalSubmissions();
    }

    private Assignment getAssignmentForTeacher(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Задание не найден"));
        if (!assignment.getLesson().getSection().getCourse().getTeacher().getId().equals(teacherId)) {
            throw new BusinessException("Нет доступа");
        }
        return assignment;
    }
}