package com.courses.service;

import com.courses.config.AppProperties;
import com.courses.dto.RegisterDto;
import com.courses.entity.User;
import com.courses.entity.enums.Role;
import com.courses.exception.BusinessException;
import com.courses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AppProperties appProperties;

    @Transactional
    public User register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Пользователь с таким email уже существует");
        }
        Role role = dto.getRole() != null ? dto.getRole() : Role.STUDENT;
        if (role == Role.ADMIN) {
            role = Role.STUDENT;
        }

        User user = User.builder()
                .email(dto.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .roles(Set.of(role))
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(appProperties.getPasswordResetTokenExpiryHours()));
            userRepository.save(user);
            String resetLink = appProperties.getBaseUrl() + "/reset-password/" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BusinessException("Недействительная ссылка восстановления"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Ссылка восстановления истекла");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Пользователь не найден"));
    }
}