package com.courses.service;

import com.courses.config.AppProperties;
import com.courses.dto.RegisterDto;
import com.courses.entity.User;
import com.courses.entity.enums.Role;
import com.courses.exception.BusinessException;
import com.courses.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @Mock private AppProperties appProperties;

    @InjectMocks private UserService userService;

    @Test
    void register_shouldCreateStudent() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@test.ru");
        dto.setPassword("123456");
        dto.setFirstName("Тест");
        dto.setLastName("Тестов");
        dto.setRole(Role.STUDENT);

        when(userRepository.existsByEmail("test@test.ru")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.register(dto);

        assertEquals("test@test.ru", result.getEmail());
        assertTrue(result.getRoles().contains(Role.STUDENT));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("exists@test.ru");
        when(userRepository.existsByEmail("exists@test.ru")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.register(dto));
    }
}