package com.example.syu_ctn_be.service;

import com.example.syu_ctn_be.entity.User;
import com.example.syu_ctn_be.dto.LoginRequestDto;
import com.example.syu_ctn_be.dto.SignUpRequestDto;
import com.example.syu_ctn_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // 1. 아이디 중복 체크
    public boolean checkIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 2. 회원가입 (순서 교정 완료본)
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
                request.getUserType().name()
        );
        userRepository.save(newUser);
    }

    // 3. 로그인 인증
    @Transactional(readOnly = true)
    public boolean authenticateUser(LoginRequestDto request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return user.getPassword().equals(request.getPassword());
    }

    // 4. 마이페이지 내 정보 조회 (추가)
    @Transactional(readOnly = true)
    public User getUserInfo(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // AuthService.java 수정
    @Transactional
    public void updatePassword(String loginId, String newPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // user.setPassword(newPassword); 👈 기존의 이 줄을 지우고 아래로 교체
        user.changePassword(newPassword);
    }
}