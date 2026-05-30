package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.dto.LoginRequestDto;
import com.example.syu_ctn_be.dto.SignUpRequestDto;
import com.example.syu_ctn_be.entity.User;
import com.example.syu_ctn_be.exception.ResourceNotFoundException;
import com.example.syu_ctn_be.repository.ChatSessionRepository;
import com.example.syu_ctn_be.repository.CompletedCourseRepository;
import com.example.syu_ctn_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompletedCourseRepository completedCourseRepository;
    private final ChatSessionRepository chatSessionRepository;

    public boolean checkIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Transactional
    public void registerNewUser(SignUpRequestDto request) {
        User newUser = User.registerUser(
                request.getLoginId(),
                request.getPassword(),
                request.getName(),
                request.getEmail(),
                request.getDepartment(),
                request.getGrade(),
                request.getPhone(),
                request.getUserType()
        );
        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public boolean authenticateUser(LoginRequestDto request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return user.getPassword().equals(request.getPassword());
    }

    @Transactional(readOnly = true)
    public User getUserInfo(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void updatePassword(String loginId, String newPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.changePassword(newPassword);
    }

    @Transactional
    public void deleteMe(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("loginId는 필수입니다.");
        }

        if (!userRepository.existsByLoginId(loginId)) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다. loginId=" + loginId);
        }

        completedCourseRepository.deleteByLoginId(loginId);
        chatSessionRepository.deleteByUser_LoginId(loginId);
        userRepository.deleteByLoginId(loginId);
    }
}
